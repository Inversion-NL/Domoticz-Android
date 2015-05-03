package nl.inversion.domoticz.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.inversion.domoticz.R;

public class Dashboard extends Fragment {

    public static Fragment newInstance(Context context) {
        Dashboard f = new Dashboard();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_dashboard, null);
        return root;
    }
}