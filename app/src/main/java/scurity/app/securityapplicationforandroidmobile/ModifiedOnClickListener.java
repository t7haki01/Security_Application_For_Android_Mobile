package scurity.app.securityapplicationforandroidmobile;

import android.net.wifi.ScanResult;
import android.view.View;

public class ModifiedOnClickListener implements View.OnClickListener
{

    ScanResult someValue;
    String someString;

    public ModifiedOnClickListener(ScanResult someValue) {
        this.someValue = someValue;
    }

    public ModifiedOnClickListener(String someString) {
        this.someString = someString;
    }

    @Override
    public void onClick(View v)
    {
    }

};
