/**
 * Created by evgenijavstein on 28/05/15.
 */
var Plug = require("./../model/plug.js"),
    validator = require('node-validator');


var fileHelper = require('./filehelper');
var transmitterNativeProcess;

//plug states
var PLUG_ON = "ON";
var PLUG_OFF = "OFF";

//End of sequence to turn on/off
var ACTION_ON = "10";
var ACTION_OFF = "01";

var MESSAGE_NO_SUCH_PLUG = "No such plug found!"

var debugMode;
var config;
var YAML;

//if DEBUG_DEV_MACHINE is chonse in init() the sequence is not modulated but piped to cat tool
exports.DEBUG_DEV_MACHINE = 0;
exports.DEBUG_RASPBERRY_PI = 1;


/**
 *  Sets debug mode: If set DEBUG_DEV_MACHINE transmission binary is not accessed.
 *  Instead the transmitted sequence is put to stdout via cat child process.
 *  If set DEBUG_RASPBERRY_PI the binary is accessed the plug code + ACTION are piped to
 *  binary. Transmission process is started only once here. And used via piping for each turnOn/turnOff
 *  request
 *  Reads config.yaml file
 *  Plugs are stored in configPlugsList.
 *  The server keeps state of plugs, when plugs are turned off/on in config.yaml
 * @param mode
 */
exports.init = function (mode) {

    debugMode = mode;
    YAML = require('yamljs');
    // Load yaml file using YAML.load
    config = YAML.load("config.yaml");
    //configPlugsList = nativeObj.wireless_plugs;

    //RUN child process for radio transmission/cat if DEBUG_RASPBERRY_PI/DEBUG_DEV_MACHINE
    transmitterNativeProcess = runRadioTransmitter();

};
/**
 * Send the list of plugs to client: each item contains only
 * id, name. Clients just refer the id to turn a plug on/off.
 * House & switch code stays internal.
 */
exports.sendPlugList = function (req, res) {
    //array holding plugs without plug code
    var plugList = new Array();
    var plug;

    for (i = 0; i < config.wireless_plugs.length; i++) {

        //Plug object just contains name & id to
        //client just refers the id to turnOn/turnOff plug
        plug = new Plug(config.wireless_plugs[i]);
        plugList.push(plug);
    }

    res.send(plugList);
}
/**
 * Turns on a plug using ACTION_OFF at the end of piped sequence
 * @param req
 * @param res
 * @param next
 */
exports.turnOnPlug = function (req, res, next) {
    switchPlug(req, res, ACTION_ON, next);

}

/**
 * Turns off a plug using ACTION_OFF at the end of piped sequence
 * @param req
 * @param res
 * @param next
 */
exports.turnOffPlug = function (req, res, next) {
    switchPlug(req, res, ACTION_OFF, next);
}

/**
 * Adds a plug if data provided is valid
 * @param req
 * @param res
 */
exports.addPlug = function (req, res) {

    var check = validator.isObject()
        .withRequired('name', validator.isString({regex: /^[a-z ,.'-]+$/i}))
        .withRequired('house_code', validator.isString({regex: /^(0|1){5}$/}))
        .withRequired('switch_code', validator.isString({regex: /^(0|1){5}$/}))
        .withRequired('state', validator.isString({regex: /(ON|OFF)$/}));

    var newPlugConfig = {};
    newPlugConfig.name = req.body.name;
    newPlugConfig.house_code = req.body.house_code;
    newPlugConfig.switch_code = req.body.switch_code;
    newPlugConfig.state = req.body.state;


    validator.run(check, newPlugConfig, function (errorCount, errors) {
        if (errorCount == 0) {
            //add new plug
            config.wireless_plugs.push(newPlugConfig);
            var newPlugId = config.wireless_plugs.indexOf(newPlugConfig) + 1;
            newPlugConfig.id = newPlugId;

            //persist state
            fileHelper.saveFile(YAML.stringify(config, 4), "/../config.yaml");
            res.send(new Plug(newPlugConfig));
        } else {
            res.send(errors);
        }

    })


}
/**
 * Removes plug or if not found sends 400
 * @param req
 * @param res
 */
exports.removePlug = function (req, res) {
    var plugId = req.params.id;
    var deletedPlugConf = removePlugById(plugId);
    if (deletedPlugConf)
        res.send(new Plug(deletedPlugConf));//send deleted plug as ack of deletion
    else
        res.status(400).send({message: MESSAGE_NO_SUCH_PLUG});//send bad request if no obj with such id could be deleted
}
/**
 * Switches a plug by writing to unix pipe of the process which is started in runRadioTransmitter()-method
 * @param req
 * @param res
 * @param action
 * @param next
 */
function switchPlug(req, res, action, next) {
    //plugId from GET request
    var plugId = req.params.id;
    //new state according to turnOn/turnOff request
    var newState = action == ACTION_ON ? PLUG_ON : PLUG_OFF;
    //update config list to new state
    var plugConfig = updateConfigPlugsList(plugId, newState);

    if (plugConfig) {
        ////pipe stream to turnoff on the plug via shell
        transmitterNativeProcess.stdin.write(plugConfig.house_code + plugConfig.switch_code + action);
        //Plug obj contains only data which the client should know, not more
        var plug = new Plug(plugConfig);
        req.plug = plug;//plug obj is appended on request obj and next next middle ware can access it there
        next();//run next middleware (next callback in chain)


    } else {
        res.status(400).send({message: MESSAGE_NO_SUCH_PLUG});
    }

}

/**
 * Runs binary via shell command, which modulates the signal corresponding to the plug code and action
 * as a pulse width modulation. The sequence to be modulated is read from pipe (stdin).

 */
function runRadioTransmitter() {


    var spawn = require('child_process').spawn;
    //only for debug mode without raspberry since it accepts a unix pipe,too
    var command = "cat";

    if (debugMode == exports.DEBUG_RASPBERRY_PI) {
        //run binary command
        command = "./../rspimodulator/rspimodulator";//to make it work you need to start server from sudo, due to GPIO access
    }

    //process is started only once here and used via pipe again and again
    var child = spawn(command, []);
    child.stdout.on('data',
        function (buffer) {
            console.log(buffer.toString("utf-8"))
        }
    );
    child.stderr.on('data',
        function (data) {
            console.log('err data: ' + data);
        }
    );
    return child;
}
/**
 * Method looks up inside configPlugs array and marks plug referred by
 * passed recID as state. Returns updated plug item.
 * @param recId
 * @param state
 * @return plug
 */
function updateConfigPlugsList(recId, state) {

    var foundPlug = findPlugById(recId);
    if (foundPlug) {
        foundPlug.state = state;
        fileHelper.saveFile(YAML.stringify(config, 4), "/../config.yaml");
        return foundPlug;
    }

}
/**
 * Remove a plug if one found with this id
 * @param recId
 * @returns {Plug|exports|module.exports}
 */
function removePlugById(recId) {

    var foundPlug = findPlugById(recId);
    if (foundPlug) {
        var index = config.wireless_plugs.indexOf(foundPlug);
        var deletedPlug = new Plug(config.wireless_plugs[i]);
        config.wireless_plugs.splice(index, 1);
        //persist current state in config file
        fileHelper.saveFile(YAML.stringify(config, 4), "/../config.yaml");
        return deletedPlug;
    }

}

/**
 * Find plug by id
 * @param recId
 * @returns {*}
 */
function findPlugById(recId) {
    for (i = 0; i < config.wireless_plugs.length; i++) {
        if (config.wireless_plugs[i].id == recId) {

            return config.wireless_plugs[i];
        }

    }
}

