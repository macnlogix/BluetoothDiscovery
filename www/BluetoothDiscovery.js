var exec = require('cordova/exec');
var pluginName = "BluetoothDiscovery";

let bluetoothDiscovery = {
    coolMethod: function(successCallback, errorCallback, params) {
        exec(successCallback, errorCallback, pluginName, "coolMethod", [params]);
    },
}

// exports.coolMethod = function (arg0, success, error) {
//     exec(success, error, pluginName, 'coolMethod', [arg0]);
// };

module.exports = bluetoothDiscovery;

