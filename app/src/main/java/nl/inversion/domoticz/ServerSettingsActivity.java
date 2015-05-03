package nl.inversion.domoticz;

import android.app.AlertDialog;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.Utils.SharedPrefUtil;

public class ServerSettingsActivity extends ActionBarActivity {

    SharedPrefUtil mSharedPrefs;

    EditText server_input, port_input, username_input, password_input;
    Spinner protocol_spinner, startScreen_spinner;
    Switch localServer_switch;
    int protocolSelectedPosition, startScreenSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_settings);

        mSharedPrefs = new SharedPrefUtil(this);

        getLayoutReferences();
        setPreferenceValues();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (onBackPressedCheck()) NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {

        shutdownCheck();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedCheck()) super.onBackPressed();
    }

    private boolean onBackPressedCheck() {
        shutdownCheck();
        Domoticz mDomoticz = new Domoticz(this);

        if (!mDomoticz.isConnectionDataComplete()) showConnectionDataWarning();
        else if (mSharedPrefs.getDomoticzRemoteUrl().startsWith("http")) showUrlMalformedDialog();
        else return true;

        return false;
    }

    private void shutdownCheck(){

        writePreferenceValues();

        Switch useSameAddress = (Switch) findViewById(R.id.localServer_switch);
        if (!useSameAddress.isChecked()) {
            mSharedPrefs.setLocalSameAddressAsRemote();
        }
    }

    /**
     * Shows a dialog to warn the user for incorrect server address settings
     */
    private void showUrlMalformedDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle(R.string.msg_connectionUrlMalformed_title)
                .setCancelable(true)
                .setMessage(getString(R.string.msg_connectionUrlMalformed_msg))
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog emptyCredentialsAlertDialog = alertDialogBuilder.create();
        emptyCredentialsAlertDialog.show();
    }

    /**
     * Shows a dialog to warn the user for missing connection settings
     */
    private void showConnectionDataWarning() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle(R.string.msg_connectionSettingsIncomplete_title)
                .setCancelable(true)
                .setMessage(getString(R.string.msg_connectionSettingsIncomplete_msg1) + "\n\n" +
                            getString(R.string.msg_connectionSettingsIncomplete_msg2))
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog emptyCredentialsAlertDialog = alertDialogBuilder.create();
        emptyCredentialsAlertDialog.show();
    }

    private void getLayoutReferences() {

        server_input = (EditText) findViewById(R.id.server_input);
        port_input = (EditText) findViewById(R.id.port_input);
        username_input  = (EditText) findViewById(R.id.username_input);
        password_input  = (EditText) findViewById(R.id.password_input);
        protocol_spinner = (Spinner) findViewById(R.id.protocol_spinner);
        startScreen_spinner = (Spinner) findViewById(R.id.startScreen_spinner);
        localServer_switch = (Switch) findViewById(R.id.localServer_switch);

    }

    private void setPreferenceValues() {

        username_input.setText(mSharedPrefs.getDomoticzRemoteUsername());
        password_input.setText(mSharedPrefs.getDomoticzRemotePassword());
        server_input.setText(mSharedPrefs.getDomoticzRemoteUrl());
        port_input.setText(mSharedPrefs.getDomoticzRemotePort());

        setProtocol_spinner();
        setStartScreen_spinner();

    }

    private void setProtocol_spinner() {

        String[] protocols = getResources().getStringArray(R.array.remote_server_protocols);

        ArrayAdapter<String> protocolAdapter
                = new ArrayAdapter<>(this, R.layout.spinner_list_item, protocols);
        protocol_spinner.setAdapter(protocolAdapter);
        protocol_spinner.setSelection(getPrefsDomoticzSecureIndex());
        protocol_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                protocolSelectedPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setStartScreen_spinner() {

        String[] startScreens = getResources().getStringArray(R.array.drawer_actions);

        ArrayAdapter<String> startScreenAdapter
                = new ArrayAdapter<>(this, R.layout.spinner_list_item, startScreens);
        startScreen_spinner.setAdapter(startScreenAdapter);
        startScreen_spinner.setSelection(mSharedPrefs.getStartupScreenIndex());
        startScreen_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                startScreenSelectedPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void writePreferenceValues() {

        mSharedPrefs.setDomoticzRemoteUsername(username_input.getText().toString());
        mSharedPrefs.setDomoticzRemotePassword(password_input.getText().toString());
        mSharedPrefs.setDomoticzRemoteUrl(server_input.getText().toString());
        mSharedPrefs.setDomoticzRemotePort(port_input.getText().toString());
        mSharedPrefs.setDomoticzRemoteSecure(getSpinnerDomoticzRemoteSecureBoolean());
        mSharedPrefs.setStartupScreenIndex(startScreenSelectedPosition);

    }

    private boolean getSpinnerDomoticzRemoteSecureBoolean() {

        String[] protocols = getResources().getStringArray(R.array.remote_server_protocols);

        if (protocols[protocolSelectedPosition].equalsIgnoreCase(Domoticz.PROTOCOL_SECURE)) return true;
        else return false;
    }

    private int getPrefsDomoticzSecureIndex() {

        boolean isSecure = mSharedPrefs.isDomoticzRemoteSecure();
        String[] protocols = getResources().getStringArray(R.array.remote_server_protocols);
        int i = 0;
        String protocolString;

        if (isSecure) protocolString = Domoticz.PROTOCOL_SECURE;
        else protocolString = Domoticz.PROTOCOL_INSECURE;

        for (String protocol : protocols) {
            if (protocol.equalsIgnoreCase(protocolString)) return i;
            i++;
        }
        return i;
    }

}