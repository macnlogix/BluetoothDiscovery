/********* BluetoothDiscovery.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>
#import "BluetoothManagerHandler.h"

@interface BluetoothDiscovery : CDVPlugin {
    BluetoothManagerHandler *bmH;
}

- (void)coolMethod:(CDVInvokedUrlCommand*)command;
- (void)getAdapterInfoAction:(CDVInvokedUrlCommand*)command;
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

- (void)getAdapterInfoAction:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)scanDevices:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

}

@end
