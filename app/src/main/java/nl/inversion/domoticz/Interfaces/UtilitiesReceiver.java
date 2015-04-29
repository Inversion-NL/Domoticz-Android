package nl.inversion.domoticz.Interfaces;

import java.util.ArrayList;

import nl.inversion.domoticz.Containers.UtilitiesInfo;

public interface UtilitiesReceiver {

    void onReceiveUtilities(ArrayList<UtilitiesInfo> mUtilitiesInfos);

    void onError(Exception error);
}
