package nl.inversion.domoticz.Domoticz;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.inversion.domoticz.Interfaces.DevicesReceiver;
import nl.inversion.domoticz.Interfaces.ScenesReceiver;
import nl.inversion.domoticz.Interfaces.StatusReceiver;
import nl.inversion.domoticz.Interfaces.SwitchesReceiver;
import nl.inversion.domoticz.Interfaces.UtilitiesReceiver;
import nl.inversion.domoticz.Interfaces.VersionReceiver;
import nl.inversion.domoticz.Interfaces.setCommandReceiver;
import nl.inversion.domoticz.R;
import nl.inversion.domoticz.SettingsActivity;
import nl.inversion.domoticz.Utils.PhoneConnectionUtil;
import nl.inversion.domoticz.Utils.RequestUtil;
import nl.inversion.domoticz.Utils.SharedPrefUtil;
import nl.inversion.domoticz.Utils.UsefulBits;
import nl.inversion.domoticz.Utils.VolleyUtil;

@SuppressWarnings("unused")
public class Domoticz {

    public static final String AUTH_METHOD_LOGIN_FORM = "Login form";
    public static final String AUTH_METHOD_BASIC_AUTHENTICATION = "Basic authentication";
    /*
     *  Public variables
     */
    public static final String PROTOCOL_SECURE = "HTTPS";
    public static final String PROTOCOL_INSECURE = "HTTP";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String JSON_FIELD_RESULT = "result";
    public static final String JSON_FIELD_STATUS = "status";
    public static final String JSON_FIELD_VERSION = "version";

    public static final int JSON_REQUEST_URL_DASHBOARD = 1;
    public static final int JSON_REQUEST_URL_SCENES = 2;
    public static final int JSON_REQUEST_URL_SWITCHES = 3;
    public static final int JSON_REQUEST_URL_UTILITIES = 4;
    public static final int JSON_REQUEST_URL_TEMPERATURE = 5;
    public static final int JSON_REQUEST_URL_WEATHER = 6;
    public static final int JSON_REQUEST_URL_CAMERAS = 7;
    public static final int JSON_REQUEST_URL_SUNRISE_SUNSET = 8;
    public static final int JSON_REQUEST_URL_VERSION = 9;
    public static final int JSON_REQUEST_URL_DEVICES = 10;

    public static final int JSON_SET_URL_SCENES = 101;
    public static final int JSON_SET_URL_SWITCHES = 102;
    public static final int JSON_SET_URL_TEMP = 103;
    public static final int JSON_SET_URL_FAVORITE = 104;

    public static final int JSON_GET_STATUS = 301;

    public static final int JSON_ACTION_ON = 201;
    public static final int JSON_ACTION_OFF = 202;
    public static final int JSON_ACTION_UP = 203;
    public static final int JSON_ACTION_STOP = 204;
    public static final int JSON_ACTION_DOWN = 205;
    public static final int JSON_ACTION_MIN = 206;
    public static final int JSON_ACTION_PLUS = 207;
    public static final int JSON_ACTION_DIMLEVEL = 210;
    public static final int JSON_ACTION_FAVORITE_ON = 208;
    public static final int JSON_ACTION_FAVORITE_OFF = 209;

    public static final String SCENE_TYPE_GROUP = "Group";
    public static final String SCENE_TYPE_SCENE = "Scene";
    public static final String UTILITIES_TYPE_THERMOSTAT = "Thermostat";

    public static final int SWITCH_TYPE_ON_OFF = 0;
    public static final int SWITCH_TYPE_CONTACT = 2;
    public static final int SWITCH_TYPE_BLINDS = 3;
    public static final int SWITCH_TYPE_SMOKE_DETECTOR = 5;
    public static final int SWITCH_TYPE_DIMMER = 7;
    public static final int SWITCH_TYPE_PUSH_ON_BUTTON = 9;
    public static final String SWITCH_HIDDEN_CHARACTER = "$";

