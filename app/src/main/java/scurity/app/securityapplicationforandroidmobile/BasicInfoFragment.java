package scurity.app.securityapplicationforandroidmobile;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class BasicInfoFragment extends Fragment {


    public BasicInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_basic_info, container, false);

        /*
            TODO HERE STARTS THE MAGIC...
         */

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager()
        .getDefaultDisplay().getMetrics(metrics);


        float height=metrics.heightPixels/metrics.xdpi;
        float width=metrics.widthPixels/metrics.ydpi;
//To do. It is needed to add the fragment to somewhere now
        TextView textView =  getView().findViewById(R.id.basic);

        textView.setText(
                "The screen size is:"+(float)Math.sqrt(height*height+width*width) +"\n" +
                        "Manufacturer: " + Build.MANUFACTURER +"\n"

                        +  "Brand:" + Build.BRAND+"\n"
                        +"Model: " + Build.MODEL+"\n"
                        +  "Board: " + Build.BOARD+"\n"
                        + "Hardware: " + Build.HARDWARE+"\n"
                        +  "Serial: " + Build.SERIAL+"\n"

                        + "Bootloader: " + Build.BOOTLOADER+"\n"
                        + "User: " + Build.USER+"\n"
                        +  "Host: " + Build.HOST+"\n"
                        + "Android version: " + Build.VERSION.RELEASE+"\n"
                        +  "API level: " + Build.VERSION.SDK_INT + ""+"\n"
                        +  "Build ID: " + Build.ID+"\n"
                        +  "Build time: " + Build.TIME +"\n"
                        +  "Fingerprint: " + Build.FINGERPRINT+"\n"

        );

    }
}
