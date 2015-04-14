package nl.inversion.domoticz.Containers;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unused")
public class ExtendedStatusInfo {

    JSONObject jsonObject;

    String IsDimmer;
    String Name;
    String SubType;
    String type;
    int idx;

    public ExtendedStatusInfo(JSONObject row) throws JSONException {
        this.jsonObject = row;


        IsDimmer = row.getString("IsDimmer");
        Name = row.getString("Name");
        SubType = row.getString("SubType");
        type = row.getString("Type");
        idx = row.getInt("idx");
    }

}