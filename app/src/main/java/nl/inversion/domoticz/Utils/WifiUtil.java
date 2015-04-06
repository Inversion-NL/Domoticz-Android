package nl.inversion.domoticz.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

@SuppressWarnings("unused")
public class WifiUtil {

    Context mContext;
    final WifiManager wifiManager;
    ConnectivityManager connManager;
    NetworkInfo networkInfo;

    public WifiUtil(Context mContext){
        this.mContext = mContext;
        wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    }

    public CharSequence[] startSsidScanAsCharSequence() {
        List<ScanResult> results = startSsidScan();
        CharSequence[] entries = new CharSequence[results.size()];

        int i = 0;
        for (ScanResult result : results) {
            entries[i] = result.SSID;
            i++;
        }
        return entries;
    }

    public List<ScanResult> startSsidScan() {
        List<ScanResult> results;

        if (wifiManager.startScan()) {
            results = wifiManager.getScanResults();
            return results;
        } else {
            return null;
        }
    }

    public boolean isConnected() {
        return networkInfo.isConnected();
    }

    public String getCurrentSsid() {
        String ssid = null;

        if (networkInfo.isConnected()) {
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !connectionInfo.getSSID().isEmpty()) {
                ssid = connectionInfo.getSSID();
            }
        }
        return ssid;
    }
}