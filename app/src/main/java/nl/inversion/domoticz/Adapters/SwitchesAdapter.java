package nl.inversion.domoticz.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nl.inversion.domoticz.Containers.ExtendedStatusInfo;
import nl.inversion.domoticz.Domoticz.Domoticz;
import nl.inversion.domoticz.Interfaces.switchesClickListener;
import nl.inversion.domoticz.R;

// Example used: http://www.ezzylearning.com/tutorial/customizing-android-listview-items-with-custom-arrayadapter
// And: http://www.survivingwithandroid.com/2013/02/android-listview-adapter-checkbox-item_7.html

public class SwitchesAdapter extends BaseAdapter {

    private final int ID_TEXTVIEW = 1000;
    private final int ID_SWITCH = 2000;
    private Context context;
    private ArrayList<ExtendedStatusInfo> data = null;
    private switchesClickListener listener;
    private int layoutResourceId;
    private int previousDimmerValue;

    public SwitchesAdapter(Context context,
                           ArrayList<ExtendedStatusInfo> data,
                           switchesClickListener listener) {

        super();

        this.context = context;
        Collections.sort(data, new Comparator<ExtendedStatusInfo>() {
            @Override
            public int compare(ExtendedStatusInfo left, ExtendedStatusInfo right) {
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
        ExtendedStatusInfo extendedStatusInfo = data.get(position);

        //if (convertView == null) {
            holder = new ViewHolder();
            convertView = setSwitchRowId(extendedStatusInfo, holder);
            convertView.setTag(holder);
        //} else holder = (ViewHolder) convertView.getTag();

        setSwitchRowData(extendedStatusInfo, holder, convertView);

        return convertView;
    }

    private View setSwitchRowId(ExtendedStatusInfo mExtendedStatusInfo, ViewHolder holder) {

        View row;

        switch (mExtendedStatusInfo.getSwitchTypeVal()) {
            case Domoticz.Device.Type.Value.ON_OFF:
                row = setOnOffSwitchRowId(holder);
                break;

            case Domoticz.Device.Type.Value.BLINDS:
                row = setBlindsRowId(holder);
                break;

            case Domoticz.Device.Type.Value.DIMMER:
                row = setDimmerRowId(holder);
                break;

            default:
                throw new NullPointerException(
                        "Switch type not supported in the adapter for: \n"
                                + mExtendedStatusInfo.toString());
        }
        return row;
    }

    private View setOnOffSwitchRowId(ViewHolder holder) {

        layoutResourceId = R.layout.switch_row_on_off;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(layoutResourceId, null);

        holder.onOffSwitch = (Switch) row.findViewById(R.id.switch_button);
        holder.signal_level = (TextView) row.findViewById(R.id.switch_signal_level);
        holder.switch_name = (TextView) row.findViewById(R.id.switch_name);
        holder.switch_battery_level = (TextView) row.findViewById(R.id.switch_battery_level);

        return row;
    }

    private View setBlindsRowId(ViewHolder holder) {

        layoutResourceId = R.layout.switch_row_blinds;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(layoutResourceId, null);

        holder.switch_name = (TextView) row.findViewById(R.id.switch_name);
        holder.switch_status = (TextView) row.findViewById(R.id.switch_status);
        holder.switch_battery_level = (TextView) row.findViewById(R.id.switch_signal_level);
        holder.buttonUp = (ImageButton) row.findViewById(R.id.switch_button_up);
        holder.buttonStop = (ImageButton) row.findViewById(R.id.switch_button_stop);
        holder.buttonDown = (ImageButton) row.findViewById(R.id.switch_button_down);

        return row;
    }

    private View setDimmerRowId(ViewHolder holder) {

        layoutResourceId = R.layout.switch_row_dimmer;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(layoutResourceId, null);

        holder.switch_name = (TextView) row.findViewById(R.id.switch_name);
        holder.signal_level = (TextView) row.findViewById(R.id.switch_signal_level);
        holder.switch_battery_level = (TextView) row.findViewById(R.id.switch_battery_level);
        holder.switch_dimmer_level = (TextView) row.findViewById(R.id.switch_dimmer_level);
        holder.dimmerOnOffSwitch = (Switch) row.findViewById(R.id.switch_dimmer_switch);
        holder.dimmer = (SeekBar) row.findViewById(R.id.switch_dimmer);

        return row;
    }

    private void setSwitchRowData(ExtendedStatusInfo mExtendedStatusInfo,
                                  ViewHolder holder,
                                  View convertView) {

        switch (mExtendedStatusInfo.getSwitchTypeVal()) {
            case Domoticz.Device.Type.Value.ON_OFF:
                setOnOffSwitchRowData(mExtendedStatusInfo, holder);
                break;

            case Domoticz.Device.Type.Value.BLINDS:
                setBlindsRowData(mExtendedStatusInfo, holder);
                break;

            case Domoticz.Device.Type.Value.DIMMER:
                setDimmerRowData(mExtendedStatusInfo, holder);
                break;

            default:
                throw new NullPointerException(
                        "No supported switch type defined in the adapter (setSwitchRowData)");
        }
    }

    private void setOnOffSwitchRowData(ExtendedStatusInfo mExtendedStatusInfo,
                                       ViewHolder holder) {

        holder.isProtected = mExtendedStatusInfo.isProtected();
        holder.switch_name.setText(mExtendedStatusInfo.getName());

        String text = context.getText(R.string.signal_level) +
                ": " + String.valueOf(mExtendedStatusInfo.getSignalLevel());
        holder.signal_level.setText(text);

        int batteryLevel = mExtendedStatusInfo.getBatteryLevel();
        if (batteryLevel == 255) text = context.getText(R.string.battery_level) +
                ": " + "N/A";
        else text = context.getString(R.string.battery_level) +
                ": " + String.valueOf(batteryLevel);
        holder.switch_battery_level.setText(text);

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

    private void setBlindsRowData(ExtendedStatusInfo mExtendedStatusInfo,
                                  ViewHolder holder) {
        holder.isProtected = mExtendedStatusInfo.isProtected();

        holder.switch_name.setText(mExtendedStatusInfo.getName());

        String text = context.getText(R.string.status) + ": " + mExtendedStatusInfo.getStatus();
        holder.switch_status.setText(text);

        text = context.getString(R.string.battery_level) + ": "
                + String.valueOf(mExtendedStatusInfo.getSignalLevel());
        holder.switch_battery_level.setText(text);

        if (holder.isProtected) holder.buttonUp.setEnabled(false);
        holder.buttonUp.setId(mExtendedStatusInfo.getIdx());
        holder.buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBlindsClick(view.getId(), Domoticz.Device.Blind.Action.UP);
            }
        });

        if (holder.isProtected) holder.buttonStop.setEnabled(false);
        holder.buttonStop.setId(mExtendedStatusInfo.getIdx());
        holder.buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBlindsClick(view.getId(), Domoticz.Device.Blind.Action.STOP);
            }
        });

        if (holder.isProtected) holder.buttonDown.setEnabled(false);
        holder.buttonDown.setId(mExtendedStatusInfo.getIdx());
        holder.buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBlindsClick(view.getId(), Domoticz.Device.Blind.Action.DOWN);
            }
        });
    }

    private void setDimmerRowData(final ExtendedStatusInfo mExtendedStatusInfo,
                                  ViewHolder holder) {

        holder.isProtected = mExtendedStatusInfo.isProtected();

        holder.switch_name.setText(mExtendedStatusInfo.getName());

        String text = context.getText(R.string.signal_level) +
                ": " + String.valueOf(mExtendedStatusInfo.getSignalLevel());
        holder.signal_level.setText(text);

        int batteryLevel = mExtendedStatusInfo.getBatteryLevel();
        if (batteryLevel == 255) text = context.getText(R.string.battery_level) +
                ": " + "N/A";
        else text = context.getString(R.string.battery_level) +
                ": " + String.valueOf(batteryLevel);
        holder.switch_battery_level.setText(text);

        holder.switch_dimmer_level.setId(mExtendedStatusInfo.getIdx() + ID_TEXTVIEW);
        String percentage = calculateDimPercentage(
                mExtendedStatusInfo.getMaxDimLevel(), mExtendedStatusInfo.getLevel());
        holder.switch_dimmer_level.setText(percentage);

        holder.dimmerOnOffSwitch.setId(mExtendedStatusInfo.getIdx() + ID_SWITCH);
        if (holder.isProtected) holder.dimmerOnOffSwitch.setEnabled(false);
        holder.dimmerOnOffSwitch.setChecked(mExtendedStatusInfo.getStatusBoolean());
        holder.dimmerOnOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                handleOnOffSwitchClick(compoundButton.getId(), checked);
                mExtendedStatusInfo.setStatusBoolean(checked);
            }
        });

        if (holder.isProtected) holder.dimmer.setEnabled(false);
        holder.dimmer.setProgress(mExtendedStatusInfo.getLevel());
        holder.dimmer.setMax(mExtendedStatusInfo.getMaxDimLevel());
        holder.dimmer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String percentage = calculateDimPercentage(seekBar.getMax(), progress);
                TextView switch_dimmer_level = (TextView) seekBar.getRootView()
                        .findViewById(mExtendedStatusInfo.getIdx() + ID_TEXTVIEW);
                switch_dimmer_level.setText(percentage);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                previousDimmerValue = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                Switch dimmerOnOffSwitch = (Switch) seekBar.getRootView()
                        .findViewById(mExtendedStatusInfo.getIdx() + ID_SWITCH);

                if (progress == 0 && dimmerOnOffSwitch.isChecked()) {
                    dimmerOnOffSwitch.setChecked(false);
                    seekBar.setProgress(previousDimmerValue);
                }
                else if (progress > 0 && !dimmerOnOffSwitch.isChecked())
                    dimmerOnOffSwitch.setChecked(true);
                handleDimmerChange(mExtendedStatusInfo.getIdx(), progress + 1);
                mExtendedStatusInfo.setLevel(progress);
            }
        });

    }

    private String calculateDimPercentage(int maxDimLevel, int level) {
        float percentage = ((float) level / (float) maxDimLevel) * 100;
        return String.format("%.0f", percentage) + "%";
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