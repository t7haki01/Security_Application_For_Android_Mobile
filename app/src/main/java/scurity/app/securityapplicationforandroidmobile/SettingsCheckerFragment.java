package scurity.app.securityapplicationforandroidmobile;

import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;

import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsCheckerFragment extends Fragment
    implements View.OnClickListener {

    // Button
    private Button btnCheck;

    // Member to check IMEI (not needed at the moment)
    //private TextView txtIMEI;
    //final int PERMISSION_READ_STATE = 0;

    private int count = 1;

    // Context
    private Context context;

    // TextViews
    private TextView txtPincode;
    private TextView txtDevMode;
    private TextView txtWiFi;
    private TextView txtBluetooth;
    private TextView txtLocation;
    private TextView txtNFC;

    // TODO delete when finished
    private TextView txtTestings;

    // Toggle Switches
    private Switch switchWiFi;
    private Switch switchMobileData;
    private Switch switchBluetooth;
    private Switch switchNFC;
    private Switch switchLocation;

    // Manager for Connections
    private WifiManager wifiManager;
    private ConnectivityManager connectivityManager;
    private LocationManager locationManager;
    private BluetoothAdapter bluetoothAdapter;
    private NfcAdapter nfcAdapter;
    private NfcManager nfcManager;
    private KeyguardManager keyguardManager;

    // Constructor
    public SettingsCheckerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_checker, container, false);
        context = view.getContext();

        // Button
        btnCheck = (Button) view.findViewById(R.id.btn_check_settings);
        btnCheck.setOnClickListener(this);

        // TextFields assigned to View
        txtPincode = view.findViewById(R.id.txt_pincode);
        txtDevMode = view.findViewById(R.id.txt_dev_mode);
        txtWiFi = view.findViewById(R.id.txt_wifi);
        txtBluetooth = view.findViewById(R.id.txt_bluetooth);
        txtLocation = view.findViewById(R.id.txt_location);
        txtNFC = view.findViewById(R.id.txt_nfc);

        // Switches assigned to View
        switchWiFi = view.findViewById(R.id.switch_wifi);
        switchWiFi.setEnabled(false);
        switchMobileData = view.findViewById(R.id.switch_mobiledata);
        switchMobileData.setEnabled(false);
        switchBluetooth = view.findViewById(R.id.switch_bluetooth);
        switchBluetooth.setEnabled(false);
        switchNFC = view.findViewById(R.id.switch_nfc);
        switchNFC.setEnabled(false);
        switchLocation = view.findViewById(R.id.switch_location);
        switchLocation.setEnabled(false);

        // Manager
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        nfcManager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

        txtTestings = view.findViewById(R.id.txt_testings);

        // Set initial state for switches
        SetSwitchBluetooth();
        SetSwitchLocation();
        SetSwitchNfc();

        CheckSettings();

        return view;
    }

    // Register Broadcast Receivers
    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilterWifi = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        IntentFilter intentFilterLocation = new IntentFilter(LocationManager.MODE_CHANGED_ACTION);
        IntentFilter intentFilterMobileData = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        IntentFilter intentFilterBluetooth = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        IntentFilter intentFilterNFC = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        context.registerReceiver(wifiStateReceiver, intentFilterWifi);
        context.registerReceiver(locationStateReceiver, intentFilterLocation);
        context.registerReceiver(mobileDataStateReceiver, intentFilterMobileData);
        context.registerReceiver(bluetoothStateReceiver, intentFilterBluetooth);
        context.registerReceiver(nfcStateReceiver, intentFilterNFC);
    }

    // Broadcast Receiver for WiFi state
    private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // WIFI_STATE_UNKNOWN: default value, if we don't receive anything)
            int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);

            switch (wifiStateExtra){
                case WifiManager.WIFI_STATE_ENABLED:
                    switchWiFi.setChecked(true);
                    switchWiFi.setText("On");
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    switchWiFi.setChecked(false);
                    switchWiFi.setText("Off");
                    break;
            }
        }
    };

    // Broadcast Receiver for Location state
    private BroadcastReceiver locationStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SetSwitchLocation();
        }
    };

    // Broadcast Receiver for mobile data state
    // TODO Not shure if this really makes sense, mobiledata shows up unpredictably
    private BroadcastReceiver mobileDataStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean mobileDataAllowed = Settings.Secure.getInt(context.getContentResolver(),
                    "mobile_data", 1) == 1;

            if(mobileDataAllowed){
                switchMobileData.setChecked(true);
                switchMobileData.setText("On");
            } else{
                switchMobileData.setChecked(false);
                switchMobileData.setText("Off");
            }
        }
    };

    // Broadcast Receiver for NFC state
    private BroadcastReceiver nfcStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SetSwitchNfc();
        }
    };

    // Broadcast Receiver for bluetooth state
    private BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SetSwitchBluetooth();
        }
    };

    // Unregister all broadcast receivers
    @Override
    public void onStop() {
        super.onStop();
        context.unregisterReceiver(wifiStateReceiver);
        context.unregisterReceiver(locationStateReceiver);
        context.unregisterReceiver(mobileDataStateReceiver);
        context.unregisterReceiver(bluetoothStateReceiver);
        context.unregisterReceiver(nfcStateReceiver);
    }


    // Check NFC state and set switch
    private void SetSwitchNfc(){

        if(nfcAdapter.isEnabled() == true){
            switchNFC.setChecked(true);
            switchNFC.setText("On");
        } else{
            switchNFC.setChecked(false);
            switchNFC.setText("Off");
        }
    }

    // Check bluetooth state and set switch
    // Get Bluetooth status as String
    private void SetSwitchBluetooth(){

        if(bluetoothAdapter.isEnabled()){
            switchBluetooth.setChecked(true);
            switchBluetooth.setText("On");
        } else{
            switchBluetooth.setChecked(false);
            switchBluetooth.setText("Off");
        }
    }

    // Check location state and set switch
    // Needed to set initial state of the switch and also for the broadcast receiver
    private void SetSwitchLocation(){

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == true){
            switchLocation.setChecked(true);
            switchLocation.setText("On");
        } else{
            switchLocation.setChecked(false);
            switchLocation.setText("Off");
        }
    }

    // TODO Show up some infos or redirect user to settings
    // onClick method for the Button
    @Override
    public void onClick(View view) {
        // TODO handle click...
    }

    // Check the current settings
    private void CheckSettings(){
        // true if a PIN, pattern or password is set or a SIM card is locked.
        String secureString = keyguardManager.isKeyguardSecure() ? "Set" : "Not set";

        // Check if Developer Mode is on/off
        // 1 = on, 0 = off
        int devMode = Settings.Secure.getInt(context.getContentResolver(),
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED , 0);

        String devModeString;

        if(devMode == 1){
            devModeString = "On";
        } else if (devMode == 1){
            devModeString = "Off";
        } else {
            devModeString = "???";
        }

        txtPincode.setText(secureString);
        txtDevMode.setText(devModeString);
    }

    /*
    // handling toggle Switch for WIFI
    private void WifiSwitchHandler() {
        switchWiFi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    wifiManager.setWifiEnabled(true);
                    switchWiFi.setText("On");
                } else {
                    wifiManager.setWifiEnabled(false);
                    switchWiFi.setText("Off");
                }
            }
        });

        if (wifiManager.isWifiEnabled()) {
            switchWiFi.setChecked(true);
            switchWiFi.setText("On");
        } else {
            switchWiFi.setChecked(false);
            switchWiFi.setText("Off");
        }
    }

    */
    // EVERYTHING DOWN HERE: Just to get IMEI of the device
    // NOT NEEDED AT THE MOMENT
    /*
    // check if user is permitted to read IMEI from device
    public void CheckPermissionForIMEI(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_READ_STATE);

        } else {
            GetIMEI();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_READ_STATE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    // READ_PHONE_STATE permission is already been granted.
                    GetIMEI();

                } else {
                    // permission denied
                    txtIMEI.setText("DENIED");
                }
                return;
            }
        }
    }

    // Get IMEI of device
    private void GetIMEI(){
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        String imei = "";

        if (android.os.Build.VERSION.SDK_INT >= 26) {
            imei = telephonyManager.getImei();
        }
        else
        {
            imei = telephonyManager.getDeviceId();
        }
        txtIMEI.setText("IMEI: " + imei);
    }
    */
}
