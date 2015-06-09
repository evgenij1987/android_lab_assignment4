package lab.android.rwth.evgenijandkate.plugscontrolclient.model;

/**
 * Created by elobanova on 07.06.2015.
 *
 * A class representing the object for storing the plugs data in order to transfer it to the server,
 * when the user with the administrative rights is adding a new plug. He can specify the plug/s
 * human readable name, a switch code, a house code and the state in which the plug is initially.
 */
public class PlugTransferableData {
    private String name;
    private String switchCode;
    private String houseCode;
    private StateEnum state;

    public PlugTransferableData(String name, String switchCode, String houseCode, StateEnum state) {
        this.name = name;
        this.switchCode = switchCode;
        this.houseCode = houseCode;
        this.state = state;
    }

    /**
     * Sets the plug's human readable name
     *
     * @param name the plug's human readable name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the plug's human readable name
     *
     * @return the plug's human readable name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the plug's switch code
     *
     * @return a string representing a switch code
     */
    public String getSwitchCode() {
        return this.switchCode;
    }

    /**
     * Returns the plug's house code
     *
     * @return a string representing a house code
     */
    public String getHouseCode() {
        return this.houseCode;
    }

    /**
     * Returns the plug's state (either "ON" or "OFF")
     *
     * @return the plug's state (either "ON" or "OFF")
     */
    public StateEnum getState() {
        return this.state;
    }
}
