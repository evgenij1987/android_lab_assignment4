/**
 * Created by evgenijavstein on 05/01/15.
 */
// Load required packages
var passport = require('passport');
var BasicStrategy = require('passport-http').BasicStrategy;
var userController=require('./../controller/user.controller');

passport.use(new BasicStrategy(
    function(username, password, callback) {


        var user=userController.findUser(username);
        if(!user){
            callback(null, false);
        }else{
            var result=userController.authenticateUser(username,password);
            if(result){
                return callback(null, user);
            }else{
                callback(null, false);
            }
        }
    }
));

exports.isAuthenticated = passport.authenticate('basic', { session : false });