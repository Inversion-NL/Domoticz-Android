package nl.inversion.domoticz.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import nl.inversion.domoticz.Containers.UtilitiesInfo;
import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.Interfaces.thermostatClickListener;
import nl.inversion.domoticz.R;

// Example used: http://www.ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter
// And: http://www.survivingwithandroid.com/2013/02/android-listview-adapter-checkbox-item_7.html
public class UtilityAdapter extends ArrayAdapter<UtilitiesInfo> {

    Context context;
    ArrayList<UtilitiesInfo> data = null;
    private final thermostatClickListener listener;


    public UtilityAdapter(Context context,
                          ArrayList<UtilitiesInfo> data,
                          thermostatClickListener listener) {
        super(context, 0, data);

        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        int layoutResourceId;

        UtilitiesInfo mUtilitiesInfo = data.get(position);

        if (row == null) {
            holder = new ViewHolder();

            if (Domoticz.UTILITIES_TYPE_THERMOSTAT.equalsIgnoreCase(mUtilitiesInfo.getType())) {

                layoutResourceId = R.layout.utilities_row_thermostat;

                final long setPoint = mUtilitiesInfo.getSetPoint();

                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder.thermostat_name = (TextView) row.findViewById(R.id.thermostat_name);
                holder.lastSeen = (TextView) row.findViewById(R.id.thermostat_lastSeen);
                holder.setPoint = (TextView) row.findViewById(R.id.thermostat_set_point);
                holder.buttonPlus = (ImageButton) row.findViewById(R.id.utilities_plus);
                holder.buttonPlus.setId(mUtilitiesInfo.getIdx());
                holder.buttonMinus = (ImageButton) row.findViewById(R.id.utilities_minus);
                holder.buttonMinus.setId(mUtilitiesInfo.getIdx());

                holder.thermostat_name.setText(mUtilitiesInfo.getName());
                holder.lastSeen.setText(mUtilitiesInfo.getLastUpdate());
                holder.setPoint.setText(context.getString(R.string.set_point) + ": " + String.valueOf(setPoint));

                holder.buttonMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        long newValue = setPoint - 1;
                        handleThermostatClick(view.getId(), Domoticz.THERMOSTAT_ACTION_MIN, newValue);
                    }
                });
                holder.buttonPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        long newValue = setPoint + 1;
                        handleThermostatClick(view.getId(), Domoticz.THERMOSTAT_ACTION_PLUS, newValue);
                    }
                });
            }
            if (row != null) row.setTag(holder);

        } else holder = (ViewHolder) row.getTag();

        return row;
    }

    public void handleThermostatClick(int idx, int action, long newSetPoint) {
            listener.onClick(idx, action, newSetPoint);
    }

    static class ViewHolder {
        TextView thermostat_name;
        TextView lastSeen;
        TextView setPoint;
        ImageButton buttonPlus;
        ImageButton buttonMinus;
    }
}