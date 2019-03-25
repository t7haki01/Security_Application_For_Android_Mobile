package scurity.app.securityapplicationforandroidmobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.util.Log;

import java.io.InvalidClassException;
import java.util.List;

public class WifiGetter {
    private boolean isConnected = false;
    private Context context;
    private WifiManager wifiManager ;
    private WifiInfo wifiInfo ;
    private ConnectivityManager connectivityManager ;
    private int wifiState;

    static final String SECURITY_PSK = "PSK";
    static final String SECURITY_EAP = "EAP";
    static final String SECURITY_WEP = "WEP";
    static final String SECURITY_NONE = "NONE";


    /*
    * Here would comes some data files for wifi info
    * */

    /**
     * Here comes initialization of context because current context is required for using android wifi api*/
    public WifiGetter(Context context){
        this.context = context;
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo.isConnected()){
            setWifiApi();
        }
    }

    private void setWifiApi(){
        this.wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        this.wifiInfo = this.wifiManager.getConnectionInfo();
        this.wifiState = this.wifiManager.getWifiState();
    }

    public String getSsid(){
        Log.d("From wifiGetter", this.wifiInfo.getSSID());
        return this.wifiInfo.getSSID();
    }
    public void getAllAvailable(){
        Log.d("wifi Rssi", ""+wifiInfo.getRssi());
        Log.d("wifi toString", ""+wifiInfo.toString());
        Log.d("wifi network Id", ""+wifiInfo.getNetworkId());
        Log.d("wifi mac address", ""+wifiInfo.getMacAddress());
        Log.d("wifi ip address", ""+wifiInfo.getIpAddress());
        Log.d("wifi Hidden SSID", ""+wifiInfo.getHiddenSSID());
        /**frequenct for api 21*/
//        Log.d("wifi frequency", ""+wifiInfo.getFrequency());
        Log.d("wifi BSSID", ""+wifiInfo.getBSSID());
        Log.d("wifi desribe contents", ""+wifiInfo.describeContents());
        Log.d("wifi Link speed", ""+wifiInfo.getLinkSpeed());
        Log.d("wifi supplicant state", ""+wifiInfo.getSupplicantState());

        Log.d("State change", "**********From here used wifi manager***************");
        Log.d("wifi dhcp info", ""+wifiManager.getDhcpInfo());
        Log.d("wifi connet info", ""+wifiManager.getConnectionInfo());
        Log.d("wifi wifi state", ""+wifiManager.getWifiState());
        Log.d("wifi is wifi enabled", ""+wifiManager.isWifiEnabled());
    }

    public void wifiScan(){
        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
//                boolean success = intent.getBooleanExtra(
//                        WifiManager.EXTRA_PREVIOUS_WIFI_STATE, false);
//
                boolean success = WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction());
                Log.d("scan status from broad", ""+success);
                if (success) {
                    scanSuccess();
                } else {
                    Log.d("not success", "Fired inside broad");
                    // scan failure handling
                    scanFailure();
                }

            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifiManager.startScan();
        Log.d("scan status", ""+success);
        if (!success) {
            Log.d("not success", "Fired before broad");
            // scan failure handling
//            scanFailure();
        }
    }

    private void scanSuccess() {
        List<ScanResult> results = wifiManager.getScanResults();
//  ... use new scan results ...
        for(int i = 0; i<results.size(); i++){
            Log.d("Wifi Scan in success ", "Tested " + i + " Wifi info "+results.get(i) + " and security check " + getSecurity(results.get(i)));
        }

    }

    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        List<ScanResult> results = wifiManager.getScanResults();
        Log.d("Wifi Scan failed", "Failed but here comes possible previous" + results);
//  ... potentially use older scan results ...
    }

    static String getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) ||
                config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }

    static String getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }
        return SECURITY_NONE;
    }

}
