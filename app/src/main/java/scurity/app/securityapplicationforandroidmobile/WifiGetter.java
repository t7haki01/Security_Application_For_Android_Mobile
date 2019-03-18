package scurity.app.securityapplicationforandroidmobile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiGetter {
    private boolean isConnected = false;
    private Context context;
    private WifiManager wifiManager ;
    private WifiInfo wifiInfo ;
    private ConnectivityManager connectivityManager ;
    /*
    * Here would comes some data files for wifi info
    * */

    /**
     * Here comes initialization of context because current context is required for using android wifi api*/
    public WifiGetter(Context context){
        this.context = context;
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = this.connectivityManager.getActiveNetworkInfo();
        if(networkInfo.isConnected()){
            setWifiApi();
        }
    }

    private void setWifiApi(){
        this.wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        this.wifiInfo = this.wifiManager.getConnectionInfo();
    }

    public String getSsid(){
        Log.d("From wifiGetter", this.wifiInfo.getSSID());
        return this.wifiInfo.getSSID();
    }
}
