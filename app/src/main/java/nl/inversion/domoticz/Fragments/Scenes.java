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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import nl.inversion.domoticz.Containers.SceneInfo;
import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.Interfaces.ScenesReceiver;
import nl.inversion.domoticz.Interfaces.setCommandReceiver;
import nl.inversion.domoticz.R;

public class Scenes extends Fragment {

    private static final String TAG = Scenes.class.getSimpleName();
    LinearLayout container;
    private ProgressDialog progressDialog;
    private Domoticz mDomoticz;
    private TextView debugText;
    private boolean debug;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_scenes, null);

        getActionBar().setTitle(R.string.title_scenes);

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
     * Gets the data through the scenes receiver with a call back on receive or error
     */
    private void getData() {

        showProgressDialog();

        mDomoticz.getScenes(new ScenesReceiver() {
            @Override
            public void onReceiveScenes(ArrayList<SceneInfo> scenes) {

                successHandling(scenes.toString());
                hideProgressDialog();

                for (SceneInfo mScene : scenes) {
                    createRow(mScene);
                }
            }

            @Override
            public void onError(Exception error) {
                errorHandling(error);
            }
        });
    }

    /**
     * Creates a row dynamically based on the data of the scene
     * @param mScene the scene information
     */
    private void createRow(SceneInfo mScene) {
        // Example: http://android-er.blogspot.nl/2013/05/add-and-remove-view-dynamically.html

        if (debug) {
            String temp = debugText.getText().toString();
            temp = temp + "\n\n";
            temp = temp + mScene.getJsonObject().toString();
            debugText.setText(temp);
        }

        if (mScene.getType().equalsIgnoreCase(Domoticz.Scene.Type.SCENE)) {

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

        } else if (mScene.getType().equalsIgnoreCase(Domoticz.Scene.Type.GROUP)) {

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
     * @param idx idx code of the button (Domoticz)
     * @param checked is the button currently checked?
     */
    public void handleClick(int idx, boolean checked) {
        Log.d(TAG, "handleClick");
        Log.d(TAG, "Set idx " + idx + " to " + checked);

        int jsonAction;
        int jsonUrl = Domoticz.Json.Url.Set.SCENES;

        if (checked) jsonAction = Domoticz.Scene.Action.ON;
        else jsonAction = Domoticz.Scene.Action.OFF;

        mDomoticz.setAction(idx, jsonUrl, jsonAction, 0, new setCommandReceiver() {
            @Override
            public void onReceiveResult(String result) {
                Toast.makeText(getActivity(), R.string.action_success, Toast.LENGTH_LONG).show();
                successHandling(result);
                hideProgressDialog();
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
        hideProgressDialog();

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