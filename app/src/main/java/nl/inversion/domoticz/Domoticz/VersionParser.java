package nl.inversion.domoticz.Domoticz;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import nl.inversion.domoticz.Interfaces.JSONParserInterface;
import nl.inversion.domoticz.Interfaces.VersionReceiver;

public class VersionParser implements JSONParserInterface {

    private static final String TAG = VersionParser.class.getSimpleName();
    private VersionReceiver receiver;

    public VersionParser(VersionReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void parseResult(String result) {
        receiver.onReceiveVersion(result);
    }

    @Override
    public void onError(Exception error) {
        Log.e(TAG, "VersionParser of JSONParserInterface exception");
        receiver.onError(error);
    }

}