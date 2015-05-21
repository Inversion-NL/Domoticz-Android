package nl.inversion.domoticz.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import nl.inversion.domoticz.Containers.ExtendedStatusInfo;
import nl.inversion.domoticz.R;

public class switchInfoDialog implements DialogInterface.OnDismissListener {

    private final InfoDialogSwitchChangeListener infoDialogSwitchChangeListener;
    private final MaterialDialog.Builder mdb;
    private InfoDialogDismissListener infoDialogDismissListener;
    private ExtendedStatusInfo mSwitch;
    private String idx;
    private String lastUpdate;
    private String signalLevel;
    private String batteryLevel;
    private boolean isFavorite;

    public switchInfoDialog(Context mContext,
                            ExtendedStatusInfo mSwitch,
                            int layout,
                            InfoDialogSwitchChangeListener infoDialogSwitchChangeListener) {
        this.mSwitch = mSwitch;
        this.infoDialogSwitchChangeListener = infoDialogSwitchChangeListener;
        mdb = new MaterialDialog.Builder(mContext);
        boolean wrapInScrollView = true;
        mdb.customView(layout, wrapInScrollView)
           .positiveText(android.R.string.ok);
        mdb.dismissListener(this);
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setSignalLevel(String signalLevel) {
        this.signalLevel = signalLevel;
    }

    public void setBatteryLevel(String batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public void show() {

        mdb.title(mSwitch.getName());

        MaterialDialog md = mdb.build();
        View view = md.getCustomView();

        TextView IDX_name = (TextView) view.findViewById(R.id.IDX_name);
        IDX_name.setText(idx);

        TextView LastUpdate_name = (TextView) view.findViewById(R.id.LastUpdate_name);
        LastUpdate_name.setText(lastUpdate);

        TextView SignalLevel_name = (TextView) view.findViewById(R.id.SignalLevel_name);
        SignalLevel_name.setText(signalLevel);

        TextView BatteryLevel_name = (TextView) view.findViewById(R.id.BatteryLevel_name);
        BatteryLevel_name.setText(batteryLevel);

        Switch favorite_switch = (Switch) view.findViewById(R.id.favorite_switch);
        favorite_switch.setChecked(isFavorite);
        favorite_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                infoDialogSwitchChangeListener.onInfoDialogSwitchChange(
                        compoundButton.getId(), isChecked);
            }
        });

        md.show();
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (infoDialogDismissListener != null )infoDialogDismissListener.onDismiss();
    }

    public void onDismissListener(InfoDialogDismissListener infoDialogDismissListener) {
        this.infoDialogDismissListener = infoDialogDismissListener;
    }

    public interface InfoDialogSwitchChangeListener {
        void onInfoDialogSwitchChange(int id, boolean isChecked);
    }
    
    public interface InfoDialogDismissListener {
        void onDismiss();
    }
}