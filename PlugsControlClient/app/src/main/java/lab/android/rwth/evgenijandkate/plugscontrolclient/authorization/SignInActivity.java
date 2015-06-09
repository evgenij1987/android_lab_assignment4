package lab.android.rwth.evgenijandkate.plugscontrolclient.authorization;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import lab.android.rwth.evgenijandkate.plugscontrolclient.R;

/**
 * Created by ekaterina on 07.06.2015.
 *
 * An activity which holds the LogInFragment to be shown to the user when he first launches the application
 * or logs out from one of its activities.
 */
public class SignInActivity extends FragmentActivity {
    private final static String FRAGMENT_TAG = "login_fragment";
    private LogInFragment loginFragment;

    /**
     * Creates or restores the LogInFragment instance if it was already created.
     *
     * @param savedInstanceState a bundle instance with the saved state before recreation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        FragmentManager fragmentManager = getFragmentManager();
        //fetch the fragment if it was saved (e.g. during orientation change)
        loginFragment = (LogInFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (loginFragment == null) {
            // add the fragment
            loginFragment = new LogInFragment();
            fragmentManager.beginTransaction().add(R.id.login_fragment_container, loginFragment, FRAGMENT_TAG).commit();
        }
    }
}