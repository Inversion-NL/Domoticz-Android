package nl.inversion.domoticz.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.inversion.domoticz.R;

public class Switches extends Fragment {

    private String password = "Vak93zv";
    private String username = "Admin";

    private String http = "http://";
    private String url = "domotica.inversion.nl";
    private String port = "8080";
    private String lightSwitches = "/json.htm?type=command&param=getlightswitches";

    public static Fragment newInstance(Context context) {
        Switches f = new Switches();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_switches, null);
        return root;
    }
}