package nl.inversion.domoticz.Containers;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import nl.inversion.domoticz.Domoticz.Domoticz;

@SuppressWarnings("unused")
public class SwitchInfo {

    JSONObject jsonObject;

    String IsDimmer;
    String Name;
    String SubType;
    String type;
    int idx;

    private static final String UNKNOWN = "Unknown";
    private final String TAG = SwitchInfo.class.getSimpleName();

    public SwitchInfo(JSONObject row) throws JSONException {
        this.jsonObject = row;

        try {
            IsDimmer = row.getString("IsDimmer");
        } catch (Exception e) {
            exceptionHandling(e);
            IsDimmer = "False";
        }
        try {
            Name = row.getString("Name");
        } catch (Exception e) {
            exceptionHandling(e);
            Name = UNKNOWN;
        }
        try {
            SubType = row.getString("SubType");
        } catch (Exception e) {
            exceptionHandling(e);
            SubType = UNKNOWN;
        }
        try {
            type = row.getString("Type");
        } catch (Exception e) {
            exceptionHandling(e);
            type = UNKNOWN;
        }
        try {
            idx = row.getInt("idx");
        } catch (Exception e) {
            exceptionHandling(e);
            idx = Domoticz.DOMOTICZ_FAKE_ID;
        }
    }

    @Override
    public String toString() {
        return "SwitchInfo{" +
                "IsDimmer='" + IsDimmer + '\'' +
                ", Name='" + Name + '\'' +
                ", SubType='" + SubType + '\'' +
                ", type='" + type + '\'' +
                ", idx=" + idx +
                '}';
    }

    public String getIsDimmerString() {
        return IsDimmer;
    }

    public boolean getIsDimmerBoolean() {
        return IsDimmer.equalsIgnoreCase("true") ? true : false;
    }

    public void setIsDimmer(String isDimmer) {
        IsDimmer = isDimmer;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSubType() {
        return SubType;
    }

    public void setSubType(String subType) {
        SubType = subType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    private void exceptionHandling(Exception error) {
        Log.e(TAG, "Exception occurred");
        error.printStackTrace();
    }
}