    public static final int BLINDS_ACTION_UP = 1;
    public static final int BLINDS_ACTION_STOP = 2;
    public static final int BLINDS_ACTION_DOWN = 3;
    public static final int SWITCH_ACTION_ON = 10;
    public static final int SWITCH_ACTION_OFF = 11;
    public static final int SWITCH_ACTION_DIMLEVEL = 12;
    public static final int THERMOSTAT_ACTION_PLUS = 21;
    public static final int THERMOSTAT_ACTION_MIN = 22;
    public static final String[] ITEMS_UTILITIES = {UTILITIES_TYPE_THERMOSTAT};
    /*
     *  Log tag
     */
    private static final String TAG = Domoticz.class.getSimpleName();
    /*
     *  Private variables
     */
    private static final String ACTION_ON =             "On";
    private static final String ACTION_OFF =            "Off";
    private static final String ACTION_UP =             "Up";
    private static final String ACTION_STOP =           "Stop";
    private static final String ACTION_DOWN =           "Down";
    private static final String ACTION_PLUS =           "Plus";
    private static final String ACTION_MIN =            "Min";
    private static final String ACTION_FAVORITE_ON =    "1";
    private static final String ACTION_FAVORITE_OFF =   "0";
    private static final String URL_VERSION =           "/json.htm?type=command&param=getversion";
    private static final String URL_DASHBOARD =         "";
    private static final String URL_SCENES =            "/json.htm?type=scenes";
    private static final String URL_SWITCHES =          "/json.htm?type=command&param=getlightswitches";
    private static final String URL_TEMPERATURE =       "";
    private static final String URL_WEATHER =           "";
    private static final String URL_CAMERAS =           "";
    private static final String URL_DEVICES =           "/json.htm?type=devices";
    private static final String URL_UTILITIES = Domoticz.URL_DEVICES;
    private static final String URL_DEVICE_STATUS =     "/json.htm?type=devices&rid=";
    private static final String URL_SUNRISE_SUNSET =    "/json.htm?type=command&param=getSunRiseSet";
    private static final String URL_SWITCH_DIM_LEVEL = "Set%20Level&level=";
    private static final String URL_SWITCH_SCENE =      "/json.htm?type=command&param=switchscene&idx=";
    private static final String URL_SWITCH_SWITCHES =   "/json.htm?type=command&param=switchlight&idx=";
    private static final String URL_SWITCH_CMD =        "&switchcmd=";
    private static final String URL_SWITCH_LEVEL = "&level=";
    private static final String URL_TEMP_BASE =         "/json.htm?type=command&param=udevice&idx=";
    private static final String URL_TEMP_VALUE =        "&nvalue=0&svalue=";
    private static final String URL_FAVORITE_BASE =     "/json.htm?type=command&param=makefavorite&idx=";
    private static final String URL_FAVORITE_VALUE =    "&isfavorite=";
    private static final String URL_PROTOCOL_INSECURE = "http://";
    private static final String URL_PROTOCOL_SECURE =   "https://";
    public static boolean debug;
    private final SharedPrefUtil mSharedPrefUtil;
    private final PhoneConnectionUtil mPhoneConnectionUtil;
    Context mContext;

    public Domoticz(Context mContext) {
        this.mContext = mContext;
        mSharedPrefUtil = new SharedPrefUtil(mContext);
        mPhoneConnectionUtil = new PhoneConnectionUtil(mContext);
        debug = mSharedPrefUtil.isDebugEnabled();
    }

    public boolean isDebugEnabled() {
        return mSharedPrefUtil.isDebugEnabled();
    }

    public boolean isUserOnLocalWifi() {

        boolean userIsLocal = false;

        if (mSharedPrefUtil.isLocalServerAddressDifferent()) {
            Set<String> localSsid = mSharedPrefUtil.getLocalSsid();

            if (mPhoneConnectionUtil.isWifiConnected() && localSsid != null) {

                String currentSsid = mPhoneConnectionUtil.getCurrentSsid();

                // Remove quotes from current SSID read out
                currentSsid = currentSsid.substring(1, currentSsid.length() - 1);

                for (String ssid : localSsid) {
                    if (ssid.equals(currentSsid)) userIsLocal = true;
                }
            }
        }

        return userIsLocal;
    }

