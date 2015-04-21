package nl.inversion.domoticz.Containers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unused")
public class ExtendedStatusInfo {

    JSONObject jsonObject;

    String name;
    int level;
    String type;
    String status;
    int batteryLevel;
    int signalLevel;
    int switchTypeVal;
    String switchType;
    String lastUpdate;
    int idx;

    public ExtendedStatusInfo(JSONObject row) throws JSONException {
        this.jsonObject = row;

        name = row.getString("Name");
        level = row.getInt("LevelInt");
        type = row.getString("Type");
        status = row.getString("Status");
        batteryLevel = row.getInt("BatteryLevel");
        signalLevel = row.getInt("SignalLevel");
        switchType = row.getString("SwitchType");
        switchTypeVal = row.getInt("SwitchTypeVal");
        lastUpdate = row.getString("LastUpdate");
        idx = row.getInt("idx");
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public boolean getStatusBoolean() {
        boolean statusBoolean = false;
        if (status.equalsIgnoreCase("On")) statusBoolean = true;
        return statusBoolean;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public int getSignalLevel() {
        return signalLevel;
    }

    public int getSwitchTypeVal() {
        return switchTypeVal;
    }

    public String getSwitchType() {
        return switchType;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public int getIdx() {
        return idx;
    }
}