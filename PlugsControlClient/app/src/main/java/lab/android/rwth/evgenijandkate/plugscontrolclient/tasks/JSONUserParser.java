package lab.android.rwth.evgenijandkate.plugscontrolclient.tasks;

import org.json.JSONException;
import org.json.JSONObject;

import lab.android.rwth.evgenijandkate.plugscontrolclient.model.RoleEnum;
import lab.android.rwth.evgenijandkate.plugscontrolclient.model.User;

/**
 * Created by ekaterina on 08.06.2015.
 *
 * A parser of the json data which represents the authenticated user being sent from the server.
 * A json object has a first name, a last name and a role of the user (ADMIN or limited USER).
 */
public class JSONUserParser {
    private static final String FIRST_NAME_PROPERTY_NAME = "first_name";
    private static final String LAST_NAME_PROPERTY_NAME = "last_name";
    private static final String ROLE_PROPERTY_NAME = "role";

    /**
     * Constructs an instance of the logged in user from the json object
     *
     * @param jsonString    a json string with the user's data
     * @param loggingInUser the user object which is about to log in
     * @return an instance of the logged in User
     * @throws JSONException
     */
    public static User parse(String jsonString, User loggingInUser) throws JSONException {
        JSONObject userJSONObject = new JSONObject(jsonString);
        String userFirstName = userJSONObject.getString(FIRST_NAME_PROPERTY_NAME);
        String userLastName = userJSONObject.getString(LAST_NAME_PROPERTY_NAME);
        boolean isAdmin = RoleEnum.isAdmin(userJSONObject.getString(ROLE_PROPERTY_NAME));
        return new User.UserBuilder(loggingInUser).firstName(userFirstName).lastName(userLastName).isAdmin(isAdmin).build();
    }
}
