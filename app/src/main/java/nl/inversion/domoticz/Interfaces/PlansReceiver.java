package nl.inversion.domoticz.Interfaces;

import java.util.ArrayList;

import nl.inversion.domoticz.Containers.PlanInfo;

public interface PlansReceiver {

    void onReceiveScenes(ArrayList<PlanInfo> plans);

    void onError(Exception error);
}
