package nl.inversion.domoticz.Welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.inversion.domoticz.R;

public class WelcomePage1 extends Fragment{

    public static WelcomePage1 newInstance() {
        return new WelcomePage1();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcome1, container, false);


        return v;
    }
}