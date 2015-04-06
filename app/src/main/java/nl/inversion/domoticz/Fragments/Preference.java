package nl.inversion.domoticz.Fragments;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;

import java.util.List;

import nl.inversion.domoticz.R;

public class Preference extends PreferenceFragment {

    List<ScanResult> results;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        WifiManager wifi = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        if (wifi.startScan()) {
            results = wifi.getScanResults();
            CharSequence[] entries = new CharSequence[results.size()];

            int i = 0;
            for (ScanResult result : results) {
                entries[i] = result.SSID;
                i++;
            }

            MultiSelectListPreference listPref = (MultiSelectListPreference)findPreference("local_server_ssid");
            listPref.setEntries(entries);
            listPref.setEntryValues(entries);

        } else {
            // no wifi ssid's nearby found!
        }


    }

}