    public boolean isConnectionDataComplete() {

        boolean result = true;
        HashMap<String, String> stringHashMap = new HashMap<>();
        stringHashMap.put("Domoticz local URL", mSharedPrefUtil.getDomoticzLocalUrl());
        stringHashMap.put("Domoticz local port", mSharedPrefUtil.getDomoticzLocalPort());
        stringHashMap.put("Domoticz remote URL", mSharedPrefUtil.getDomoticzRemoteUrl());
        stringHashMap.put("Domoticz remote port", mSharedPrefUtil.getDomoticzRemotePort());

        for (Map.Entry<String, String> entry : stringHashMap.entrySet()) {

            if (UsefulBits.isStringEmpty(entry.getValue())) {
                Log.d(TAG, entry.getKey() + " is empty");
                result = false;
                break;
            }

        }
        if (debug) Log.d(TAG, "isConnectionDataComplete = " + result);
        return result;
    }

    public boolean isUrlValid() {

        boolean result = true;
        HashMap<String, String> stringHashMap = new HashMap<>();
        stringHashMap.put("Domoticz local URL", mSharedPrefUtil.getDomoticzLocalUrl());
        stringHashMap.put("Domoticz remote URL", mSharedPrefUtil.getDomoticzRemoteUrl());

        for (Map.Entry<String, String> entry : stringHashMap.entrySet()) {

            if (entry.getValue().toLowerCase().startsWith("http")) {
                Log.d(TAG, entry.getKey() + " starts with http");
                result = false;
                break;
            }

        }
        return result;
    }

    public void errorToast(Exception error) {

        String cause;

        if (debug) {
            cause = error.toString();
        } else {
            cause = getErrorMessage(error);
        }
        Toast.makeText(mContext, cause, Toast.LENGTH_LONG).show();
    }

    public String getErrorMessage(Exception error) {

        String errorMessage;

        if (error instanceof VolleyError) {

            VolleyUtil mVolleyUtil = new VolleyUtil(mContext);
            VolleyError volleyError = (VolleyError) error;

            errorMessage = mVolleyUtil.getVolleyErrorMessage(volleyError);
        } else {
            errorMessage = error.getMessage();
        }

        return errorMessage;
    }

    public List<Integer> getSupportedSwitches() {

        List<Integer> switchesSupported = new ArrayList<>();
        switchesSupported.add(Domoticz.SWITCH_TYPE_ON_OFF);
        switchesSupported.add(Domoticz.SWITCH_TYPE_DIMMER);
        switchesSupported.add(Domoticz.SWITCH_TYPE_BLINDS);
        //switchesSupported.add(Domoticz.SWITCH_TYPE_SMOKE_DETECTOR);
        //switchesSupported.add(Domoticz.SWITCH_TYPE_PUSH_ON_BUTTON);

        return switchesSupported;
    }

    public void debugTextToClipboard(TextView debugText) {
        String message = debugText.getText().toString();

        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Domoticz debug data", message);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(mContext, R.string.msg_copiedToClipboard, Toast.LENGTH_SHORT).show();
    }

    /*
     * Domoticz API get and set commands
     */

