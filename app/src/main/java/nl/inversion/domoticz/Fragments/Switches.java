package nl.inversion.domoticz.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import nl.inversion.domoticz.Containers.ExtendedStatusInfo;
import nl.inversion.domoticz.Containers.SwitchInfo;
import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.Interfaces.setCommandReceiver;
import nl.inversion.domoticz.Interfaces.StatusReceiver;
import nl.inversion.domoticz.Interfaces.SwitchesReceiver;
import nl.inversion.domoticz.R;

public class Switches extends Fragment {

    private static final String TAG = Switches.class.getSimpleName();
    private ProgressDialog progressDialog;
    private Domoticz mDomoticz;
    private int numberOfSwitches, currentSwitch;
    LinearLayout container;
    private TextView debugText;
    private boolean debug;

    public static Fragment newInstance(Context context) {
        Switches f = new Switches();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_switches, null);

        getActionBar().setTitle(R.string.title_switches);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDomoticz = new Domoticz(getActivity());
        debug = Domoticz.debug;

        container = (LinearLayout) getView().findViewById(R.id.container);

        // Initialize the progress dialog
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage(getString(R.string.msg_please_wait));
        progressDialog.setCancelable(false);

        if (debug) {
            LinearLayout debugLayout = (LinearLayout) getView().findViewById(R.id.debugLayout);
            debugLayout.setVisibility(View.VISIBLE);

            debugText = (TextView) getView().findViewById(R.id.debugText);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cleanScreen();
        getData();
    }

    /**
     * Clears the container layout and, if in debugging, the debug text
     */
    private void cleanScreen() {
        if (debug) {
            debugText.setText("");
        }
        container.removeAllViews();
    }

    /**
     * Gets the data through the switches receiver with a call back on receive or error
     */
    private void getData() {

        showProgressDialog();

        mDomoticz.getSwitches(new SwitchesReceiver() {
            @Override
            public void onReceiveSwitches(ArrayList<SwitchInfo> switches) {

                successHandling(switches.toString());

                numberOfSwitches = switches.size();
                for (SwitchInfo mSwitch : switches) {
                    getExtendedInfo(mSwitch);
                }
            }

            @Override
            public void onError(Exception error) {
                errorHandling(error);
            }
        });
    }

    /**
     * Gets the extended info with a callback onReceive which creates the row
     * @param mSwitch
     */
    private void getExtendedInfo(SwitchInfo mSwitch) {

        int idx = mSwitch.getIdx();

        mDomoticz.getStatus(idx, new StatusReceiver() {
            @Override
            public void onReceiveStatus(ExtendedStatusInfo mExtendedStatusInfo) {
                successHandling(mExtendedStatusInfo.toString());
                createRow(mExtendedStatusInfo);
            }

            @Override
            public void onError(Exception error) {
                errorHandling(error);
            }
        });

    }

    /**
     * Creates a row dynamically based on the data of the scene
     * @param mExtendedStatusInfo containing the information
     */
    private void createRow(ExtendedStatusInfo mExtendedStatusInfo) {

        int switchTypeVal = mExtendedStatusInfo.getSwitchTypeVal();
        String name = mExtendedStatusInfo.getName();

        if (!name.startsWith(Domoticz.SWITCH_HIDDEN_CHARACTER)) {

            if (debug) {
                String temp = debugText.getText().toString();
                temp = temp + "\n\n";
                temp = temp + mExtendedStatusInfo.getJsonObject().toString();
                debugText.setText(temp);
            }

            if (switchTypeVal == mDomoticz.SWITCH_TYPE_ON_OFF) {

                LayoutInflater layoutInflater =
                        (LayoutInflater) getActivity()
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View switchRow_onOff = layoutInflater.inflate(R.layout.switch_row_on_off, null);

                String lastUpdate = mExtendedStatusInfo.getLastUpdate();
                int signalLevel = mExtendedStatusInfo.getSignalLevel();
                final boolean status = mExtendedStatusInfo.getStatusBoolean();

                Switch mSwitch = (Switch) switchRow_onOff.findViewById(R.id.switch_button);
                mSwitch.setId(mExtendedStatusInfo.getIdx());
                mSwitch.setChecked(status);
                mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        handleSwitchClick(compoundButton.getId(), checked);
                    }
                });


                TextView switch_name = (TextView) switchRow_onOff.findViewById(R.id.switch_name);
                TextView switch_last_seen =
                        (TextView) switchRow_onOff.findViewById(R.id.switch_lastSeen);
                TextView switch_level =
                        (TextView) switchRow_onOff.findViewById(R.id.switch_signal_level);

                switch_name.setText(name);
                switch_last_seen.setText(lastUpdate);
                switch_level.setText(getText(R.string.signal_level) + ": " + signalLevel);

