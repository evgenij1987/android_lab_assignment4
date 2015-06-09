package lab.android.rwth.evgenijandkate.plugscontrolclient;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import lab.android.rwth.evgenijandkate.plugscontrolclient.model.IListItem;
import lab.android.rwth.evgenijandkate.plugscontrolclient.tasks.OnResponseListener;
import lab.android.rwth.evgenijandkate.plugscontrolclient.tasks.PlugsListGetRequest;

/**
 * Created by ekaterina on 04.06.2015.
 *
 * The fragment for holding the list displaying all available plugs.
 * If the user has administrative rights, he/she will be also presented with the
 * add and delete buttons, letting him/her add or remove plugs as well as change their state.
 * The user with the limited rights will only be allowed to change the plug's state between ON and OFF.
 */
public class PlugsControlFragment extends Fragment {
    private final static String FRAGMENT_TAG = "list_fragment";
    private PlugsListGetRequest getRequest;
    private PlugsListFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //retain state on configuration change (e.g. on orientation change)
        setRetainInstance(true);
        loadData();
    }

    private void loadData() {
        getRequest = new PlugsListGetRequest(getActivity());
        getRequest.setOnResponseListener(new OnResponseListener<List<IListItem>>() {

            @Override
            public void onResponse(List<IListItem> items) {
                FragmentManager fragmentManager = getFragmentManager();
                //fetch the fragment if it was saved (e.g. during orientation change)
                fragment = (PlugsListFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
                if (fragment == null) {
                    // add the fragment
                    fragment = PlugsListFragment.newInstance(items);
                    fragmentManager.beginTransaction().add(R.id.plugs_control_fragment_container, fragment, FRAGMENT_TAG).commit();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        getRequest.send();
    }

    /**
     * Creates the plug control view, a layout is just a container for the list displaying available plugs.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state.
     * @return a fragment's view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.plugs_control_fragment, container, false);
    }

    /**
     * Refreshes the list of available plugs.
     */
    public void refreshList() {
        FragmentManager fragmentManager = getFragmentManager();
        fragment = (PlugsListFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null) {
            // remove the fragment
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
        loadData();
    }
}
