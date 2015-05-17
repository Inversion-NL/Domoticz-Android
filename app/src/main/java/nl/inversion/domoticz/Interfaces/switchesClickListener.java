package nl.inversion.domoticz.Interfaces;

public interface switchesClickListener {

    void onSwitchClick(int idx, boolean action);

    void onBlindClick(int idx, int action);

}