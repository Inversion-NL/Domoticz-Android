package nl.inversion.domoticz.Containers;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unused")
public class SceneInfo {

    private final boolean isProtected;
    JSONObject jsonObject;

    int favorite;
    int hardwareID;
    String lastUpdate;
    String name;
    String offAction;
    String onAction;
    String status;
    Boolean timers;
    String type;
    int idx;

    public SceneInfo(JSONObject row) throws JSONException {
        this.jsonObject = row;

        favorite = row.getInt("Favorite");
        isProtected = row.getBoolean("Protected");
        hardwareID = row.getInt("HardwareID");
        lastUpdate = row.getString("LastUpdate");
        name = row.getString("Name");
        offAction = row.getString("OffAction");
        onAction = row.getString("OnAction");
        status = row.getString("Status");
        timers = row.getBoolean("Timers");
        type = row.getString("Type");
        idx = row.getInt("idx");
    }

    @Override
    public String toString() {
        return "SceneInfo{" +
                "isProtected=" + isProtected +
                ", jsonObject=" + jsonObject +
                ", favorite=" + favorite +
                ", hardwareID=" + hardwareID +
                ", lastUpdate='" + lastUpdate + '\'' +
                ", name='" + name + '\'' +
                ", offAction='" + offAction + '\'' +
                ", onAction='" + onAction + '\'' +
                ", status='" + status + '\'' +
                ", timers=" + timers +
                ", type='" + type + '\'' +
                ", idx=" + idx +
                '}';
    }

    public boolean isProtected() {
        return isProtected;
    }

    public int getFavorite() {
        return favorite;
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

    public int getHardwareID() {
        return hardwareID;
    };

    public String getLastUpdate() {
        return lastUpdate;
    }

    public String getName() {
        return name;
    }

    public String getOffAction() {
        return offAction;
    }

    public String getOnAction() {
        return onAction;
    }

    public boolean getStatusInBoolean() {
        return status.equalsIgnoreCase("on");
    }

    public String getStatusInString() {
        return status;
    }

    public Boolean isTimers() {
        return timers;
    }

    public String getType() {
        return type;
    }

    public int getIdx() {
        return idx;
    }

    public JSONObject getJsonObject() {
        return this.jsonObject;
    }
}