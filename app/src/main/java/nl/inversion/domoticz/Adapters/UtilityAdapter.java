package nl.inversion.domoticz.Adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import nl.inversion.domoticz.Containers.ExtendedStatusInfo;

// Example used: http://www.ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter
public class UtilityAdapter extends ArrayAdapter<ExtendedStatusInfo> {

    Context context;
    int layoutResourceId;
    ExtendedStatusInfo data[] = null;

    public UtilityAdapter(Context context, int resource, ExtendedStatusInfo[] data) {
        super(context, resource, data);

        this.context = context;
        this.layoutResourceId = resource;
        this.data = data;
    }



}
