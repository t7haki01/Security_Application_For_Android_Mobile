package scurity.app.securityapplicationforandroidmobile;


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

    public void wifiBtnClicked(Context context){
        wifiTable.removeAllViews();

        wifiInfo = new WifiGetter(context);

        if(wifiInfo.isWifiConnected()){

//            wifiBtn.setVisibility(View.GONE);
//            loadingBar.setVisibility(View.VISIBLE);

            setProgressDialogState(true);

            TableRow firstRow = new TableRow(context);
            firstRow.setGravity(Gravity.CENTER_HORIZONTAL);
            TableRow.LayoutParams headParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            headParams.weight = Float.parseFloat("1");
            headParams.setMargins(0,10,0,0);
            firstRow.setLayoutParams(headParams);

            int tableFontSzie = 14;

            TextView headName = makeTableText(tableFontSzie, "WIFI", true, false);
            TextView headSec = makeTableText(tableFontSzie, "Frequency", true, false);
            TextView headSecPoint = makeTableText(tableFontSzie, "Rate", true, false);
            TextView headSignal = makeTableText(tableFontSzie, "STRENGTH", true, false);
            TextView headLink = makeTableText(tableFontSzie, "Detail", true, true);

            firstRow.addView(headName);
            firstRow.addView(headSec);
            firstRow.addView(headSecPoint);
            firstRow.addView(headSignal);
            firstRow.addView(headLink);

            wifiTable.addView(firstRow);

            TableRow secondRow = new TableRow(context);
            secondRow.setGravity(Gravity.CENTER_HORIZONTAL);

            TableRow.LayoutParams secondParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            secondParams.setMargins(0,10,0,0);
            secondParams.weight = Float.parseFloat("1");
            secondRow.setLayoutParams(secondParams);

            int connectedWifiColor = Color.rgb(118, 200, 19);

            TextView wifiSsid = makeTableText(tableFontSzie, wifiInfo.getSsid(), false, false );
            wifiSsid.setTextColor(connectedWifiColor);

            TextView wifiSec = makeTableText(tableFontSzie, wifiInfo.getDhcpInfo().gateway+"", false, false );
            wifiSec.setTextColor(connectedWifiColor);

            TextView wifiSecPoint = makeTableText(tableFontSzie, ""+3, false, false );
            wifiSecPoint.setTextColor(connectedWifiColor);

            TextView wifiRssi = makeTableText(tableFontSzie, wifiInfo.getRssi(), false, false );
            wifiRssi.setTextColor(connectedWifiColor);

            TextView wifiLink = makeTableText(tableFontSzie, wifiInfo.getLinkSpeed(), false, true );
            wifiLink.setTextColor(connectedWifiColor);

            secondRow.addView(wifiSsid);
            secondRow.addView(wifiSec);
            secondRow.addView(wifiSecPoint);
            secondRow.addView(wifiRssi);
            secondRow.addView(wifiLink);

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
            warnTextParams.setMargins(0,10,0,0);
            warnTextParams.weight = Float.parseFloat("1");

            warnText.setLayoutParams(warnParams);

            warnText.setText("Currently WIFI is not connected or unable!");
            warnText.setTextColor(Color.RED);

            warnRow.addView(warnText);

            wifiTable.addView(warnRow);

        }
    }

    public TextView makeTableText(int fontSize, String text, boolean isHead, boolean isEnd){
        TextView textView = new TextView(context);
        textView.setTextSize(fontSize);
        textView.setText(text);
        TableRow.LayoutParams params  = new TableRow.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT);
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
        params.setMargins(0,10,0,0);
        tableRow.setLayoutParams(params);

        return tableRow;
    }

    private void getExtraWifi(){
        int tableFontSzie = 14, rowBackgoundColorIndex = 1;
        HashMap<String, ScanResult> scanResultHashMap = wifiInfo.getScanResultHashMap();
        Iterator hashMapIndex = scanResultHashMap.keySet().iterator();

        while(hashMapIndex.hasNext()){
            String key = (String) hashMapIndex.next();
            TableRow tableRow = makeTableRow();
            if(rowBackgoundColorIndex%2 ==1){
                tableRow.setBackgroundColor(R.drawable.row_gray_background);
            }
            rowBackgoundColorIndex++;
            TextView textView1 = makeTableText(tableFontSzie, scanResultHashMap.get(key).SSID, false, false);
            TextView textView2 = makeTableText(tableFontSzie, ""+scanResultHashMap.get(key).frequency, false, false);
            TextView textView3 = makeTableText(tableFontSzie, ""+wifiInfo.getSecurityPoint(scanResultHashMap.get(key)), false, false);
            TextView textView4 = makeTableText(tableFontSzie, ""+scanResultHashMap.get(key).level, false, false);
            TextView textView5 = makeTableText(tableFontSzie, "More", false, true);
            tableRow.addView(textView1);
            tableRow.addView(textView2);
            tableRow.addView(textView3);
            tableRow.addView(textView4);
            tableRow.addView(textView5);
            wifiTable.addView(tableRow);
        }
        setProgressDialogState(false);
//        loadingBar.setVisibility(View.GONE);
//        wifiBtn.setVisibility(View.VISIBLE);
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
}
