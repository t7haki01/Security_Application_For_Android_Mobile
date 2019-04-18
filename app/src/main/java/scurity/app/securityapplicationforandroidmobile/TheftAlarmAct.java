package scurity.app.securityapplicationforandroidmobile;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

public class TheftAlarmAct {

    private MediaPlayer alarm = null;
    private ComponentName componentName;
    private DevicePolicyManager devicePolicyManager;
    private Context context;
    private WarningAlarm warningAlarm;
    ConstraintLayout loadingLayout = null;
    ConstraintLayout registeredLayout = null;
    ConstraintLayout unregisteredLayout = null;

    public TheftAlarmAct(Context context){
        this.context = context;
//        this.alarm = new MediaPlayer();
        this.componentName = new ComponentName(this.context, AdminForLock.class);
        this.devicePolicyManager = (DevicePolicyManager) this.context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        warningAlarm = new WarningAlarm();
    }

    public TheftAlarmAct(ConstraintLayout loadingLayout, ConstraintLayout registeredLayout, ConstraintLayout unregisteredLayout,
                         Context context){
        this.registeredLayout = registeredLayout;
        this.loadingLayout = loadingLayout;
        this.unregisteredLayout = unregisteredLayout;
        this.context = context;
    }

    public void ringTheBell(){
        makeSoundMax();
        makeRunEvenSleep();
        makeScreenLock();
        /**
         * Choose among thoes three alarm or something else
         * kihun 3.4.2019
         * */
        //This is for the stoppable way of implementing
        warningAlarm.play(context, R.raw.ring);

        /**This is kinda unstoppable unless alarm state is changed and receiever check again since mediaplayer already released so can not get the stop context*/
//        if( alarm == null){
//            alarm = MediaPlayer.create(this.context, R.raw.ring3);
//            if(!alarm.isPlaying()){
//                alarm.setLooping(true);
//                alarm.start();
//            }
//        }
    }
    private void makeSoundMax(){
        AudioManager audioManager = (AudioManager) this.context.getApplicationContext().getSystemService(this.context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC), 0);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, audioManager.getStreamMaxVolume(audioManager.STREAM_ALARM), 0);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(audioManager.STREAM_RING), 0);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, audioManager.getStreamMaxVolume(audioManager.STREAM_NOTIFICATION), 0);
    }

    public void killTheBell(){
        try{
            warningAlarm.stop(context);
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    private void makeRunEvenSleep(){
        PowerManager powerManager = (PowerManager)this.context.getSystemService(this.context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:theftAlarm");
        wakeLock.acquire();
    }

    private void makeScreenLock(){

        if(this.devicePolicyManager.isAdminActive(this.componentName)){
            this.devicePolicyManager.lockNow();
        }
        /**
         * Just in case user does not allow the admin policy to our app
         * Mimic the lock function with brightness as zero
         * it used to be automatically locked in lower version of android
         * */
        else{
            ((MainActivity) context).addAdminToDevice(this.componentName);
            WindowManager.LayoutParams params = ((MainActivity)context).getWindow().getAttributes();
            params.screenBrightness = 0;
            ((MainActivity) context).getWindow().setAttributes(params);
        }
    }

    public void checkRegister(){
        String url = "http://www.students.oamk.fi/~t7haki01/sysknife/index.php/api/TheftAlarm/mobiles/id/123";
        setVisual(false, false);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        String id = null;
                        setVisual(true, true);
                        try{
                            Log.d("Response: ", response.getJSONObject(0).getString("id"));
                            Log.d("Response: ", ""+response.getJSONObject(0).get("id"));
                            Log.d("Response", response+"");
                            try{
                                id = response.getJSONObject(0).getString("id");
                            }catch(NullPointerException e){
                                e.getLocalizedMessage();
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        setVisual(true, false);
                        error.getLocalizedMessage();
                    }
                });
        VolleyHttpSingletone.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    private void setVisual(boolean isLoadingDone, boolean isRegistered){
        if(isLoadingDone){
            loadingLayout.setVisibility(View.GONE);
            if(isRegistered){
                registeredLayout.setVisibility(View.VISIBLE);
                unregisteredLayout.setVisibility(View.GONE);
            }else{
                registeredLayout.setVisibility(View.GONE);
                unregisteredLayout.setVisibility(View.VISIBLE);
            }
        }else{
            loadingLayout.setVisibility(View.VISIBLE);
        }
    }
}
