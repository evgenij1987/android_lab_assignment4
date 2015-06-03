/**
 * Created by evgenijavstein on 01/06/15.
 */

// Constructor
function User(obj) {
    // always initialize all instance properties
    this.login = obj.login;
    this.first_name = obj.first_name;
    this.last_name = obj.last_name;
    this.role=obj.role;
    this.password=obj.password;
    this.salt=obj.salt;
}


// export the class
module.exports = User;