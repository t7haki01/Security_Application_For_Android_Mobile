package scurity.app.securityapplicationforandroidmobile;


import android.app.Activity;
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
    private Button linkBtn = null;
    private ConstraintLayout unregisteredLayout = null;
    private ConstraintLayout registeredLayout = null;
    private ConstraintLayout loadingLayout = null;

    private View view = null;

    TheftAlarmAct theftAlarmAct = null;

    public TheftAlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_theft_alarm, container, false);
        }
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if(context == null) {
            alarm = new MediaPlayer();
            context = getContext();
            theftAlarmFragment = getView().findViewById(R.id.frame_theftAlarm);
            unregisteredLayout = getView().findViewById(R.id.unregistered_layout);
            registeredLayout = getView().findViewById(R.id.registered_layout);
            loadingLayout = getView().findViewById(R.id.loading_layout);

            new TheftAlarmAct(loadingLayout, registeredLayout, unregisteredLayout, context).checkRegister();

            componentName = new ComponentName(this.context, AdminForLock.class);
            devicePolicyManager = (DevicePolicyManager) this.context.getSystemService(Context.DEVICE_POLICY_SERVICE);

            linkBtn = getView().findViewById(R.id.signUp_btn);
            linkBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linkToSite();
                }
            });
        }else{
            new TheftAlarmAct(loadingLayout, registeredLayout, unregisteredLayout, context).checkRegister();
        }

        if(!devicePolicyManager.isAdminActive(componentName)){
            makeAdminAble();
        }
    }

    private void makeAdminAble(){
        ((MainActivity) getActivity()).addAdminToDevice(componentName);
    }

    private void linkToSite(){
        Uri uri = Uri.parse("http://www.students.oamk.fi/~t7haki01/sysknife/demo/site/pricing");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        ((MainActivity) context ).setGoingOut(true);
    }

    public void registerCheck(){
        new TheftAlarmAct(loadingLayout, registeredLayout, unregisteredLayout, context).checkRegister();
    }
}

