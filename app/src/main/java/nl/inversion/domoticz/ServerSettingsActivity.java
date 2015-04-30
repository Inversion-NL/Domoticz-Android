package nl.inversion.domoticz;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import java.lang.reflect.Array;

import nl.inversion.domoticz.Utils.SharedPrefUtil;

public class ServerSettingsActivity extends ActionBarActivity {

    SharedPrefUtil mSharedPrefs;

    EditText server_input, port_input, username_input, password_input;
    Spinner protocol_spinner, startScreen_spinner;
    Switch localServer_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_settings);

        mSharedPrefs = new SharedPrefUtil(this);

        getLayoutReferences();
        setPreferenceValues();
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

        server_input.setText(mSharedPrefs.getDomoticzRemoteUrl());
        port_input.setText(mSharedPrefs.getDomoticzRemotePort());
        username_input.setText(mSharedPrefs.getDomoticzRemoteUsername());
        password_input.setText(mSharedPrefs.getDomoticzRemotePassword());

        setProtocol_spinner();

    }

    private void setProtocol_spinner() {

        String[] protocols = getResources().getStringArray(R.array.remote_server_protocols);

        ArrayAdapter<String> protocolAdapter
                = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, protocols);
        protocol_spinner.setAdapter(protocolAdapter);
        protocol_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

}