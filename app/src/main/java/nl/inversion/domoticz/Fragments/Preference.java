package nl.inversion.domoticz.Fragments;

import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;

import nl.inversion.domoticz.R;
import nl.inversion.domoticz.Utils.WifiUtil;

public class Preference extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        WifiUtil mWifiUtil = new WifiUtil(getActivity());
        MultiSelectListPreference listPref =
                (MultiSelectListPreference)findPreference("local_server_ssid");

        CharSequence[] entries = mWifiUtil.startSsidScanAsCharSequence();
        if (entries.length < 1) {
            // no wifi ssid's nearby found!
            entries[0] = getString(R.string.msg_no_ssid_found);
        }
        listPref.setEntries(entries);
        listPref.setEntryValues(entries);
    }

}