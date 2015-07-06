package nl.inversion.domoticz.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nl.inversion.domoticz.Adapters.PlansAdapter;
import nl.inversion.domoticz.Containers.PlanInfo;
import nl.inversion.domoticz.Containers.SceneInfo;
import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.Interfaces.PlansReceiver;
import nl.inversion.domoticz.R;
import nl.inversion.domoticz.UI.sceneInfoDialog;
import nl.inversion.domoticz.Utils.PhoneConnectionUtil;

@SuppressWarnings("unused")
public class Plans extends Fragment {

    private static final String TAG = Plans.class.getSimpleName();

    private ProgressDialog progressDialog;
    private Activity mActivity;
    private Domoticz mDomoticz;
    private RecyclerView mRecyclerView;
    private PlansAdapter mAdapter;
    private ArrayList<PlanInfo> mPlans;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_plans, null);

        mDomoticz = new Domoticz(getActivity());
        boolean debug = mDomoticz.isDebugEnabled();

        // if (debug) showDebugLayout();

        mRecyclerView = (RecyclerView) root.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (connectionOk()) getPlans();
        else Toast.makeText(getActivity(), "Connection not OK!", Toast.LENGTH_SHORT).show();

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Checks for a active connection
     */
    public boolean connectionOk() {

        PhoneConnectionUtil mPhoneConnectionUtil = new PhoneConnectionUtil(getActivity());

        if (mPhoneConnectionUtil.isNetworkAvailable()) {
            //addDebugText("Connection OK");
            return true;
        }
        else {
            //setErrorMessage(getString(R.string.error_notConnected));
            return false;
        }
    }

    public void getPlans() {
        showProgressDialog();

        mDomoticz = new Domoticz(mActivity);
        mDomoticz.getPlans(new PlansReceiver() {

            @Override
            public void OnReceivePlans(ArrayList<PlanInfo> plans) {
                //successHandling(plans.toString(), false);

                Plans.this.mPlans = plans;

                Collections.sort(plans, new Comparator<PlanInfo>() {
                    @Override
                    public int compare(PlanInfo left, PlanInfo right) {
                        return left.getOrder() - right.getOrder();
                    }
                });

                mAdapter = new PlansAdapter(plans, getActivity());
                mAdapter.setOnItemClickListener(new PlansAdapter.onClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        Toast.makeText(getActivity(), "Clicked " + mPlans.get(position).getName(), Toast.LENGTH_SHORT).show();
                    }
                });
                mRecyclerView.setAdapter(mAdapter);
                hideProgressDialog();
            }

            @Override
            public void onError(Exception error) {
                errorHandling(error);
            }
        });
    }

    private void showInfoDialog(final SceneInfo mSceneInfo) {

        sceneInfoDialog infoDialog = new sceneInfoDialog(
                getActivity(),
                mSceneInfo,
                R.layout.dialog_scene_info);
        infoDialog.setIdx(String.valueOf(mSceneInfo.getIdx()));
        infoDialog.setLastUpdate(mSceneInfo.getLastUpdate());
        infoDialog.setIsFavorite(mSceneInfo.getFavoriteBoolean());
        infoDialog.show();
        infoDialog.onDismissListener(new sceneInfoDialog.DismissListener() {

            @Override
            public void onDismiss(boolean isChanged, boolean isFavorite) {
                //TODO
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        getActionBar().setTitle(R.string.title_plans);
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

    public void errorHandling(Exception error) {
        //super.errorHandling(error);
        hideProgressDialog();
    }

    public ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }
}