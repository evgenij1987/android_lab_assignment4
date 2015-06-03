/**
 * Created by evgenijavstein on 03/06/15.
 */
var path = require('path');
var fs = require('fs');
/**
 * Helper for saving an obj or string to file
 * @param data
 * @param filename
 */
exports.saveFile=function (data, filename) {
    if (typeof data !== "string") data = JSON.stringify(data);
    var file = path.join(__dirname, './', filename);
    fs.writeFile(file, data);
};