package nl.inversion.domoticz.Domoticz;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import nl.inversion.domoticz.Containers.ExtendedStatusInfo;
import nl.inversion.domoticz.Interfaces.JSONParserInterface;
import nl.inversion.domoticz.Interfaces.StatusReceiver;

public class StatusInfoParser implements JSONParserInterface {

    private static final String TAG = StatusInfoParser.class.getSimpleName();
    private StatusReceiver statusReceiver;

    public StatusInfoParser(StatusReceiver receiver) {
        this.statusReceiver = receiver;
    }

    @Override
    public void parseResult(String result) {
        // Change the result data here so the view class gets the ready data

        try {

            JSONArray jsonArray = new JSONArray(result);
            ArrayList<ExtendedStatusInfo> mExtendedStatusInfo = new ArrayList<>();

            if (jsonArray.length() > 0) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject row = jsonArray.getJSONObject(i);
                    mExtendedStatusInfo.add(new ExtendedStatusInfo(row));
                }
            }
            statusReceiver.onReceiveStatus(mExtendedStatusInfo);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Exception error) {
        Log.d(TAG, "PutCommandParser onError");
        statusReceiver.onError(error);
    }
}