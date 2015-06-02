/**
 * Created by evgenijavstein on 01/06/15.
 */
// Model used to hide information, which client doesn't need to know, like plug code
// Constructor
function Plug(obj) {
    // always initialize all instance properties
    this.id = obj.id;
    this.name = obj.name;
    this.state = obj.state;
}


// export the class
module.exports = Plug;