package scurity.app.securityapplicationforandroidmobile;

import android.app.Activity;
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

import static com.android.volley.VolleyLog.TAG;

public class TheftAlarmCheckReceiver extends BroadcastReceiver {
    final static protected String THEFT_ALARM_ACTIVATE_ON = "1";
    final static protected String THEFT_ALARM_ACTIVATE_OFF = "0";
    private Context context;
    private TheftAlarmAct theftAlarmAct = null;

    @Override
    public void onReceive(Context context, Intent intent) {

//        StringBuilder sb = new StringBuilder();
//        sb.append("Action: " + intent.getAction() + "\n");
//        sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
//        String log = sb.toString();
//        Log.d(TAG, log);
//        Toast.makeText(context, log, Toast.LENGTH_LONG).show();

        this.context = context;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        boolean isConnected = wifi != null && wifi.isConnectedOrConnecting() ||
                mobile != null && mobile.isConnectedOrConnecting();
        if (isConnected) {
            activateTheAlarm(context);
        }
    }

    private void activateTheAlarm(Context context){
        String url = "http://www.students.oamk.fi/~t7haki01/sysknife/index.php/api/TheftAlarm/mobiles/id/1";
        try{
            theftAlarmAct = new TheftAlarmAct(context);
        }catch(IllegalStateException e){
            e.printStackTrace();
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            Log.d("Response: ", response.getJSONObject(0).getString("alert"));
                            Log.d("Response: ", ""+response.getJSONObject(0).get("alert"));
                            String alertState = response.getJSONObject(0).getString("alert");
                            if(alertState.equalsIgnoreCase(THEFT_ALARM_ACTIVATE_ON)){
                                theftAlarmAct.ringTheBell();
                            }
                            else if(alertState.equalsIgnoreCase(THEFT_ALARM_ACTIVATE_OFF)){
                                theftAlarmAct.killTheBell();
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    error.getLocalizedMessage();
                    }
                });
        VolleyHttpSingletone.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }
}
