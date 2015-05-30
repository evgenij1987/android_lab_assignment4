/**
 * Created by evgenijavstein on 28/05/15.
 */

var configPlugsList;
var child;
var PLUG_ON="ON";
var PLUG_OFF="OFF";
var ACTION_ON="10";
var ACTION_OFF="01";
/**
 * Reads config.yaml file
 * Plugs are stored in configPlugsList. Initially all plugs are marked
 * as OFF. The server keeps state of plugs, when plugs are turned off/on.
 */
exports.init=function(){
    YAML = require('yamljs');
    // Load yaml file using YAML.load
    var nativeObj= YAML.load('config.yaml');
    configPlugsList=nativeObj.wireless_plugs;
    for(i=0;i<configPlugsList.length;i++){
        configPlugsList[i].state="OFF";//initially all plugs are marked as OFF
    }
    runRadioTransmitter();
    child.stdout.on('data', function (buffer) {
        console.log(buffer.toString("utf-8")) });
    }
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
        child.stdin.write(plug.house_code+plug.switch_code+ACTION_ON);
        res.sendStatus(200);
    }else{
        res.sendStatus(500);
    }

}


exports.turnOffPlug=function(req, res){
    var turnOffId=req.params.id;
    var plug=updateConfigPlugsList(turnOffId,PLUG_OFF);

    if(plug){
        child.stdin.write(plug.house_code+plug.switch_code+ACTION_OFF);
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
function runRadioTransmitter(house_code, switch_code, action){


    var exec = require('child_process').spawn;

    //var command="sudo ./../rspimodulator/rspimodulator "+house_code+switch_code+action;
    //var command="echo ./../rspimodulator/rspimodulator "+house_code+switch_code+action;
    //var command="cat";
    var command="sudo ./../rspimodulator/rspimodulator";
    child=exec(command, function (error, stdout, stderr) {

        console.log('stdout: ' + stdout);

        console.log('stderr: ' + stderr);

        if (error !== null) {

            console.log('exec error: ' + error);

        }

    });
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