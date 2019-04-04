package scurity.app.securityapplicationforandroidmobile;


import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class TheftAlarmFragment extends Fragment {

    private Context context;
    private MediaPlayer alarm;
    private FrameLayout theftAlarmFragment;
    private ComponentName componentName;
    private DevicePolicyManager devicePolicyManager;

    public TheftAlarmFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_theft_alarm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.context = getContext();
        this.alarm = new MediaPlayer();
        this.theftAlarmFragment = getView().findViewById(R.id.frame_theftAlarm);
        this.componentName = new ComponentName(this.context, AdminForLock.class);
        this.devicePolicyManager = (DevicePolicyManager) this.context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        if(!this.devicePolicyManager.isAdminActive(componentName)){
            ((MainActivity) getActivity()).addAdminToDevice(this.componentName);
        }

        LinearLayout layout = new LinearLayout(this.context);

        Button testBtn = new Button(this.context);
        testBtn.setText("Lock");
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringTheBell();
            }
        });

        Button adminBtn = new Button(this.context);
        adminBtn.setText("Admin");
        adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmCheck();
            }
        });



        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        testBtn.setLayoutParams(params);
        adminBtn.setLayoutParams(params);

        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(testBtn);
        layout.addView(adminBtn);
        this.theftAlarmFragment.addView(layout);


    }

    void alertOn(boolean alarmOn){
        if(alarmOn){
            ringTheBell();
        }
    }

    private void makeSoundMax(){
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(this.context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(audioManager.STREAM_MUSIC), 0);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, audioManager.getStreamMaxVolume(audioManager.STREAM_ALARM), 0);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(audioManager.STREAM_RING), 0);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, audioManager.getStreamMaxVolume(audioManager.STREAM_NOTIFICATION), 0);
    }

    private void makeRunEvenSleep(){
        PowerManager powerManager = (PowerManager)this.context.getSystemService(this.context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:theftAlarm");
        wakeLock.acquire();
    }

    private void makeScreenLock(){

        if(this.devicePolicyManager.isAdminActive(this.componentName)){
            this.devicePolicyManager.lockNow();
        }else{
            ((MainActivity) getActivity()).addAdminToDevice(this.componentName);
            WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
            params.screenBrightness = 0;
            getActivity().getWindow().setAttributes(params);
        }
    }

    private void makeAdminAble(){
        ((MainActivity) getActivity()).addAdminToDevice(this.componentName);
    }

    private void ringTheBell(){
        makeSoundMax();
        makeRunEvenSleep();
        makeScreenLock();
        /**
         * Choose among thoes three alarm or something else
         * kihun 3.4.2019
         * */
        this.alarm = MediaPlayer.create(this.context, R.raw.ring3);
        alarm.setLooping(true);
        alarm.start();
    }


    void alarmCheck(){

        String url = "http://www.students.oamk.fi/~t7haki01/sysknife/index.php/api/TheftAlarm/mobiles/id/4";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            Log.d("Response: ", response.getJSONObject(0).getString("alert"));
                            Log.d("Response: ", ""+response.getJSONObject(0).get("alert"));
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

//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("Response: ", response.toString());
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // TODO: Handle error
//                        Log.d("Error", ""+error);
//                    }
//                });

        VolleyHttpSingletone.getInstance(this.context).addToRequestQueue(jsonArrayRequest);

    }
}
