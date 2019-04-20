package scurity.app.securityapplicationforandroidmobile;


import android.content.Context;
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
    private TextView textView;

    public BasicInfoFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_basic_info, container, false);

        /*
            TODO HERE STARTS THE MAGIC...
         */
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        textView = view.findViewById(R.id.text_view);

        double height=metrics.heightPixels/metrics.xdpi;
        double width=metrics.widthPixels/metrics.ydpi;


        textView.setText(
                "The screen size is:"+Math.sqrt(height*height+width*width) +"\n\n" +
                        "Manufacturer: " + Build.MANUFACTURER +"\n"

                        +  "Brand:" + Build.BRAND+"\n\n"
                        +"Model: " + Build.MODEL+"\n\n"
                        +  "Board: " + Build.BOARD+"\n\n"
                        + "Hardware: " + Build.HARDWARE+"\n\n"
                        +  "Serial: " + Build.SERIAL+"\n\n"

                        + "Bootloader: " + Build.BOOTLOADER+"\n\n"
                        + "User: " + Build.USER+"\n\n"
                        +  "Host: " + Build.HOST+"\n\n"
                        + "Android version: " + Build.VERSION.RELEASE+"\n\n"
                        +  "API level: " + Build.VERSION.SDK_INT + ""+"\n\n"
                        +  "Build ID: " + Build.ID+"\n\n"
                        +  "Build time: " + Build.TIME +"\n\n"
                        +  "Fingerprint: " + Build.FINGERPRINT+"\n\n"
        );

        return view;
    }


}
