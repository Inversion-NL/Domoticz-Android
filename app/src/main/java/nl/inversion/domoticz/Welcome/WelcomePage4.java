package nl.inversion.domoticz.Welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.inversion.domoticz.R;

public class WelcomePage4 extends Fragment{

    public static final WelcomePage4 newInstance() {
        WelcomePage4 f = new WelcomePage4();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcome4, container, false);

        TextView messageTextView = (TextView)v.findViewById(R.id.textView);
        messageTextView.setText("Checking your connection settings. \n\nPlease wait...");

        return v;
    }
}