package lab.android.rwth.evgenijandkate.plugscontrolclient.authorization;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

import lab.android.rwth.evgenijandkate.plugscontrolclient.R;

/**
 * Created by ekaterina on 08.06.2015.
 *
 * A super class for an activity which has a logout option
 * in its action bar's menu.
 */
public class ActivityWithLogoutMenu extends Activity {
    public static final int MENU_LOGOUT = Menu.FIRST;

    /**
     * This method adds the logout option to the standard menu
     * and returns true for the menu to be displayed.
     *
     * @param menu The options menu in which we place our items.
     * @return true for the menu to be displayed.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, MENU_LOGOUT, Menu.NONE, R.string.logout_menu_label);
        return true;
    }

    /**
     * Checks which option of the menu was selected. If the user picked
     * "Log out", then LogInFragment.performLogout is called.
     *
     * @param item a menu item which was selected
     * @return true if the "Log out" option was chosen, and the super's method result otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_LOGOUT:
                LogInFragment.performLogout(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
