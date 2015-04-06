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
import android.widget.TextView;
import android.widget.Toast;

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

import nl.inversion.domoticz.Items.Scene;
import nl.inversion.domoticz.R;
import nl.inversion.domoticz.app.AppController;

public class Scenes extends Fragment {

    private String password = "Vak93zv";
    private String username = "Admin";

    private String http = "http://";
    private String url = "domotica.inversion.nl";
    private String port = "8080";
    private String scenesUrl = "/json.htm?type=scenes";
    private String lightSwitches = "/json.htm?type=command&param=getlightswitches";

    private String urlJsonObj = http + url + ":" + port + scenesUrl;

    private static String TAG = Scenes.class.getSimpleName();
    private Button btnArrayRequest;
    private TextView txtResponse;

    // Progress dialog
    private ProgressDialog progressDialog;

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

        btnArrayRequest = (Button) getView().findViewById(R.id.btnArrayRequest);
        txtResponse = (TextView) getView().findViewById(R.id.txtResponse);

        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        btnArrayRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // making json array request
                makeJsonObjectRequest();
            }
        });
    }

    /**
     * Method to make json object request where json response starts with {
     * */
    private void makeJsonObjectRequest() {

        showProgressDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlJsonObj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response
                    // response will be a json object
                    parseResult(response.getString("result"));

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
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hideProgressDialog();
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
            ArrayList<Scene> mScenes = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject row = jsonArray.getJSONObject(i);
                mScenes.add(new Scene(row));
            }

            for (Scene scene : mScenes) {

                StringBuilder text = new StringBuilder();
                text.append("Name: ").append(scene.getName()).append("\n")
                    .append("Type: ").append(scene.getType()).append("\n")
                    .append("Status: ").append(scene.getStatus()).append("\n")
                    .append("Last update: ").append(scene.getLastUpdate()).append("\n").append("\n");

                txtResponse.setText(text.toString());
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
}