package scurity.app.securityapplicationforandroidmobile;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class WifiScannerFragment extends Fragment {
    private Context context;
    private FrameLayout wifiFrame;
    private TableLayout wifiTable;
    private WifiGetter wifiInfo;
    Button wifiBtn;
    ProgressBar loadingBar;
    int securityPointId;
    TextView wifiSecRate;


    public WifiScannerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wifi_scanner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

            this.context = getContext();
            this.wifiFrame = getView().findViewById(R.id.frame_wifiScanner);
            this.wifiTable = getView().findViewById(R.id.wifiTable);
            this.securityPointId = ViewCompat.generateViewId();

            this.wifiBtn = getView().findViewById(R.id.wifi_btn);
            this.wifiBtn.setVisibility(View.VISIBLE);
            this.loadingBar = getView().findViewById(R.id.loading_bar);

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

    public void wifiBtnClicked(Context context){
        ((MainActivity) getActivity()).setWifiScanned(true);
        wifiTable.removeAllViews();

        wifiInfo = new WifiGetter(context);

        int colorForBtn = getResources().getColor(R.color.colorForDetailBtn);
        int tableFontSzie = 14;
        int columnWidth = 100;

        if(wifiInfo.isWifiConnected()){

            wifiBtn.setVisibility(View.GONE);
            loadingBar.setVisibility(View.VISIBLE);

//            setProgressDialogState(true);

            TableRow firstRow = new TableRow(context);
            firstRow.setGravity(Gravity.CENTER_HORIZONTAL);
            TableRow.LayoutParams headParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            headParams.weight = Float.parseFloat("1");
            headParams.setMargins(10,10,10,0);
            firstRow.setLayoutParams(headParams);

            TextView headName = makeTableText(tableFontSzie, "WIFI", true, false, columnWidth);
            TextView headFrq = makeTableText(tableFontSzie, "Channel", true, false, columnWidth);
            TextView headSecPoint = makeTableText(tableFontSzie, "Rate", true, false, columnWidth);
            TextView headSignal = makeTableText(tableFontSzie, "Strength", true, false, columnWidth);
            TextView headLink = makeTableText(tableFontSzie, "Detail", true, true, columnWidth);

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
                    boolean isScanDone = wifiInfo.isScanDone();
                    if(isScanDone){
                        getExtraWifi();
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            context.registerReceiver(wifiExtra, intentFilter);
        }
        else{
            wifiTable.removeAllViews();

            TableRow warnRow = new TableRow(context);
            warnRow.setGravity(Gravity.CENTER_HORIZONTAL);
            TableRow.LayoutParams warnParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            warnRow.setLayoutParams(warnParams);

            TextView warnText = new TextView(context);
            warnText.setTextSize(20);

            TableRow.LayoutParams warnTextParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            warnTextParams.setMargins(20,30,20,0);
            warnTextParams.weight = Float.parseFloat("1");

            warnText.setLayoutParams(warnParams);

            warnText.setText("Currently WIFI is not connected or unable!");
            warnText.setTextColor(getResources().getColor(R.color.colorAccent));

            warnRow.addView(warnText);

            wifiTable.addView(warnRow);

        }
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

        this.wifiSecRate.setText(""+wifiInfo.getSecurityPoint(wifiInfo.getConnectedWifiscanResult()));

        while(hashMapIndex.hasNext()){
            String key = (String) hashMapIndex.next();
            TableRow tableRow = makeTableRow();
            int securityPoint = wifiInfo.getSecurityPoint(scanResultHashMap.get(key));

            TextView textView1 = makeTableText(tableFontSize, scanResultHashMap.get(key).SSID, false, false, columnWidth);
            TextView textView2 = makeTableText(tableFontSize, ""+scanResultHashMap.get(key).frequency, false, false, columnWidth);
            TextView textView3 = makeTableText(tableFontSize, ""+securityPoint, false, false, columnWidth);
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
//        setProgressDialogState(false);
        loadingBar.setVisibility(View.GONE);
        wifiBtn.setVisibility(View.VISIBLE);
    }

    private void setProgressDialogState(boolean isDone){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Scaning the Wifi near");
        progressDialog.setCancelable(false);
        if(isDone == true){
            progressDialog.show();
        }
        else if(isDone == false){
            progressDialog.dismiss();
        }
    }

    private void rateSecurity(int securityPoint){

        RatingBar ratingBar = new RatingBar(context, null, R.attr.ratingBarStyleSmall);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ratingBar.setLayoutParams(params);

        ratingBar.setNumStars(5);
        ratingBar.setRating(securityPoint);
        ratingBar.setIsIndicator(true);
//        ratingBar.setBackgroundColor(Color.BLUE);
    }

    public void showSimpleDialog(ScanResult detailInfo) {

        String title = "Detail";
        String message = "";

        message += "SSID: " + detailInfo.SSID;
        message += "\n\nBSSID: " + detailInfo.BSSID;
        message += "\n\nFrequency: " + detailInfo.frequency;
        message += "\n\nLevel: " + detailInfo.level;
        message += "\n\nCapabilities: " + detailInfo.capabilities;
        message += "\n\n\n\nExtra for checking: \n" + detailInfo;

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

    private String getAllConnectedWifiInfo(){
        String info = "";
        info += "SSID:" + wifiInfo.getSsid() ;
        info += "\n\nMac address:" + wifiInfo.getMacAddress() ;
        info += "\n\nRSSI: " + wifiInfo.getRssi() ;
        info += "\n\nConnection Info: " + wifiInfo.getConnectionInfo() ;
        info += "\n\nDHCP Info: " + wifiInfo.getDhcpInfo() ;
        info += "\n\nConfiguration Info: " + wifiInfo.GetCurrentWifiConfiguration() ;
        info += "\n\nDhcpInfo from manager: " + wifiInfo.getWifiManager().getDhcpInfo() ;
        info += "\n\nState from manager: " + wifiInfo.getWifiManager().getWifiState() ;
        return info;
    }

}


