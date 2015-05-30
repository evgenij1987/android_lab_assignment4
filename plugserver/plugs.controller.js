/**
 * Created by evgenijavstein on 28/05/15.
 */

var configPlugsList;
var transmitterNativeProcess;
var PLUG_ON="ON";
var PLUG_OFF="OFF";
var ACTION_ON="10";
var ACTION_OFF="01";

var debugMode;


exports.DEBUG_DEV_MACHINE=0;
exports.DEBUG_RASPBERRY_PI=1;



/**
 * - Sets debug mode:If set DEBUG_DEV_MACHINE transmission binary is not accessed.
 *  Instead the transmissed sequence is put to stdout via cat child process.
 *  If set DEBUG_RASPBERRY_PI the binary is accessed the plug code + ACTION are piped to
 *  binary. Transmission process is started only once here. And used via piping for each turnOn/turnOff
 *  request
 * - Reads config.yaml file
 *  Plugs are stored in configPlugsList. Initially all plugs are marked
 *  as OFF. The server keeps state of plugs, when plugs are turned off/on.
 * @param mode
 */
exports.init=function(mode) {
    debugMode=mode;
    YAML = require('yamljs');
    // Load yaml file using YAML.load
    var nativeObj = YAML.load('config.yaml');
    configPlugsList = nativeObj.wireless_plugs;
    for (i = 0; i < configPlugsList.length; i++) {
        configPlugsList[i].state = "OFF";//initially all plugs are marked as OFF
    }

    //RUN child process for transmission/cat if DEBUG_RASPBERRY_PI/DEBUG_DEV_MACHINE
    transmitterNativeProcess=runRadioTransmitter();

};
/**
 * Send the list of plugs to client: each item contains only
 * id, name. Clients just refer the id to turn a plug on/off.
 * House & switch code stays internal.
 */
exports.sendPlugList=function(req, res){
    //array holding plugs without plug code
    var clientPlugs=new Array();
    var item;
   for( i=0;i<configPlugsList.length;i++){

       //just send name & id to client
       //client just refers the id
       item={};
       item.id=configPlugsList[i].id;
       item.name=configPlugsList[i].name;
       item.state=configPlugsList[i].state;
       clientPlugs.push(item);
   }

    res.send(clientPlugs);
}

exports.turnOnPlug=function(req, res){
    var turnOnID= req.params.id;
    //update plug list, get updated element

    var plug=updateConfigPlugsList(turnOnID,PLUG_ON);
    if(plug){
        //run binary rspimodulator to turn on the plug via shell
        //runRadioTransmitter(plug.house_code, plug.switch_code, ACTION_ON);
        transmitterNativeProcess.stdin.write(plug.house_code+plug.switch_code+ACTION_ON);
        res.sendStatus(200);
    }else{
        res.sendStatus(500);
    }

}


exports.turnOffPlug=function(req, res){
    var turnOffId=req.params.id;
    var plug=updateConfigPlugsList(turnOffId,PLUG_OFF);

    if(plug){
        transmitterNativeProcess.stdin.write(plug.house_code+plug.switch_code+ACTION_OFF);
        //runRadioTransmitter(plug.house_code, plug.switch_code, ACTION_OFF)
    }else{
        res.sendStatus(500);
    }

    res.sendStatus(200);
}
/**
 * Runs binary via shell command, which modulates the signal corresponding to the plug code and action
 * as a pulse width modulation.
 * @param house_code
 * @param switch_code
 * @param action
 */
function runRadioTransmitter(house_code, switch_code, action) {


    var spawn = require('child_process').spawn;

    var command = "cat";
    if(debugMode==exports.DEBUG_RASPBERRY_PI) {
        command="./../rspimodulator/rspimodulator";//to make it work you need to start server from sudo, due to GPIO access
    }



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
function updateConfigPlugsList(recId, state){

    for(i=0;i<configPlugsList.length;i++){
        if(configPlugsList[i].id==recId){
            configPlugsList[i].state=state;
           return configPlugsList[i];
        }

    }
}
