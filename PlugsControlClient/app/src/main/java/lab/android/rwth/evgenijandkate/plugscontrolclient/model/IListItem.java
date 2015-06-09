package lab.android.rwth.evgenijandkate.plugscontrolclient.model;

import java.io.Serializable;

/**
 * Created by ekaterina on 04.06.2015.
 *
 * An interface for generalizing the list view adapter's item.
 * The collection of this type is then used in the PlugsListAdapter.java.
 */
public interface IListItem extends Serializable {

    /**
     * Returns the id of the item (for instance, an id of the plug)
     *
     * @return the item's id
     */
    int getListItemId();

    /**
     * Returns the label which will be shown in the adapter's list view.
     *
     * @return the item's label
     */
    String getListItemLabel();

    /**
     * Returns the state of the item to be displayed with the switch (both the user with administrative rights
     * and the user with the limited rights can change the state of an item).
     *
     * @return the item's state
     */
    StateEnum getState();

    /**
     * Sets the state of the item to be displayed with the switch (both the user with administrative rights
     * and the user with the limited rights can change the state of an item).
     *
     * @param stateEnum the item's state
     */
    void setState(StateEnum stateEnum);

    /**
     * Sets this item's ischecked flag to be used in the list view for the state of a combo box
     *
     * @param isChecked true if the item is checked or false otherwise
     */
    void setChecked(boolean isChecked);

    /**
     * Returns this item's ischecked flag to be used in the list view for the state of a combo box
     *
     * @return true if the item is checked or false otherwise
     */
    boolean isChecked();
}
