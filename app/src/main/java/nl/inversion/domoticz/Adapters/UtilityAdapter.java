package nl.inversion.domoticz.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nl.inversion.domoticz.Containers.UtilitiesInfo;
import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.Interfaces.thermostatClickListener;
import nl.inversion.domoticz.R;

// Example used: http://www.ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter
// And: http://www.survivingwithandroid.com/2013/02/android-listview-adapter-checkbox-item_7.html
public class UtilityAdapter extends BaseAdapter {

    private static final String TAG = UtilityAdapter.class.getSimpleName();

    private final thermostatClickListener listener;
    Context context;
    ArrayList<UtilitiesInfo> data = null;


    public UtilityAdapter(Context context,
                          ArrayList<UtilitiesInfo> data,
                          thermostatClickListener listener) {
        super();

        this.context = context;
        Collections.sort(data, new Comparator<UtilitiesInfo>() {
            @Override
            public int compare(UtilitiesInfo left, UtilitiesInfo right) {
                return left.getName().compareTo(right.getName());
            }
        });
        this.data = data;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int layoutResourceId;

        UtilitiesInfo mUtilitiesInfo = data.get(position);
        final long setPoint = mUtilitiesInfo.getSetPoint();

        //if (convertView == null) {
            holder = new ViewHolder();

            if (Domoticz.UTILITIES_TYPE_THERMOSTAT.equalsIgnoreCase(mUtilitiesInfo.getType())) {

                layoutResourceId = R.layout.utilities_row_thermostat;
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);

                holder.isProtected = mUtilitiesInfo.isProtected();

                holder.thermostat_name = (TextView) convertView.findViewById(R.id.thermostat_name);
                holder.lastSeen = (TextView) convertView.findViewById(R.id.thermostat_lastSeen);
                holder.setPoint = (TextView) convertView.findViewById(R.id.thermostat_set_point);
                holder.buttonPlus = (ImageButton) convertView.findViewById(R.id.utilities_plus);
                if (holder.isProtected) holder.buttonPlus.setEnabled(false);
                holder.buttonMinus = (ImageButton) convertView.findViewById(R.id.utilities_minus);
                if (holder.isProtected) holder.buttonMinus.setEnabled(false);

                holder.buttonMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        long newValue = setPoint - 1;
                        handleThermostatClick(view.getId(),
                                Domoticz.Device.Thermostat.Action.MIN,
                                newValue);
                    }
                });
                holder.buttonPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        long newValue = setPoint + 1;
                        handleThermostatClick(view.getId(),
                                Domoticz.Device.Thermostat.Action.PLUS,
                                newValue);
                    }
                });
            } else throw new NullPointerException("Scene type not supported in the adapter for:\n"
                    + mUtilitiesInfo.toString());
            convertView.setTag(holder);

        //} else holder = (ViewHolder) convertView.getTag();

        if (Domoticz.UTILITIES_TYPE_THERMOSTAT.equalsIgnoreCase(mUtilitiesInfo.getType())) {

            holder.buttonPlus.setId(mUtilitiesInfo.getIdx());
            holder.buttonMinus.setId(mUtilitiesInfo.getIdx());
            holder.thermostat_name.setText(mUtilitiesInfo.getName());
            holder.lastSeen.setText(mUtilitiesInfo.getLastUpdate());
            holder.setPoint.setText(context.getString(R.string.set_point) + ": " + String.valueOf(setPoint));
        } else throw new NullPointerException(TAG + ": "+ "No layout defined for scene type: "
                + mUtilitiesInfo.getType());

        return convertView;
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
        Boolean isProtected;
    }
}