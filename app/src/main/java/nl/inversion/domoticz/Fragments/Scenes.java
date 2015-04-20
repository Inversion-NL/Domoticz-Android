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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import nl.inversion.domoticz.Containers.SceneInfo;
import nl.inversion.domoticz.Interfaces.PutCommandReceiver;
import nl.inversion.domoticz.R;
import nl.inversion.domoticz.SettingsActivity;
import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.Interfaces.ScenesReceiver;

public class Scenes extends Fragment {

    private static final String TAG = Scenes.class.getSimpleName();
    private ProgressDialog progressDialog;
    private Domoticz mDomoticz;

    public static Fragment newInstance(Context context) {
        Scenes f = new Scenes();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_scenes, null);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Initialize the progress dialog
        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage(getString(R.string.msg_please_wait));
        progressDialog.setCancelable(false);

        mDomoticz = new Domoticz(getActivity());
    }

    /**
     * Gets the data through the scenes receiver with a call back on receive or error
     */
    private void getData() {

        showProgressDialog();

        mDomoticz.getScenes(new ScenesReceiver() {
            @Override
            public void onReceiveScenes(ArrayList<SceneInfo> scenes) {

                hideProgressDialog();

                for (SceneInfo mScene : scenes) {
                    createRow(mScene);
                }
            }

            @Override
            public void onError(Exception error) {
                error.printStackTrace();
                String cause;
                if (error.getCause() != null) {
                    cause = error.getCause().getMessage();
                    Toast.makeText(getActivity(), cause, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                }
                hideProgressDialog();
            }
        });
    }

    /**
     * Creates a row dynamically based on the data of the scene
     * @param mScene the scene information
     */
    private void createRow(SceneInfo mScene) {
        // Example: http://android-er.blogspot.nl/2013/05/add-and-remove-view-dynamically.html

        if (mScene.getType().equalsIgnoreCase(Domoticz.SCENE_TYPE_SCENE)) {

            LinearLayout container = (LinearLayout) getView().findViewById(R.id.container);

            LayoutInflater layoutInflater =
                    (LayoutInflater) getActivity()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View sceneRow_switch = layoutInflater.inflate(R.layout.scene_row_scene, null);

            String lastUpdate = mScene.getLastUpdate();
            String name = mScene.getName();

            Button button = (Button) sceneRow_switch.findViewById(R.id.scene_button);
            button.setId(mScene.getIdx());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleClick(view.getId(), true);
                }
            });


            TextView switch_name = (TextView) sceneRow_switch.findViewById(R.id.scene_name);
            TextView switch_last_seen =
                    (TextView) sceneRow_switch.findViewById(R.id.switch_lastSeen);

            switch_name.setText(name);
            switch_last_seen.setText(lastUpdate);

            container.addView(sceneRow_switch);

        } else if (mScene.getType().equalsIgnoreCase(Domoticz.SCENE_TYPE_GROUP)) {

            LinearLayout container = (LinearLayout) getView().findViewById(R.id.container);

            LayoutInflater layoutInflater =
                    (LayoutInflater) getActivity()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View sceneRow_switch = layoutInflater.inflate(R.layout.scene_row_group, null);

            String lastUpdate = mScene.getLastUpdate();
            String name = mScene.getName();
            boolean status = mScene.getStatusInBoolean();

            ToggleButton toggleButton =
                    (ToggleButton) sceneRow_switch.findViewById(R.id.scene_button);
            toggleButton.setId(mScene.getIdx());
            toggleButton.setChecked(status);
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    handleClick(compoundButton.getId(), checked);
                }
            });

            TextView switch_name = (TextView) sceneRow_switch.findViewById(R.id.scene_name);
            TextView switch_last_seen = (
                    TextView) sceneRow_switch.findViewById(R.id.switch_lastSeen);

            switch_name.setText(name);
            switch_last_seen.setText(lastUpdate);

            container.addView(sceneRow_switch);
        }
    }

    /**
     * Handles the clicks of the dynamically created rows
     *
     * @param idx idx code of the button (Domoticz)
     * @param checked is the button currently checked?
     */
    public void handleClick(int idx, boolean checked) {
        Log.d(TAG, "handleClick");
        Log.d(TAG, "Set idx " + idx + " to " + checked);

        int jsonAction;

        if (checked) jsonAction = mDomoticz.JSON_ACTION_ON;
        else jsonAction = mDomoticz.JSON_ACTION_OFF;

        mDomoticz.setAction(idx, jsonAction, new PutCommandReceiver() {
            @Override
            public void onReceiveResult(String result) {
                hideProgressDialog();
                Log.d(TAG, result);
            }

            @Override
            public void onError(Exception error) {
                hideProgressDialog();
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
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
}