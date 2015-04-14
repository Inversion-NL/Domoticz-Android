package nl.inversion.domoticz.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import nl.inversion.domoticz.Containers.ExtendedStatusInfo;
import nl.inversion.domoticz.Containers.SwitchInfo;
import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.Interfaces.StatusReceiver;
import nl.inversion.domoticz.Interfaces.SwitchesReceiver;
import nl.inversion.domoticz.R;
import nl.inversion.domoticz.SettingsActivity;

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

        // Checks if connection data (username, password, url and port) have data
        if (mDomoticz.isConnectionDataComplete()) getData();
        else showConnectionSettingsMissingDialog();
    }

    private void getData() {

        showProgressDialog();

        mDomoticz.getSwitches(new SwitchesReceiver() {
            @Override
            public void onReceiveSwitches(ArrayList<SwitchInfo> switches) {

                for (SwitchInfo mSwitch : switches) {
                    createRow(mSwitch);
                }

                hideProgressDialog();
            }

            @Override
            public void onError(Exception error) {
                error.printStackTrace();
                errorToast(error);

                hideProgressDialog();
            }
        });
    }

    private void createRow(SwitchInfo mSwitch) {

        int idx = mSwitch.getIdx();
        boolean isDimmer = mSwitch.getIsDimmerBoolean();
        String name = mSwitch.getName();
        String subType = mSwitch.getSubType();
        String type = mSwitch.getType();

        mDomoticz.getStatus(idx, new StatusReceiver() {
            @Override
            public void onReceiveStatus(ArrayList<ExtendedStatusInfo> extendedStatusInfo) {

            }

            @Override
            public void onError(Exception error) {
                error.printStackTrace();
                errorToast(error);
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

    private void errorToast(Exception error) {
        String cause;
        if (error.getCause() != null) {
            cause = error.getCause().getMessage();
            Toast.makeText(getActivity(), cause, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void showConnectionSettingsMissingDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.msg_emptyCredentials_title)
                .setMessage(getString(R.string.msg_emptyCredentials_msg1) + "\n\n" +
                        getString(R.string.msg_emptyCredentials_msg2))
                .setPositiveButton(R.string.settingsActivity_name, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getActivity(), SettingsActivity.class));
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setIcon(android.R.drawable.ic_dialog_alert);

        AlertDialog emptyCredentialsAlertDialog = alertDialogBuilder.create();
        emptyCredentialsAlertDialog.show();
    }

}