    private String getJsonGetUrl(int jsonGetUrl) {

        String url = URL_SWITCHES;

        switch (jsonGetUrl) {
            case JSON_REQUEST_URL_VERSION:
                url = URL_VERSION;
                break;

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

            case JSON_REQUEST_URL_DEVICES:
                url = URL_DEVICES;
                break;

            case JSON_GET_STATUS:
                url = URL_DEVICE_STATUS;
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

        if (debug) Log.d(TAG, "Constructed url: " + fullString);

        return fullString;
    }

    public String constructSetUrl(int jsonSetUrl, int idx, int action, long value) {

        String protocol, baseUrl, url, port, jsonUrl = null, actionUrl = null;
        StringBuilder buildUrl = new StringBuilder();
        SharedPrefUtil mSharedPrefUtil = new SharedPrefUtil(mContext);

        if (isUserOnLocalWifi()) {

            if (mSharedPrefUtil.isDomoticzLocalSecure()) {
                protocol = URL_PROTOCOL_SECURE;
            } else protocol = URL_PROTOCOL_INSECURE;

            baseUrl = mSharedPrefUtil.getDomoticzLocalUrl();
            port = mSharedPrefUtil.getDomoticzLocalPort();

        } else {
            if (mSharedPrefUtil.isDomoticzRemoteSecure()) {
                protocol = URL_PROTOCOL_SECURE;
            } else protocol = URL_PROTOCOL_INSECURE;
            baseUrl = mSharedPrefUtil.getDomoticzRemoteUrl();
            port = mSharedPrefUtil.getDomoticzRemotePort();
        }

        switch (action) {
            case JSON_ACTION_ON:
                actionUrl = ACTION_ON;
                break;

            case JSON_ACTION_OFF:
                actionUrl = ACTION_OFF;
                break;

            case JSON_ACTION_UP:
                actionUrl = ACTION_UP;
                break;

            case JSON_ACTION_STOP:
                actionUrl = ACTION_STOP;
                break;

            case JSON_ACTION_DOWN:
                actionUrl = ACTION_DOWN;
                break;

            case JSON_ACTION_MIN:
                actionUrl = String.valueOf(value);
                break;

            case JSON_ACTION_PLUS:
                actionUrl = String.valueOf(value);
                break;

            case JSON_ACTION_FAVORITE_ON:
                actionUrl = ACTION_FAVORITE_ON;
                break;

            case JSON_ACTION_FAVORITE_OFF:
                actionUrl = ACTION_FAVORITE_OFF;
                break;

            case JSON_ACTION_DIMLEVEL:
                actionUrl = URL_SWITCH_DIM_LEVEL + String.valueOf(value);
                break;
        }

        switch (jsonSetUrl) {
            case JSON_SET_URL_SCENES:
                url = URL_SWITCH_SCENE;
                jsonUrl = url
                        + String.valueOf(idx)
                        + URL_SWITCH_CMD + actionUrl;
                break;

            case JSON_SET_URL_SWITCHES:
                url = URL_SWITCH_SWITCHES;
                jsonUrl = url
                        + String.valueOf(idx)
                        + URL_SWITCH_CMD + actionUrl;
                break;

            case JSON_SET_URL_TEMP:
                url = URL_TEMP_BASE;
                jsonUrl = url
                        + String.valueOf(idx)
                        + URL_TEMP_VALUE + actionUrl;
                break;

            case JSON_SET_URL_FAVORITE:
                url = URL_FAVORITE_BASE;
                jsonUrl = url
                        + String.valueOf(idx)
                        + URL_FAVORITE_VALUE + actionUrl;
                break;
        }

        String fullString = buildUrl.append(protocol)
                .append(baseUrl).append(":")
                .append(port)
                .append(jsonUrl).toString();

        if (debug) Log.d(TAG, "Constructed url: " + fullString);

        return fullString;
    }

    /**
     * Shows a dialog where the users is warned for missing connection settings and
     * gives the ability to redirect the user to the app settings
     */
    public void showConnectionSettingsMissingDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext)
                .setTitle(R.string.msg_connectionSettingsIncomplete_title)
                .setCancelable(false)
                .setMessage(mContext.getString(R.string.msg_connectionSettingsIncomplete_msg1) + "\n\n" +
                        mContext.getString(R.string.msg_connectionSettingsIncomplete_msg2))
                .setPositiveButton(R.string.settingsActivity_name, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mContext.startActivity(new Intent(mContext, SettingsActivity.class));
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog emptyCredentialsAlertDialog = alertDialogBuilder.create();
        emptyCredentialsAlertDialog.show();
    }

