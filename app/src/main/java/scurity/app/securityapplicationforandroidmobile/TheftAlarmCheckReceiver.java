package scurity.app.securityapplicationforandroidmobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class TheftAlarmCheckReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"now sysknife know that booting up device", Toast.LENGTH_SHORT).show();
        Log.d("Sysknife", "Detecting the bootup");

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        boolean isConnected = wifi != null && wifi.isConnectedOrConnecting() ||
                mobile != null && mobile.isConnectedOrConnecting();
//        if (isConnected) {
//            Toast.makeText(context,"now sysknife got the change of wifi state, connected", Toast.LENGTH_SHORT).show();
//            Log.d("Network Available ", "YES");
//        } else {
//            Toast.makeText(context,"now sysknife got the change of wifi state, disconnected", Toast.LENGTH_SHORT).show();
//            Log.d("Network Available ", "NO");
//        }
    }
}
