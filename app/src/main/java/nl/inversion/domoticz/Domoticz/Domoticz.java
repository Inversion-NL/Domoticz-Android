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

    /*
     *  Public variables
     */
    public static final String UTILITIES_TYPE_THERMOSTAT = "Thermostat";
    public static final String AUTH_METHOD_LOGIN_FORM = "Login form";
    public static final String AUTH_METHOD_BASIC_AUTHENTICATION = "Basic authentication";

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String HIDDEN_CHARACTER = "$";
    public static final String[] ITEMS_UTILITIES = {UTILITIES_TYPE_THERMOSTAT};
    /*
    *  Log tag
    */
    private static final String TAG = Domoticz.class.getSimpleName();
    private static final String URL_DEVICE_STATUS = "/json.htm?type=devices&rid=";
    private static final String URL_SUNRISE_SUNSET = "/json.htm?type=command&param=getSunRiseSet";
    private static final String URL_SWITCH_DIM_LEVEL = "Set%20Level&level=";

    /*
     *  Private variables
     */
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
        switchesSupported.add(Device.Type.ON_OFF);
        switchesSupported.add(Device.Type.DIMMER);
        switchesSupported.add(Device.Type.BLINDS);
        //switchesSupported.add(Switch.Type.SMOKE_DETECTOR);  // Not yet supported
        //switchesSupported.add(Switch.Type.PUSH_ON_BUTTON);    // Not yet supported

        return switchesSupported;
    }

    public void debugTextToClipboard(TextView debugText) {
        String message = debugText.getText().toString();

        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Domoticz debug data", message);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(mContext, R.string.msg_copiedToClipboard, Toast.LENGTH_SHORT).show();
    }

    private String getJsonGetUrl(int jsonGetUrl) {

        String url = Url.Category.SWITCHES;

        switch (jsonGetUrl) {
            case Json.Url.Request.VERSION:
                url = Url.Category.VERSION;
                break;

            case Json.Url.Request.DASHBOARD:
                url = Url.Category.DASHBOARD;
                break;

            case Json.Url.Request.SCENES:
                url = Url.Category.SCENES;
                break;

            case Json.Url.Request.SWITCHES:
                url = Url.Category.SWITCHES;
                break;

            case Json.Url.Request.UTILITIES:
                url = Url.Category.UTILITIES;
                break;

            case Json.Url.Request.TEMPERATURE:
                url = Url.Category.TEMPERATURE;
                break;

            case Json.Url.Request.WEATHER:
                url = Url.Category.WEATHER;
                break;

            case Json.Url.Request.CAMERAS:
                url = Url.Category.CAMERAS;
                break;

            case Json.Url.Request.DEVICES:
                url = Url.Category.DEVICES;
                break;

            case Json.Get.STATUS:
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

        String protocol, baseUrl, url, port, jsonUrl = null, actionUrl;
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
            case Device.Switch.Action.ON:
                actionUrl = Url.Action.ON;
                break;

            case Device.Switch.Action.OFF:
                actionUrl = Url.Action.OFF;
                break;

            case Device.Blind.Action.UP:
                actionUrl = Url.Action.UP;
                break;

            case Device.Blind.Action.STOP:
                actionUrl = Url.Action.STOP;
                break;

            case Device.Blind.Action.DOWN:
                actionUrl = Url.Action.DOWN;
                break;

            case Device.Thermostat.Action.MIN:
                actionUrl = String.valueOf(value);
                break;

            case Device.Thermostat.Action.PLUS:
                actionUrl = String.valueOf(value);
                break;

            case Device.Favorite.ON:
                actionUrl = FavoriteAction.ON;
                break;

            case Device.Favorite.OFF:
                actionUrl = FavoriteAction.OFF;
                break;

            case Device.Dimmer.Action.DIM_LEVEL:
                actionUrl = URL_SWITCH_DIM_LEVEL + String.valueOf(value);
                break;

            default:
                throw new NullPointerException("Action not found in method Domoticz.constructSetUrl");
        }

        switch (jsonSetUrl) {
            case Json.Url.Set.SCENES:
                url = URL_SWITCH_SCENE;
                jsonUrl = url
                        + String.valueOf(idx)
                        + URL_SWITCH_CMD + actionUrl;
                break;

            case Json.Url.Set.SWITCHES:
                url = URL_SWITCH_SWITCHES;
                jsonUrl = url
                        + String.valueOf(idx)
                        + URL_SWITCH_CMD + actionUrl;
                break;

            case Json.Url.Set.TEMP:
                url = URL_TEMP_BASE;
                jsonUrl = url
                        + String.valueOf(idx)
                        + URL_TEMP_VALUE + actionUrl;
                break;

            case Json.Url.Set.FAVORITE:
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
        } else return "";
    }

    public void getVersion(VersionReceiver receiver) {
        VersionParser parser = new VersionParser(receiver);
        String url = constructGetUrl(Json.Url.Request.VERSION);
        RequestUtil.makeJsonVersionRequest(parser,
                getUserCredentials(USERNAME), getUserCredentials(PASSWORD), url);
    }

    public void getScenes(ScenesReceiver receiver) {
        ScenesParser parser = new ScenesParser(receiver);
        String url = constructGetUrl(Json.Url.Request.SCENES);
        RequestUtil.makeJsonGetRequest(parser,
                getUserCredentials(USERNAME), getUserCredentials(PASSWORD), url, mSharedPrefUtil.isDomoticzLocalSecure());
    }

    public void getSwitches(SwitchesReceiver switchesReceiver) {
        SwitchesParser parser = new SwitchesParser(switchesReceiver);
        String url = constructGetUrl(Json.Url.Request.SWITCHES);
        RequestUtil.makeJsonGetRequest(parser,
                getUserCredentials(USERNAME), getUserCredentials(PASSWORD), url, mSharedPrefUtil.isDomoticzLocalSecure());
    }

    public void setAction(int idx,
                          int jsonUrl,
                          int jsonAction,
                          long value,
                          setCommandReceiver receiver) {

        setCommandParser parser = new setCommandParser(receiver);
        String url = constructSetUrl(jsonUrl, idx, jsonAction, value);
        RequestUtil.makeJsonPutRequest(parser,
                getUserCredentials(USERNAME), getUserCredentials(PASSWORD), url);
    }

    public void getStatus(int idx, StatusReceiver receiver) {
        StatusInfoParser parser = new StatusInfoParser(receiver);
        String url = constructGetUrl(Json.Get.STATUS) + String.valueOf(idx);
        if (debug) Log.d(TAG, "for idx: " + String.valueOf(idx));

        RequestUtil.makeJsonGetRequest(parser,
                getUserCredentials(USERNAME), getUserCredentials(PASSWORD), url, mSharedPrefUtil.isDomoticzLocalSecure());
    }

    public void getUtilities(UtilitiesReceiver receiver) {
        UtilitiesParser parser = new UtilitiesParser(receiver);
        String url = constructGetUrl(Json.Url.Request.UTILITIES);
        RequestUtil.makeJsonGetRequest(parser,
                getUserCredentials(USERNAME), getUserCredentials(PASSWORD), url, mSharedPrefUtil.isDomoticzLocalSecure());
    }

    public void getDevices(DevicesReceiver receiver) {
        DevicesParser parser = new DevicesParser(receiver);
        String url = constructGetUrl(Json.Url.Request.DEVICES);
        RequestUtil.makeJsonGetRequest(parser,
                getUserCredentials(USERNAME), getUserCredentials(PASSWORD), url, mSharedPrefUtil.isDomoticzLocalSecure());
    }

    public interface Protocol {
        String SECURE = "HTTPS";
        String INSECURE = "HTTP";
    }

    public interface Device {
        interface Switch {
            interface Action {
                int ON = 10;
                int OFF = 11;
            }
        }

        interface Dimmer {
            interface Action {
                int DIM_LEVEL = 20;
            }
        }

        interface Blind {
            interface Action {
                int UP = 30;
                int STOP = 31;
                int DOWN = 32;
            }
        }

        interface Thermostat {
            interface Action {
                int MIN = 50;
                int PLUS = 51;
            }
        }

        interface Type {
            int DOORBELL = 1;
            int CONTACT = 2;
            int BLINDS = 3;
            int SMOKE_DETECTOR = 5;
            int DIMMER = 7;
            int MOTION = 8;
            int PUSH_ON_BUTTON = 9;
            int ON_OFF = 0;
        }

        interface Favorite {
            int ON = 208;
            int OFF = 209;
        }
    }

    public interface Json {
        interface Field {
            String RESULT = "result";
            String STATUS = "status";
            String VERSION = "version";
        }

        interface Url {
            interface Request {
                int DASHBOARD = 1;
                int SCENES = 2;
                int SWITCHES = 3;
                int UTILITIES = 4;
                int TEMPERATURE = 5;
                int WEATHER = 6;
                int CAMERAS = 7;
                int SUNRISE_SUNSET = 8;
                int VERSION = 9;
                int DEVICES = 10;
            }

            interface Set {
                int SCENES = 101;
                int SWITCHES = 102;
                int TEMP = 103;
                int FAVORITE = 104;
            }
        }

        interface Get {
            int STATUS = 301;
        }
    }

    public interface Scene {
        interface Type {
            String GROUP = "Group";
            String SCENE = "Scene";
        }

        interface Action {
            int ON = 40;
            int OFF = 41;
        }
    }

    /*
     * Domoticz API get and set commands
     */

    private interface Url{
        interface Action {
            String ON =             "On";
            String OFF =            "Off";
            String UP =             "Up";
            String STOP =           "Stop";
            String DOWN =           "Down";
            String PLUS =           "Plus";
            String MIN =            "Min";
        }
        interface Category {
            String VERSION =           "/json.htm?type=command&param=getversion";
            String DASHBOARD =         "";
            String SCENES =            "/json.htm?type=scenes";
            String SWITCHES =          "/json.htm?type=command&param=getlightswitches";
            String TEMPERATURE =       "";
            String WEATHER =           "";
            String CAMERAS =           "";
            String DEVICES =           "/json.htm?type=devices";
            String UTILITIES =          DEVICES;
        }
    }

    private interface FavoriteAction {
        String ON =    "1";
        String OFF =   "0";

    }


}