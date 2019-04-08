package scurity.app.securityapplicationforandroidmobile;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class TheftAlarmAct {
    private MediaPlayer alarm;
    private ComponentName componentName;
    private DevicePolicyManager devicePolicyManager;
    private Context context;

    public TheftAlarmAct(Context context){
        this.context = context;
        this.alarm = new MediaPlayer();
        this.componentName = new ComponentName(this.context, AdminForLock.class);
        this.devicePolicyManager = (DevicePolicyManager) this.context.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }


    public void ringTheBell(){
        makeSoundMax();
        makeRunEvenSleep();
        makeScreenLock();
        /**
         * Choose among thoes three alarm or something else
         * kihun 3.4.2019
         * */
        this.alarm = MediaPlayer.create(this.context, R.raw.ring3);
        alarm.setLooping(true);
//        alarm.start();
        alarm.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                alarm.start();
            }
        });

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
            if(alarm.isLooping()){
                alarm.pause();
            }
            alarm.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    alarm.stop();
                }
            });
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
//        else{
//            ((MainActivity) getActivity()).addAdminToDevice(this.componentName);
//            WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
//            params.screenBrightness = 0;
//            getActivity().getWindow().setAttributes(params);
//        }
    }
}
