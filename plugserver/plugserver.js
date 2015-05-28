/**
 * Created by evgenijavstein on 28/05/15.
 */
var express = require('express');
var app = express();
var PORT=3000;
app.get('/api', function(request, response) {
    //response.send({name:"Evgenij Avstein",age:27});

    runShellCommand();
    response.sendStatus(200);
});
app.listen(PORT);
console.log("plugserver running on: "+PORT);

function turnOnLamp(){


    var exec = require('child_process').exec;


    exec("ls -ali", function (error, stdout, stderr) {

        console.log('stdout: ' + stdout);

        console.log('stderr: ' + stderr);

        if (error !== null) {

            console.log('exec error: ' + error);

        }

    });
}