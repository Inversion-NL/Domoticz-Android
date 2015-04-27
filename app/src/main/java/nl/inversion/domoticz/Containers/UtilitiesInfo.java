package nl.inversion.domoticz.Containers;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unused")
public class UtilitiesInfo {

    JSONObject jsonObject;

    int idx;
    String Name;
    String LastUpdate;
    long setPoint;
    String Type;
    int Favorite;
    int HardwareID;
    int signalLevel;

    public UtilitiesInfo(JSONObject row) throws JSONException {
        this.jsonObject = row;

        Favorite = row.getInt("Favorite");
        HardwareID = row.getInt("HardwareID");
        LastUpdate = row.getString("LastUpdate");
        setPoint = row.getLong("SetPoint");
        Name = row.getString("Name");
        Type = row.getString("Type");
        idx = row.getInt("idx");
        signalLevel = row.getInt("SignalLevel");
    }

    @Override
    public String toString() {
        return "UtilitiesInfo{" +
                "idx=" + idx +
                ", Name='" + Name + '\'' +
                ", LastUpdate='" + LastUpdate + '\'' +
                ", setPoint=" + setPoint +
                ", Type='" + Type + '\'' +
                ", Favorite=" + Favorite +
                ", HardwareID=" + HardwareID +
                '}';
    }

    public int getIdx() {
        return idx;
    }

    public String getName() {
        return Name;
    }

    public long getSetPoint() {
        return setPoint;
    }

    public int getFavorite() {
        return Favorite;
    }

    public int getHardwareID() {
        return HardwareID;
    };

    public String getType() {
        return Type;
    }

    public String getLastUpdate() {
        return LastUpdate;
    }

    public JSONObject getJsonObject() {
        return this.jsonObject;
    }
}