/**
 * Created by evgenijavstein on 28/05/15.
 */
var express = require('express');
var app = express();
var PORT=3000;
app.get('/api', function(request, response) {
    //response.send({name:"Evgenij Avstein",age:27});

    turnOnLamp();
    response.sendStatus(200);
});
app.listen(PORT);
console.log("plugserver running on: "+PORT);

function turnOnLamp(){


    var exec = require('child_process').exec;

	//start binary modulation module, turn on plug
    exec("sudo ./../rspimodulator/rspimodulator 100001000010", function (error, stdout, stderr) {

        console.log('stdout: ' + stdout);

        console.log('stderr: ' + stderr);

        if (error !== null) {

            console.log('exec error: ' + error);

        }

    });
}
