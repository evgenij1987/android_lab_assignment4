/**
 * Created by evgenijavstein on 31/05/15.
 */

//constructor
var WebSocketServer = require('ws').Server,
    userController = require('./../controller/user.controller'),
    https = require('https'),
    fs = require('fs');

//helper variable to be accessed from inner class
var webSocketServerController;

//messages to be accepted
var SUBSCRIBE = "subscribe";
var UNSUBSCRIBE = "unsubscribe";

//response messages
var SUCCESS_UNSUBSCRIBE = "success_unsubscribe";
var SUCCESS_SUBSCRIBE = "success_subscribe";
var NOT_AUTHENTICATED = "not authenticated!";

/**
 * Constructor, a new array for incoming connections is created and filled later by each
 * subscription for event.
 * @constructor
 */
function WebSocketServerController() {

    webSocketServerController = this;//workaround, otherwise  'this' is not accessible from ws.on callbacks
    this.authorisedClients = new Array();
}
/**
 * Listens on provided port, accepts web socket connections, if authorization header allows
 * to do so. Subscription is successful if user provided in header is authenticated and a SUBSCRIBE
 * is sent.Then the websocket will receive notifications.
 * @param port
 */
WebSocketServerController.prototype.listen = function (port) {
    //https server for websocket http handshake
    var app = https.createServer({

        // providing server with  SSL key/cert
        key: fs.readFileSync('key.pem'),
        cert: fs.readFileSync('cert.pem')

    } ).listen( port);

    this.webSocketServer = new WebSocketServer({server:app});
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
            ws.send(NOT_AUTHENTICATED);
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
 * Send a notification to all clients, that plug list has changed,
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
    //find authorization header
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