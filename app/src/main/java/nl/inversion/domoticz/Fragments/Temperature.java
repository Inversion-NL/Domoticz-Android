package nl.inversion.domoticz.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.Interfaces.DomoticzFragmentListener;
import nl.inversion.domoticz.R;
import nl.inversion.domoticz.app.DomoticzFragment;

public class Temperature extends DomoticzFragment implements DomoticzFragmentListener {

    private static final String TAG = Temperature.class.getSimpleName();
    private ProgressDialog progressDialog;
    private Domoticz mDomoticz;
    private Context mActivity;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        getActionBar().setTitle(R.string.title_temperature);
    }

    @Override
    public void onConnectionOk() {

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
}