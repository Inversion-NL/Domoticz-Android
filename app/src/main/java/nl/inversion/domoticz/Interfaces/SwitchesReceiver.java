package nl.inversion.domoticz.Interfaces;

import java.util.ArrayList;

import nl.inversion.domoticz.Containers.SwitchInfo;

public interface SwitchesReceiver {

    void onReceiveSwitches(ArrayList<SwitchInfo> switches);

    void onError(Exception error);
}