                container.addView(switchRow_onOff);

            } else if (switchTypeVal == mDomoticz.SWITCH_TYPE_BLINDS) {

                LayoutInflater layoutInflater =
                        (LayoutInflater) getActivity()
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View switchRow_blinds = layoutInflater.inflate(R.layout.switch_row_blinds, null);

                String lastUpdate = mExtendedStatusInfo.getLastUpdate();
                String status = mExtendedStatusInfo.getStatus();

                ImageButton buttonUp = (ImageButton) switchRow_blinds.findViewById(R.id.switch_button_up);
                buttonUp.setId(mExtendedStatusInfo.getIdx());
                buttonUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleBlindsClick(view.getId(), Domoticz.BLINDS_ACTION_UP);
                    }
                });

                ImageButton buttonStop = (ImageButton) switchRow_blinds.findViewById(R.id.switch_button_stop);
                buttonStop.setId(mExtendedStatusInfo.getIdx());
                buttonStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleBlindsClick(view.getId(), Domoticz.BLINDS_ACTION_STOP);
                    }
                });

                ImageButton buttonDown = (ImageButton) switchRow_blinds.findViewById(R.id.switch_button_down);
                buttonDown.setId(mExtendedStatusInfo.getIdx());
                buttonDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleBlindsClick(view.getId(), Domoticz.BLINDS_ACTION_DOWN);
                    }
                });

                TextView switch_name = (TextView) switchRow_blinds.findViewById(R.id.switch_name);
                TextView switch_last_seen =
                        (TextView) switchRow_blinds.findViewById(R.id.switch_lastSeen);
                TextView switch_status =
                        (TextView) switchRow_blinds.findViewById(R.id.switch_status);

                switch_name.setText(name);
                switch_last_seen.setText(lastUpdate);
                switch_status.setText(getText(R.string.status) + ": " + status);

                container.addView(switchRow_blinds);
            }
        }

        // Calculate if this is the last switch were working on
        // If so: hide progress dialog
        if (currentSwitch+1 == numberOfSwitches) hideProgressDialog();
        else currentSwitch++;

    }

    private void handleBlindsClick(int idx, int action) {
        Log.d(TAG, "handleBlindsClick");

        int jsonUrl = Domoticz.JSON_SET_URL_SWITCHES;
        int jsonAction = mDomoticz.JSON_ACTION_UP;

        switch (action) {
            case Domoticz.BLINDS_ACTION_UP:
                Log.d(TAG, "Set idx " + idx + " to up");
                jsonAction = mDomoticz.JSON_ACTION_UP;
                break;

            case Domoticz.BLINDS_ACTION_STOP:
                Log.d(TAG, "Set idx " + idx + " to stop");
                jsonAction = mDomoticz.JSON_ACTION_STOP;
                break;

            case Domoticz.BLINDS_ACTION_DOWN:
                Log.d(TAG, "Set idx " + idx + " to down");
                jsonAction = mDomoticz.JSON_ACTION_DOWN;
                break;
        }

        mDomoticz.setAction(idx, jsonUrl, jsonAction, 0, new setCommandReceiver() {
            @Override
            public void onReceiveResult(String result) {
                Toast.makeText(getActivity(), R.string.action_success, Toast.LENGTH_LONG).show();
                successHandling(result);
            }

            @Override
            public void onError(Exception error) {
                errorHandling(error);
            }
        });
    }

    /**
     * Handles the clicks of the dynamically created rows
     * @param idx idx code of the button (Domoticz)
     * @param checked is the button currently checked?
     */
    private void handleSwitchClick(int idx, boolean checked) {
        Log.d(TAG, "handleSwitchClick");
        Log.d(TAG, "Set idx " + idx + " to " + checked);

        int jsonAction;
        int jsonUrl = Domoticz.JSON_SET_URL_SWITCHES;

        if (checked) jsonAction = mDomoticz.JSON_ACTION_ON;
        else jsonAction = mDomoticz.JSON_ACTION_OFF;

        mDomoticz.setAction(idx, jsonUrl, jsonAction, 0, new setCommandReceiver() {
            @Override
            public void onReceiveResult(String result) {
                successHandling(result);
            }

            @Override
            public void onError(Exception error) {
                errorHandling(error);
            }
        });
    }

    /**
     * Shows the progress dialog if isn't already showing
     */
    private void showProgressDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    /**
     * Hides the progress dialog if it is showing
     */
    private void hideProgressDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    /**
     * Handles the success messages
     * @param result String result to handle
     */
    private void successHandling(String result) {

        Log.d(TAG, result);
        if (debug) {
            String temp = debugText.getText().toString();
            debugText.setText(temp + "\n\n" + result);
        }
    }

    /**
     * Handles the error messages
     * @param error Exception
     */
    private void errorHandling(Exception error) {
        hideProgressDialog();

        error.printStackTrace();

        if (debug) {
            String temp = debugText.getText().toString();
            debugText.setText(temp  + "\n\n" + error.getCause().getMessage());
        } else {
            mDomoticz.errorToast(error);
        }
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }
}