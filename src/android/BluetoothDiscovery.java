package cordova.plugin.bluetooth.discovery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothManager;
import android.content.pm.PackageManager;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
/**
 * This class echoes a string called from JavaScript.
 */
public class BluetoothDiscovery extends CordovaPlugin {
    
    private static final String TAG = "BluetoothDiscovery";
    private static final boolean D = true;
    Handler mHandler;
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

    private final String logOperationUnsupported = "Operation unsupported";

    private CallbackContext enableBluetoothCallback;
    private CallbackContext deviceDiscoveredCallback;

    private BluetoothAdapter bluetoothAdapter;

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    private static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int CHECK_PERMISSIONS_REQ_CODE = 2;
    private final int REQUEST_ACCESS_COARSE_LOCATION = 59628;
    private final int REQUEST_LOCATION_SOURCE_SETTINGS = 59629;

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

    private List<BluetoothDevice> devices = new ArrayList();
    private List<String> deviceAddresses = new ArrayList();

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        LOG.d(TAG, "action = " + action);
        mHandler=new Handler();
        if (bluetoothAdapter == null) {
            bluetoothAdapter = getAdapter();
        }
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        if (action.equals("getAdapterInfo")) {
            this.getAdapterInfoAction(callbackContext);
            return true;
        }else if ("hasPermission".equals(action)) {
            hasPermissionAction(callbackContext);
            return true;
        }else if ("scanDevices".equals(action)) {
            this.scanDevices(callbackContext);
            return true;
        } else if ("requestPermission".equals(action)) {
            requestPermissionAction(callbackContext);
            return true;
        } else if ("isLocationEnabled".equals(action)) {
            isLocationEnabledAction(callbackContext);
            return true;
        } else if ("requestLocation".equals(action)) {
            requestLocationAction(callbackContext);
            return true;
        }
        return false;
    }

    private final BroadcastReceiver bReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Create a new device item
                //scanCallbackContext.success("{\"deviceName\":"+device.getName()+",\"deviceName\":"+device.getAddress()+"}");
                if(device.getName() != null && !deviceAddresses.contains(device.getAddress())){
                    devices.add(device);
                    deviceAddresses.add(device.getAddress());
                }


                Log.d(TAG, "ACTION_FOUND--"+device.getName()+"---"+device.getAddress());
            }
        }
    };



    BroadcastReceiver scanModeReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }

    };

    @SuppressLint("LongLogTag")
    private BluetoothAdapter getAdapter(){
        BluetoothAdapter mBluetoothAdapter = null;
         if (cordova.getActivity().checkCallingOrSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
            BluetoothManager bm;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                bm = (BluetoothManager) cordova.getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = bm.getAdapter();
            } else {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            //Log.i("TEST", mBluetoothAdapter.getAddress());
//            if(mBluetoothAdapter.isEnabled()){
//                Log.i("BluetoothAdapter---isEnabled--", mBluetoothAdapter.isEnabled()+"");
//            }
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
        checkBTPermissions();
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

    public void discoverableEnable() {
        Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        cordova.getActivity().startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        cordova.getActivity().registerReceiver(scanModeReciver,intentFilter);
    }

    private void scanDevices(CallbackContext callbackContext) {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");
        IntentFilter intentFilter = new IntentFilter(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        cordova.getActivity().registerReceiver(scanModeReciver,intentFilter);
        devices = new ArrayList();
        deviceAddresses = new ArrayList();
        scanCallbackContext = callbackContext;
        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            cordova.getActivity().registerReceiver(bReciever, discoverDevicesIntent);
        }
        if(!bluetoothAdapter.isDiscovering()){

            //check BT permissions in manifest
            checkBTPermissions();

            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            cordova.getActivity().registerReceiver(bReciever, discoverDevicesIntent);
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                JSONArray devicesJsonArr = new JSONArray();
                for (BluetoothDevice tempDevice : devices)
                {
                    JSONObject devicesJsonObj = new JSONObject();
                    addProperty(devicesJsonObj, "name", tempDevice.getName());
                    addProperty(devicesJsonObj, "address", tempDevice.getAddress());
                    devicesJsonArr.put(devicesJsonObj);
                }
                JSONObject returnObj = new JSONObject();

                addProperty(returnObj, "devices", devicesJsonArr);
                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnObj);
                pluginResult.setKeepCallback(true);
                scanCallbackContext.sendPluginResult(pluginResult);
            }
        }, 10000);

    }

    public void hasPermissionAction(CallbackContext callbackContext) {
        JSONObject returnObj = new JSONObject();

        addProperty(returnObj, "hasPermission", (cordova.hasPermission(ACCESS_COARSE_LOCATION) && cordova.hasPermission(ACCESS_FINE_LOCATION)));

        callbackContext.success(returnObj);
    }

    public void requestPermissionAction(CallbackContext callbackContext) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            JSONObject returnObj = new JSONObject();
            addProperty(returnObj, keyError, "requestPermission");
            addProperty(returnObj, keyMessage, logOperationUnsupported);
            callbackContext.error(returnObj);
            return;
        }

        permissionsCallback = callbackContext;
        cordova.requestPermission(this, REQUEST_ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    private void isLocationEnabledAction(CallbackContext callbackContext) {
        JSONObject returnObj = new JSONObject();

        addProperty(returnObj, "isLocationEnabled", isLocationEnabled());

        callbackContext.success(returnObj);
    }

    private boolean isLocationEnabled() {
        boolean result = true;

        //Only applies to Android 6.0, which requires the users to have location services enabled to scan for devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                result = (Settings.Secure.getInt(cordova.getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE) != Settings.Secure.LOCATION_MODE_OFF);
            } catch (Settings.SettingNotFoundException e) {
                result = true; //Probably better to default to true
            }
        }

        return result;
    }

    private void requestLocationAction(CallbackContext callbackContext) {
        locationCallback = callbackContext;

        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        cordova.startActivityForResult(this, intent, REQUEST_LOCATION_SOURCE_SETTINGS);
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        if (permissionsCallback == null) {
            return;
        }

        //Just call hasPermission again to verify
        JSONObject returnObj = new JSONObject();

        addProperty(returnObj, "requestPermission", cordova.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION));

        permissionsCallback.success(returnObj);
    }

    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = cordova.getActivity().checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += cordova.getActivity().checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                cordova.getActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
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
