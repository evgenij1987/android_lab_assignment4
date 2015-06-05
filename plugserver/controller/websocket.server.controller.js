/**
 * Created by evgenijavstein on 31/05/15.
 */
var WebSocketServer = require('ws').Server;
var SUBSCRIBE = "subscribe";
var UNSUBSCRIBE = "unsubscribe";
var SUCCESS_UNSUBSCRIBE = "success_unsubscribe";
var SUCCESS_SUBSCRIBE = "success_subscribe";
var NOT_AUTHENTICATED = "not authenticated!";
var userController = require('./../controller/user.controller');
var webSocketServerController;
function WebSocketServerController() {

    webSocketServerController = this;//workaround, otherwise  'this' is not accessible from ws.on callbacks
    this.authorisedClients = new Array();
}

WebSocketServerController.prototype.listen = function (port) {

    this.webSocketServer = new WebSocketServer({port: port});
    this.webSocketServer.on('connection', function (ws) {

        //reject if no attempt too authenticate with http basic
        //authenticate user with provided credentials in authorization header
        var credentials = getCredentials(ws.upgradeReq);
        if (credentials && userController.authenticateUser(credentials[0], credentials[1])) {
            ws.on('message', function (message) {


                //if authorised and asking for subscription add to list
                if (message == SUBSCRIBE) {
                    webSocketServerController.authorisedClients.push(ws);
                    ws.send(SUCCESS_SUBSCRIBE);
                }
                //close connection if unsubscribe request, connection is then kicked out in 'close' event handler
                else if (message == UNSUBSCRIBE) {
                    ws.send(SUCCESS_UNSUBSCRIBE);
                    ws.close();
                } else {
                    ws.close();
                }


            });
        } else { //reject if not authenticated connection
            ws.send("connection not authenticated");
            ws.close();
        }


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

/**
 * Helper method to parse the basic http authorization header to credentials
 * @param req
 * @returns {*}
 */
function getCredentials(req) {
    var authorization = req.headers['authorization'];
    if (!authorization) {
        return null;
    }

    var parts = authorization.split(' ')
    if (parts.length < 2) {
        return null;
    }

    var scheme = parts[0]
        , credentials = new Buffer(parts[1], 'base64').toString().split(':');

    if (!/Basic/i.test(scheme)) {
        return null;
    }
    if (credentials.length < 2) {
        return null;
    }

    return credentials;
}

module.exports = WebSocketServerController;