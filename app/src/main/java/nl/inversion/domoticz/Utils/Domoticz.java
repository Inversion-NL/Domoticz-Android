package nl.inversion.domoticz.Utils;

import android.content.Context;
import android.util.Log;

import java.util.Set;

import nl.inversion.domoticz.app.SharedPref;

@SuppressWarnings("unused")
public class Domoticz {

    private static final String TAG = Domoticz.class.getSimpleName();

    public static final String JSON_FIELS_RESULT = "result";

    public final int JSON_REQUEST_URL_DASHBOARD = 1;
    public final int JSON_REQUEST_URL_SCENES = 2;
    public final int JSON_REQUEST_URL_SWITCHES = 3;
    public final int JSON_REQUEST_URL_UTILITIES = 4;
    public final int JSON_REQUEST_URL_TEMPERATURE = 5;
    public final int JSON_REQUEST_URL_WEATHER = 6;
    public final int JSON_REQUEST_URL_CAMERAS = 7;
    public final int JSON_REQUEST_URL_SUNRISE_SUNSET = 8;

    public final int JSON_SET_URL_SCENES = 101;

    public final int JSON_ACTION_ON = 201;
    public final int JSON_ACTION_OFF = 202;

    public static final String SCENE_TYPE_GROUP = "Group";
    public static final String SCENE_TYPE_SCENE = "Scene";

    private final String ACTION_ON = "On";
    private final String ACTION_OFF = "Off";

    private static final String URL_DASHBOARD = "";
    private static final String URL_SCENES = "/json.htm?type=scenes";
    private static final String URL_SWITCHES = "/json.htm?type=command&param=getlightswitches";
    private static final String URL_UTILITIES = "/json.htm?type=scenes";
    private static final String URL_TEMPERATURE = "/json.htm?type=scenes";
    private static final String URL_WEATHER = "/json.htm?type=scenes";
    private static final String URL_CAMERAS = "/json.htm?type=scenes";
    private static final String URL_SUNRISE_SUNSET = "/json.htm?type=command&param=getSunRiseSet";

    private static final String URL_SWITCH_SCENE_PART1 = "/json.htm?type=command&param=switchscene&idx=";
    private static final String URL_SWITCH_CMD = "&switchcmd=";

    private static final String PROTOCOL_INSECURE = "http://";
    private static final String PROTOCOL_SECURE = "https://";
    Context mContext;

    public Domoticz(Context mContext) {
        this.mContext = mContext;
    }

    public boolean isUserLocal() {

        boolean local = false;

        SharedPref mSharedPref = new SharedPref(mContext);
        WifiUtil mWifiUtil = new WifiUtil(mContext);

        if (mWifiUtil.isConnected()) {

            Set<String> localSsid = mSharedPref.getLocalSsid();
            String currentSsid = mWifiUtil.getCurrentSsid();

            // Remove quotes from current SSID read out
            currentSsid = currentSsid.substring(1, currentSsid.length() - 1);

            for (String ssid : localSsid) {
                if (ssid.equals(currentSsid)) local = true;
            }
        }

        return local;
    }

    private String getJsonRequestUrl(int jsonRequestUrl) {

        String url = URL_SWITCHES;

        switch (jsonRequestUrl) {
            case JSON_REQUEST_URL_DASHBOARD:
                url = URL_DASHBOARD;
                break;

            case JSON_REQUEST_URL_SCENES:
                url = URL_SCENES;
                break;

            case JSON_REQUEST_URL_SWITCHES:
                url = URL_SWITCHES;
                break;

            case JSON_REQUEST_URL_UTILITIES:
                url = URL_UTILITIES;
                break;

            case JSON_REQUEST_URL_TEMPERATURE:
                url = URL_TEMPERATURE;
                break;

            case JSON_REQUEST_URL_WEATHER:
                url = URL_WEATHER;
                break;

            case JSON_REQUEST_URL_CAMERAS:
                url = URL_CAMERAS;
                break;
        }
        return url;
    }

    private String getJsonSetUrl(int jsonSetUrl) {

        String url = URL_SWITCHES;

        switch (jsonSetUrl) {
            case JSON_SET_URL_SCENES:
                url = URL_SWITCH_SCENE_PART1;
                break;

        }
        return url;
    }

    public String constructRequestUrl(int jsonRequestUrl) {

        String protocol, url, port, jsonUrl;
        StringBuilder buildUrl = new StringBuilder();

        SharedPref mSharedPref = new SharedPref(mContext);

        if (isUserLocal()) {

            if (mSharedPref.isDomoticzLocalSecure()) {
                protocol = PROTOCOL_SECURE;
            }
            else protocol = PROTOCOL_INSECURE;
            url = mSharedPref.getDomoticzLocalUrl();
            port = mSharedPref.getDomoticzLocalPort();

        } else {
            if (mSharedPref.isDomoticzRemoteSecure()) {
                protocol = PROTOCOL_SECURE;
            }
            else protocol = PROTOCOL_INSECURE;
            url = mSharedPref.getDomoticzRemoteUrl();
            port = mSharedPref.getDomoticzRemotePort();
        }

        jsonUrl = getJsonRequestUrl(jsonRequestUrl);

        String fullString = buildUrl.append(protocol).append(url).append(":").append(port).append(jsonUrl).toString();
        Log.d(TAG, "Constructed url: " + fullString);

        return fullString;
    }

    public String constructSetUrl(int jsonSetUrl, int idx, int action) {

        String protocol, url, port, jsonUrl, actionUrl;
        StringBuilder buildUrl = new StringBuilder();

        SharedPref mSharedPref = new SharedPref(mContext);

        if (isUserLocal()) {

            if (mSharedPref.isDomoticzLocalSecure()) {
                protocol = PROTOCOL_SECURE;
            }
            else protocol = PROTOCOL_INSECURE;
            url = mSharedPref.getDomoticzLocalUrl();
            port = mSharedPref.getDomoticzLocalPort();

        } else {
            if (mSharedPref.isDomoticzRemoteSecure()) {
                protocol = PROTOCOL_SECURE;
            }
            else protocol = PROTOCOL_INSECURE;
            url = mSharedPref.getDomoticzRemoteUrl();
            port = mSharedPref.getDomoticzRemotePort();
        }

        switch (action) {
            case JSON_ACTION_ON:
                actionUrl = ACTION_ON;
                break;

            case JSON_ACTION_OFF:
                actionUrl = ACTION_OFF;
                break;

            default:
                actionUrl = ACTION_ON;
        }

        jsonUrl = getJsonSetUrl(JSON_SET_URL_SCENES) + String.valueOf(idx) + URL_SWITCH_CMD + actionUrl;

        String fullString = buildUrl.append(protocol).append(url).append(":").append(port).append(jsonUrl).toString();
        Log.d(TAG, "Constructed url: " + fullString);

        return fullString;
    }
}