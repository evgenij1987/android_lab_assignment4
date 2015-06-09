package lab.android.rwth.evgenijandkate.plugscontrolclient.model;

/**
 * Created by ekaterina on 04.06.2015.
 *
 * An implementation of IListItem.java. The PlugItem is being used as a type of items in the
 * plugs list adapter set for the list view, from which the user can manage the plugs.
 * All the methods are implemented from an interface IListItem.java.
 */
public class PlugItem implements IListItem {
    private final int id;
    private String name;
    private StateEnum state;
    private boolean isChecked = false;

    public PlugItem(int id, String name, StateEnum state) {
        this.id = id;
        this.name = name;
        this.state = state;
    }

    @Override
    public int getListItemId() {
        return this.id;
    }

    @Override
    public String getListItemLabel() {
        return this.name;
    }

    @Override
    public StateEnum getState() {
        return this.state;
    }

    @Override
    public void setState(StateEnum stateEnum) {
        this.state = stateEnum;
    }

    @Override
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public boolean isChecked() {
        return this.isChecked;
    }
}
