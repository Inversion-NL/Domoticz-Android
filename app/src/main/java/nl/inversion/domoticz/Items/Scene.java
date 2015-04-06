package nl.inversion.domoticz.Items;

import org.json.JSONException;
import org.json.JSONObject;

public class Scene {

    JSONObject jsonObject;

    int Favorite;
    int HardwareID;
    String LastUpdate;
    String Name;
    String OffAction;
    String OnAction;
    String Status;
    Boolean Timers;
    String Type;
    int idx;

    public Scene(JSONObject row) throws JSONException {
        this.jsonObject = row;

        Favorite = row.getInt("Favorite");
        HardwareID = row.getInt("HardwareID");
        LastUpdate = row.getString("LastUpdate");
        Name = row.getString("Name");
        OffAction = row.getString("OffAction");
        OnAction = row.getString("OnAction");
        Status = row.getString("Status");
        Timers = row.getBoolean("Timers");
        Type = row.getString("Type");
        idx = row.getInt("idx");
    }

    public int getFavorite() {
        return Favorite;
    }

    public int getHardwareID() {
        return HardwareID;
    };

    public String getLastUpdate() {
        return LastUpdate;
    }

    public String getName() {
        return Name;
    }

    public String getOffAction() {
        return OffAction;
    }

    public String getOnAction() {
        return OnAction;
    }

    public String getStatus() {
        return Status;
    }

    public Boolean isTimers() {
        return Timers;
    }

    public String getType() {
        return Type;
    }

    public int getIdx() {
        return idx;
    }

    public JSONObject getJsonObject() {
        return this.jsonObject;
    }
}