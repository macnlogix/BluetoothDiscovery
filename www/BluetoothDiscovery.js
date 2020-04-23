var pluginName = "BluetoothDiscovery";

let bluetoothDiscovery = {
    coolMethod: function(successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, pluginName, "coolMethod", [params]);
    },
    isBluetoothActive: function(successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, pluginName, "isBluetoothActive", [params]);
    },
}

// exports.coolMethod = function (arg0, success, error) {
//     exec(success, error, pluginName, 'coolMethod', [arg0]);
// };

module.exports = bluetoothDiscovery;

