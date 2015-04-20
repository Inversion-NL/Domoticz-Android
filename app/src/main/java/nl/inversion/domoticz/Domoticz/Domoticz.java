package nl.inversion.domoticz.Domoticz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import java.util.Set;

import nl.inversion.domoticz.Interfaces.ScenesReceiver;
import nl.inversion.domoticz.Interfaces.PutCommandReceiver;
import nl.inversion.domoticz.Interfaces.StatusReceiver;
import nl.inversion.domoticz.Interfaces.SwitchesReceiver;
import nl.inversion.domoticz.R;
import nl.inversion.domoticz.SettingsActivity;
import nl.inversion.domoticz.Utils.PhoneConnectionUtil;
import nl.inversion.domoticz.Utils.RequestUtil;
import nl.inversion.domoticz.Utils.UsefulBits;
import nl.inversion.domoticz.Utils.SharedPrefUtil;

@SuppressWarnings("unused")
public class Domoticz {

    /*
     *  Log tag
     */
    private static final String TAG = Domoticz.class.getSimpleName();

    /*
     *  Public variables
     */
    public static final String JSON_FIELD_RESULT = "result";
    public static final String JSON_FIELD_STATUS = "status";

    public static final int JSON_REQUEST_URL_DASHBOARD = 1;
    public static final int JSON_REQUEST_URL_SCENES = 2;
    public static final int JSON_REQUEST_URL_SWITCHES = 3;
    public static final int JSON_REQUEST_URL_UTILITIES = 4;
    public static final int JSON_REQUEST_URL_TEMPERATURE = 5;
    public static final int JSON_REQUEST_URL_WEATHER = 6;
    public static final int JSON_REQUEST_URL_CAMERAS = 7;
    public static final int JSON_REQUEST_URL_SUNRISE_SUNSET = 8;

    public static final int JSON_SET_URL_SCENES = 101;
    public static final int JSON_GET_STATUS = 102;

    public static final int JSON_ACTION_ON = 201;
    public static final int JSON_ACTION_OFF = 202;

    public static final String SCENE_TYPE_GROUP = "Group";
    public static final String SCENE_TYPE_SCENE = "Scene";

    /*
     *  Private variables
     */
    private static final String ACTION_ON = "On";
    private static final String ACTION_OFF = "Off";

    private static final String URL_DASHBOARD = "";
    private static final String URL_SCENES = "/json.htm?type=scenes";
    private static final String URL_SWITCHES = "/json.htm?type=command&param=getlightswitches";
    private static final String URL_UTILITIES = "";
    private static final String URL_TEMPERATURE = "";
    private static final String URL_WEATHER = "";
    private static final String URL_CAMERAS = "";

    private static final String URL_STATUS = "/json.htm?type=devices&rid=";
    private static final String URL_SUNRISE_SUNSET = "/json.htm?type=command&param=getSunRiseSet";

    /**
     * Level should be a value between 1 (0%) and 16 (100%)
     * When the light is off, it will be turned on
     * Returns:
     *      "status" : "OK",
     "      "title" : "SwitchLight"
     */
    private static final String URL_SWITCH_DIM_LEVEL = "&switchcmd=Set%20Level&level=";
    private static final String URL_SWITCH_SCENE = "/json.htm?type=command&param=switchscene&idx=";
    private static final String URL_SWITCH_CMD = "&switchcmd=";

    private static final String URL_PROTOCOL_INSECURE = "http://";
    private static final String URL_PROTOCOL_SECURE = "https://";

    Context mContext;
    private final SharedPrefUtil mSharedPrefUtil;
    private final PhoneConnectionUtil mPhoneConnectionUtil;

    public Domoticz(Context mContext) {
        this.mContext = mContext;
        mSharedPrefUtil = new SharedPrefUtil(mContext);
        mPhoneConnectionUtil = new PhoneConnectionUtil(mContext);
    }

