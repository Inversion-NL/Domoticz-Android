package nl.inversion.domoticz.Interfaces;

public interface setCommandReceiver {

    void onReceiveResult(String result);

    void onError(Exception error);

}
