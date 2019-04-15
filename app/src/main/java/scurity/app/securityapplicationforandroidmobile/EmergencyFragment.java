package scurity.app.securityapplicationforandroidmobile;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmergencyFragment extends Fragment implements View.OnClickListener {
    private static final String[] CALL_PERMISSIONS = {
            Manifest.permission.CALL_PHONE
    };

    private Button dial;
    


    public EmergencyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_emergency, container, false);
        Context context=view.getContext();
//paste
        dial = (Button) view.findViewById(R.id.btn_dial);
        dial.setOnClickListener(this);
        int permissionCallPhone = ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
        if (permissionCallPhone != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    CALL_PERMISSIONS,
                    1

            );
        }

    return view;

    }


    //paste the rest

    public void onDialButton (View v){
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:+123"));
        startActivity(intent);
        //verifyPermissions();
    }
}
