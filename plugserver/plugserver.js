/**
 * Created by evgenijavstein on 28/05/15.
 */
var express = require('express');
var bodyParser = require('body-parser');
var passport = require('passport');

//passport with http basic authentication
var authController = require('./auth/passport');
var WebSocketServerController = require('./controller/websocket.server.controller.js');

var PORT = 3000;
var WEB_SOCKET_PORT = 3001;

var app = express();


//passport is used for authenticating
app.use(passport.initialize());

// parse application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({extended: false}))

// parse application/json
app.use(bodyParser.json())

//controller switching on plugs and holding state
var plugController = require('./controller/plugs.controller.js');
plugController.init(plugController.DEBUG_DEV_MACHINE);//reading all available plugs from config.yaml

var userController=require('./controller/user.controller');

//wrapper around websocket server to notifiy clients about plug switch change
var webSocketServerController = new WebSocketServerController();


app.route('/api/plugs')
    //API to list all available plugs
    .get(authController.isAuthenticated,plugController.sendPlugList)
    //API to add a new plug
    .post(authController.isAuthenticated,userController.isAdmin,plugController.addPlug);

//API to delete a plug by id
app.delete('/api/plugs/:id', authController.isAuthenticated,userController.isAdmin,plugController.removePlug);

//API to turn on a plug by id
//after plugController.turnOnPlug() is run, socketServerController.notifyAll notifies all clients via web sockets about change
app.get('/api/plugs/turnON/:id', authController.isAuthenticated,plugController.turnOnPlug,
    function (req, res) {
        //req contains plug which was switched, notify all about the plug switched
        webSocketServerController.notifyAll(req, res);
        res.sendStatus(200);
    }
);
//API to turn off a plug by id
//notify all clients via web sockets about change, same as above
app.get('/api/plugs/turnOFF/:id', authController.isAuthenticated,plugController.turnOffPlug,
    function (req, res) {
        //req contains plug which was switched, notify all about the plug switched
        webSocketServerController.notifyAll(req, res);
        res.sendStatus(200);
    }
);


app.route('/api/users')
        //API to add a new user, can be done by admin user
        .post(authController.isAuthenticated,userController.isAdmin,userController.addUser)
        //API to remove an existing user by admin user
        .delete(authController.isAuthenticated,userController.isAdmin,userController.removeUser);




app.listen(PORT);
webSocketServerController.listen(WEB_SOCKET_PORT);


console.log("plugserver running on: " + PORT);
console.log("websocket notification server running on: " + WEB_SOCKET_PORT);
