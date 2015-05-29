package nl.inversion.domoticz.Containers;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unused")
public class ExtendedStatusInfo {

    JSONObject jsonObject;

    String name;
    String hardwareName;
    boolean isProtected;
    int level;
    int maxDimLevel;
    int favorite;
    String type;
    String status;
    boolean statusBoolean;
    int batteryLevel;
    int signalLevel;
    int switchTypeVal;
    String switchType;
    String lastUpdate;
    int idx;

    public ExtendedStatusInfo(JSONObject row) throws JSONException {
        this.jsonObject = row;

        name = row.getString("Name");
        hardwareName = row.getString("HardwareName");
        isProtected = row.getBoolean("Protected");
        level = row.getInt("LevelInt");
        favorite = row.getInt("Favorite");
        type = row.getString("Type");
        status = row.getString("Status");
        batteryLevel = row.getInt("BatteryLevel");
        signalLevel = row.getInt("SignalLevel");
        maxDimLevel = row.getInt("MaxDimLevel");
        switchType = row.getString("SwitchType");
        switchTypeVal = row.getInt("SwitchTypeVal");
        lastUpdate = row.getString("LastUpdate");
        idx = row.getInt("idx");
    }

    @Override
    public String toString() {
        return "ExtendedStatusInfo{" +
                "jsonObject=" + jsonObject +
                ", name='" + name + '\'' +
                ", hardwareName='" + hardwareName + '\'' +
                ", level=" + level +
                ", favorite=" + favorite +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", batteryLevel=" + batteryLevel +
                ", signalLevel=" + signalLevel +
                ", switchTypeVal=" + switchTypeVal +
                ", switchType='" + switchType + '\'' +
                ", lastUpdate='" + lastUpdate + '\'' +
                ", idx=" + idx +
                '}';
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHardwareName() {
        return hardwareName;
    }

    public void setHardwareName(String hardwareName) {
        this.hardwareName = hardwareName;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public int getMaxDimLevel() {
        return maxDimLevel;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public boolean getFavoriteBoolean() {
        boolean favorite = false;
        if (this.favorite == 1) favorite = true;
        return favorite;
    }

    public void setFavoriteBoolean(boolean favorite) {
        if (favorite) this.favorite = 1;
        else this.favorite = 0;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean getStatusBoolean() {
        boolean statusBoolean = true;
        if (status.equalsIgnoreCase("On")) statusBoolean = true;
        else if (status.equalsIgnoreCase("Off")) statusBoolean = false;
        this.statusBoolean = statusBoolean;
        return statusBoolean;
    }

    public void setStatusBoolean(boolean status) {
        this.statusBoolean = status;
        setStatus("On");
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

    public void setIsProtected(boolean isProtected) {
        this.isProtected = isProtected;
    }
}