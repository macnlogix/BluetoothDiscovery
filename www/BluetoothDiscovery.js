var pluginName = "BluetoothDiscovery";

let bluetoothDiscovery = {
    coolMethod: function(params, successCallback, errorCallback) {
        console.log("bluetoothDiscovery-----coolMethod------",successCallback,errorCallback, params);
        cordova.exec(successCallback, errorCallback, pluginName, "coolMethod", [params]);
    },
    isBluetoothActive: function(successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, pluginName, "isBluetoothActive", [params]);
    },
    enable: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, pluginName, "enable", []);
      },
    disable: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, pluginName, "disable", []);
    },
    getAdapterInfo: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, pluginName, "getAdapterInfo", []);
    },  
    startScan: function(successCallback, errorCallback, params) {
        cordova.exec(successCallback, errorCallback, pluginName, "startScan", [params]);
    },
    stopScan: function(successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, pluginName, "stopScan", []);
    },
}

// exports.coolMethod = function (arg0, success, error) {
//     exec(success, error, pluginName, 'coolMethod', [arg0]);
// };

module.exports = bluetoothDiscovery;

