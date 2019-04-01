package scurity.app.securityapplicationforandroidmobile;

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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsCheckerFragment extends Fragment
    implements View.OnClickListener {

    // Button
    private Button btnCheck;
    // private HashMap settingsMap = new HashMap();

    //private TextView txtIMEI;
    //final int PERMISSION_READ_STATE = 0;

    private Context context;

    // TextViews
    private TextView txtPincode;
    private TextView txtWiFi;
    private TextView txtMobileData;
    private TextView txtBluetooth;
    private TextView txtLocation;
    private TextView txtNFC;
    //TEST
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
    private NfcManager nfcManager;

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
        txtWiFi = view.findViewById(R.id.txt_wifi);
        txtMobileData = view.findViewById(R.id.txt_mobile_data);
        txtBluetooth = view.findViewById(R.id.txt_bluetooth);
        txtLocation = view.findViewById(R.id.txt_location);
        txtNFC = view.findViewById(R.id.txt_nfc);

        // Switches assigned to View
        switchWiFi = view.findViewById(R.id.switch_wifi);
        switchMobileData = view.findViewById(R.id.switch_mobile_data);
        switchBluetooth = view.findViewById(R.id.switch_bluetooth);
        switchNFC = view.findViewById(R.id.switch_nfc);
        switchLocation = view.findViewById(R.id.switch_location);

        // Manager
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // ToDO other managers

        // TODO wifi check with broadcast receiver (hopefully)
        switchWiFi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    wifiManager.setWifiEnabled(true);
                    switchWiFi.setText("On");
                } else{
                    wifiManager.setWifiEnabled(false);
                    switchWiFi.setText("Off");
                }
            }
        });

        if(wifiManager.isWifiEnabled()){
            switchWiFi.setChecked(true);
            switchWiFi.setText("On");
        } else {
            switchWiFi.setChecked(false);
            switchWiFi.setText("Off");
        }

        // TEST
        txtTestings = view.findViewById(R.id.txt_testings);

        return view;
    }

    // Register Broadcast Receiver
    @Override
    public void onStart() {
        super.onStart();
        // activate broadcast receiver on start
        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        context.registerReceiver(wifiStateReceiver, intentFilter);
    }

    // Unregister broadcast receiver
    @Override
    public void onStop() {
        super.onStop();
        context.unregisterReceiver(wifiStateReceiver);
    }

    // Broadcast Receiver
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


    // onClick method for the button
    @Override
    public void onClick(View view) {
        CheckCurrentSettings();
    }

    private void CheckCurrentSettings(){
        // TODO pincode
        // TODO lockscreen
        // TODO fingerprint
        // TODO developer mode

        // WiFi
        txtWiFi.setText(GetWifiStatus());

        // Mobile Data
        txtMobileData.setText(GetMobileDataStatus());      ;

        // Bluetooth
        txtBluetooth.setText(GetBluetoothStatus());

        // NFC
        txtNFC.setText(GetNFCStatus());

        // GPS (Location)
        txtLocation.setText(GetGpsStatus());

    }

    // Get WiFi status as String
    private String GetWifiStatus(){
        if(wifiManager.isWifiEnabled() == true){
            switchWiFi.setChecked(true);
            return "ON";
        } else {
            switchWiFi.setChecked(false);
            return "OFF";
        }
    }

    // Get Mobile Data status as String
    private String GetMobileDataStatus(){
       boolean mobileDataAllowed = Settings.Secure.getInt(context.getContentResolver(),
               "mobile_data", 1) == 1;
       if(mobileDataAllowed){
           return "ON";
       } else{
           return "OFF";
       }
    }

    // Get NFC status as String
    private String GetNFCStatus(){
        nfcManager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        NfcAdapter nfcAdapter = nfcManager.getDefaultAdapter();
        if(nfcAdapter.isEnabled() == true){
            return "ON";
        } else{
            return "OFF";
        }
    }

    // Get Bluetooth status as String
    private String GetBluetoothStatus(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter.isEnabled()){
            return "ON";
        } else {
            return "OFF";
        }
    }

    // Get GPS status as String
    // (true = activated, false = deactivated)
    private String GetGpsStatus(){
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == true){
            return "ON";
        } else{
            return "OFF";
        }
    }

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
