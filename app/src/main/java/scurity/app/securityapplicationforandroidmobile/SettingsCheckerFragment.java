package scurity.app.securityapplicationforandroidmobile;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
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
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */

    // TODO Da Button bringt die zu die Settings,
    // TODO ba die Settings kust owa a direkt zu die Einstellungen leiten

public class SettingsCheckerFragment extends Fragment
    implements View.OnClickListener {

    // Button
    private Button btnGoToSettings, btnGoToConnections;

    // Member to check IMEI (not needed at the moment)
    //private TextView txtIMEI;
    //final int PERMISSION_READ_STATE = 0;

    private int count = 1;

    // Context
    private Context context;

    // TextViews
    private TextView txtPincode, txtEncryption, txtDevMode;

    // Int Icon sources
    private final int GREEN_TICK = R.drawable.tick;
    private final int YELLOW_WARNING = R.drawable.warning_yellow;
    private final int RED_WARNING = R.drawable.warning_red;

    //ImageViews
    private ImageView imgPin, imgEncrypState, imgDevMode, imgWifi,
        imgMobileData, imgBluetooth, imgNFC, imgLocation;

    // Toggle Switches
    private Switch switchWiFi, switchMobileData, switchBluetooth,
            switchNFC, switchLocation;

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

        // Buttons
        btnGoToSettings = (Button) view.findViewById(R.id.btn_check_securitysettings);
        btnGoToSettings.setOnClickListener(this);
        btnGoToConnections = (Button) view.findViewById(R.id.btn_settings);
        btnGoToConnections.setOnClickListener(this);

        // TextFields assigned to View
        txtPincode = view.findViewById(R.id.txt_pincode);
        txtEncryption = view.findViewById(R.id.txt_encryption);
        txtDevMode = view.findViewById(R.id.txt_dev_mode);

        // ImageViews
        imgPin = view.findViewById(R.id.img_pin);
        imgEncrypState = view.findViewById(R.id.img_encryption);
        imgDevMode = view.findViewById(R.id.img_dev_mode);
        imgWifi = view.findViewById(R.id.img_wifi);
        imgMobileData = view.findViewById(R.id.img_mobile_data);
        imgBluetooth = view.findViewById(R.id.img_bluetooth);
        imgNFC = view.findViewById(R.id.img_nfc);
        imgLocation = view.findViewById(R.id.img_location);

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

        // Set initial state for the switches
        setSwitchBluetooth();
        setSwitchLocation();
        setSwitchNfc();

        // Check all current settings
        checkSettings();

        return view;
    }

    // Button: onClick handler
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.btn_check_securitysettings:
                intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                startActivity(intent);
                break;
            case R.id.btn_settings:
                intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                startActivity(intent);
                break;
        }
        //Toast.makeText(context,"REDIRECT ME, PLEASE!", Toast.LENGTH_SHORT).show();
    }

    // Check all settings
    private void checkSettings(){
        checkPincode();
        checkDeveloperMode();
        checkDeviceEncryptionStatus(context);
    }

    // Check if user has set a pincode
    private void checkPincode(){
        // true if a PIN, pattern or password is set or a SIM card is locked.
        String secureString = "";
        if(keyguardManager.isKeyguardSecure()){
            secureString = "SET";
            imgPin.setImageResource(GREEN_TICK);
        }else{
            secureString = "NOT SET";
            imgPin.setImageResource(RED_WARNING);
        }
        txtPincode.setText(secureString);
    }

    // Get Encryption status
    // 0: ECRYPTION_STATUS_UNSUPPORTED, 1: ENCRYPTION_STATUS_INACTIVE
    // 2: ENCRYPTION_STATUS_ACTIVATING, 3: ENCRYPTION_STATUS_ACTIVE
    // 4: ENCRYPTION_STATUS_ACTIVE_DEFAULT_KEY, 5: ENCRYPTION_STATUS_ACTIVE_PER_USER
    @TargetApi(11)
    private void checkDeviceEncryptionStatus(Context context)
    {
        int status = DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED;

        if (Build.VERSION.SDK_INT >= 11) {
            final DevicePolicyManager dpm =
                    (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            if (dpm != null) {
                status = dpm.getStorageEncryptionStatus();
            }
        }

        if (status < 2 ){
            txtEncryption.setText("NOT ENCRYPTED");
            imgEncrypState.setImageResource(RED_WARNING);
        } else{
            txtEncryption.setText("ENCRYPTED");
            imgEncrypState.setImageResource(GREEN_TICK);
        }
    }

    // Check if device is in developer mode
    private void checkDeveloperMode(){
        // 1 = on, 0 = off
        int devMode = Settings.Secure.getInt(context.getContentResolver(),
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED , 0);

        String devModeString;

        if(devMode == 0){
            txtDevMode.setText("OFF");
            imgDevMode.setImageResource(GREEN_TICK);
        } else {
            txtDevMode.setText("ON");
            imgDevMode.setImageResource(RED_WARNING);
        }
    }

    // BROADCAST RECEIVERS
    // Register Broadcast Receivers for Connection-Changes
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
                    switchWiFi.setText("ON");
                    imgWifi.setImageResource(YELLOW_WARNING);
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    switchWiFi.setChecked(false);
                    switchWiFi.setText("OFF");
                    imgWifi.setImageResource(0);
                    break;
            }
        }
    };

    // Broadcast Receiver for Location state
    private BroadcastReceiver locationStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setSwitchLocation();
        }
    };

    // Broadcast Receiver for mobile data state
    private BroadcastReceiver mobileDataStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean mobileDataAllowed = Settings.Secure.getInt(context.getContentResolver(),
                    "mobile_data", 1) == 1;

            if(mobileDataAllowed){
                switchMobileData.setChecked(true);
                switchMobileData.setText("ON");
                imgMobileData.setImageResource(YELLOW_WARNING);
            } else{
                switchMobileData.setChecked(false);
                switchMobileData.setText("OFF");
                imgMobileData.setImageResource(0);
            }
        }
    };

    // Broadcast Receiver for NFC state
    private BroadcastReceiver nfcStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setSwitchNfc();
        }
    };

    // Broadcast Receiver for bluetooth state
    private BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setSwitchBluetooth();
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
    private void setSwitchNfc(){

        if(nfcAdapter.isEnabled() == true){
            switchNFC.setChecked(true);
            switchNFC.setText("ON");
            imgNFC.setImageResource(YELLOW_WARNING);
        } else{
            switchNFC.setChecked(false);
            switchNFC.setText("OFF");
            imgNFC.setImageResource(0);
        }
    }

    // Check bluetooth state and set switch
    // Get Bluetooth status as String
    private void setSwitchBluetooth(){

        if(bluetoothAdapter.isEnabled()){
            switchBluetooth.setChecked(true);
            switchBluetooth.setText("ON");
            imgBluetooth.setImageResource(YELLOW_WARNING);
        } else{
            switchBluetooth.setChecked(false);
            switchBluetooth.setText("OFF");
            imgBluetooth.setImageResource(0);
        }
    }

    // Check location state and set switch
    // Needed to set initial state of the switch and also for the broadcast receiver
    private void setSwitchLocation(){

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == true){
            switchLocation.setChecked(true);
            switchLocation.setText("ON");
            imgLocation.setImageResource(YELLOW_WARNING);
        } else{
            switchLocation.setChecked(false);
            switchLocation.setText("OFF");
            imgLocation.setImageResource(0);
        }
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
