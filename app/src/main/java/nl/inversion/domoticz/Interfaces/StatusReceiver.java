package nl.inversion.domoticz.Interfaces;

import java.util.ArrayList;

import nl.inversion.domoticz.Containers.ExtendedStatusInfo;

public interface StatusReceiver {

    void onReceiveStatus(ArrayList<ExtendedStatusInfo> extendedStatusInfo);

    void onError(Exception error);
}