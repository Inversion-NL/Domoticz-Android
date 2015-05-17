package nl.inversion.domoticz.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import nl.inversion.domoticz.Adapters.SwitchesAdapter;
import nl.inversion.domoticz.Containers.ExtendedStatusInfo;
import nl.inversion.domoticz.Containers.SwitchInfo;
import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.Interfaces.setCommandReceiver;
import nl.inversion.domoticz.Interfaces.switchesClickListener;
import nl.inversion.domoticz.Interfaces.StatusReceiver;
import nl.inversion.domoticz.Interfaces.SwitchesReceiver;
import nl.inversion.domoticz.R;
import nl.inversion.domoticz.Utils.switchInfoDialog;

public class Temperature extends Fragment implements switchesClickListener {

    private static final String TAG = Temperature.class.getSimpleName();
    private ProgressDialog progressDialog;
    private Domoticz mDomoticz;
    private TextView debugText;
    private boolean debug;
    private SwitchesAdapter adapter;
    private Context mActivity;
    private ListView switchesListView;
    private int currentSwitch = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_temperature, null);

        getActionBar().setTitle(R.string.title_temperature);

        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDomoticz = new Domoticz(getActivity());
        debug = Domoticz.debug;

        // Initialize the progress dialog
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage(getString(R.string.msg_please_wait));
        progressDialog.setCancelable(false);

        if (debug) {
            LinearLayout debugLayout = (LinearLayout) getView().findViewById(R.id.debugLayout);
            debugLayout.setVisibility(View.VISIBLE);

            debugText = (TextView) getView().findViewById(R.id.debugText);
            debugText.setMovementMethod(new ScrollingMovementMethod());
            debugText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(mActivity, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
        getData();
    }

    private void getData() {

        showProgressDialog();

        mDomoticz.getSwitches(new SwitchesReceiver() {
            @Override
            public void onReceiveSwitches(ArrayList<SwitchInfo> switches) {
                processSwitches(switches);
            }

            @Override
            public void onError(Exception error) {
                errorHandling(error);
            }
        });
    }

    private void processSwitches(ArrayList<SwitchInfo> switchInfos) {

        final ArrayList<ExtendedStatusInfo> extendedStatusSwitches = new ArrayList<>();
        final int totalNumberOfSwitches = switchInfos.size();

        for (SwitchInfo switchInfo : switchInfos) {
            successHandling(switchInfos.toString());
            int idx = switchInfo.getIdx();

            mDomoticz.getStatus(idx, new StatusReceiver() {
                @Override
                public void onReceiveStatus(ExtendedStatusInfo extendedStatusInfo) {
                    extendedStatusSwitches.add(extendedStatusInfo);     // Add to array
                    if (currentSwitch == totalNumberOfSwitches) {
                        createListView(extendedStatusSwitches);         // All extended info is in
                    }
                    else currentSwitch++;                               // Not there yet
                }

                @Override
                public void onError(Exception error) {
                    errorHandling(error);
                }
            });
        }
    }

    private void createListView(ArrayList<ExtendedStatusInfo> switches) {

        final ArrayList<ExtendedStatusInfo> supportedSwitches = new ArrayList<>();

        for (ExtendedStatusInfo mExtendedStatusInfo : switches) {
            String name = mExtendedStatusInfo.getName();

            if (!name.startsWith(Domoticz.SWITCH_HIDDEN_CHARACTER) && mDomoticz.getSupportedSwitches().contains(mExtendedStatusInfo.getSwitchTypeVal())) {
                supportedSwitches.add(mExtendedStatusInfo);
            }
        }

        final switchesClickListener listener = this;

        adapter = new SwitchesAdapter(mActivity, supportedSwitches, listener);
        switchesListView = (ListView) getView().findViewById(R.id.temperatureListView);
        switchesListView.setAdapter(adapter);
        switchesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long id) {
                ExtendedStatusInfo mSwitch = supportedSwitches.get(index);

                FragmentManager fragmentManager = getFragmentManager();
                switchInfoDialog infoDialog = new switchInfoDialog();
                infoDialog.setSwitchName(mSwitch.getName());
                infoDialog.setIdx(String.valueOf(mSwitch.getIdx()));
                infoDialog.setLastUpdate(mSwitch.getLastUpdate());
                infoDialog.setSignalLevel(String.valueOf(mSwitch.getSignalLevel()));
                infoDialog.setBatteryLevel(String.valueOf(mSwitch.getBatteryLevel()));
                infoDialog.show(fragmentManager, "tag");
                return false;
            }
        });

        hideProgressDialog();
    }

    @Override
    public void onSwitchClick(int idx, boolean checked) {
        Log.d(TAG, "handleSwitchClick");
        Log.d(TAG, "Set idx " + idx + " to " + checked);

        int jsonAction;
        int jsonUrl = Domoticz.JSON_SET_URL_SWITCHES;

        if (checked) jsonAction = Domoticz.JSON_ACTION_ON;
        else jsonAction = Domoticz.JSON_ACTION_OFF;

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

    @Override
    public void onBlindClick(int idx, int action) {
        Log.d(TAG, "handleBlindsClick");

        int jsonUrl = Domoticz.JSON_SET_URL_SWITCHES;
        int jsonAction = Domoticz.JSON_ACTION_UP;

        switch (action) {
            case Domoticz.BLINDS_ACTION_UP:
                Log.d(TAG, "Set idx " + idx + " to up");
                jsonAction = Domoticz.JSON_ACTION_UP;
                break;

            case Domoticz.BLINDS_ACTION_STOP:
                Log.d(TAG, "Set idx " + idx + " to stop");
                jsonAction = Domoticz.JSON_ACTION_STOP;
                break;

            case Domoticz.BLINDS_ACTION_DOWN:
                Log.d(TAG, "Set idx " + idx + " to down");
                jsonAction = Domoticz.JSON_ACTION_DOWN;
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

        Log.d(TAG, "Result: " + result);
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
            debugText.setText(temp + mDomoticz.getErrorMessage(error));
        } else {
            mDomoticz.errorToast(error);
        }
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }
}