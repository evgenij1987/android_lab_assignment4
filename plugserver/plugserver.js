/**
 * Created by evgenijavstein on 28/05/15.
 */
var express = require('express');
var app = express();
var PORT=3000;

//controller switching on plugs and holding state
plugController=require('./plugs.controller');
plugController.init();//reading all available plugs from config.yaml

//API to list all available plugs
app.get('/api/plugs',plugController.sendPlugList);

//API to turn on a plug by id served from /api/plugs e.g /api/plugs/turnOn/1
app.get('/api/plugs/turnON/:id', plugController.turnOnPlug);
//API to turn off a plug by id served from /api/plugs
app.get('/api/plugs/turnOFF/:id', plugController.turnOffPlug);

app.listen(PORT);

console.log("plugserver running on: "+PORT);