    public boolean isUserOnLocalWifi() {

        boolean local = false;
        Set<String> localSsid = mSharedPrefUtil.getLocalSsid();

        if (mPhoneConnectionUtil.isWifiConnected()&& localSsid != null) {

            String currentSsid = mPhoneConnectionUtil.getCurrentSsid();

            // Remove quotes from current SSID read out
            currentSsid = currentSsid.substring(1, currentSsid.length() - 1);

            for (String ssid : localSsid) {
                if (ssid.equals(currentSsid)) local = true;
            }
        }
        return local;
    }

    public boolean isConnectionDataComplete() {

        boolean result = true;

        String[] stringsToCheck = {
                URL_PROTOCOL_SECURE,
                URL_PROTOCOL_INSECURE,
                mSharedPrefUtil.getDomoticzLocalUrl(),
                mSharedPrefUtil.getDomoticzLocalPort(),
                mSharedPrefUtil.getDomoticzRemoteUrl(),
                mSharedPrefUtil.getDomoticzRemotePort()};

        for(String string : stringsToCheck) {
            if(UsefulBits.isStringEmpty(string)) {
                result = false;
                break;
            }
        }
        return result;
    }

    private String getJsonGetUrl(int jsonGetUrl) {

        String url = URL_SWITCHES;

        switch (jsonGetUrl) {
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

            case JSON_GET_STATUS:
                url = URL_STATUS;
                break;
        }
        return url;
    }

    private String getJsonSetUrl(int jsonSetUrl) {

        String url = URL_SWITCHES;

        switch (jsonSetUrl) {
            case JSON_SET_URL_SCENES:
                url = URL_SWITCH_SCENE;
                break;

        }
        return url;
    }

    private String constructGetUrl(int jsonGetUrl) {

        String protocol, url, port, jsonUrl;
        StringBuilder buildUrl = new StringBuilder();
        SharedPrefUtil mSharedPrefUtil = new SharedPrefUtil(mContext);

        if (isUserOnLocalWifi()) {
            if (mSharedPrefUtil.isDomoticzLocalSecure()) protocol = URL_PROTOCOL_SECURE;
            else protocol = URL_PROTOCOL_INSECURE;

            url = mSharedPrefUtil.getDomoticzLocalUrl();
            port = mSharedPrefUtil.getDomoticzLocalPort();

        } else {
            if (mSharedPrefUtil.isDomoticzRemoteSecure()) protocol = URL_PROTOCOL_SECURE;
            else protocol = URL_PROTOCOL_INSECURE;

            url = mSharedPrefUtil.getDomoticzRemoteUrl();
            port = mSharedPrefUtil.getDomoticzRemotePort();

        }
        jsonUrl = getJsonGetUrl(jsonGetUrl);

        String fullString = buildUrl.append(protocol)
                .append(url).append(":")
                .append(port)
                .append(jsonUrl).toString();

        Log.d(TAG, "Constructed url: " + fullString);

        return fullString;
    }

    public String constructSetUrl(int jsonSetUrl, int idx, int action) {

        String protocol, url, port, jsonUrl, actionUrl;
        StringBuilder buildUrl = new StringBuilder();
        SharedPrefUtil mSharedPrefUtil = new SharedPrefUtil(mContext);

        if (isUserOnLocalWifi()) {

            if (mSharedPrefUtil.isDomoticzLocalSecure()) {
                protocol = URL_PROTOCOL_SECURE;
            } else protocol = URL_PROTOCOL_INSECURE;

            url = mSharedPrefUtil.getDomoticzLocalUrl();
            port = mSharedPrefUtil.getDomoticzLocalPort();

        } else {
            if (mSharedPrefUtil.isDomoticzRemoteSecure()) {
                protocol = URL_PROTOCOL_SECURE;
            } else protocol = URL_PROTOCOL_INSECURE;
            url = mSharedPrefUtil.getDomoticzRemoteUrl();
            port = mSharedPrefUtil.getDomoticzRemotePort();
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

        jsonUrl = getJsonSetUrl(JSON_SET_URL_SCENES)
                + String.valueOf(idx)
                + URL_SWITCH_CMD + actionUrl;

        String fullString = buildUrl.append(protocol)
                .append(url).append(":")
                .append(port)
                .append(jsonUrl).toString();

        Log.d(TAG, "Constructed url: " + fullString);

        return fullString;
    }

    /**
     * Shows a dialog where the users is warned for missing connection settings and
     * gives the ability to redirect the user to the app settings
     */
    public void showConnectionSettingsMissingDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext)
                .setTitle(R.string.msg_emptyCredentials_title)
                .setCancelable(false)
                .setMessage(mContext.getString(R.string.msg_emptyCredentials_msg1) + "\n\n" +
                        mContext.getString(R.string.msg_emptyCredentials_msg2))
                .setPositiveButton(R.string.settingsActivity_name, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mContext.startActivity(new Intent(mContext, SettingsActivity.class));
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog emptyCredentialsAlertDialog = alertDialogBuilder.create();
        emptyCredentialsAlertDialog.show();
    }

