package nl.inversion.domoticz.Welcome;

import android.app.Fragment;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Set;

import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.R;
import nl.inversion.domoticz.Utils.PhoneConnectionUtil;
import nl.inversion.domoticz.Utils.SharedPrefUtil;
import nl.inversion.domoticz.app.MultiSelectionSpinner;

public class WelcomePage3 extends Fragment {

    SharedPrefUtil mSharedPrefs;

    FloatingLabelEditText remote_server_input, remote_port_input,
            remote_username_input, remote_password_input,
            local_server_input, local_password_input,
            local_username_input, local_port_input;
    Spinner remote_protocol_spinner, local_protocol_spinner, startScreen_spinner;
    Switch localServer_switch;
    int remoteProtocolSelectedPosition, localProtocolSelectedPosition, startScreenSelectedPosition;
    private View v;
    boolean lostUserVisibility = false;
    MultiSelectionSpinner spinner;

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
            if (lostUserVisibility) {
                writePreferenceValues();
            }
        } else {
            lostUserVisibility = true;
        }
    }

    private void getLayoutReferences() {

        remote_server_input = (FloatingLabelEditText) v.findViewById(R.id.remote_server_input);
        remote_port_input = (FloatingLabelEditText) v.findViewById(R.id.remote_port_input);
        remote_username_input = (FloatingLabelEditText) v.findViewById(R.id.remote_username_input);
        remote_password_input = (FloatingLabelEditText) v.findViewById(R.id.remote_password_input);
        remote_protocol_spinner = (Spinner) v.findViewById(R.id.remote_protocol_spinner);

        local_server_input = (FloatingLabelEditText) v.findViewById(R.id.local_server_input);
        local_port_input = (FloatingLabelEditText) v.findViewById(R.id.local_port_input);
        local_username_input = (FloatingLabelEditText) v.findViewById(R.id.local_username_input);
        local_password_input = (FloatingLabelEditText) v.findViewById(R.id.local_password_input);
        local_protocol_spinner = (Spinner) v.findViewById(R.id.local_protocol_spinner);
        spinner = (MultiSelectionSpinner) v.findViewById(R.id.local_wifi);

        startScreen_spinner = (Spinner) v.findViewById(R.id.startScreen_spinner);
        final LinearLayout local_server_settings = (LinearLayout)
                v.findViewById(R.id.local_server_settings);
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

        remote_username_input.setInputWidgetText(mSharedPrefs.getDomoticzRemoteUsername());
        remote_password_input.setInputWidgetText(mSharedPrefs.getDomoticzRemotePassword());
        remote_server_input.setInputWidgetText(mSharedPrefs.getDomoticzRemoteUrl());
        remote_port_input.setInputWidgetText(mSharedPrefs.getDomoticzRemotePort());

        localServer_switch.setChecked(mSharedPrefs.isLocalServerAddressDifferent());

        local_username_input.setInputWidgetText(mSharedPrefs.getDomoticzLocalUsername());
        local_password_input.setInputWidgetText(mSharedPrefs.getDomoticzLocalPassword());
        local_server_input.setInputWidgetText(mSharedPrefs.getDomoticzLocalUrl());
        local_port_input.setInputWidgetText(mSharedPrefs.getDomoticzLocalPort());

        Set<String> ssidFromPrefs = mSharedPrefs.getLocalSsid();
        ArrayList<String> ssidsListFromPrefs = new ArrayList<>();
        ArrayList<String> ssids = new ArrayList<>();

        if (ssidFromPrefs != null) {
            if (ssidFromPrefs.size() > 0) {
                for (String wifi : ssidFromPrefs) {
                    ssids.add(wifi);
                    ssidsListFromPrefs.add(wifi);
                }
            }
        }


        PhoneConnectionUtil mPhoneConnectionUtil = new PhoneConnectionUtil(getActivity());
        CharSequence[] ssidEntries = mPhoneConnectionUtil.startSsidScanAsCharSequence();

        if (ssidEntries.length < 1) {
            ssids.add(getString(R.string.msg_no_ssid_found)); // no wifi ssid nearby found!
        } else {
            for (CharSequence ssid : ssidEntries) {
                if (!ssids.contains(ssid)) ssids.add(ssid.toString());
            }
        }

        spinner.setItems(ssids);
        spinner.setSelection(ssidsListFromPrefs);

        setProtocol_spinner();
        setStartScreen_spinner();

    }

    private void setProtocol_spinner() {

        String[] protocols = getResources().getStringArray(R.array.remote_server_protocols);

        ArrayAdapter<String> protocolAdapter
                = new ArrayAdapter<>(getActivity(), R.layout.spinner_list_item, protocols);
        remote_protocol_spinner.setAdapter(protocolAdapter);
        remote_protocol_spinner.setSelection(getPrefsDomoticzRemoteSecureIndex());
        remote_protocol_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView,
                                       View view, int position, long id) {
                remoteProtocolSelectedPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        local_protocol_spinner.setAdapter(protocolAdapter);
        local_protocol_spinner.setSelection(getPrefsDomoticzLocalSecureIndex());
        local_protocol_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView,
                                       View view, int position, long id) {
                localProtocolSelectedPosition = position;
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
            public void onItemSelected(AdapterView<?> adapterView,
                                       View view, int position, long id) {
                startScreenSelectedPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void writePreferenceValues() {

        mSharedPrefs.setDomoticzRemoteUsername(
                remote_username_input.getInputWidgetText().toString());
        mSharedPrefs.setDomoticzRemotePassword(
                remote_password_input.getInputWidgetText().toString());
        mSharedPrefs.setDomoticzRemoteUrl(
                remote_server_input.getInputWidgetText().toString());
        mSharedPrefs.setDomoticzRemotePort(
                remote_port_input.getInputWidgetText().toString());
        mSharedPrefs.setDomoticzRemoteSecure(
                getSpinnerDomoticzRemoteSecureBoolean());

        mSharedPrefs.setStartupScreenIndex(startScreenSelectedPosition);

        Switch useSameAddress = (Switch) v.findViewById(R.id.localServer_switch);
        if (!useSameAddress.isChecked()) {
            mSharedPrefs.setLocalSameAddressAsRemote();
            mSharedPrefs.setLocalServerUsesSameAddress(false);
        } else {
            mSharedPrefs.setDomoticzLocalUsername(
                    local_username_input.getInputWidgetText().toString());
            mSharedPrefs.setDomoticzLocalPassword(
                    local_password_input.getInputWidgetText().toString());
            mSharedPrefs.setDomoticzLocalUrl(
                    local_server_input.getInputWidgetText().toString());
            mSharedPrefs.setDomoticzLocalPort(
                    local_port_input.getInputWidgetText().toString());
            mSharedPrefs.setDomoticzLocalSecure(
                    getSpinnerDomoticzLocalSecureBoolean());
            mSharedPrefs.setLocalServerUsesSameAddress(true);
        }

        mSharedPrefs.setLocalSsid(spinner.getSelectedStrings());

    }

    private boolean getSpinnerDomoticzRemoteSecureBoolean() {
        String[] protocols = getResources().getStringArray(R.array.remote_server_protocols);
        return protocols[remoteProtocolSelectedPosition].equalsIgnoreCase(Domoticz.PROTOCOL_SECURE);
    }

    private boolean getSpinnerDomoticzLocalSecureBoolean() {
        String[] protocols = getResources().getStringArray(R.array.remote_server_protocols);
        return protocols[localProtocolSelectedPosition].equalsIgnoreCase(Domoticz.PROTOCOL_SECURE);
    }

    private int getPrefsDomoticzRemoteSecureIndex() {

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

    private int getPrefsDomoticzLocalSecureIndex() {

        boolean isSecure = mSharedPrefs.isDomoticzLocalSecure();
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