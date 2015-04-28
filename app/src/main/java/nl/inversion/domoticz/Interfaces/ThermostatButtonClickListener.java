package nl.inversion.domoticz.Interfaces;

public interface ThermostatButtonClickListener {

    void onThermostatClick(int idx, int action, long newSetPoint);

}