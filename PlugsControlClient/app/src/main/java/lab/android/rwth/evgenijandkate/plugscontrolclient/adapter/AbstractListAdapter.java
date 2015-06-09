package lab.android.rwth.evgenijandkate.plugscontrolclient.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lab.android.rwth.evgenijandkate.plugscontrolclient.model.IListItem;

/**
 * Created by ekaterina on 04.06.2015.
 *
 * An abstract class to represent the list adapter. A list adapter is used by the list fragments,
 * for example, a list of available plugs. The entities of type T are being stored in the collection
 * and maintained via public methods, e.g. add, removeAll, etc. Further on, an instance of AbstractListAdapter will
 * be set as an adapter for a list view (see PlugsListFragment).
 */
public abstract class AbstractListAdapter<T extends Serializable> extends BaseAdapter {
    protected final List<T> items = new ArrayList<T>();
    protected final Context context;

    /**
     * A public constructor to be overridden by the subclasses. Constructs an adapter for a list view.
     *
     * @param context an instance of the context specifying the according activity.
     */
    public AbstractListAdapter(Context context) {
        this.context = context;
    }

    /**
     * Adds the item into a list of items stored in adapter
     *
     * @param item a serializable item to be added to an adapter items
     */
    public void add(T item) {
        items.add(item);
        notifyDataSetChanged();
    }

    /**
     * Is used to return the number of items kept in the adapter's list.
     *
     * @return a size of the adapter items collection.
     */
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public T getItem(int pos) {
        return items.get(pos);
    }

    /**
     * A method for getting an adapter's item id.
     *
     * @param position an item's position in adapter.
     * @return the item's id.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Returns the items collection of this adapter.
     *
     * @return the list of items in adapter.
     */
    public List<T> getItems() {
        return items;
    }

    /**
     * Removes all the items passed as a parameter from the adapter's items collection.
     *
     * @param itemsToDelete a collection of items to be removed from this adapter.
     */
    public void removeAll(List<T> itemsToDelete) {
        items.removeAll(itemsToDelete);
        notifyDataSetChanged();
    }
}
