/**
 * Created by evgenijavstein on 01/06/15.
 */

// Constructor
function Plug(obj) {
    // always initialize all instance properties
    this.id = obj.id;
    this.name = obj.name;
    this.state = obj.state;
}


// export the class
module.exports = Plug;