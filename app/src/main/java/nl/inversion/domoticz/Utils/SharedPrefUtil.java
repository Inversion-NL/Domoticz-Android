package nl.inversion.domoticz.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.R;

@SuppressWarnings("unused")
public class SharedPrefUtil {

    public static final String PREF_STARTUP_SCREEN = "startup_screen";
    public static final String PREF_DEBUGGING = "debugging";
    Context mContext;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private static final String PREF_FIRST_START = "isFirstStart";
    private static final String PREF_WELCOME_SUCCESS = "welcomeSuccess";

    private static final String http = "http://";
    private static final String https = "https://";

    private static final String REMOTE_SERVER_USERNAME = "remote_server_username";
    private static final String REMOTE_SERVER_PASSWORD = "remote_server_password";
    private static final String REMOTE_SERVER_URL = "remote_server_url";
    private static final String REMOTE_SERVER_PORT = "remote_server_port";
    private static final String REMOTE_SERVER_SECURE = "remote_server_secure";
    private static final String REMOTE_SERVER_AUTHENTICATION_METHOD =
            "remote_server_authentication_method";

    private static final String LOCAL_SERVER_USES_SAME_ADDRESS = "local_server_different_address";
    private static final String LOCAL_SERVER_USERNAME = "local_server_username";
    private static final String LOCAL_SERVER_PASSWORD = "local_server_password";
    private static final String LOCAL_SERVER_URL = "local_server_url";
    private static final String LOCAL_SERVER_PORT = "local_server_port";
    private static final String LOCAL_SERVER_SECURE = "local_server_secure";
    private static final String LOCAL_SERVER_AUTHENTICATION_METHOD =
            "local_server_authentication_method";
    private static final String LOCAL_SERVER_SSID = "local_server_ssid";

    public SharedPrefUtil(Context mContext) {
        this.mContext = mContext;
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = prefs.edit();
    }

    /*
     *      Generic settings
     */
    public boolean isFirstStart() {
        return prefs.getBoolean(PREF_FIRST_START, true);
    }

    public void setFirstStart(boolean firstStart) {
        editor.putBoolean(PREF_FIRST_START, firstStart).apply();
    }

    public boolean isWelcomeWizardSuccess() {
        return prefs.getBoolean(PREF_WELCOME_SUCCESS, false);
    }

    public void setWelcomeWizardSuccess(boolean success) {
        editor.putBoolean(PREF_WELCOME_SUCCESS, success).apply();
    }

    public int getStartupScreenIndex() {

        String startupScreenSelectedValue = prefs.getString(PREF_STARTUP_SCREEN, null);
        if (startupScreenSelectedValue == null) return 0;
        else {
            String[] startupScreenValues = mContext.getResources().getStringArray(R.array.drawer_actions);
            int i = 0;

            for (String screen : startupScreenValues) {
                if (screen.equalsIgnoreCase(startupScreenSelectedValue)) {
                    break;
                }
                i++;
            }
            return i;
        }
    }

    public void setStartupScreenIndex(int position) {

        String[] startupScreenValues = mContext.getResources().getStringArray(R.array.drawer_actions);
        String startupScreenValue;

        try {
            startupScreenValue = startupScreenValues[position];
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            startupScreenValue = startupScreenValues[0];
        }

        editor.putString(PREF_STARTUP_SCREEN, startupScreenValue).apply();
    }

    public boolean isDebugEnabled() {
        return prefs.getBoolean(PREF_DEBUGGING, false);
    }

    /*
     *      Remote server settings
     */
    public String getDomoticzRemoteUsername() {
        return prefs.getString(REMOTE_SERVER_USERNAME, "");
    }

    public void setDomoticzRemoteUsername(String username) {
        editor.putString(REMOTE_SERVER_USERNAME, username).apply();
    }

    public String getDomoticzRemotePassword() {
        return prefs.getString(REMOTE_SERVER_PASSWORD, "");
    }

    public void setDomoticzRemotePassword(String password) {
        editor.putString(REMOTE_SERVER_PASSWORD, password).apply();
    }

    public String getDomoticzRemoteUrl() {
        return prefs.getString(REMOTE_SERVER_URL, "");
    }

