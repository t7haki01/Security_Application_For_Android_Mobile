package scurity.app.securityapplicationforandroidmobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

public class TheftAlarmCheckReceiver extends BroadcastReceiver {
    final static protected String THEFT_ALARM_ACTIVATE_ON = "1";
    final static protected String THEFT_ALARM_ACTIVATE_OFF = "0";
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context,"now sysknife know that booting up device", Toast.LENGTH_SHORT).show();
//        Log.d("Sysknife", "Detecting the bootup");

        this.context = context;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        boolean isConnected = wifi != null && wifi.isConnectedOrConnecting() ||
                mobile != null && mobile.isConnectedOrConnecting();
        if (isConnected) {
            Toast.makeText(context,"now sysknife got the change of wifi state, connected", Toast.LENGTH_SHORT).show();
            Log.d("Network Available ", "YES");
            activateTheAlarm(context);
        } else {
            Toast.makeText(context,"now sysknife got the change of wifi state, disconnected", Toast.LENGTH_SHORT).show();
            Log.d("Network Available ", "NO");
        }
    }

    private void activateTheAlarm(Context context){
        String url = "http://www.students.oamk.fi/~t7haki01/sysknife/index.php/api/TheftAlarm/mobiles/id/1";
        final Context thisContext = context;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            Log.d("Response: ", response.getJSONObject(0).getString("alert"));
                            Log.d("Response: ", ""+response.getJSONObject(0).get("alert"));
                            String alertState = response.getJSONObject(0).getString("alert");
                            if(alertState.equalsIgnoreCase(THEFT_ALARM_ACTIVATE_ON)){
//                                TheftAlarmFragment alarmFragment = new TheftAlarmFragment();
//                                alarmFragment.ringTheBell();
                                TheftAlarmAct theftAlarmAct = new TheftAlarmAct(thisContext);
                                theftAlarmAct.ringTheBell();
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("Error", ""+error);
                    }
                });
        VolleyHttpSingletone.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }
}
