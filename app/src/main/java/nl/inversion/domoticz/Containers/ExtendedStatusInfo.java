package nl.inversion.domoticz.Containers;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import nl.inversion.domoticz.Domoticz.Domoticz;

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

    private final String UNKNOWN = "Unknown";
    private final String TAG = ExtendedStatusInfo.class.getSimpleName();

    public ExtendedStatusInfo(JSONObject row) throws JSONException {
        this.jsonObject = row;

        try {
        name = row.getString("Name");
        } catch (Exception e) {
            exceptionHandling(e);
            name = UNKNOWN;
        }
        try {
            hardwareName = row.getString("HardwareName");
        } catch (Exception e) {
            exceptionHandling(e);
            hardwareName = UNKNOWN;
        }
        try {
            isProtected = row.getBoolean("Protected");
        } catch (Exception e) {
            exceptionHandling(e);
            isProtected = false;
        }
        try {
            level = row.getInt("LevelInt");
        } catch (Exception e) {
            exceptionHandling(e);
            level = 0;
        }
        try {
        favorite = row.getInt("Favorite");
        } catch (Exception e) {
            exceptionHandling(e);
            favorite = 0;
        }
        try {
        type = row.getString("Type");
        } catch (Exception e) {
            exceptionHandling(e);
            type = "";
        }
        try {
            status = row.getString("Status");
        } catch (Exception e) {
            exceptionHandling(e);
            status = UNKNOWN;
        }
        try {
            batteryLevel = row.getInt("BatteryLevel");
        } catch (Exception e) {
            exceptionHandling(e);
            batteryLevel = 0;
        }
        try {
            signalLevel = row.getInt("SignalLevel");
        } catch (Exception e) {
            exceptionHandling(e);
            signalLevel = 0;
        }
        try {
            maxDimLevel = row.getInt("MaxDimLevel");
        } catch (Exception e) {
            exceptionHandling(e);
            maxDimLevel = 1;
        }
        try {
            switchType = row.getString("SwitchType");
        } catch (Exception e) {
            switchType = UNKNOWN;
            exceptionHandling(e);
        }
        try {
            switchTypeVal = row.getInt("SwitchTypeVal");
        } catch (Exception e) {
            switchTypeVal = 999999;
            exceptionHandling(e);
        }
        try {
            lastUpdate = row.getString("LastUpdate");
        } catch (Exception e) {
            lastUpdate = UNKNOWN;
            exceptionHandling(e);
        }
        try {
            idx = row.getInt("idx");
        } catch (Exception e) {
            idx = Domoticz.DOMOTICZ_FAKE_ID;
            exceptionHandling(e);
        }
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

    private void exceptionHandling(Exception error) {
        Log.e(TAG, "Exception occurred");
        error.printStackTrace();
    }
}