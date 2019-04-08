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

        Button stopBtn = new Button(this.context);
        stopBtn.setText("Deactivate");
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TheftAlarmAct theftAlarmAct = new TheftAlarmAct(context);
                theftAlarmAct.killTheBell();
            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        stopBtn.setLayoutParams(params);

        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(stopBtn);
        this.theftAlarmFragment.addView(layout);


    }
    private void makeAdminAble(){
        ((MainActivity) getActivity()).addAdminToDevice(this.componentName);
    }
}
