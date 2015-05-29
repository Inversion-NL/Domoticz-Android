package nl.inversion.domoticz.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import nl.inversion.domoticz.Containers.ExtendedStatusInfo;
import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.Interfaces.switchesClickListener;
import nl.inversion.domoticz.R;

// Example used: http://www.ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter
// And: http://www.survivingwithandroid.com/2013/02/android-listview-adapter-checkbox-item_7.html

public class SwitchesAdapter extends ArrayAdapter<ExtendedStatusInfo> {

    Context context;
    ArrayList<ExtendedStatusInfo> data = null;
    ViewHolder holder;
    private switchesClickListener listener;
    private View row;
    private int layoutResourceId;
    private ViewGroup parent;
    private Switch dimmerSwitch;

    public SwitchesAdapter(Context context,
                           ArrayList<ExtendedStatusInfo> data,
                           switchesClickListener listener) {

        super(context, 0, data);

        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        row = convertView;
        this.parent = parent;

        ExtendedStatusInfo extendedStatusInfo = data.get(position);

        if (row == null) {
            holder = new ViewHolder();
            setSwitchRowData(extendedStatusInfo);
            row.setTag(holder);
        } else holder = (ViewHolder) row.getTag();

        return row;
    }

    private void setSwitchRowData(ExtendedStatusInfo mExtendedStatusInfo) {

        switch (mExtendedStatusInfo.getSwitchTypeVal()) {
            case Domoticz.SWITCH_TYPE_ON_OFF:
                setOnOffSwitchRowData(mExtendedStatusInfo);
                break;

            case Domoticz.SWITCH_TYPE_BLINDS:
                setBlindsRowData(mExtendedStatusInfo);
                break;

            case Domoticz.SWITCH_TYPE_DIMMER:
                setDimmerRowData(mExtendedStatusInfo);
                break;

            default:
                throw new NullPointerException("No supported switch type defined in the adapter");
        }
    }

