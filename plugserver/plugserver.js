/**
 * Created by evgenijavstein on 28/05/15.
 */
var express = require('express');
var app = express();
var PORT=3000;
plugController=require('./plugs.controller');

plugController.init();//reading all available plugs from config.yaml

app.get('/api/plugs',plugController.sendPlugList);
app.get('/api/plugs/turnON/:id', plugController.turnOnPlug);
app.get('/api/plugs/turnOFF/:id', plugController.turnOffPlug);
app.listen(PORT);
console.log("plugserver running on: "+PORT);

