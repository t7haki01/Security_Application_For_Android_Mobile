package scurity.app.securityapplicationforandroidmobile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.util.Log;

import java.io.InvalidClassException;

public class WifiGetter {
    private boolean isConnected = false;
    private Context context;
    private WifiManager wifiManager ;
    private WifiInfo wifiInfo ;
    private ConnectivityManager connectivityManager ;
    private int wifiState;
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
        Log.d("wifi gidden SSID", ""+wifiInfo.getHiddenSSID());
        /**frequenct for api 21*/
//        Log.d("wifi frequency", ""+wifiInfo.getFrequency());
        Log.d("wifi BSSID", ""+wifiInfo.getBSSID());
        Log.d("wifi desribe contents", ""+wifiInfo.describeContents());
        Log.d("wifi Link speed", ""+wifiInfo.getLinkSpeed());
        Log.d("wifi supplicant state", ""+wifiInfo.getSupplicantState());

        Log.d("State change", "**********From here used wifi manager***************");
        Log.d("wifi dhcp info", ""+wifiManager.getDhcpInfo());
        Log.d("wifi scan result", ""+wifiManager.getScanResults());
        Log.d("wifi connet info", ""+wifiManager.getConnectionInfo());
        Log.d("wifi wifi state", ""+wifiManager.getWifiState());
        Log.d("wifi is wifi enabled", ""+wifiManager.isWifiEnabled());

        WpsInfo wpsInfo = new WpsInfo();
        Log.d("State change", "*****************From here checking with wps************************");
        Log.d("wps info", ""+wpsInfo.toString());
        Log.d("wps info", ""+wpsInfo.describeContents());



    }
}
