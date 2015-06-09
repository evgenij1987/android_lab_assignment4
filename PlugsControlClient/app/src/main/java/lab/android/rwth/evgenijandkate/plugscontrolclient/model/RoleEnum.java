package lab.android.rwth.evgenijandkate.plugscontrolclient.model;

/**
 * Created by ekaterina on 04.06.2015.
 *
 * An enum representing the user's role. ADMIN stands for the user with an administrative role,
 * whereas USER states for the user with the limited rights.
 */
public enum RoleEnum {
    ADMIN("admin"), USER("user");

    private final String name;

    private RoleEnum(String name) {
        this.name = name;
    }

    /**
     * A public method for checking if the user with the specified role is
     * a user with administrative rights or not
     *
     * @param role a role of  the user which is being checked
     * @return true if the user has administrative rights
     */
    public static boolean isAdmin(String role) {
        return RoleEnum.ADMIN.name.equals(role);
    }
}
