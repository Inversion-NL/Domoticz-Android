package nl.inversion.domoticz.Containers;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("unused")
public class PlanInfo {

    JSONObject jsonObject;

    int devices;
    String name;
    int order;
    int idx;

    public PlanInfo(JSONObject row) throws JSONException {
        this.jsonObject = row;

        devices = row.getInt("Devices");
        name = row.getString("Name");
        order = row.getInt("Order");
        idx = row.getInt("idx");
    }
}