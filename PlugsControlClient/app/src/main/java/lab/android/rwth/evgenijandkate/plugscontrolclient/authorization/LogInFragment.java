package lab.android.rwth.evgenijandkate.plugscontrolclient.authorization;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import lab.android.rwth.evgenijandkate.plugscontrolclient.PlugsControlActivity;
import lab.android.rwth.evgenijandkate.plugscontrolclient.R;
import lab.android.rwth.evgenijandkate.plugscontrolclient.model.User;
import lab.android.rwth.evgenijandkate.plugscontrolclient.tasks.CheckUserTask;
import lab.android.rwth.evgenijandkate.plugscontrolclient.tasks.OnResponseListener;

/**
 * Created by ekaterina on 07.06.2015.
 *
 * A fragment which will be initially shown to the user for him to log in.
 * The logged in information is then stored in the preferences, e.g. his user name,
 * password, ip he is connecting to and the port, if the user has administrative rights,
 * all the checks are done through http get tasks and via authenticating api.
 */
public class LogInFragment extends Fragment {
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String login = "loginKey";
    public static final String password = "passwordKey";
    public static final String ip = "ipKey";
    public static final String port = "portKey";
    public static final String isAdmin = "isAdmin";
    private static final String TAG = "plug_login_tag";
    private static SharedPreferences sharedpreferences;

    private Button loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    /**
     * A log in screen will be inflated displaying the entries for user logging name, password,
     * ip address and port number of the host he is about to connect to and a login button,
     * which has an onClick listener contacting the authorising api via async task.
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
        final View fragmentView = inflater.inflate(R.layout.login_fragment, container, false);
        this.loginButton = (Button) fragmentView.findViewById(R.id.login_button);
        this.loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attemptToLogin(fragmentView.findViewById(R.id.username_input), fragmentView.findViewById(R.id.password_input),
                                fragmentView.findViewById(R.id.ip_address), fragmentView.findViewById(R.id.port_number));
                    }
                }
        );
        return fragmentView;
    }

    private void attemptToLogin(View userAccountNameInput, View passwordInput, View ipInput, View portInput) {
        if (userAccountNameInput instanceof EditText && passwordInput instanceof EditText &&
                ipInput instanceof EditText && portInput instanceof EditText) {
            final String accountName = ((EditText) userAccountNameInput).getText().toString();
            final String passwordValue = ((EditText) passwordInput).getText().toString();
            final String ipValue = ((EditText) ipInput).getText().toString();
            final String portValue = ((EditText) portInput).getText().toString();

            CheckUserTask checkUserTask = new CheckUserTask(getActivity());
            checkUserTask.setOnResponseListener(new OnResponseListener<User>() {

                @Override
                public void onResponse(User connectedUser) {
                    if (connectedUser != null) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(login, connectedUser.getUserAccountName());
                        editor.putString(password, connectedUser.getPassword());
                        editor.putString(ip, connectedUser.getIpValue());
                        editor.putString(port, connectedUser.getPortValue());
                        editor.putBoolean(isAdmin, connectedUser.isAdmin());
                        editor.commit();

                        Intent plugsControlActivityIntent = new Intent(getActivity(), PlugsControlActivity.class);
                        startActivity(plugsControlActivityIntent);
                    } else {
                        onError(getResources().getString(R.string.failed_to_login_message));
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e(TAG, errorMessage);
                }
            });
            checkUserTask.send(new User.UserBuilder(accountName, passwordValue).ipAddress(ipValue).portAddress(portValue).build());
        }
    }

    /**
     * Encodes the user's logging name and password as a base64 string to be sent to the server.
     *
     * @param login a user's logging name
     * @param pass  a user's password
     * @return the encoded string, containing user's logging name and password as a base64 string
     */
    public static String getB64Auth(String login, String pass) {
        String source = login + ":" + pass;
        String ret = "Basic " + Base64.encodeToString(source.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
        return ret;
    }

    /**
     * Retrieves the connected user from the preferences file, where the logged in information is stored,
     * e.g. his user name, password, ip he is connecting to, the port and if the user has administrative rights or not.
     *
     * @return a connected user or null if no user is connected
     */
    public static User getConnectedUser() {
        if (sharedpreferences != null) {
            String pass = sharedpreferences.getString(password, "");
            String userLogin = sharedpreferences.getString(login, "");
            String ipValue = sharedpreferences.getString(ip, "");
            String hostValue = sharedpreferences.getString(port, "");
            boolean userIsAdmin = sharedpreferences.getBoolean(isAdmin, false);

            return new User.UserBuilder(userLogin, pass).ipAddress(ipValue).portAddress(hostValue).isAdmin(userIsAdmin).build();
        }
        return null;
    }

    /**
     * Logges out the user from the system by removing his session information
     * from the preference file. The user then is redirected to the initial log in page.
     *
     * @param currentActivity the activity from which action bar the user picked a log out option.
     */
    public static void performLogout(Activity currentActivity) {
        if (sharedpreferences != null) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();

            //redirect to the login screen
            Intent redirectToLoginIntent = new Intent(currentActivity, SignInActivity.class);
            currentActivity.startActivity(redirectToLoginIntent);
            currentActivity.finish();
        }
    }
}