    private void setOnOffSwitchRowData(ExtendedStatusInfo mExtendedStatusInfo) {

        layoutResourceId = R.layout.switch_row_on_off;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder.isProtected = mExtendedStatusInfo.isProtected();

        holder.switch_name = (TextView) row.findViewById(R.id.switch_name);
        holder.switch_name.setText(mExtendedStatusInfo.getName());

        holder.signal_level = (TextView) row.findViewById(R.id.switch_signal_level);
        String text = context.getText(R.string.signal_level) +
                ": " + String.valueOf(mExtendedStatusInfo.getSignalLevel());
        holder.signal_level.setText(text);

        holder.switch_battery_level = (TextView) row.findViewById(R.id.switch_battery_level);
        text = context.getString(R.string.battery_level) +
                ": " + String.valueOf(mExtendedStatusInfo.getBatteryLevel());
        holder.switch_battery_level.setText(text);

        holder.onOffSwitch = (Switch) row.findViewById(R.id.switch_button);
        if (holder.isProtected) holder.onOffSwitch.setEnabled(false);
        holder.onOffSwitch.setId(mExtendedStatusInfo.getIdx());
        holder.onOffSwitch.setChecked(mExtendedStatusInfo.getStatusBoolean());
        holder.onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                handleOnOffSwitchClick(compoundButton.getId(), checked);
            }
        });
    }

    private void setBlindsRowData(ExtendedStatusInfo mExtendedStatusInfo) {

        layoutResourceId = R.layout.switch_row_blinds;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder.isProtected = mExtendedStatusInfo.isProtected();

        holder.switch_name = (TextView) row.findViewById(R.id.switch_name);
        holder.switch_name.setText(mExtendedStatusInfo.getName());

        holder.switch_status = (TextView) row.findViewById(R.id.switch_status);
        String text = context.getText(R.string.status) + ": " + mExtendedStatusInfo.getStatus();
        holder.switch_status.setText(text);

        holder.switch_battery_level = (TextView) row.findViewById(R.id.switch_signal_level);
        text = context.getString(R.string.battery_level) + ": "
                + String.valueOf(mExtendedStatusInfo.getSignalLevel());
        holder.switch_battery_level.setText(text);

        holder.buttonUp = (ImageButton) row.findViewById(R.id.switch_button_up);
        if (holder.isProtected) holder.buttonUp.setEnabled(false);
        holder.buttonUp.setId(mExtendedStatusInfo.getIdx());
        holder.buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBlindsClick(view.getId(), Domoticz.BLINDS_ACTION_UP);
            }
        });

        holder.buttonStop = (ImageButton) row.findViewById(R.id.switch_button_stop);
        if (holder.isProtected) holder.buttonStop.setEnabled(false);
        holder.buttonStop.setId(mExtendedStatusInfo.getIdx());
        holder.buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBlindsClick(view.getId(), Domoticz.BLINDS_ACTION_STOP);
            }
        });

        holder.buttonDown = (ImageButton) row.findViewById(R.id.switch_button_down);
        if (holder.isProtected) holder.buttonDown.setEnabled(false);
        holder.buttonDown.setId(mExtendedStatusInfo.getIdx());
        holder.buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBlindsClick(view.getId(), Domoticz.BLINDS_ACTION_DOWN);
            }
        });
    }

    private void setDimmerRowData(final ExtendedStatusInfo mExtendedStatusInfo) {

        layoutResourceId = R.layout.switch_row_dimmer;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder.isProtected = mExtendedStatusInfo.isProtected();

        holder.switch_name = (TextView) row.findViewById(R.id.switch_name);
        holder.switch_name.setText(mExtendedStatusInfo.getName());

        holder.signal_level = (TextView) row.findViewById(R.id.switch_signal_level);
        String text = context.getText(R.string.signal_level) +
                ": " + String.valueOf(mExtendedStatusInfo.getSignalLevel());
        holder.signal_level.setText(text);

        holder.switch_battery_level = (TextView) row.findViewById(R.id.switch_battery_level);
        text = context.getString(R.string.battery_level) +
                ": " + String.valueOf(mExtendedStatusInfo.getBatteryLevel());
        holder.switch_battery_level.setText(text);

        holder.switch_dimmer_level = (TextView) row.findViewById(R.id.switch_dimmer_level);
        String percentage = calculateDimPercentage(
                mExtendedStatusInfo.getMaxDimLevel(), mExtendedStatusInfo.getLevel());
        holder.switch_dimmer_level.setText(percentage + "%");

        holder.dimmerOnOffSwitch = (Switch) row.findViewById(R.id.switch_dimmer_switch);
        dimmerSwitch = holder.dimmerOnOffSwitch;
        holder.dimmerOnOffSwitch.setId(mExtendedStatusInfo.getIdx());
        if (holder.isProtected) holder.dimmerOnOffSwitch.setEnabled(false);
        holder.dimmerOnOffSwitch.setChecked(mExtendedStatusInfo.getStatusBoolean());
        holder.dimmerOnOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                handleOnOffSwitchClick(compoundButton.getId(), checked);
                mExtendedStatusInfo.setStatusBoolean(checked);
            }
        });

        holder.dimmer = (SeekBar) row.findViewById(R.id.switch_dimmer);
        if (holder.isProtected) holder.dimmer.setEnabled(false);
        holder.dimmer.setProgress(mExtendedStatusInfo.getLevel());
        holder.dimmer.setMax(mExtendedStatusInfo.getMaxDimLevel());
        holder.dimmer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String percentage = calculateDimPercentage(seekBar.getMax(), progress);
                TextView switch_dimmer_level = (TextView) seekBar.getRootView()
                        .findViewById(R.id.switch_dimmer_level);
                switch_dimmer_level.setText(percentage + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (seekBar.getProgress() == 0 && dimmerSwitch.isChecked())
                    dimmerSwitch.setChecked(false);
                else if (seekBar.getProgress() > 0 && !dimmerSwitch.isChecked())
                    dimmerSwitch.setChecked(true);

                handleDimmerChange(mExtendedStatusInfo.getIdx(), seekBar.getProgress() + 1);
            }
        });
    }

    private String calculateDimPercentage(int maxDimLevel, int level) {
        float percentage = ((float) level / (float) maxDimLevel) * 100;
        return String.format("%.0f", percentage);
    }

    private void handleOnOffSwitchClick(int idx, boolean action) {
        listener.onSwitchClick(idx, action);
    }

    private void handleBlindsClick(int idx, int action) {
        listener.onBlindClick(idx, action);
    }

    private void handleDimmerChange(final int idx, final int value) {
        listener.onDimmerChange(idx, value);
    }

    static class ViewHolder {
        TextView switch_name, signal_level, switch_status, switch_battery_level, switch_dimmer_level;
        Switch onOffSwitch, dimmerOnOffSwitch;
        ImageButton buttonUp, buttonDown, buttonStop;
        Boolean isProtected;
        SeekBar dimmer;
    }
}