package nl.inversion.domoticz.Containers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unused")
public class ExtendedStatusInfo {

    JSONObject jsonObject;

    String name;
    String level;
    String type;
    String status;
    int batteryLevel;
    int signalLevel;
    String switchType;
    String lastUpdate;
    int idx;

    public ExtendedStatusInfo(JSONObject row) throws JSONException {
        this.jsonObject = row;

        name = row.getString("Name");
        level = row.getString("Level");
        type = row.getString("Type");
        status = row.getString("Status");
        batteryLevel = row.getInt("BatteryLevel");
        signalLevel = row.getInt("SignalLevel");
        switchType = row.getString("SwitchType");
        lastUpdate = row.getString("LastUpdate");
        idx = row.getInt("idx");
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public String getName() {
        return name;
    }

    public String getLevel() {
        return level;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public int getSignalLevel() {
        return signalLevel;
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