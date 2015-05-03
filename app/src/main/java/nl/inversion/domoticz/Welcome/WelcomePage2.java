package nl.inversion.domoticz.Welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.inversion.domoticz.R;

public class WelcomePage2 extends Fragment{

    public static final WelcomePage2 newInstance() {
        WelcomePage2 f = new WelcomePage2();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcome2, container, false);

        return v;
    }
}