package nl.inversion.domoticz.Containers;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unused")
public class StatusInfo {

    JSONObject jsonObject;

    String IsDimmer;
    String Name;
    String SubType;
    String type;
    int idx;

    public StatusInfo(JSONObject row) throws JSONException {
        this.jsonObject = row;
        // {"IsDimmer":false,
        // "Name":"Rolluik woonkamer achter",
        // "SubType":"RFY",
        // "Type":"RFY",
        // "idx":"20"}

        IsDimmer = row.getString("IsDimmer");
        Name = row.getString("Name");
        SubType = row.getString("SubType");
        type = row.getString("Type");
        idx = row.getInt("idx");
    }

    public String getIsDimmer() {
        return IsDimmer;
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
}