package lab.android.rwth.evgenijandkate.plugscontrolclient.model;

/**
 * Created by ekaterina on 04.06.2015.
 *
 * An enum for the states of the plugs. The plug can be either in an "ON" or "OFF" state.
 * "ON" represents the plug's state when it is turned on, and "OFF" stands for the plug's
 * state when it is turned off.
 */
public enum StateEnum {
    ON("ON"), OFF("OFF");

    private final String name;

    private StateEnum(String name) {
        this.name = name;
    }

    /**
     * Returns the string representation of the plug's state (either "OFF" or "ON").
     *
     * @return the string representation of the plug's state (either "OFF" or "ON").
     */
    public String getName() {
        return this.name;
    }
}
