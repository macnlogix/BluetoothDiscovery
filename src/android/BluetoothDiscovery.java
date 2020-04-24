package cordova.plugin.bluetooth.discovery;

import android.Manifest;
import android.content.pm.PackageManager;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;
/**
 * This class echoes a string called from JavaScript.
 */
public class BluetoothDiscovery extends CordovaPlugin {
    
    private static final String TAG = "BluetoothDiscovery";
    private static final boolean D = true;

    // actions
    private static final String LIST = "list";
    private static final String CONNECT = "connect";
    private static final String CONNECT_INSECURE = "connectInsecure";
    private static final String DISCONNECT = "disconnect";
    private static final String WRITE = "write";
    private static final String AVAILABLE = "available";
    private static final String READ = "read";
    private static final String READ_UNTIL = "readUntil";
    private static final String SUBSCRIBE = "subscribe";
    private static final String UNSUBSCRIBE = "unsubscribe";
    private static final String SUBSCRIBE_RAW = "subscribeRaw";
    private static final String UNSUBSCRIBE_RAW = "unsubscribeRaw";
    private static final String IS_ENABLED = "isEnabled";
    private static final String IS_CONNECTED = "isConnected";
    private static final String CLEAR = "clear";
    private static final String SETTINGS = "showBluetoothSettings";
    private static final String ENABLE = "enable";
    private static final String DISCOVER_UNPAIRED = "discoverUnpaired";
    private static final String SET_DEVICE_DISCOVERED_LISTENER = "setDeviceDiscoveredListener";
    private static final String CLEAR_DEVICE_DISCOVERED_LISTENER = "clearDeviceDiscoveredListener";
    private static final String SET_NAME = "setName";
    private static final String SET_DISCOVERABLE = "setDiscoverable";

    private CallbackContext enableBluetoothCallback;
    private CallbackContext deviceDiscoveredCallback;

    private BluetoothAdapter bluetoothAdapter;

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    private static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int CHECK_PERMISSIONS_REQ_CODE = 2;
    private CallbackContext permissionCallback;

    //Object keys
    private final String keyStatus = "status";
    private final String keyError = "error";
    private final String keyMessage = "message";
    private final String keyRequest = "request";
    private final String keyStatusReceiver = "statusReceiver";
    private final String keyName = "name";
    private final String keyAddress = "address";
    private final String keyRssi = "rssi";
    private final String keyScanMode = "scanMode";
    private final String keyMatchMode = "matchMode";
    private final String keyMatchNum = "matchNum";
    private final String keyCallbackType = "callbackType";
    private final String keyAdvertisement = "advertisement";
    private final String keyUuid = "uuid";
    private final String keyService = "service";
    private final String keyServices = "services";
    private final String keyCharacteristic = "characteristic";
    private final String keyCharacteristics = "characteristics";
    private final String keyProperties = "properties";
    private final String keyPermissions = "permissions";
    private final String keyDescriptor = "descriptor";
    private final String keyDescriptors = "descriptors";
    private final String keyValue = "value";
    private final String keyType = "type";
    private final String keyIsInitialized = "isInitialized";
    private final String keyIsEnabled = "isEnabled";
    private final String keyIsScanning = "isScanning";
    private final String keyIsBonded = "isBonded";
    private final String keyIsConnected = "isConnected";
    private final String keyIsDiscovered = "isDiscovered";
    private final String keyIsDiscoverable = "isDiscoverable";
    private final String keyPeripheral = "peripheral";
    private final String keyState = "state";
    private final String keyDiscoveredState = "discoveredState";
    private final String keyConnectionPriority = "connectionPriority";
    private final String keyMtu = "mtu";

    private CallbackContext initCallbackContext;
    private CallbackContext scanCallbackContext;
    private CallbackContext permissionsCallback;
    private CallbackContext locationCallback;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        LOG.d(TAG, "action = " + action);

        if (bluetoothAdapter == null) {
            bluetoothAdapter = getAdapter()
        }
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        if (action.equals("getAdapterInfo")) {
            this.getAdapterInfoAction(callbackContext);
            return true;
        }
        return false;
    }

    private BluetoothAdapter getAdapter(){
        BluetoothAdapter mBluetoothAdapter = null;
         if (cordova.getActivity().checkCallingOrSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
            BluetoothManager bm;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                    mBluetoothAdapter = bm.getAdapter();
                    } else {
                        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    }
                    Log.i("TEST", mBluetoothAdapter.getAddress());
            if(mBluetoothAdapter.isEnabled()){
                Log.i("BluetoothAdapter---isEnabled--", mBluetoothAdapter.isEnabled()+"");
            }
        }
        return mBluetoothAdapter;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    

    private void getAdapterInfoAction(CallbackContext callbackContext) {  

        JSONObject returnObj = new JSONObject();    
        
        addProperty(returnObj, keyAddress, bluetoothAdapter.getAddress());
        addProperty(returnObj, keyName, bluetoothAdapter.getName());
        addProperty(returnObj, keyIsInitialized, true);
        addProperty(returnObj, keyIsEnabled, bluetoothAdapter.isEnabled());
        addProperty(returnObj, keyIsScanning, (scanCallbackContext != null));
        addProperty(returnObj, keyIsDiscoverable, bluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);      
        return;
    }


    //General Helpers
    private void addProperty(JSONObject obj, String key, Object value) {
        //Believe exception only occurs when adding duplicate keys, so just ignore it
        try {
        if (value == null) {
            obj.put(key, JSONObject.NULL);
        } else {
            obj.put(key, value);
        }
        } catch (JSONException e) {
        }
    }

}
