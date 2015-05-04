package nl.inversion.domoticz.Welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.Interfaces.VersionReceiver;
import nl.inversion.domoticz.R;

public class WelcomePage4 extends Fragment {
    LinearLayout please_wait_layout;
    private TextView result;
    private LinearLayout result_layout;

    public static final WelcomePage4 newInstance() {
        WelcomePage4 f = new WelcomePage4();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcome4, container, false);

        please_wait_layout = (LinearLayout) v.findViewById(R.id.layout_please_wait);
        result_layout = (LinearLayout) v.findViewById(R.id.layout_result);
        result = (TextView) v.findViewById(R.id.result);

        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            resetLayout();
            checkConnectionData();
        }
    }

    private void checkConnectionData() {
        Domoticz mDomoticz = new Domoticz(getActivity());

        if (!mDomoticz.isConnectionDataComplete()) {
            setResultText(getString(R.string.welcome_msg_connectionDataIncomplete) + "\n\n"
                    + getString(R.string.welcome_msg_correctOnPreviousPage));
        } else if (!mDomoticz.isUrlValid()) {
            setResultText(getString(R.string.welcome_msg_connectionDataInvalid) + "\n\n"
                    + getString(R.string.welcome_msg_correctOnPreviousPage));
        } else {

            mDomoticz.getVersion(new VersionReceiver() {
                @Override
                public void onReceiveVersion(String version) {
                    setResultText("Version of the server: " + version);
                }

                @Override
                public void onError(Exception error) {
                    setResultText(error.getMessage());
                }
            });
        }
    }

    private void setResultText(String text) {
        please_wait_layout.setVisibility(View.GONE);
        result_layout.setVisibility(View.VISIBLE);
        result.setText(text);
    }

    private void resetLayout() {
        please_wait_layout.setVisibility(View.VISIBLE);
        result_layout.setVisibility(View.GONE);
        result.setText("");
    }
}