package nl.inversion.domoticz.Welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.marvinlabs.widget.floatinglabel.edittext.FloatingLabelEditText;

import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.R;
import nl.inversion.domoticz.Utils.SharedPrefUtil;

public class WelcomePage3 extends Fragment {

    SharedPrefUtil mSharedPrefs;

    FloatingLabelEditText server_input, port_input, username_input, password_input;
    Spinner protocol_spinner, startScreen_spinner;
    Switch localServer_switch;
    int protocolSelectedPosition, startScreenSelectedPosition;
    private View v;
    boolean hasBeenVisibleToUser = false;

    public static WelcomePage3 newInstance() {
        return new WelcomePage3();
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (!isVisibleToUser) {
            if (hasBeenVisibleToUser) {
                writePreferenceValues();
            }
        } else {
            hasBeenVisibleToUser = true;
        }
    }

    private void getLayoutReferences() {

        server_input = (FloatingLabelEditText) v.findViewById(R.id.server_input);
        port_input = (FloatingLabelEditText) v.findViewById(R.id.port_input);
        username_input  = (FloatingLabelEditText) v.findViewById(R.id.username_input);
        password_input  = (FloatingLabelEditText) v.findViewById(R.id.password_input);
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

        username_input.setInputWidgetText(mSharedPrefs.getDomoticzRemoteUsername());
        password_input.setInputWidgetText(mSharedPrefs.getDomoticzRemotePassword());
        server_input.setInputWidgetText(mSharedPrefs.getDomoticzRemoteUrl());
        port_input.setInputWidgetText(mSharedPrefs.getDomoticzRemotePort());

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

        mSharedPrefs.setDomoticzRemoteUsername(username_input.getInputWidgetText().toString());
        mSharedPrefs.setDomoticzRemotePassword(password_input.getInputWidgetText().toString());
        mSharedPrefs.setDomoticzRemoteUrl(server_input.getInputWidgetText().toString());
        mSharedPrefs.setDomoticzRemotePort(port_input.getInputWidgetText().toString());
        mSharedPrefs.setDomoticzRemoteSecure(getSpinnerDomoticzRemoteSecureBoolean());
        mSharedPrefs.setStartupScreenIndex(startScreenSelectedPosition);

        Switch useSameAddress = (Switch) v.findViewById(R.id.localServer_switch);
        if (!useSameAddress.isChecked()) {
            mSharedPrefs.setLocalSameAddressAsRemote();
        }

    }

    private boolean getSpinnerDomoticzRemoteSecureBoolean() {

        String[] protocols = getResources().getStringArray(R.array.remote_server_protocols);

        return protocols[protocolSelectedPosition].equalsIgnoreCase(Domoticz.PROTOCOL_SECURE);
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