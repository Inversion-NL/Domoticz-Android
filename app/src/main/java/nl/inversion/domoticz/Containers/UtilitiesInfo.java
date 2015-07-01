package nl.inversion.domoticz.Containers;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unused")
public class UtilitiesInfo {

    private final boolean isProtected;
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
        isProtected = row.getBoolean("Protected");
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
                "isProtected=" + isProtected +
                ", jsonObject=" + jsonObject +
                ", idx=" + idx +
                ", Name='" + Name + '\'' +
                ", LastUpdate='" + LastUpdate + '\'' +
                ", setPoint=" + setPoint +
                ", Type='" + Type + '\'' +
                ", Favorite=" + Favorite +
                ", HardwareID=" + HardwareID +
                ", signalLevel=" + signalLevel +
                '}';
    }

    public int getIdx() {
        return idx;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public int getSignalLevel() {
        return signalLevel;
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

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setLastUpdate(String lastUpdate) {
        LastUpdate = lastUpdate;
    }

    public void setSetPoint(long setPoint) {
        this.setPoint = setPoint;
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