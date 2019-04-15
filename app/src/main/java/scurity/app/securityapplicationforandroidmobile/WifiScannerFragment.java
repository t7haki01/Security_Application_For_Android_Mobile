package scurity.app.securityapplicationforandroidmobile;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class WifiScannerFragment extends Fragment {
    private Context context = null;
    private FrameLayout wifiFrame;
    private TableLayout wifiTable = null;
    private WifiGetter wifiInfo;
    Button wifiBtn;
    ProgressBar loadingBar;
    int securityPointId;
    TextView wifiSecRate;
    View view = null;
    boolean isScanned = false;

    public WifiScannerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view == null){
            view = inflater.inflate(R.layout.fragment_wifi_scanner, container, false);
        }
        return view;
    }

    private void scanBtnCheck(){
        if(isScanned){
            try{
                wifiBtn.setVisibility(View.VISIBLE);
                loadingBar.setVisibility(View.GONE);
            }catch(NullPointerException e){
                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
        else{
            try{
                wifiBtn.setVisibility(View.GONE);
                loadingBar.setVisibility(View.VISIBLE);
            }
            catch(NullPointerException e){
                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(wifiTable == null){
            context = getContext();
            Activity activity = getActivity();
            if(isAdded() && activity != null){
                scanBtnCheck();
                wifiFrame = getView().findViewById(R.id.frame_wifiScanner);
                wifiTable = getView().findViewById(R.id.wifiTable);
                securityPointId = ViewCompat.generateViewId();

                wifiBtn = getView().findViewById(R.id.wifi_btn);
                wifiBtn.setVisibility(View.VISIBLE);
                loadingBar = getView().findViewById(R.id.loading_bar);

                TableRow guideRow = new TableRow(context);
                guideRow.setGravity(Gravity.CENTER_HORIZONTAL);
                TableRow.LayoutParams guideParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                guideRow.setLayoutParams(guideParams);

                TextView guideText = new TextView(context);

                guideText.setText("Press Button to Scan WIFI");

                guideRow.addView(guideText);

                wifiTable.addView(guideRow);

                wifiBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wifiBtnClicked(context);
                    }
                });
            }
            else{
                getFragmentManager().executePendingTransactions();
                scanBtnCheck();
            }
        }
        else{
            scanBtnCheck();
        }

    }

    void wifiBtnClicked(Context context){
        if(Build.VERSION.SDK_INT >= 23){
            statusCheck();
        }else{
            wifiBuilder(context);
        }

    }

    public void wifiBuilder(Context context){
        wifiTable.removeAllViews();

        wifiInfo = new WifiGetter(context);

        int colorForBtn = getResources().getColor(R.color.colorForDetailBtn);
        int tableFontSzie = 14;
        int columnWidth = 100;

        if(wifiInfo.isWifiConnected()){

            wifiBtn.setVisibility(View.GONE);
            loadingBar.setVisibility(View.VISIBLE);

            ((MainActivity) getActivity()).setWifiScanned(true);

            TableRow firstRow = new TableRow(context);
            firstRow.setGravity(Gravity.CENTER_HORIZONTAL);
            TableRow.LayoutParams headParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            headParams.weight = Float.parseFloat("1");
            headParams.setMargins(10,10,10,0);
            firstRow.setLayoutParams(headParams);

            TextView headName = makeTableText(tableFontSzie, "WiFi\n(SSID)", true, false, columnWidth);
            headName.setGravity(Gravity.CENTER);
            TextView headFrq = makeTableText(tableFontSzie, "Channel\n(Frequency)", true, false, columnWidth);
            headFrq.setGravity(Gravity.CENTER);
            TextView headSecPoint = makeTableText(tableFontSzie, "Security\nLevel", true, false, columnWidth);
            headSecPoint.setGravity(Gravity.CENTER);
            TextView headSignal = makeTableText(tableFontSzie, "Strength\n(Signal)", true, false, columnWidth);
            headSignal.setGravity(Gravity.CENTER);
            TextView headLink = makeTableText(tableFontSzie, "Check\nDetail", true, true, columnWidth);
            headLink.setGravity(Gravity.CENTER);

            firstRow.addView(headName);
            firstRow.addView(headFrq);
            firstRow.addView(headSecPoint);
            firstRow.addView(headSignal);
            firstRow.addView(headLink);

            wifiTable.addView(firstRow);

            TableRow secondRow = new TableRow(context);
            secondRow.setGravity(Gravity.CENTER_HORIZONTAL);

            TableRow.LayoutParams secondParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            secondParams.setMargins(10,10,10,0);
            secondParams.weight = Float.parseFloat("1");
            secondRow.setLayoutParams(secondParams);

            int connectedWifiColor = Color.rgb(118, 200, 19);

            TextView wifiSsid = makeTableText(tableFontSzie, wifiInfo.getSsid(), false, false, columnWidth );
            wifiSsid.setTextColor(connectedWifiColor);

            TextView wifiFreq = makeTableText(tableFontSzie, wifiInfo.getWifiManager().getConnectionInfo().getHiddenSSID()? "YES":"NO"
                    /**Uncomment this when production mode or in the environment API level higher than 21 which is able to use following api
                     * wifiInfo.getConnectionInfo().getFrequency()**/
                    , false, false, columnWidth );
            wifiFreq.setTextColor(connectedWifiColor);

            this.wifiSecRate = makeTableText(tableFontSzie, "Rating..", false, false, columnWidth );
            this.wifiSecRate.setTextColor(connectedWifiColor);
            this.wifiSecRate.setId(this.securityPointId);

            TextView wifiRssi = makeTableText(tableFontSzie, wifiInfo.getRssi(), false, false, columnWidth );
            wifiRssi.setTextColor(connectedWifiColor);

            Button detailBtn = new Button(context);
            detailBtn.setText("Detail");
            detailBtn.setTextSize(tableFontSzie);
            TableRow.LayoutParams params  = new TableRow.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight=Float.parseFloat("0.2");
            detailBtn.setLayoutParams(params);
            detailBtn.setBackgroundColor(colorForBtn);
            final String info = getAllConnectedWifiInfo();

            detailBtn.setOnClickListener(new ModifiedOnClickListener(info) {
                @Override
                public void onClick(View v) {
                    showSimpleDialog(info);
                }
            });

            secondRow.addView(wifiSsid);
            secondRow.addView(wifiFreq);
            secondRow.addView(wifiSecRate);
            secondRow.addView(wifiRssi);
            secondRow.addView(detailBtn);

            wifiTable.addView(secondRow);
            wifiInfo.wifiScan();

            BroadcastReceiver wifiExtra = new BroadcastReceiver() {
                @Override
                public void onReceive(Context c, Intent intent) {
                    Activity activity = getActivity();
                    if(activity!=null){
                        boolean isScanDone = wifiInfo.isScanDone();
                        if(isScanDone){
                            getExtraWifi();
                        }
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            context.registerReceiver(wifiExtra, intentFilter);
        }
        else{
            failMsg("Currently WIFI is not connected or disabled!");
        }
    }

    private void failMsg(String msg){
        wifiTable.removeAllViews();
        TableRow warnRow = new TableRow(context);
        warnRow.setGravity(Gravity.CENTER_HORIZONTAL);
        TableRow.LayoutParams warnParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        warnRow.setLayoutParams(warnParams);

        TextView warnText = new TextView(context);
        warnText.setTextSize(20);

        TableRow.LayoutParams warnTextParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        warnTextParams.setMargins(50,30,50,0);
        warnTextParams.weight = Float.parseFloat("1");

        warnText.setLayoutParams(warnParams);

        warnText.setText(msg);
        warnText.setTextColor(getResources().getColor(R.color.colorAccent));

        warnRow.addView(warnText);

        wifiTable.addView(warnRow);
    }

    public TextView makeTableText(int fontSize, String text, boolean isHead, boolean isEnd, int width){
        TextView textView = new TextView(context);
        textView.setTextSize(fontSize);
        textView.setText(text);
        TableRow.LayoutParams params  = new TableRow.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight=Float.parseFloat("0.2");
        if(!isEnd){
            params.setMargins(0,0,5,0);
        }
        if(isHead)
            textView.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        textView.setLayoutParams(params);
        return textView;
    }

    public TableRow makeTableRow(){
        TableRow tableRow = new TableRow(context);
        tableRow.setGravity(Gravity.CENTER_HORIZONTAL);
        TableRow.LayoutParams params = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = Float.parseFloat("1");
        params.setMargins(10,10,10,0);
        tableRow.setLayoutParams(params);

        return tableRow;
    }

    private void getExtraWifi(){
        int colorForTableRow = getResources().getColor(R.color.colorForTableRow);
        int tableFontSize = 14, rowBackgoundColorIndex = 1, columnWidth = 100;
        HashMap<String, ScanResult> scanResultHashMap = wifiInfo.getScanResultHashMap();
        Iterator hashMapIndex = scanResultHashMap.keySet().iterator();

        if(scanResultHashMap != null){
            int connectedWifiSecurityP = wifiInfo.getSecurityPoint(wifiInfo.getConnectedWifiscanResult());
            wifiSecRate.setText(securityPasTextGreen(connectedWifiSecurityP), TextView.BufferType.SPANNABLE);
            makeClickable(connectedWifiSecurityP, wifiSecRate);


            while(hashMapIndex.hasNext()){
                String key = (String) hashMapIndex.next();
                TableRow tableRow = makeTableRow();
                int securityPoint = wifiInfo.getSecurityPoint(scanResultHashMap.get(key));

                TextView textView1 = makeTableText(tableFontSize, scanResultHashMap.get(key).SSID, false, false, columnWidth);
                TextView textView2 = makeTableText(tableFontSize, ""+scanResultHashMap.get(key).frequency, false, false, columnWidth);

                TextView textView3 = makeTableText(tableFontSize, "", false, false, columnWidth);
                textView3.setText(securityPasTextBlack(securityPoint), TextView.BufferType.SPANNABLE);
                makeClickable(securityPoint, textView3);

                TextView textView4 = makeTableText(tableFontSize, ""+scanResultHashMap.get(key).level, false, false, columnWidth);
                Button detailBtn = makeDetailBtn(tableFontSize, scanResultHashMap.get(key));

                if(rowBackgoundColorIndex%2 ==1){
                    tableRow.setBackgroundColor(colorForTableRow);
                    detailBtn.setBackgroundColor(colorForTableRow);
                }
                rowBackgoundColorIndex++;

                tableRow.addView(textView1);
                tableRow.addView(textView2);
                tableRow.addView(textView3);
                tableRow.addView(textView4);
                tableRow.addView(detailBtn);

                wifiTable.addView(tableRow);
            }
        }
        loadingBar.setVisibility(View.GONE);
        wifiBtn.setVisibility(View.VISIBLE);
        isScanned = true;
    }

    private SpannableStringBuilder securityPasTextBlack(int securityP){
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

        String plusSign = "\u1F7A7";
        String xSign = "\u1F7AE";

        String blackStar = "\u2605";

        String result = "";

        for(int i = 0; i<5; i++){
            result += blackStar;
        }
        SpannableString grayStar = new SpannableString(result);
        grayStar.setSpan(new ForegroundColorSpan(Color.LTGRAY),0,5,0);
        grayStar.setSpan(new ForegroundColorSpan(Color.BLACK),0,securityP/2,0);
        spannableStringBuilder.append(grayStar);

        return spannableStringBuilder;
    }

    private SpannableStringBuilder securityPasTextGreen(int securityP){
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

        String blackStar = "\u2605";

        String result = "";

        for(int i = 0; i<5; i++){
            result += blackStar;
        }
        SpannableString grayStar = new SpannableString(result);
        grayStar.setSpan(new ForegroundColorSpan(Color.LTGRAY),0,5,0);
        grayStar.setSpan(new ForegroundColorSpan(Color.GREEN),0,securityP/2,0);
        spannableStringBuilder.append(grayStar);

        return spannableStringBuilder;
    }

    private String getAllConnectedWifiInfo(){
        String info = "";
        info += "SSID:" + wifiInfo.getSsid() ;
        info += "\n\nMac address:" + wifiInfo.getMacAddress() ;
        info += "\n\nRSSI: " + wifiInfo.getRssi() ;
        info += trimmedConnectionInfo(wifiInfo.getConnectionInfo());
        info += trimmedDhcpInfo(wifiInfo.getDhcpInfo()) ;
        info += "\n\nConfiguration Info: " + wifiInfo.GetCurrentWifiConfiguration() ;
        info += "\n\nDhcpInfo from manager: " + wifiInfo.getWifiManager().getDhcpInfo() ;
        return info;
    }

    private String trimmedConnectionInfo(WifiInfo wifiInfo){
        String info = "\n Connection Information";
        info += "\nBSSID: " + wifiInfo.getBSSID();
        info += "\nSupplicant state: " + wifiInfo.getSupplicantState();
        info += "\nLink Speed: " + wifiInfo.getLinkSpeed();
        info += "\nNet ID: " + wifiInfo.getNetworkId();
        info += "\nHidden: " + wifiInfo.getHiddenSSID();
        info += "\nIP Address: " + wifiInfo.getIpAddress();
        info += "\nDescription: " + wifiInfo.describeContents();

        return info;
    }

    private String trimmedDhcpInfo(DhcpInfo dhcpInfo){
        String dhcp = "\n DHCP Information";
        dhcp += "\nIP Address: " + dhcpInfo.ipAddress;
        dhcp += "\nGateway: " + dhcpInfo.gateway;
        dhcp += "\nNetmask: " + dhcpInfo.netmask;
        dhcp += "\nDNS1: " + dhcpInfo.dns1;
        dhcp += "\nDNS2: " + dhcpInfo.dns2;
        dhcp += "\nServer Address: " + dhcpInfo.serverAddress;
        dhcp += "\nLease duration: " + dhcpInfo.leaseDuration;

        return dhcp;
    }

    public void showSimpleDialog(ScanResult detailInfo) {

        String title = "Detail";
        String message = "";

        message += "SSID: " + detailInfo.SSID;
        message += "\n\nBSSID: " + detailInfo.BSSID;
        message += "\n\nFrequency: " + detailInfo.frequency;
        message += "\n\nLevel: " + detailInfo.level;
        message += "\n\nCapabilities: " + detailInfo.capabilities;
        message += "\nExtra: \n" + detailInfo;

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(""+message)
                .setPositiveButton("OK", null)
                .show();
    }

    public void showSimpleDialog(String detailInfo) {

        String title = "Detail";
        String message = detailInfo;

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(""+message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void makeClickable(final int securityP, TextView textView){

        textView.setOnClickListener(new ModifiedOnClickListener(securityP){
            @Override
            public void onClick(View v) {

                Dialog rankDialog = new Dialog(context, R.style.FullHeightDialog);
                rankDialog.setContentView(R.layout.rating_bar_dialog);
                rankDialog.setCancelable(true);
                RatingBar ratingBar = rankDialog.findViewById(R.id.dialog_ratingbar);
                ratingBar.setIsIndicator(true);
                ratingBar.setStepSize(Float.parseFloat("0.5"));
                ratingBar.setRating(securityP/2);
                rankDialog.show();
            }
        });
    }

    private Button makeDetailBtn(int tableFontSize, final ScanResult scanResult){
        int colorForBtn = getResources().getColor(R.color.colorForDetailBtn);

        Button detailBtn = new Button(context);
        detailBtn.setText("Detail");
        detailBtn.setTextSize(tableFontSize);
        TableRow.LayoutParams params  = new TableRow.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight=Float.parseFloat("0.2");
        detailBtn.setLayoutParams(params);

        detailBtn.setBackgroundColor(colorForBtn);

        detailBtn.setOnClickListener(new ModifiedOnClickListener(scanResult){
            @Override
            public void onClick(View v) {
                showSimpleDialog(scanResult);
            }
        });

        return detailBtn;
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }else{
            wifiBuilder(context);
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your Location service seems to be disabled, please enable it to scan the WiFi around")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        failMsg("Location Service Disabled");
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


}


