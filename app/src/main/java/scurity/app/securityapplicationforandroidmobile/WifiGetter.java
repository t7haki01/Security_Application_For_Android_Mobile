package scurity.app.securityapplicationforandroidmobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.util.Log;
import android.widget.TableLayout;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WifiGetter {

    private Context context;
    private WifiManager wifiManager ;
    private WifiInfo wifiInfo ;
    private ConnectivityManager connectivityManager ;
    private int wifiState;
    private WifiConfiguration wifiConfiguration;
    private NetworkInfo networkInfo;
    private HashMap<String, ScanResult> scanResultHashMap;
    private ScanResult connectedWifiscanResult;
    private boolean isScanDone = false;

    public boolean isScanDone() {
        return isScanDone;
    }

    public HashMap<String, ScanResult> getScanResultHashMap() {
        return scanResultHashMap;
    }

    public void setScanResultHashMap(HashMap<String, ScanResult> scanResultHashMap) {
        this.scanResultHashMap = scanResultHashMap;
    }

    public ScanResult getConnectedWifiscanResult() {
        return connectedWifiscanResult;
    }

    public void setConnectedWifiscanResult(ScanResult connectedWifiscanResult) {
        this.connectedWifiscanResult = connectedWifiscanResult;
    }

    /**
     * Another way of filtering security capabilities with returning String values below but currently using other method
     * 28.3.2019, kihun
    **/
    static final String SECURITY_PSK = "PSK";
    static final String SECURITY_EAP = "EAP";
    static final String SECURITY_WEP = "WEP";
    static final String SECURITY_NONE = "NONE";

    /**
     * Here comes construction with context because current context is required for using android wifi api
     * */

    public WifiGetter(Context context){
        this.context = context;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.connectivityManager = connectivityManager;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        this.networkInfo = networkInfo;
    }

    public boolean isWifiConnected(){
        if(this.networkInfo != null){
            if(this.networkInfo.isConnected()){
                setWifiApi();
                return true;
            }
            return false;
        }
        else{
            return false;
        }
    }

    private void setWifiApi(){
        this.wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        this.wifiInfo = this.wifiManager.getConnectionInfo();
        this.wifiState = this.wifiManager.getWifiState();
    }

    public WifiConfiguration GetCurrentWifiConfiguration() {
        if (!this.wifiManager.isWifiEnabled())
            return null;

        List<WifiConfiguration> configurationList = this.wifiManager.getConfiguredNetworks();
        WifiConfiguration configuration = null;
        int cur = this.wifiManager.getConnectionInfo().getNetworkId();
        for (int i = 0; i < configurationList.size(); ++i)
        {
            WifiConfiguration wifiConfiguration = configurationList.get(i);
            if (wifiConfiguration.networkId == cur)
                configuration = wifiConfiguration;
        }
        return configuration;
    }

    public String getSsid(){
        return this.wifiInfo.getSSID();
    }

    public String getRssi(){
        return ""+this.wifiInfo.getRssi();
    }

    public String getMacAddress(){
        return this.wifiInfo.getMacAddress();
    }

    public String getLinkSpeed() {return ""+this.wifiInfo.getLinkSpeed(); }

    public DhcpInfo getDhcpInfo(){return wifiManager.getDhcpInfo();}

    public WifiInfo getConnectionInfo(){return wifiManager.getConnectionInfo();}

    public WifiManager getWifiManager(){return wifiManager;}

    public void wifiScan(){
        isScanDone = false;
        wifiManager.startScan();
        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {

//        /*Another condition for listening intent when there are different wifis available than previous*/
//        boolean success = intent.getBooleanExtra(
//        WifiManager.EXTRA_PREVIOUS_WIFI_STATE, false);

            boolean success = WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction());

            Log.d("status in broadcast", ""+success);
            if (success) {
                scanSuccess();
            } else {
                Log.d("not success", "Fired inside broadcast");
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
            Log.d("not success", "Fired before broadcast");
            scanFailure();
        }
    }

    public void scanSuccess() {
        List<ScanResult> results = wifiManager.getScanResults();
        Date date = new Date();
        Log.d("Wifi Scanned Date: ", date.toString());
        HashMap<String, ScanResult> routerFilter = new HashMap<>();

        for(int i = 0; i<results.size(); i++){
            if(routerFilter.containsKey(results.get(i).SSID)){
                if(routerFilter.get(results.get(i).SSID).level < results.get(i).level){
                    routerFilter.remove(results.get(i).SSID);
                    routerFilter.put(results.get(i).SSID, results.get(i));
                }
            }else{
                routerFilter.put(results.get(i).SSID, results.get(i));
            }
        }

        HashMap<String, ScanResult> filteredScanResult = new HashMap<>();
        ScanResult connectedScanResult ;

        Iterator hashMapIndex = routerFilter.keySet().iterator();

        while(hashMapIndex.hasNext()){
            String key = (String) hashMapIndex.next();
            String connected = wifiInfo.getSSID().substring(1,wifiInfo.getSSID().length()-1);
            if(!connected.equals(key)){
                filteredScanResult.put(key, routerFilter.get(key));
            }
            else if(connected.equals(key)){
                connectedScanResult = routerFilter.get(key);
                setConnectedWifiscanResult(connectedScanResult);
            }
        }
        setScanResultHashMap(filteredScanResult);

        isScanDone = true;
    }

    private void scanFailure() {
/**Here is in case of failing the scan but it still get the result because possible of getting older scan result*/
        List<ScanResult> results = wifiManager.getScanResults();
        Log.d("Wifi Scan failed", "Failed but here comes possible previous" + results);
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

    private static boolean isContain(String searchFrom, String searchableWord){
        String pattern = "\\b"+searchableWord+"\\b";
        Pattern p= Pattern.compile(pattern);
        Matcher m=p.matcher(searchFrom);
        return m.find();
    }

    public int getSecurityPoint(ScanResult scanResult){
        int securityPoint = getSecurity(scanResult);

        return securityPoint;
    }

    static int getSecurity(ScanResult result) {
        String securityInfo = null;
        try{
            securityInfo = result.capabilities;
        }catch(NullPointerException e){
            e.getLocalizedMessage();
        }

        int securityScore = 0;
        if(securityInfo != null){
            while(true){
                if(isContain(securityInfo, "WEP")){
                    securityScore = securityScore + 1 ;
                    int keyIndex = securityInfo.indexOf("WEP");
                    String tempText = securityInfo.substring(0, keyIndex);
                    int lengthOfString = securityInfo.length();
                    tempText += securityInfo.substring(keyIndex+3, lengthOfString);
                    securityInfo = tempText;
                }
                else if(isContain(securityInfo, "WPA")){
                    securityScore += 2;
                    int keyIndex = securityInfo.indexOf("WPA");
                    String tempText = securityInfo.substring(0, keyIndex);
                    int lengthOfString = securityInfo.length();
                    tempText += securityInfo.substring(keyIndex+3, lengthOfString);
                    securityInfo = tempText;
                }
                else if(isContain(securityInfo, "WPA2")){
                    securityScore =+ 3 ;
                    int keyIndex = securityInfo.indexOf("WPA2");
                    String tempText = securityInfo.substring(0, keyIndex);
                    int lengthOfString = securityInfo.length();
                    tempText += securityInfo.substring(keyIndex+4, lengthOfString);
                    securityInfo = tempText;
                }
                else if(isContain(securityInfo, "WPA3")){
                    securityScore += 4 ;
                    int keyIndex = securityInfo.indexOf("WPA3");
                    String tempText = securityInfo.substring(0, keyIndex);
                    int lengthOfString = securityInfo.length();
                    tempText += securityInfo.substring(keyIndex+4, lengthOfString);
                    securityInfo = tempText;
                }
                else if(isContain(securityInfo, "PSK")){
                    ++securityScore;
                    int keyIndex = securityInfo.indexOf("PSK");
                    String tempText = securityInfo.substring(0, keyIndex);
                    int lengthOfString = securityInfo.length();
                    tempText += securityInfo.substring(keyIndex+3, lengthOfString);
                    securityInfo = tempText;
                }
                else if(isContain(securityInfo, "EAP")){
                    ++securityScore;
                    int keyIndex = securityInfo.indexOf("EAP");
                    String tempText = securityInfo.substring(0, keyIndex);
                    int lengthOfString = securityInfo.length();
                    tempText += securityInfo.substring(keyIndex+3, lengthOfString);
                    securityInfo = tempText;
                }
                else if(isContain(securityInfo, "WPS")){
                    ++securityScore;
                    int keyIndex = securityInfo.indexOf("WPS");
                    String tempText = securityInfo.substring(0, keyIndex);
                    int lengthOfString = securityInfo.length();
                    tempText += securityInfo.substring(keyIndex+3, lengthOfString);
                    securityInfo = tempText;
                }
                else{
                    break;
                }
            }
        }
        return securityScore;
    }
}
