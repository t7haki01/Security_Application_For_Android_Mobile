package scurity.app.securityapplicationforandroidmobile;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class AdminForLock extends DeviceAdminReceiver {

    static SharedPreferences getPreferences(Context context){
        return context.getSharedPreferences(DeviceAdminReceiver.class.getName(),0);
    }

    void showToast(Context context, CharSequence msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context, "DEVICE ADMIN ENABLED");
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return "If you disable this option then theft alarm would not be able to lock the phone";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        showToast(context, "DEVICE ADMIN: DISABLED");
    }
}