    public String getUserCredentials(String credential) {

        if (credential.equals(USERNAME) || credential.equals(PASSWORD)) {

            SharedPrefUtil mSharedPrefUtil = new SharedPrefUtil(mContext);
            String username, password;

            if (isUserOnLocalWifi()) {
                Log.d(TAG, "On local wifi");
                username = mSharedPrefUtil.getDomoticzLocalUsername();
                password = mSharedPrefUtil.getDomoticzLocalPassword();
            } else {
                Log.d(TAG, "Not on local wifi");
                username = mSharedPrefUtil.getDomoticzRemoteUsername();
                password = mSharedPrefUtil.getDomoticzRemotePassword();
            }
            HashMap<String, String> credentials = new HashMap<>();
            credentials.put(USERNAME, username);
            credentials.put(PASSWORD, password);

            return credentials.get(credential);
        }
        else return "";
    }

    public void getVersion(VersionReceiver receiver) {
        VersionParser parser = new VersionParser(receiver);
        String url = constructGetUrl(JSON_REQUEST_URL_VERSION);
        RequestUtil.makeJsonVersionRequest(parser,
                getUserCredentials(USERNAME), getUserCredentials(PASSWORD), url);
    }

    public void getScenes(ScenesReceiver receiver) {
        ScenesParser parser = new ScenesParser(receiver);
        String url = constructGetUrl(JSON_REQUEST_URL_SCENES);
        RequestUtil.makeJsonGetRequest(parser,
                getUserCredentials(USERNAME), getUserCredentials(PASSWORD), url);
    }

    public void getSwitches(SwitchesReceiver switchesReceiver) {
        SwitchesParser parser = new SwitchesParser(switchesReceiver);
        String url = constructGetUrl(JSON_REQUEST_URL_SWITCHES);
        RequestUtil.makeJsonGetRequest(parser,
                getUserCredentials(USERNAME), getUserCredentials(PASSWORD), url);
    }

    public void setAction(int idx,
                          int jsonUrl,
                          int jsonAction,
                          long value,
                          setCommandReceiver receiver) {

        setCommandParser parser = new setCommandParser(receiver);

        String url = null;
        switch (jsonUrl) {
            case JSON_SET_URL_SCENES:
                url = constructSetUrl(JSON_SET_URL_SCENES, idx, jsonAction, value);
                break;

            case JSON_SET_URL_SWITCHES:
                url = constructSetUrl(JSON_SET_URL_SWITCHES, idx, jsonAction, value);
                break;

            case JSON_SET_URL_TEMP:
                url = constructSetUrl(JSON_SET_URL_TEMP, idx, jsonAction, value);
                break;

            case JSON_SET_URL_FAVORITE:
                url = constructSetUrl(JSON_SET_URL_FAVORITE, idx, jsonAction, value);

        }

        RequestUtil.makeJsonPutRequest(parser,
                getUserCredentials(USERNAME), getUserCredentials(PASSWORD), url);
    }

    public void getStatus(int idx, StatusReceiver receiver) {
        StatusInfoParser parser = new StatusInfoParser(receiver);
        String url = constructGetUrl(JSON_GET_STATUS) + String.valueOf(idx);
        if (debug) Log.d(TAG, "for idx: " + String.valueOf(idx));

        RequestUtil.makeJsonGetRequest(parser,
                getUserCredentials(USERNAME), getUserCredentials(PASSWORD), url);
    }

    public void getUtilities(UtilitiesReceiver receiver) {
        UtilitiesParser parser = new UtilitiesParser(receiver);
        String url = constructGetUrl(JSON_REQUEST_URL_UTILITIES);
        RequestUtil.makeJsonGetRequest(parser,
                getUserCredentials(USERNAME), getUserCredentials(PASSWORD), url);
    }

    public void getDevices(DevicesReceiver receiver) {
        DevicesParser parser = new DevicesParser(receiver);
        String url = constructGetUrl(JSON_REQUEST_URL_DEVICES);
        RequestUtil.makeJsonGetRequest(parser,
                getUserCredentials(USERNAME), getUserCredentials(PASSWORD), url);
    }
}