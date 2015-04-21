package nl.inversion.domoticz.Interfaces;

public interface PutCommandReceiver {

    void onReceiveResult(String result);

    void onError(Exception error);
}

