package nl.inversion.domoticz.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

@SuppressWarnings("unused")
public class SharedPrefUtil {

    Context mContext;
    SharedPreferences preferences;

    private static final String http = "http://";
    private static final String https = "https://";

    private static final String LOCAL_SERVER_USERNAME = "local_server_username";
    private static final String LOCAL_SERVER_PASSWORD = "local_server_password";
    private static final String LOCAL_SERVER_URL = "local_server_url";
    private static final String LOCAL_SERVER_PORT = "local_server_port";
    private static final String LOCAL_SERVER_SECURE = "local_server_secure";
    private static final String LOCAL_SERVER_AUTHENTICATION_METHOD =
                                                            "local_server_authentication_method";
    private static final String LOCAL_SERVER_SSID = "local_server_ssid";

    private static final String REMOTE_SERVER_USERNAME = "remote_server_username";
    private static final String REMOTE_SERVER_PASSWORD = "remote_server_password";
    private static final String REMOTE_SERVER_URL = "remote_server_url";
    private static final String REMOTE_SERVER_PORT = "remote_server_port";
    private static final String REMOTE_SERVER_SECURE = "remote_server_secure";
    private static final String REMOTE_SERVER_AUTHENTICATION_METHOD =
            "remote_server_authentication_method";


    public SharedPrefUtil(Context mContext) {
        this.mContext = mContext;
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    /*
     *    Local server settings
     */
    public String getDomoticzLocalUsername() {
        return preferences.getString(LOCAL_SERVER_USERNAME, "");
    }

    public String getDomoticzLocalPassword() {
        return preferences.getString(LOCAL_SERVER_PASSWORD, "");
    }

    public String getDomoticzLocalUrl() {
        return preferences.getString(LOCAL_SERVER_URL, "");
    }

    public String getDomoticzLocalPort() {
        return preferences.getString(LOCAL_SERVER_PORT, "");
    }

    public boolean isDomoticzLocalSecure() {
        return preferences.getBoolean(LOCAL_SERVER_SECURE, true);
    }

    public String getDomoticzLocalAuthenticationMethod() {
        boolean localServerAuthenticationMethodIsLoginForm =
                preferences.getBoolean(LOCAL_SERVER_AUTHENTICATION_METHOD, true);
        String method;

        if (localServerAuthenticationMethodIsLoginForm) method = "Login form";
        else method = "Basic authentication";

        return method;
    }

    /*
     *    Remote server settings
     */
    public String getDomoticzRemoteUsername() {
        return preferences.getString(REMOTE_SERVER_USERNAME, "");
    }

    public String getDomoticzRemotePassword() {
        return preferences.getString(REMOTE_SERVER_PASSWORD, "");
    }

    public String getDomoticzRemoteUrl() {
        return preferences.getString(REMOTE_SERVER_URL, "");
    }

    public String getDomoticzRemotePort() {
        return preferences.getString(REMOTE_SERVER_PORT, "");
    }

    public boolean isDomoticzRemoteSecure() {
        return preferences.getBoolean(REMOTE_SERVER_SECURE, true);
    }

    public String getDomoticzRemoteAuthenticationMethod() {
        boolean remoteServerAuthenticationMethodIsLoginForm =
                preferences.getBoolean(REMOTE_SERVER_AUTHENTICATION_METHOD, true);
        String method;

        if (remoteServerAuthenticationMethodIsLoginForm) method = "Login form";
        else method = "Basic authentication";

        return method;
    }

    public Set<String> getLocalSsid() {
        return preferences.getStringSet(LOCAL_SERVER_SSID, null);
    }
}