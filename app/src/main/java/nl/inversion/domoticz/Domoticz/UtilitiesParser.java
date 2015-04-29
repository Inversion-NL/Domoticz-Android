package nl.inversion.domoticz.Domoticz;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.inversion.domoticz.Containers.SceneInfo;
import nl.inversion.domoticz.Containers.UtilitiesInfo;
import nl.inversion.domoticz.Fragments.Utilities;
import nl.inversion.domoticz.Interfaces.JSONParserInterface;
import nl.inversion.domoticz.Interfaces.ScenesReceiver;
import nl.inversion.domoticz.Interfaces.UtilitiesReceiver;

public class UtilitiesParser implements JSONParserInterface {

    private static final String TAG = UtilitiesParser.class.getSimpleName();
    private UtilitiesReceiver utilitiesReceiver;

    public UtilitiesParser(UtilitiesReceiver utilitiesReceiver) {
        this.utilitiesReceiver = utilitiesReceiver;
    }

    @Override
    public void parseResult(String result) {

        String[] utilityItems = Domoticz.ITEMS_UTILITIES;
        List<String> utilityItemsList = Arrays.asList(utilityItems);

        try {
            JSONArray jsonArray = new JSONArray(result);
            ArrayList<UtilitiesInfo> mUtilities = new ArrayList<>();


            if (jsonArray.length() > 0) {

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject row = jsonArray.getJSONObject(i);

                    if (utilityItemsList.contains(row.getString("Type"))) {
                        mUtilities.add(new UtilitiesInfo(row));
                    }

                }
            }

            utilitiesReceiver.onReceiveUtilities(mUtilities);

        } catch (JSONException e) {
            Log.e(TAG, "UtilitiesParser JSON exception");
            e.printStackTrace();
            utilitiesReceiver.onError(e);
        }
    }

    @Override
    public void onError(Exception error) {
        Log.e(TAG, "UtilitiesParser of JSONParserInterface exception");
        utilitiesReceiver.onError(error);
    }

}