    public void getScenes(ScenesReceiver receiver) {

        ScenesParser scenesParser = new ScenesParser(receiver);

        SharedPrefUtil mSharedPrefUtil = new SharedPrefUtil(mContext);
        String username, password;

        if (isUserOnLocalWifi()) {
            username = mSharedPrefUtil.getDomoticzLocalUsername();
            password = mSharedPrefUtil.getDomoticzLocalPassword();
        } else {
            username = mSharedPrefUtil.getDomoticzRemoteUsername();
            password = mSharedPrefUtil.getDomoticzRemotePassword();
        }

        String url = constructGetUrl(JSON_REQUEST_URL_SCENES);

        RequestUtil.makeJsonGetRequest(scenesParser, username, password, url);
    }

    public void getSwitches(SwitchesReceiver switchesReceiver) {

        SwitchesParser switchesParser = new SwitchesParser(switchesReceiver);

        SharedPrefUtil mSharedPrefUtil = new SharedPrefUtil(mContext);
        String username, password;

        if (isUserOnLocalWifi()) {
            username = mSharedPrefUtil.getDomoticzLocalUsername();
            password = mSharedPrefUtil.getDomoticzLocalPassword();
        } else {
            username = mSharedPrefUtil.getDomoticzRemoteUsername();
            password = mSharedPrefUtil.getDomoticzRemotePassword();
        }

        String url = constructGetUrl(JSON_REQUEST_URL_SWITCHES);

        RequestUtil.makeJsonGetRequest(switchesParser, username, password, url);
    }

    public void setAction(int idx, int jsonAction, PutCommandReceiver receiver) {

        PutCommandParser putCommandParser = new PutCommandParser(receiver);

        SharedPrefUtil mSharedPrefUtil = new SharedPrefUtil(mContext);
        String username, password;

        if (isUserOnLocalWifi()) {
            username = mSharedPrefUtil.getDomoticzLocalUsername();
            password = mSharedPrefUtil.getDomoticzLocalPassword();
        } else {
            username = mSharedPrefUtil.getDomoticzRemoteUsername();
            password = mSharedPrefUtil.getDomoticzRemotePassword();
        }

        String url = constructSetUrl(JSON_SET_URL_SCENES, idx, jsonAction);

        RequestUtil.makeJsonPutRequest(putCommandParser, username, password, url);
    }

    public void getStatus(int idx, StatusReceiver receiver) {

        StatusInfoParser statusInfoParser = new StatusInfoParser(receiver);

        SharedPrefUtil mSharedPrefUtil = new SharedPrefUtil(mContext);
        String username, password;

        if (isUserOnLocalWifi()) {
            username = mSharedPrefUtil.getDomoticzLocalUsername();
            password = mSharedPrefUtil.getDomoticzLocalPassword();
        } else {
            username = mSharedPrefUtil.getDomoticzRemoteUsername();
            password = mSharedPrefUtil.getDomoticzRemotePassword();
        }

        String url = constructGetUrl(JSON_GET_STATUS) + String.valueOf(idx);
        Log.d(TAG, "for idx: " + String.valueOf(idx));

        RequestUtil.makeJsonGetRequest(statusInfoParser, username, password, url);

    }
}