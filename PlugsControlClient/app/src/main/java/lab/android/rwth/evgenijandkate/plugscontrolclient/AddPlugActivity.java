package lab.android.rwth.evgenijandkate.plugscontrolclient;

import android.app.FragmentManager;
import android.os.Bundle;

import lab.android.rwth.evgenijandkate.plugscontrolclient.authorization.ActivityWithLogoutMenu;

/**
 * Created by ekaterina on 07.06.2015.
 *
 * An activity holding the fragment for adding a new plug.
 * This activity will be started only if the user has administrative rights.
 */
public class AddPlugActivity extends ActivityWithLogoutMenu {
    private final static String FRAGMENT_TAG = "add";
    private AddPlugFragment addPlugFragment;

    /**
     * Creates or restores the AddPlugFragment instance if it was already created.
     *
     * @param savedInstanceState a bundle instance with the saved state before recreation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_plug_activity);

        FragmentManager fragmentManager = getFragmentManager();
        //fetch the fragment if it was saved (e.g. during orientation change)
        addPlugFragment = (AddPlugFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (addPlugFragment == null) {
            // add the fragment
            addPlugFragment = new AddPlugFragment();
            fragmentManager.beginTransaction().add(R.id.add_plug_fragment_container, addPlugFragment, FRAGMENT_TAG).commit();
        }
    }
}
