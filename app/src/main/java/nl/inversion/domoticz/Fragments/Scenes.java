package nl.inversion.domoticz.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.inversion.domoticz.Containers.SceneInfo;
import nl.inversion.domoticz.R;
import nl.inversion.domoticz.Utils.Domoticz;
import nl.inversion.domoticz.app.AppController;
import nl.inversion.domoticz.app.SharedPref;

public class Scenes extends Fragment implements View.OnClickListener {

    private static final String TAG = Scenes.class.getSimpleName();

    public static final int JSON_METHOD_GET = Request.Method.GET;
    public static final int JSON_METHOD_POST = Request.Method.POST;
    public static final int JSON_METHOD_PUT = Request.Method.PUT;

    private TextView txtResponse;
    private ArrayList<SceneInfo> mScenes;

    private ProgressDialog progressDialog;
    private String url;
    private Domoticz mDomoticz;
    private SharedPref mSharedPref;

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

        txtResponse = (TextView) getView().findViewById(R.id.txtResponse);

        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage(getString(R.string.msg_please_wait));
        progressDialog.setCancelable(false);

        mDomoticz = new Domoticz(getActivity());
        mSharedPref = new SharedPref(getActivity());

        getData();
    }

    private void getData(){

        String username, password;

        if (mDomoticz.isUserLocal()) {
            username = mSharedPref.getDomoticzLocalUsername();
            password = mSharedPref.getDomoticzLocalPassword();
        } else {
            username = mSharedPref.getDomoticzRemoteUsername();
            password = mSharedPref.getDomoticzRemotePassword();
        }

        url = mDomoticz.constructRequestUrl(mDomoticz.JSON_REQUEST_URL_SCENES);

        showProgressDialog();
        makeJsonObjectRequest(username, password, JSON_METHOD_GET, url);
    }

    private void updateViews(SceneInfo scene) {
        // Example: http://android-er.blogspot.nl/2013/05/add-and-remove-view-dynamically.html

        if (scene.getType().equalsIgnoreCase(Domoticz.SCENE_TYPE_SCENE)) {

            LinearLayout container = (LinearLayout) getView().findViewById(R.id.container);

            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View sceneRow_switch = layoutInflater.inflate(R.layout.scene_row_scene, null);

            int idx = scene.getIdx();
            String lastUpdate = scene.getLastUpdate();
            String name = scene.getName();

            Button button = (Button) sceneRow_switch.findViewById(R.id.scene_button);
            button.setOnClickListener(this);
            button.setId(idx);

            TextView switch_name = (TextView) sceneRow_switch.findViewById(R.id.scene_name);
            TextView switch_last_seen = (TextView) sceneRow_switch.findViewById(R.id.switch_lastSeen);

            switch_name.setText(name);
            switch_last_seen.setText(lastUpdate);

            container.addView(sceneRow_switch);

        } else if (scene.getType().equalsIgnoreCase(Domoticz.SCENE_TYPE_GROUP)) {

            int id = R.id.container;

            LinearLayout container = (LinearLayout) getView().findViewById(R.id.container);

            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View sceneRow_switch = layoutInflater.inflate(R.layout.scene_row_group, null);

            int idx = scene.getIdx();
            String lastUpdate = scene.getLastUpdate();
            String name = scene.getName();
            boolean status = scene.getStatusInBoolean();

            ToggleButton toggleButton = (ToggleButton) sceneRow_switch.findViewById(R.id.scene_button);
            toggleButton.setOnClickListener(this);
            toggleButton.setId(idx);
            toggleButton.setChecked(status);

            TextView switch_name = (TextView) sceneRow_switch.findViewById(R.id.scene_name);
            TextView switch_last_seen = (TextView) sceneRow_switch.findViewById(R.id.switch_lastSeen);

            switch_name.setText(name);
            switch_last_seen.setText(lastUpdate);

            container.addView(sceneRow_switch);
        }
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "button ID: " + String.valueOf(view.getId()));

        for (SceneInfo scene : mScenes) {
            Log.d(TAG, "Idx value: " + String.valueOf(scene.getIdx()));
            if (scene.getIdx() == view.getId()) {
                String username, password;

                if (mDomoticz.isUserLocal()) {
                    username = mSharedPref.getDomoticzLocalUsername();
                    password = mSharedPref.getDomoticzLocalPassword();
                } else {
                    username = mSharedPref.getDomoticzRemoteUsername();
                    password = mSharedPref.getDomoticzRemotePassword();
                }

                url = mDomoticz.constructSetUrl(mDomoticz.JSON_SET_URL_SCENES, view.getId(), mDomoticz.JSON_ACTION_ON);

                showProgressDialog();
                makeJsonObjectRequest(username, password, JSON_METHOD_PUT, url);
            }
        }
    }
    private void showProgressDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    /**
     * Method to make json object request where json response starts with {
     * */
    private void makeJsonObjectRequest(final String username, final String password, int method, String url) {

        JsonObjectRequest jsonObjReq =
                new JsonObjectRequest(method,
                        url, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            // Parsing json object response
                            // response will be a string of a json object
                            parseResult(response.getString(Domoticz.JSON_FIELS_RESULT));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        hideProgressDialog();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        hideProgressDialog();
                        // TODO notify user settings has to be corrected
                    }
                })
                {

                    @Override
                    // HTTP basic authentication
                    // Taken from: http://blog.lemberg.co.uk/volley-part-1-quickstart
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return createBasicAuthHeader(username, password);
                    }

                };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    /**
     * Method to create a basic HTTP base64 encrypted authentication header
     * @param username Username
     * @param password Password
     * @return Base64 encrypted header map
     */
    Map<String, String> createBasicAuthHeader(String username, String password) {

        Map<String, String> headerMap = new HashMap<>();

        String credentials = username + ":" + password;
        String base64EncodedCredentials =
                Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        headerMap.put("Authorization", "Basic " + base64EncodedCredentials);

        return headerMap;
    }

    private void parseResult(String result) throws JSONException {

        JSONArray jsonArray = new JSONArray(result);

        if (jsonArray.length() > 0) {
            mScenes = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject row = jsonArray.getJSONObject(i);
                mScenes.add(new SceneInfo(row));
            }

            StringBuilder text = new StringBuilder();
            int i = 0;
            for (SceneInfo scene : mScenes) {

                if (i != 0) text.append("\n").append("\n");

                text.append("Name: ")
                        .append(scene.getName()).append("\n")
                        .append("Type: ").append(scene.getType()).append("\n")
                        .append("Status: ").append(scene.getStatusInString()).append("\n")
                        .append("Last update: ").append(scene.getLastUpdate());

                txtResponse.setText(text.toString());
                updateViews(scene);
                i++;
            }
        }
    }

}