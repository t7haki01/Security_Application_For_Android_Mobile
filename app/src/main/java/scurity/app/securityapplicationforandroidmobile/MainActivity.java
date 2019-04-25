package scurity.app.securityapplicationforandroidmobile;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private Context context;
    final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;
    final int ADMIN_REQUEST_GRANTED = 11;

    // Fragments
    private FragmentManager fragmentManager;
    private WifiScannerFragment wifiScanFrag = null;
    private SettingsCheckerFragment settingCheckFrag;
    private TheftAlarmFragment theftAlarmFrag;
    private CompassFragment compassFragment;
    private BasicInfoFragment basicInfoFrag;
    private EmergencyFragment emergencyFragment;
    private AboutAppFragment aboutAppFrag;

    private boolean isWifiScanned = false;
    public boolean isRegistered = false;
    public boolean isLoadingDone = false;
    private boolean isGoingOut = false;

    public void setGoingOut(boolean goingOut) {
        isGoingOut = goingOut;
    }

    public void setWifiScanned(boolean wifiScanned) {
        isWifiScanned = wifiScanned;
    }

    @Override
    protected void onResume() {
        if(isGoingOut){
            theftAlarmFrag.registerCheck();
            isGoingOut = false;
        }
        else{
            isGoingOut = false;
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        // Set wifiScanFrag as first fragment
        fragmentManager = getSupportFragmentManager();
        wifiScanFrag = new WifiScannerFragment();

        fragmentManager.beginTransaction().replace(
                R.id.main_fragment, wifiScanFrag).commit();


        /*
         * Since Marshmallow, Android changed policy for the "better user privacy",
         * So we(app) need to acquire permission from user directly with asking "nicely"
         * to scan wifi near since it is able to get personal location with Wifi information near(such as RSSI, IP and so on)
         * https://developer.android.com/about/versions/marshmallow/android-6.0-changes.html#behavior-hardware-id
         * **/

        // Marshmallow+ Permission APIs
        if (Build.VERSION.SDK_INT >= 23) {
            permissionHandler();
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * In case, nav (drawer) is opened and back btn is pressed to close
     * */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        switch (id) {
            case R.id.nav_wifiscanner:
                setTitle("WiFi Scanner");
                if(wifiScanFrag == null){
                    wifiScanFrag = new WifiScannerFragment();
                }
                fragmentTransaction.replace(R.id.main_fragment, wifiScanFrag);
                fragmentTransaction.commit();
                break;
            case R.id.nav_settingschecker:
                setTitle("Settings & Connections");
                if(settingCheckFrag == null){
                    settingCheckFrag = new SettingsCheckerFragment();
                }
                fragmentManager.beginTransaction().replace(
                        R.id.main_fragment, settingCheckFrag).commit();
                break;
            case R.id.nav_theftalarm:
                setTitle("Theft Alarm");
                if(theftAlarmFrag == null){
                    theftAlarmFrag = new TheftAlarmFragment();
                }
                fragmentTransaction.replace(R.id.main_fragment, theftAlarmFrag);
                fragmentTransaction.commit();
                break;

            case R.id.nav_compass:
                setTitle("Compass");
                if(compassFragment == null){
                    compassFragment = new CompassFragment();
                }
                fragmentManager.beginTransaction().replace(
                        R.id.main_fragment, compassFragment).commit();
                break;

            case R.id.nav_basicinfo:
                setTitle("Device Info");
                if(basicInfoFrag == null){
                    basicInfoFrag = new BasicInfoFragment();
                }
                fragmentManager.beginTransaction().replace(
                        R.id.main_fragment, basicInfoFrag).commit();
                break;
            case R.id.nav_emergency:
                setTitle("Emergency");
                if(emergencyFragment == null){
                    emergencyFragment = new EmergencyFragment();
                }

                fragmentTransaction.replace(R.id.main_fragment, emergencyFragment);
                fragmentTransaction.commit();
                break;
            case R.id.nav_aboutapp:
                setTitle("About SysKnife");
                if(aboutAppFrag == null){
                    aboutAppFrag = new AboutAppFragment();
                }
                fragmentManager.beginTransaction().replace(
                        R.id.main_fragment, aboutAppFrag).commit();
                break;
            default:
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addAdminToDevice(ComponentName componentName){
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "For forcing the mobile to be locked, app requires Admin authority");
        startActivityForResult(intent, ADMIN_REQUEST_GRANTED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case ADMIN_REQUEST_GRANTED:
                if(resultCode == Activity.RESULT_OK){
                    Log.d("From activity", resultCode + " and "+ requestCode);
                }
                else{
                    Log.d("From activity", resultCode + " and "+ requestCode);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * From here for handling permissions App needed,
     * which is currently used for location permission in case of higher versions of android than Marshmallow
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    // All Permissions Granted
                    Toast.makeText(MainActivity.this, "All Permission GRANTED !! Good to go!", Toast.LENGTH_SHORT).show();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "One or More Permissions are DENIED, App Requires Permissions to work on it ", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void permissionHandler() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("Show Location");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {

                // Need Rationale
                String message = "App need access to " + permissionsNeeded.get(0);

                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);

                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }

        Toast.makeText(MainActivity.this, "No new Permission Required - Everything Good to go!", Toast.LENGTH_SHORT).show();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {

        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);

            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }
}