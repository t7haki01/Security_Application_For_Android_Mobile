package scurity.app.securityapplicationforandroidmobile;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.TextView;

import java.lang.reflect.Method;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsCheckerFragment extends Fragment
    implements View.OnClickListener {

    private Button btnCheck;
    // private HashMap settingsMap = new HashMap();

    //private TextView txtIMEI;
    //final int PERMISSION_READ_STATE = 0;

    private Context context;
    private TextView txtPincode;
    private TextView txtWiFi;
    private TextView txtMobileData;
    private TextView txtBluetooth;
    private TextView txtLocation;
    private TextView txtNFC;

    //TEST
    private TextView txtTestings;

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

        // TextFields assigned to the View
        txtWiFi = view.findViewById(R.id.txt_wifi);
        txtMobileData = view.findViewById(R.id.txt_mobile_data);
        txtBluetooth = view.findViewById(R.id.txt_bluetooth);
        txtLocation = view.findViewById(R.id.txt_location);
        txtNFC = view.findViewById(R.id.txt_nfc);

        // TEST
        txtTestings = view.findViewById(R.id.txt_testings);

        return view;
    }

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

    // Get Mobile Data status as String
    private String GetMobileDataStatus(){
       boolean mobileDataAllowed = Settings.Secure.getInt(context.getContentResolver(), "mobile_data", 1) == 1;
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

    // Get WiFi status as String
    private String GetWifiStatus(){
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled() == true){
            return "ON";
        } else {
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

    // EVERYTHING DOWN HERE: IMEI
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
