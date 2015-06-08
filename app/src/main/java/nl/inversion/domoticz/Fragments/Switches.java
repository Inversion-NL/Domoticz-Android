package nl.inversion.domoticz.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import nl.inversion.domoticz.Adapters.SwitchesAdapter;
import nl.inversion.domoticz.Containers.ExtendedStatusInfo;
import nl.inversion.domoticz.Containers.SwitchInfo;
import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.Interfaces.DomoticzFragmentListener;
import nl.inversion.domoticz.Interfaces.StatusReceiver;
import nl.inversion.domoticz.Interfaces.SwitchesReceiver;
import nl.inversion.domoticz.Interfaces.setCommandReceiver;
import nl.inversion.domoticz.Interfaces.switchesClickListener;
import nl.inversion.domoticz.R;
import nl.inversion.domoticz.UI.switchInfoDialog;
import nl.inversion.domoticz.app.DomoticzFragment;

public class Switches extends DomoticzFragment implements DomoticzFragmentListener,
        switchesClickListener,
        switchInfoDialog.InfoDialogSwitchChangeListener {

    private static final String TAG = Temperature.class.getSimpleName();
    private final ArrayList<ExtendedStatusInfo> supportedSwitches = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Domoticz mDomoticz;
    private Context mActivity;
    private int currentSwitch = 1;
    private boolean infoDialogIsFavoriteSwitch;
    private boolean infoDialogIsFavoriteSwitchIsChanged = false;
    private SwitchesAdapter adapter;
    private ListView switchesListView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        getActionBar().setTitle(R.string.title_switches);
    }

    @Override
    public void onConnectionOk() {
        showProgressDialog();

        mDomoticz = new Domoticz(mActivity);
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
            successHandling(switchInfo.toString(), false);
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

    // add dynamic list view
    // https://github.com/nhaarman/ListViewAnimations
    private void createListView(ArrayList<ExtendedStatusInfo> switches) {

        final List<Integer> appSupportedSwitchesValues = mDomoticz.getSupportedSwitchesValues();
        final List<String> appSupportedSwitchesNames = mDomoticz.getSupportedSwitchesNames();

        for (ExtendedStatusInfo mExtendedStatusInfo : switches) {
            String name = mExtendedStatusInfo.getName();
            int switchTypeVal = mExtendedStatusInfo.getSwitchTypeVal();
            String switchType = mExtendedStatusInfo.getSwitchType();

            if (!name.startsWith(Domoticz.HIDDEN_CHARACTER) &&
                    appSupportedSwitchesValues.contains(switchTypeVal) &&
                    appSupportedSwitchesNames.contains(switchType)) {
                supportedSwitches.add(mExtendedStatusInfo);
            }
        }

        final switchesClickListener listener = this;

        adapter = new SwitchesAdapter(mActivity, supportedSwitches, listener);
        switchesListView = (ListView) getView().findViewById(R.id.listView);
        switchesListView.setAdapter(adapter);
        switchesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view,
                                           int index, long id) {
                showInfoDialog(supportedSwitches.get(index));
                return true;
            }
        });

        hideProgressDialog();
    }

    private void showInfoDialog(final ExtendedStatusInfo mSwitch) {

        switchInfoDialog infoDialog = new switchInfoDialog(
                getActivity(),
                mSwitch,
                R.layout.dialog_switch_info, this);
        infoDialog.setIdx(String.valueOf(mSwitch.getIdx()));
        infoDialog.setLastUpdate(mSwitch.getLastUpdate());
        infoDialog.setSignalLevel(String.valueOf(mSwitch.getSignalLevel()));
        infoDialog.setBatteryLevel(String.valueOf(mSwitch.getBatteryLevel()));
        infoDialog.setIsFavorite(mSwitch.getFavoriteBoolean());
        infoDialog.show();
        infoDialog.onDismissListener(new switchInfoDialog.InfoDialogDismissListener() {
            @Override
            public void onDismiss() {
                changeFavorite(mSwitch.getIdx(), infoDialogIsFavoriteSwitch);
            }
        });
    }

    private void changeFavorite(int idx, boolean infoDialogIsFavoriteSwitch) {
        Log.d(TAG, "changeFavorite");
        Log.d(TAG, "Set idx " + idx + " favorite to " + infoDialogIsFavoriteSwitchIsChanged);

        if (infoDialogIsFavoriteSwitchIsChanged) {
            int jsonAction;
            int jsonUrl = Domoticz.Json.Url.Set.FAVORITE;

            if (infoDialogIsFavoriteSwitch) jsonAction = Domoticz.Device.Favorite.ON;
            else jsonAction = Domoticz.Device.Favorite.OFF;

            mDomoticz.setAction(idx, jsonUrl, jsonAction, 0, new setCommandReceiver() {
                @Override
                public void onReceiveResult(String result) {
                    successHandling(result, false);
                }

                @Override
                public void onError(Exception error) {
                    // Domoticz always gives an error: ignore
                    errorHandling(error);
                }
            });
            infoDialogIsFavoriteSwitchIsChanged = false;
        }
    }

    @Override
    public void onSwitchClick(int idx, boolean checked) {
        Log.d(TAG, "onSwitchClick");
        Log.d(TAG, "Set idx " + idx + " to " + checked);

        int jsonAction;
        int jsonUrl = Domoticz.Json.Url.Set.SWITCHES;

        if (checked) jsonAction = Domoticz.Device.Switch.Action.ON;
        else jsonAction = Domoticz.Device.Switch.Action.OFF;

        mDomoticz.setAction(idx, jsonUrl, jsonAction, 0, new setCommandReceiver() {
            @Override
            public void onReceiveResult(String result) {
                successHandling(result, false);
            }

            @Override
            public void onError(Exception error) {
                errorHandling(error);
            }
        });
    }

    @Override
    public void onBlindClick(int idx, int jsonAction) {
        Log.d(TAG, "onBlindClick");
        Log.d(TAG, "Set idx " + idx + " to " + String.valueOf(jsonAction));

        int jsonUrl = Domoticz.Json.Url.Set.SWITCHES;
        mDomoticz.setAction(idx, jsonUrl, jsonAction, 0, new setCommandReceiver() {
            @Override
            public void onReceiveResult(String result) {
                successHandling(result, true);
            }

            @Override
            public void onError(Exception error) {
                errorHandling(error);
            }
        });
    }

    @Override
    public void onDimmerChange(int idx, int value) {
        Log.d(TAG, "onDimmerChange");

        int jsonUrl = Domoticz.Json.Url.Set.SWITCHES;
        int jsonAction = Domoticz.Device.Dimmer.Action.DIM_LEVEL;

        mDomoticz.setAction(idx, jsonUrl, jsonAction, value, new setCommandReceiver() {
            @Override
            public void onReceiveResult(String result) {
                successHandling(result, false);
            }

            @Override
            public void onError(Exception error) {
                errorHandling(error);
            }
        });
    }

    /**
     * Notifies the list view adapter the data has changed and refreshes the list view
     */
    private void notifyDataSetChanged() {
        switchesListView.setAdapter(adapter);
    }

    /**
     * Initializes the progress dialog
     */
    private void initProgressDialog() {
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage(getString(R.string.msg_please_wait));
        progressDialog.setCancelable(false);
    }

    /**
     * Shows the progress dialog if isn't already showing
     */
    private void showProgressDialog() {
        if (progressDialog == null) initProgressDialog();
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

    @Override
    public void errorHandling(Exception error) {
        super.errorHandling(error);
        hideProgressDialog();
    }

    @Override
    public void onInfoDialogSwitchChange(int id, boolean isChecked) {
        infoDialogIsFavoriteSwitchIsChanged = true;
        infoDialogIsFavoriteSwitch = isChecked;
    }
}