    public void setDomoticzRemoteUrl(String url) {
        editor.putString(REMOTE_SERVER_URL, url).apply();
    }

    public String getDomoticzRemotePort() {
        return prefs.getString(REMOTE_SERVER_PORT, "");
    }

    public void setDomoticzRemotePort(String port) {
        editor.putString(REMOTE_SERVER_PORT, port).apply();
    }

    public boolean isDomoticzRemoteSecure() {
        return prefs.getBoolean(REMOTE_SERVER_SECURE, true);
    }

    public void setDomoticzRemoteSecure(boolean secure) {
        editor.putBoolean(REMOTE_SERVER_SECURE, secure).apply();
    }

    public String getDomoticzRemoteAuthenticationMethod() {
        boolean remoteServerAuthenticationMethodIsLoginForm =
                prefs.getBoolean(REMOTE_SERVER_AUTHENTICATION_METHOD, true);
        String method;

        if (remoteServerAuthenticationMethodIsLoginForm) method = Domoticz.AUTH_METHOD_LOGIN_FORM;
        else method = Domoticz.AUTH_METHOD_BASIC_AUTHENTICATION;

        return method;
    }


    /*
     *      Local server settings
     */
    public boolean serverUsesSameAddress() {
        return prefs.getBoolean(LOCAL_SERVER_USES_SAME_ADDRESS, true);
    }

    public String getDomoticzLocalUsername() {
        return prefs.getString(LOCAL_SERVER_USERNAME, "");
    }

    public void setDomoticzLocalUsername(String username) {
        editor.putString(LOCAL_SERVER_USERNAME, username).apply();
    }

    public String getDomoticzLocalPassword() {
        return prefs.getString(LOCAL_SERVER_PASSWORD, "");
    }

    public void setDomoticzLocalPassword(String password) {
        editor.putString(LOCAL_SERVER_PASSWORD, password).apply();
    }

    public String getDomoticzLocalUrl() {
        return prefs.getString(LOCAL_SERVER_URL, "");
    }

    public void setDomoticzLocalUrl(String url) {
        editor.putString(LOCAL_SERVER_URL, url).apply();
    }

    public String getDomoticzLocalPort() {
        return prefs.getString(LOCAL_SERVER_PORT, "");
    }

    public void setDomoticzLocalPort(String port) {
        editor.putString(LOCAL_SERVER_PORT, port).apply();
    }

    public boolean isDomoticzLocalSecure() {
        return prefs.getBoolean(LOCAL_SERVER_SECURE, true);
    }

    public void setDomoticzLocalSecure(boolean secure) {
        editor.putBoolean(LOCAL_SERVER_SECURE, secure).apply();
    }

    public String getDomoticzLocalAuthenticationMethod() {
        boolean localServerAuthenticationMethodIsLoginForm =
                prefs.getBoolean(LOCAL_SERVER_AUTHENTICATION_METHOD, true);
        String method;

        if (localServerAuthenticationMethodIsLoginForm) method = Domoticz.AUTH_METHOD_LOGIN_FORM;
        else method = Domoticz.AUTH_METHOD_BASIC_AUTHENTICATION;

        return method;
    }

    public void setDomoticzLocalAuthenticationMethod(String method) {

        boolean methodIsLoginForm;

        if (method.equalsIgnoreCase(Domoticz.AUTH_METHOD_LOGIN_FORM)) methodIsLoginForm = true;
        else methodIsLoginForm = false;

        editor.putBoolean(LOCAL_SERVER_AUTHENTICATION_METHOD, methodIsLoginForm).apply();
    }

    public Set<String> getLocalSsid() {
        return prefs.getStringSet(LOCAL_SERVER_SSID, null);
    }


    /**
     * Method for setting local server addresses the same as the remote server addresses
     */
    public void setLocalSameAddressAsRemote() {

        setDomoticzLocalUsername(getDomoticzRemoteUsername());
        setDomoticzLocalPassword(getDomoticzRemotePassword());
        setDomoticzLocalUrl(getDomoticzRemoteUrl());
        setDomoticzLocalPort(getDomoticzRemotePort());
        setDomoticzLocalSecure(isDomoticzRemoteSecure());
        setDomoticzLocalAuthenticationMethod(getDomoticzRemoteAuthenticationMethod());

    }
}