package nl.inversion.domoticz.Interfaces;

import nl.inversion.domoticz.Containers.UtilitiesInfo;

public interface UtilitiesReceiver {

    void onReceiveUtilities(UtilitiesInfo[] mUtilitiesInfos);

    void onError(Exception error);
}
