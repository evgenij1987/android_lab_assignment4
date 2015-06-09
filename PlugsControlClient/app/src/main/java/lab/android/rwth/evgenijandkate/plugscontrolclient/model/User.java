package lab.android.rwth.evgenijandkate.plugscontrolclient.model;

import java.io.Serializable;

/**
 * Created by ekaterina on 07.06.2015.
 *
 * A model class for the user entity. The user's logging name, password, ip address and the port value of the
 * host he intends to reach, his first name and last name and whether he has administrative rights or not is
 * being stored. In order to set all these values a builder pattern is used.
 */
public class User implements Serializable {
    private String userAccountName;
    private String password;
    private String ipValue;
    private String portValue;
    private String firstName;
    private String lastName;
    private boolean isAdmin = false;

    private User(UserBuilder builder) {
        this.userAccountName = builder.nestedUserAccountName;
        this.password = builder.nestedPassword;
        this.ipValue = builder.nestedIpValue;
        this.portValue = builder.nestedPortValue;
        this.isAdmin = builder.nestedIsAdmin;
    }

    /**
     * Returns the user's account name
     *
     * @return the user's account name
     */
    public String getUserAccountName() {
        return this.userAccountName;
    }

    /**
     * Returns the user's password
     *
     * @return the user's password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Returns the ip address of the host the user intends to reach
     *
     * @return the ip address of the host the user intends to reach
     */
    public String getIpValue() {
        return this.ipValue;
    }

    /**
     * Returns the port value of the host the user intends to reach
     *
     * @return the port value of the host the user intends to reach
     */
    public String getPortValue() {
        return this.portValue;
    }

    /**
     * Returns true if the user has administrative rights and false otherwise
     *
     * @return true if the user has administrative rights and false otherwise
     */
    public boolean isAdmin() {
        return this.isAdmin;
    }

    /**
     * A builder pattern class through which the user's data is being built.
     */
    public static class UserBuilder {
        private String nestedUserAccountName;
        private String nestedPassword;
        private String nestedIpValue;
        private String nestedPortValue;
        private String nestedFirstName;
        private String nestedLastName;
        private boolean nestedIsAdmin;

        public UserBuilder(String accountName, String nestedPassword) {
            this.nestedUserAccountName = accountName;
            this.nestedPassword = nestedPassword;
        }

        public UserBuilder(User user) {
            this.nestedUserAccountName = user.userAccountName;
            this.nestedPassword = user.password;
            this.nestedIpValue = user.ipValue;
            this.nestedPortValue = user.portValue;
            this.nestedFirstName = user.firstName;
            this.nestedLastName = user.lastName;
            this.nestedIsAdmin = user.isAdmin;
        }

        /**
         * Sets the ip address of the host the user intends to reach
         *
         * @param ipAddress the ip address of the host the user intends to reach
         * @return the current instance of the builder
         */
        public UserBuilder ipAddress(String ipAddress) {
            this.nestedIpValue = ipAddress;
            return this;
        }

        /**
         * Sets the port value of the host the user intends to reach
         *
         * @param port the port value of the host the user intends to reach
         * @return the current instance of the builder
         */
        public UserBuilder portAddress(String port) {
            this.nestedPortValue = port;
            return this;
        }

        /**
         * Sets the first name of the logging in user
         *
         * @param firstName the first name of the logging in user
         * @return the current instance of the builder
         */
        public UserBuilder firstName(String firstName) {
            this.nestedFirstName = firstName;
            return this;
        }

        /**
         * Sets the last name of the logging in user
         *
         * @param lastName the last name of the logging in user
         * @return the current instance of the builder
         */
        public UserBuilder lastName(String lastName) {
            this.nestedLastName = lastName;
            return this;
        }

        /**
         * Sets isAdmin flag to true if the user has the administrative rights
         *
         * @param isAdmin true if the user has the administrative rights
         * @return the current instance of the builder
         */
        public UserBuilder isAdmin(boolean isAdmin) {
            this.nestedIsAdmin = isAdmin;
            return this;
        }

        /**
         * Builds the instance of the user
         *
         * @return the built user entity
         */
        public User build() {
            return new User(this);
        }
    }
}