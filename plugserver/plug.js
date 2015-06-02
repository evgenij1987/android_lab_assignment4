/**
 * Created by evgenijavstein on 01/06/15.
 */
// Constructor
function Plug(id, name, state) {
    // always initialize all instance properties
    this.id=id;
    this.name=name;
    this.state=state;
}
// class methods

// export the class
module.exports = Plug;