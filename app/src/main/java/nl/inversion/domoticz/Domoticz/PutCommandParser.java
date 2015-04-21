package nl.inversion.domoticz.Domoticz;

import android.util.Log;

import nl.inversion.domoticz.Interfaces.JSONParserInterface;
import nl.inversion.domoticz.Interfaces.PutCommandReceiver;

public class PutCommandParser implements JSONParserInterface{

    private static final String TAG = PutCommandParser.class.getSimpleName();
    private PutCommandReceiver putCommandReceiver;

    public PutCommandParser(PutCommandReceiver putCommandReceiver) {
        this.putCommandReceiver = putCommandReceiver;
    }

    @Override
    public void parseResult(String result) {
        putCommandReceiver.onReceiveResult(result);
    }

    @Override
    public void onError(Exception error) {
        Log.d(TAG, "PutCommandParser onError");
        putCommandReceiver.onError(error);
    }
}