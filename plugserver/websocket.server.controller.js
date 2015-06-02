/**
 * Created by evgenijavstein on 31/05/15.
 */
var WebSocketServer = require('ws').Server;
var SUBSCRIBE="subscribe";
var UNSUBSCRIBE="unsubscribe";
var SUCCESS_UNSUBSCRIBE="success_unsubscribe";
var SUCCESS_SUBSCRIBE="success_subscribe";

var webSocketServerController;
function WebSocketServerController() {

    webSocketServerController = this;//workaround, otherwise  'this' is not accessible from ws.on callbacks
    this.authorisedClients = new Array();
}

WebSocketServerController.prototype.listen = function (port, debug) {

    this.webSocketServer = new WebSocketServer({port: port});
    this.webSocketServer.on('connection', function (ws) {

        ws.on('message', function (message) {
            //reject if not authorised connection
            if (ws.upgradeReq.headers['auth'] != '123' ) //TODO: replace with auth header, which is received onlogin
                ws.close();
            //if authorised and asking for subscription add to list
            if (message == SUBSCRIBE) {
                webSocketServerController.authorisedClients.push(ws);
                ws.send(SUCCESS_SUBSCRIBE);
            }
            //close connection if unsubscribe request, connection is then kicked out in 'close' event handler
            else if (message == UNSUBSCRIBE) {
                ws.send(SUCCESS_UNSUBSCRIBE);
                ws.close();
            }else{
                ws.close();
            }


        });
        ws.on('close', function () {//Called when socket is closed, no matter from client or server
            //kick out clients closing sockets or clients who just were closed because of UNSUBSCRIBE
            var index = webSocketServerController.authorisedClients.indexOf(ws);
            if (index >= 0)
                webSocketServerController.authorisedClients.splice(index, 1);

        });

    });
}


/**
 * Send a notification to all clients, that plugslist has changed,
 * clients need to get the new version themselves via /api/plugs
 */
WebSocketServerController.prototype.notifyAll = function (req, res) {

    for (var i = 0; i < this.authorisedClients.length; i++) {
        //send plug with changed state to all clients
        this.authorisedClients[i].send(JSON.stringify(req.plug));
    }
}

module.exports = WebSocketServerController;