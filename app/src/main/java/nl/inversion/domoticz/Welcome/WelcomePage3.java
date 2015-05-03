package nl.inversion.domoticz.Welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.inversion.domoticz.R;

public class WelcomePage3 extends Fragment{

    public static final WelcomePage3 newInstance() {
        WelcomePage3 f = new WelcomePage3();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcome3, container, false);

        TextView messageTextView = (TextView)v.findViewById(R.id.textView);
        messageTextView.setText("Instellingen");

        return v;
    }
}