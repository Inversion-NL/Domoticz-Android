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
import nl.inversion.domoticz.Interfaces.PlansReceiver;
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

    public static final int batteryLevelMax = 100;
    public static final int signalLevelMax = 12;
    public static final int DOMOTICZ_FAKE_ID = 99999;
    public static final String HIDDEN_CHARACTER = "$";

    public static final String UTILITIES_TYPE_THERMOSTAT = "Thermostat";
    public static final String[] ITEMS_UTILITIES = {UTILITIES_TYPE_THERMOSTAT};

    public interface Authentication {
        String USERNAME = "username";
        String PASSWORD = "password";

        interface Method {
            String AUTH_METHOD_LOGIN_FORM = "Login form";
            String AUTH_METHOD_BASIC_AUTHENTICATION = "Basic authentication";
        }
    }
    public interface Result {
        String ERROR = "ERR";
        String OK = "OK";
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
            interface Value {
                int DOORBELL = 1;
                int CONTACT = 2;
                int BLINDS = 3;
                int SMOKE_DETECTOR = 5;
                int DIMMER = 7;
                int MOTION = 8;
                int PUSH_ON_BUTTON = 9;
                int ON_OFF = 0;
                int SECURITY = 0;
            }

            interface Name {
                String DOORBELL = "Doorbell";
                String CONTACT = "Contact";
                String BLINDS = "Blinds";
                String SMOKE_DETECTOR = "";
                String DIMMER = "Dimmer";
                String MOTION = "Motion Sensor";
                String PUSH_ON_BUTTON = "";
                String ON_OFF = "On/Off";
                String SECURITY = "Security";
            }
        }
        interface Favorite {
            int ON = 208;
            int OFF = 209;
        }
        interface Utility{}
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
                int PLANS = 11;
                int PLAN_DEVICES = 12;
                int LOG = 13;
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
    *  Log tag
    */
    private static final String TAG = Domoticz.class.getSimpleName();

    private interface Url{
        interface Action {
            String ON           = "On";
            String OFF          = "Off";
            String UP           = "Up";
            String STOP         = "Stop";
            String DOWN         = "Down";
            String PLUS         = "Plus";
            String MIN          = "Min";
        }
        interface Category {
            String VERSION      = "/json.htm?type=command&param=getversion";
            String DASHBOARD    = "";
            String SCENES       = "/json.htm?type=scenes";
            String SWITCHES     = "/json.htm?type=command&param=getlightswitches";
            String TEMPERATURE  = "";
            String WEATHER      = "";
            String CAMERAS      = "";
            String DEVICES      = "/json.htm?type=devices";
            String UTILITIES    = DEVICES;
            String PLANS        = "/json.htm?type=plans";
        }
        interface Switch{
            String DIM_LEVEL    = "Set%20Level&level=";
            String GET          = "/json.htm?type=command&param=switchlight&idx=";
            String CMD          = "&switchcmd=";
            String LEVEL        = "&level=";
        }
        interface Scene{
            String GET          = "/json.htm?type=command&param=switchscene&idx=";
        }
        interface Temp{
            String GET          = "/json.htm?type=command&param=udevice&idx=";
            String VALUE        = "&nvalue=0&svalue=";
        }
        interface Favorite{
            String GET          =     "/json.htm?type=command&param=makefavorite&idx=";
            String VALUE        =    "&isfavorite=";
        }
        interface Protocol{
            String HTTP         = "http://";
            String HTTPS        = "https://";
        }
        interface Device{
            String STATUS       = "/json.htm?type=devices&rid=";
        }
        interface Sunrise{
            String GET          = "/json.htm?type=command&param=getSunRiseSet";
        }
        interface Plan{
            String GET          = "/json.htm?type=plans";
            String DEVICES      = "/json.htm?type=command&param=getplandevices&idx=";
        }
        interface Log{
            String GET_LOG      = "/json.htm?type=command&param=getlog";
            String GET_FROMLASTLOGTIME = "/json.htm?type=command&param=getlog&lastlogtime=";
        }
        interface Security{
            String GET          = "/json.htm?type=command&param=getsecstatus";
        }
    }
    private interface FavoriteAction {
        String ON =    "1";
        String OFF =   "0";
    }

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

    public void logger(String text) {
        if (debug) Log.d(TAG, text);
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
                logger(entry.getKey() + " is empty");
                result = false;
                break;
            }

        }
        logger("isConnectionDataComplete = " + result);
        return result;
    }

    public boolean isUrlValid() {

        boolean result = true;
        HashMap<String, String> stringHashMap = new HashMap<>();
        stringHashMap.put("Domoticz local URL", mSharedPrefUtil.getDomoticzLocalUrl());
        stringHashMap.put("Domoticz remote URL", mSharedPrefUtil.getDomoticzRemoteUrl());

        for (Map.Entry<String, String> entry : stringHashMap.entrySet()) {

            if (entry.getValue().toLowerCase().startsWith("http")) {
                logger(entry.getKey() + " starts with http");
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

    public List<Integer> getSupportedSwitchesValues() {

        List<Integer> switchesSupported = new ArrayList<>();
        switchesSupported.add(Device.Type.Value.ON_OFF);
        switchesSupported.add(Device.Type.Value.DIMMER);
        switchesSupported.add(Device.Type.Value.BLINDS);
        //switchesSupported.add(Switch.Type.SMOKE_DETECTOR);  // Not yet supported
        //switchesSupported.add(Switch.Type.PUSH_ON_BUTTON);    // Not yet supported

        return switchesSupported;
    }

    public List<String> getSupportedSwitchesNames() {

        List<String> switchesSupported = new ArrayList<>();
        switchesSupported.add(Device.Type.Name.ON_OFF);
        switchesSupported.add(Device.Type.Name.DIMMER);
        switchesSupported.add(Device.Type.Name.BLINDS);
        //switchesSupported.add(Device.Type.Name.SMOKE_DETECTOR);  // Not yet supported
        //switchesSupported.add(Device.Type.Name.PUSH_ON_BUTTON);    // Not yet supported

        return switchesSupported;
    }

    public void debugTextToClipboard(TextView debugText) {
        String message = debugText.getText().toString();

        ClipboardManager clipboard =
                (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Domoticz debug data", message);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(mContext, R.string.msg_copiedToClipboard, Toast.LENGTH_SHORT).show();
    }

    private String getJsonGetUrl(int jsonGetUrl) {

        String url;

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
                url = Url.Device.STATUS;
                break;

            case Json.Url.Request.PLANS:
                url = Url.Category.PLANS;
                break;

            default:
                throw new NullPointerException("getJsonGetUrl: No known JSON URL specified");
        }
        return url;
    }

    private String constructGetUrl(int jsonGetUrl) {

        String protocol, url, port, jsonUrl;
        StringBuilder buildUrl = new StringBuilder();
        SharedPrefUtil mSharedPrefUtil = new SharedPrefUtil(mContext);

        if (isUserOnLocalWifi()) {
            if (mSharedPrefUtil.isDomoticzLocalSecure()) protocol = Url.Protocol.HTTPS;
            else protocol = Url.Protocol.HTTP;

            url = mSharedPrefUtil.getDomoticzLocalUrl();
            port = mSharedPrefUtil.getDomoticzLocalPort();

        } else {
            if (mSharedPrefUtil.isDomoticzRemoteSecure()) protocol = Url.Protocol.HTTPS;
            else protocol = Url.Protocol.HTTP;

            url = mSharedPrefUtil.getDomoticzRemoteUrl();
            port = mSharedPrefUtil.getDomoticzRemotePort();

        }
        jsonUrl = getJsonGetUrl(jsonGetUrl);

        String fullString = buildUrl.append(protocol)
                .append(url).append(":")
                .append(port)
                .append(jsonUrl).toString();

        logger("Constructed url: " + fullString);

        return fullString;
    }

    public String constructSetUrl(int jsonSetUrl, int idx, int action, long value) {

        String protocol, baseUrl, url, port, jsonUrl = null, actionUrl;
        StringBuilder buildUrl = new StringBuilder();
        SharedPrefUtil mSharedPrefUtil = new SharedPrefUtil(mContext);

        if (isUserOnLocalWifi()) {

            if (mSharedPrefUtil.isDomoticzLocalSecure()) {
                protocol = Url.Protocol.HTTPS;
            } else protocol = Url.Protocol.HTTP;

            baseUrl = mSharedPrefUtil.getDomoticzLocalUrl();
            port = mSharedPrefUtil.getDomoticzLocalPort();

        } else {
            if (mSharedPrefUtil.isDomoticzRemoteSecure()) {
                protocol = Url.Protocol.HTTPS;
            } else protocol = Url.Protocol.HTTP;
            baseUrl = mSharedPrefUtil.getDomoticzRemoteUrl();
            port = mSharedPrefUtil.getDomoticzRemotePort();
        }

        switch (action) {
            case Scene.Action.ON:
                actionUrl = Url.Action.ON;
                break;

            case Scene.Action.OFF:
                actionUrl = Url.Action.OFF;
                break;

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
                actionUrl = Url.Switch.DIM_LEVEL + String.valueOf(value);
                break;

            default:
                throw new NullPointerException(
                        "Action not found in method Domoticz.constructSetUrl");
        }

        switch (jsonSetUrl) {
            case Json.Url.Set.SCENES:
                url = Url.Scene.GET;
                jsonUrl = url
                        + String.valueOf(idx)
                        + Url.Switch.CMD + actionUrl;
                break;

            case Json.Url.Set.SWITCHES:
                url = Url.Switch.GET;
                jsonUrl = url
                        + String.valueOf(idx)
                        + Url.Switch.CMD + actionUrl;
                break;

            case Json.Url.Set.TEMP:
                url = Url.Temp.GET;
                jsonUrl = url
                        + String.valueOf(idx)
                        + Url.Temp.VALUE + actionUrl;
                break;

            case Json.Url.Set.FAVORITE:
                url = Url.Favorite.GET;
                jsonUrl = url
                        + String.valueOf(idx)
                        + Url.Favorite.VALUE + actionUrl;
                break;
        }

        String fullString = buildUrl.append(protocol)
                .append(baseUrl).append(":")
                .append(port)
                .append(jsonUrl).toString();

        logger("Constructed url: " + fullString);

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
                .setMessage(mContext.getString(
                        R.string.msg_connectionSettingsIncomplete_msg1) + "\n\n" +
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

        if (credential.equals(Authentication.USERNAME)
                || credential.equals(Authentication.PASSWORD)) {

            SharedPrefUtil mSharedPrefUtil = new SharedPrefUtil(mContext);
            String username, password;

            if (isUserOnLocalWifi()) {
                logger("On local wifi");
                username = mSharedPrefUtil.getDomoticzLocalUsername();
                password = mSharedPrefUtil.getDomoticzLocalPassword();
            } else {
                logger("Not on local wifi");
                username = mSharedPrefUtil.getDomoticzRemoteUsername();
                password = mSharedPrefUtil.getDomoticzRemotePassword();
            }
            HashMap<String, String> credentials = new HashMap<>();
            credentials.put(Authentication.USERNAME, username);
            credentials.put(Authentication.PASSWORD, password);

            return credentials.get(credential);
        } else return "";
    }

    public void getVersion(VersionReceiver receiver) {
        VersionParser parser = new VersionParser(receiver);
        String url = constructGetUrl(Json.Url.Request.VERSION);
        RequestUtil.makeJsonVersionRequest(parser,
                getUserCredentials(Authentication.USERNAME),
                getUserCredentials(Authentication.PASSWORD),
                url);
    }

    public void getScenes(ScenesReceiver receiver) {
        ScenesParser parser = new ScenesParser(receiver);
        String url = constructGetUrl(Json.Url.Request.SCENES);
        RequestUtil.makeJsonGetRequest(parser,
                getUserCredentials(Authentication.USERNAME),
                getUserCredentials(Authentication.PASSWORD),
                url,
                mSharedPrefUtil.isDomoticzLocalSecure());
    }

    public void getPlans(PlansReceiver receiver) {
        PlanParser parser = new PlanParser(receiver);
        String url = constructGetUrl(Json.Url.Request.PLANS);
        RequestUtil.makeJsonGetRequest(parser,
                getUserCredentials(Authentication.USERNAME),
                getUserCredentials(Authentication.PASSWORD),
                url,
                mSharedPrefUtil.isDomoticzLocalSecure());
    }

    public void getSwitches(SwitchesReceiver switchesReceiver) {
        SwitchesParser parser = new SwitchesParser(switchesReceiver);
        String url = constructGetUrl(Json.Url.Request.SWITCHES);
        RequestUtil.makeJsonGetRequest(parser,
                getUserCredentials(Authentication.USERNAME),
                getUserCredentials(Authentication.PASSWORD),
                url,
                mSharedPrefUtil.isDomoticzLocalSecure());
    }

    public void setAction(int idx,
                          int jsonUrl,
                          int jsonAction,
                          long value,
                          setCommandReceiver receiver) {

        setCommandParser parser = new setCommandParser(receiver);
        String url = constructSetUrl(jsonUrl, idx, jsonAction, value);
        RequestUtil.makeJsonPutRequest(parser,
                getUserCredentials(Authentication.USERNAME),
                getUserCredentials(Authentication.PASSWORD),
                url);
    }

    public void getStatus(int idx, StatusReceiver receiver) {
        StatusInfoParser parser = new StatusInfoParser(receiver);
        String url = constructGetUrl(Json.Get.STATUS) + String.valueOf(idx);
        logger("for idx: " + String.valueOf(idx));

        RequestUtil.makeJsonGetRequest(parser,
                getUserCredentials(Authentication.USERNAME),
                getUserCredentials(Authentication.PASSWORD),
                url,
                mSharedPrefUtil.isDomoticzLocalSecure());
    }

    public void getUtilities(UtilitiesReceiver receiver) {
        UtilitiesParser parser = new UtilitiesParser(receiver);
        String url = constructGetUrl(Json.Url.Request.UTILITIES);
        RequestUtil.makeJsonGetRequest(parser,
                getUserCredentials(Authentication.USERNAME),
                getUserCredentials(Authentication.PASSWORD),
                url,
                mSharedPrefUtil.isDomoticzLocalSecure());
    }

    public void getDevices(DevicesReceiver receiver) {
        DevicesParser parser = new DevicesParser(receiver);
        String url = constructGetUrl(Json.Url.Request.DEVICES);
        RequestUtil.makeJsonGetRequest(parser,
                getUserCredentials(Authentication.USERNAME),
                getUserCredentials(Authentication.PASSWORD),
                url,
                mSharedPrefUtil.isDomoticzLocalSecure());
    }
}