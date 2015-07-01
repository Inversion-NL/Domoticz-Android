package nl.inversion.domoticz.Containers;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unused")
public class DevicesInfo {

    JSONObject jsonObject;

    int idx;
    String Name;
    String LastUpdate;
    String Type;
    int Favorite;
    int HardwareID;
    int signalLevel;

    public DevicesInfo(JSONObject row) throws JSONException {
        this.jsonObject = row;

        Favorite = row.getInt("Favorite");
        HardwareID = row.getInt("HardwareID");
        LastUpdate = row.getString("LastUpdate");
        Name = row.getString("Name");
        Type = row.getString("Type");
        idx = row.getInt("idx");
        signalLevel = row.getInt("SignalLevel");
    }

    @Override
    public String toString() {
        return "UtilitiesInfo{" +
                "idx=" + idx +
                ", name='" + Name + '\'' +
                ", lastUpdate='" + LastUpdate + '\'' +
                ", type='" + Type + '\'' +
                ", favorite=" + Favorite +
                ", hardwareID=" + HardwareID +
                '}';
    }

    public int getIdx() {
        return idx;
    }

    public String getName() {
        return Name;
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

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setLastUpdate(String lastUpdate) {
        LastUpdate = lastUpdate;
    }

    public void setType(String type) {
        Type = type;
    }

    public void setFavorite(int favorite) {
        Favorite = favorite;
    }

    public void setHardwareID(int hardwareID) {
        HardwareID = hardwareID;
    }

    public void setSignalLevel(int signalLevel) {
        this.signalLevel = signalLevel;
    }
}