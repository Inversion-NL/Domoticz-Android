package nl.inversion.domoticz.Interfaces;

import java.util.ArrayList;

import nl.inversion.domoticz.Containers.DevicesInfo;
import nl.inversion.domoticz.Containers.UtilitiesInfo;

public interface DevicesReceiver {

    void onReceiveDevices(ArrayList<DevicesInfo> mDevicesInfo);

    void onError(Exception error);
}
