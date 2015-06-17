package nl.inversion.domoticz.app;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import de.duenndns.ssl.MemorizingTrustManager;
import nl.inversion.domoticz.Utils.SharedPrefUtil;

@SuppressWarnings("unused")
public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static AppController mInstance;
    int socketTimeout = 1000 * 5;               // 5 seconds
    private SharedPrefUtil mSharedPref;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mSharedPref = new SharedPrefUtil(getApplicationContext());
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // register MemorizingTrustManager for HTTPS
            Context context = getApplicationContext();

            if (mSharedPref.isDomoticzLocalSecure() || mSharedPref.isDomoticzRemoteSecure()) {
                Log.d(TAG, "Secure connection, initializing MemorizingTrustManager");
                try {
                    SSLContext sc = SSLContext.getInstance("TLS");
                    MemorizingTrustManager mtm = new MemorizingTrustManager(context);
                    sc.init(null, new X509TrustManager[]{mtm}, new java.security.SecureRandom());

                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    HttpsURLConnection.setDefaultHostnameVerifier(
                            mtm.wrapHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier()));



                } catch (KeyManagementException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            } else Log.d(TAG, "No secure connection, not initializing MemorizingTrustManager");
            mRequestQueue = Volley.newRequestQueue(context);
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        req.setRetryPolicy(retryPolicy);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}