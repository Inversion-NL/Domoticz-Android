package nl.inversion.domoticz.Interfaces;

import nl.inversion.domoticz.Containers.SceneInfo;

import java.util.ArrayList;

import nl.inversion.domoticz.Containers.SceneInfo;

public interface ScenesReceiver {

    void onReceiveScenes (ArrayList<SceneInfo> scenes);

    void onError (Exception error);
}
