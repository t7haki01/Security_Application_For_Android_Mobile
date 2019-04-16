package scurity.app.securityapplicationforandroidmobile;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class TheftAlarmAct {

    private MediaPlayer alarm = null;
    private ComponentName componentName;
    private DevicePolicyManager devicePolicyManager;
    private Context context;
    private WarningAlarm warningAlarm;

    public TheftAlarmAct(Context context){
        this.context = context;
//        this.alarm = new MediaPlayer();
        this.componentName = new ComponentName(this.context, AdminForLock.class);
        this.devicePolicyManager = (DevicePolicyManager) this.context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        warningAlarm = new WarningAlarm();
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
}
