package nl.inversion.domoticz.Welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.R;
import nl.inversion.domoticz.Utils.SharedPrefUtil;

public class WelcomePage3 extends Fragment {

    SharedPrefUtil mSharedPrefs;

    EditText server_input, port_input, username_input, password_input;
    Spinner protocol_spinner, startScreen_spinner;
    Switch localServer_switch;
    int protocolSelectedPosition, startScreenSelectedPosition;
    private View v;

    public static final WelcomePage3 newInstance() {
        WelcomePage3 f = new WelcomePage3();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_welcome3, container, false);

        mSharedPrefs = new SharedPrefUtil(getActivity());

        getLayoutReferences();
        setPreferenceValues();

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        writePreferenceValues();
    }

    private void getLayoutReferences() {

        server_input = (EditText) v.findViewById(R.id.server_input);
        port_input = (EditText) v.findViewById(R.id.port_input);
        username_input  = (EditText) v.findViewById(R.id.username_input);
        password_input  = (EditText) v.findViewById(R.id.password_input);
        protocol_spinner = (Spinner) v.findViewById(R.id.protocol_spinner);
        startScreen_spinner = (Spinner) v.findViewById(R.id.startScreen_spinner);
        final LinearLayout local_server_settings = (LinearLayout) v.findViewById(R.id.local_server_settings);
        localServer_switch = (Switch) v.findViewById(R.id.localServer_switch);
        localServer_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) local_server_settings.setVisibility(View.VISIBLE);
                else local_server_settings.setVisibility(View.GONE);
            }
        });

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
                = new ArrayAdapter<>(getActivity(), R.layout.spinner_list_item, protocols);
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
                = new ArrayAdapter<>(getActivity(), R.layout.spinner_list_item, startScreens);
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

        Switch useSameAddress = (Switch) v.findViewById(R.id.localServer_switch);
        if (!useSameAddress.isChecked()) {
            mSharedPrefs.setLocalSameAddressAsRemote();
        }

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