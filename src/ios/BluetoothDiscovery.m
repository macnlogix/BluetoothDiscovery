/********* BluetoothDiscovery.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import "BluetoothManagerHandler.h"
#import "BluetoothDeviceHandler.h"
#import <CoreBluetooth/CoreBluetooth.h>


NSString *const keyError = @"error";
NSString *const keyMessage = @"message";
NSString *const keyName = @"name";
NSString *const keyAddress = @"address";
NSString *const keyIsEnabled = @"isEnabled";


NSString *const errorStartScan = @"startScan";
NSString *const logAlreadyScanning = @"Scanning already in progress";
NSString *const logNotScanning = @"Not scanning";

@interface BluetoothDiscovery : CDVPlugin {
    CBCentralManager *centralManager;
    BluetoothManagerHandler *bmH;
    NSMutableArray *devices;
    NSString* scanCallback;

}

- (void)coolMethod:(CDVInvokedUrlCommand*)command;
- (void)getAdapterInfo:(CDVInvokedUrlCommand*)command;
- (void)scanDevices:(CDVInvokedUrlCommand*)command;

@end

@implementation BluetoothDiscovery


- (void)coolMethod:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* echo = [command.arguments objectAtIndex:0];

    if (echo != nil && [echo length] > 0) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)getAdapterInfo:(CDVInvokedUrlCommand*)command
{
    if(centralManager == nil){
        centralManager = [[CBCentralManager alloc] initWithDelegate:nil queue:nil options:nil];
    }
    
    NSNumber* result = [NSNumber numberWithBool:(centralManager != nil && centralManager.state == CBManagerStatePoweredOn)];

    NSMutableDictionary* returnObj = [NSMutableDictionary dictionaryWithObjectsAndKeys: result, keyIsEnabled, nil];
    [returnObj setValue:@"iOS" forKey:keyName];
    [returnObj setValue:@"Privacy" forKey:keyAddress];
    
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:false];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)scanDevices:(CDVInvokedUrlCommand*)command
{
    
    if (scanCallback != nil) {
      NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: errorStartScan, keyError, logAlreadyScanning, keyMessage, nil];
      CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:returnObj];
      [pluginResult setKeepCallbackAsBool:false];
      [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
      return;
    }
    
    scanCallback = command.callbackId;
    [self performSelector:@selector(sendResponse) withObject:nil afterDelay:10.0];
//    NSDictionary* returnObj = [NSDictionary dictionaryWithObjectsAndKeys: statusScanStarted, keyStatus, nil];
//    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
//    [pluginResult setKeepCallbackAsBool:true];
//    [self.commandDelegate sendPluginResult:pluginResult callbackId:scanCallback];

    if (devices == nil) {
        devices = [[NSMutableArray alloc] init];
    }
    [[NSNotificationCenter defaultCenter] addObserver:self
    selector:@selector(receiveTestNotification:)
    name:@"BluetoothDeviceDiscoveredNotification"
    object:nil];
    [[BluetoothManagerHandler sharedInstance] startScan];
    
    CDVPluginResult* pluginResult = nil;
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

}

- (void) receiveTestNotification:(NSNotification *) notification
{
    
    if ([[notification name] isEqualToString:@"BluetoothDeviceDiscoveredNotification"]){
        BluetoothDeviceHandler *bdh = [[BluetoothDeviceHandler alloc] initWithNotification:notification];
        NSLog (@"Successfully received the test notification! %@ %@", [notification name], bdh.name);
        [devices addObject: [NSString stringWithFormat:@"{\"name\":\"%@\", \"address\":\"%@\"}", bdh.name, bdh.address]];
    }
}

-(void) sendResponse{
    if (scanCallback == nil) {
        return;
    }
    NSMutableDictionary* returnObj = [NSMutableDictionary dictionary];
    [returnObj setValue:devices forKey:@"devices"];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:returnObj];
    [pluginResult setKeepCallbackAsBool:true];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:scanCallback];
    scanCallback = nil;
}

@end
