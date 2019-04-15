package scurity.app.securityapplicationforandroidmobile;

import android.net.wifi.ScanResult;
import android.view.View;

public class ModifiedOnClickListener implements View.OnClickListener
{

    ScanResult someValue;
    String someString;
    int someInt;

    public ModifiedOnClickListener(ScanResult someValue) {
        this.someValue = someValue;
    }

    public ModifiedOnClickListener(String someString) {
        this.someString = someString;
    }

    public ModifiedOnClickListener(int someInt) {
        this.someInt = someInt;
    }

    @Override
    public void onClick(View v)
    {
    }

};
