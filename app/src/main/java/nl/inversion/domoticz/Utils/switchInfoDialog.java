package nl.inversion.domoticz.Utils;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import nl.inversion.domoticz.R;

public class switchInfoDialog extends DialogFragment {

    private String switchName;
    private String idx;
    private String lastUpdate;
    private String signalLevel;
    private String batteryLevel;
    private Button btnDone;

    public switchInfoDialog() {
    }

    public void setSwitchName(String switchName){
        this.switchName = switchName;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_switch_info, container);

        TextView switchName = (TextView) view.findViewById(R.id.switch_name);
        switchName.setText(this.switchName);

        TextView IDX_name = (TextView) view.findViewById(R.id.IDX_name);
        IDX_name.setText(idx);

        TextView LastUpdate_name = (TextView) view.findViewById(R.id.LastUpdate_name);
        LastUpdate_name.setText(lastUpdate);

        TextView SignalLevel_name = (TextView) view.findViewById(R.id.SignalLevel_name);
        SignalLevel_name.setText(signalLevel);

        TextView BatteryLevel_name = (TextView) view.findViewById(R.id.BatteryLevel_name);
        BatteryLevel_name.setText(batteryLevel);

        btnDone = (Button) view.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dismiss();
            }
        });

        getDialog().onBackPressed();
        return view;
    }

}
