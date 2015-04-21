package nl.inversion.domoticz.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import nl.inversion.domoticz.Containers.ExtendedStatusInfo;
import nl.inversion.domoticz.Containers.SwitchInfo;
import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.Interfaces.PutCommandReceiver;
import nl.inversion.domoticz.Interfaces.StatusReceiver;
import nl.inversion.domoticz.Interfaces.SwitchesReceiver;
import nl.inversion.domoticz.R;

public class Switches extends Fragment implements View.OnClickListener {

    private static final String TAG = Scenes.class.getSimpleName();
    private ProgressDialog progressDialog;
    private Domoticz mDomoticz;
    TextView statusText;

    public static Fragment newInstance(Context context) {
        Switches f = new Switches();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_switches, null);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage(getString(R.string.msg_please_wait));
        progressDialog.setCancelable(false);

        statusText = (TextView) getView().findViewById(R.id.statusText);

        mDomoticz = new Domoticz(getActivity());
        getData();
    }

    private void getData() {

        showProgressDialog();

        mDomoticz.getSwitches(new SwitchesReceiver() {
            @Override
            public void onReceiveSwitches(ArrayList<SwitchInfo> switches) {

                for (SwitchInfo mSwitch : switches) {
                    getExtendedInfo(mSwitch);
                }

                hideProgressDialog();
            }

            @Override
            public void onError(Exception error) {
                error.printStackTrace();
                mDomoticz.errorToast(error);

                hideProgressDialog();
            }
        });
    }

    private void getExtendedInfo(SwitchInfo mSwitch) {

        int idx = mSwitch.getIdx();

        mDomoticz.getStatus(idx, new StatusReceiver() {
            @Override
            public void onReceiveStatus(ExtendedStatusInfo mExtendedStatusInfo) {
                createRow(mExtendedStatusInfo);
            }

            @Override
            public void onError(Exception error) {
                error.printStackTrace();
                mDomoticz.errorToast(error);
            }
        });

    }

    private void createRow(ExtendedStatusInfo mExtendedStatusInfo) {

        int switchTypeVal = mExtendedStatusInfo.getSwitchTypeVal();

        String temp = statusText.getText().toString();
        temp = temp + "\n\n";
        temp = temp + mExtendedStatusInfo.getJsonObject().toString();
        statusText.setText(temp);

        if (switchTypeVal == mDomoticz.SWITCH_TYPE_ON_OFF) {

            LinearLayout container = (LinearLayout) getView().findViewById(R.id.container);

            LayoutInflater layoutInflater =
                    (LayoutInflater) getActivity()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View switchRow_onOff = layoutInflater.inflate(R.layout.switch_row_on_off, null);

            String lastUpdate = mExtendedStatusInfo.getLastUpdate();
            String name = mExtendedStatusInfo.getName();
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
            switch_level.setText(getText(R.string.signal_level)  + ": " + signalLevel);

            container.addView(switchRow_onOff);

        } else if (switchTypeVal == mDomoticz.SWITCH_TYPE_BLINDS) {

            LinearLayout container = (LinearLayout) getView().findViewById(R.id.container);

            LayoutInflater layoutInflater =
                    (LayoutInflater) getActivity()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View switchRow_blinds = layoutInflater.inflate(R.layout.switch_row_blinds, null);

            String lastUpdate = mExtendedStatusInfo.getLastUpdate();
            String name = mExtendedStatusInfo.getName();
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
            switch_status.setText(getText(R.string.status)  + ": " + status);

            container.addView(switchRow_blinds);
        }
    }

    private void handleBlindsClick(int idx, int action) {
        Log.d(TAG, "handleBlindsClick");

        switch (action) {
            case Domoticz.BLINDS_ACTION_UP:
                Log.d(TAG, "Set idx " + idx + " to up");
                break;

            case Domoticz.BLINDS_ACTION_STOP:
                Log.d(TAG, "Set idx " + idx + " to stop");
                break;

            case Domoticz.BLINDS_ACTION_DOWN:
                Log.d(TAG, "Set idx " + idx + " to down");
                break;
        }
    }

    private void handleSwitchClick(int idx, boolean checked) {
        Log.d(TAG, "handleSwitchClick");
        Log.d(TAG, "Set idx " + idx + " to " + checked);

        int jsonAction;
        int jsonUrl = Domoticz.JSON_SET_URL_SWITCHES;

        if (checked) jsonAction = mDomoticz.JSON_ACTION_ON;
        else jsonAction = mDomoticz.JSON_ACTION_OFF;

        mDomoticz.setAction(idx, jsonUrl, jsonAction, new PutCommandReceiver() {
            @Override
            public void onReceiveResult(String result) {
                hideProgressDialog();
                Log.d(TAG, result);
            }

            @Override
            public void onError(Exception error) {
                hideProgressDialog();
                mDomoticz.errorToast(error);
            }
        });
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick");
        Log.d(TAG, "button ID: " + String.valueOf(view.getId()));

        int idx = view.getId();
    }

    private void showProgressDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }


}