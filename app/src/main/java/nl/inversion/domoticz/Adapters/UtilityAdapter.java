package nl.inversion.domoticz.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import nl.inversion.domoticz.Containers.UtilitiesInfo;
import nl.inversion.domoticz.R;

// Example used: http://www.ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter
// And: http://www.survivingwithandroid.com/2013/02/android-listview-adapter-checkbox-item_7.html
public class UtilityAdapter extends ArrayAdapter<UtilitiesInfo> {

    Context context;
    UtilitiesInfo data[] = null;

    public UtilityAdapter(Context context,  UtilitiesInfo[] data) {
        super(context, 0, data);

        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        int layoutResourceId = 0;

        UtilitiesInfo mUtilitiesInfo = data[position];

        if (row == null) {
            holder = new ViewHolder();

            if (mUtilitiesInfo.getType().equalsIgnoreCase("Thermostat")) {

                layoutResourceId = R.layout.utilities_row_thermostat;

                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder.thermostat_name = (TextView) row.findViewById(R.id.thermostat_name);
                holder.lastSeen = (TextView) row.findViewById(R.id.thermostat_lastSeen);
                holder.setPoint = (TextView) row.findViewById(R.id.thermostat_set_point);
                holder.buttonPlus = (ImageButton) row.findViewById(R.id.utilities_plus);
                holder.buttonMinus = (ImageButton) row.findViewById(R.id.utilities_minus);

                holder.thermostat_name.setText(mUtilitiesInfo.getName());
                holder.lastSeen.setText(mUtilitiesInfo.getLastUpdate());
                holder.setPoint.setText(context.getString(R.string.set_point) + ": " + String.valueOf(mUtilitiesInfo.getSetPoint()));

            }



            row.setTag(holder);

        } else holder = (ViewHolder) row.getTag();




        return row;
    }

    static class ViewHolder {
        TextView thermostat_name;
        TextView lastSeen;
        TextView setPoint;
        ImageButton buttonPlus;
        ImageButton buttonMinus;
    }

}
