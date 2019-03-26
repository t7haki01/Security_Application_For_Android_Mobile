package scurity.app.securityapplicationforandroidmobile;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    // Fragments
    private FragmentManager fragmentManager;
    private WifiScannerFragment wifiScanFrag;
    private SettingsCheckerFragment settingCheckFrag;
    private UsageTrackerFragment usageTrackFrag;
    private TheftAlarmFragment theftAlarmFrag;
    private BatteryStateFragment batteryStateFrag;
    private AboutAppFragment aboutAppFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set wifiScanFrag as first fragment
        fragmentManager = getSupportFragmentManager();
        wifiScanFrag = new WifiScannerFragment();
        fragmentManager.beginTransaction().replace(
                R.id.main_fragment, wifiScanFrag).commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Button action at bottom right end of screen (mail-icon)
        // Not needed at moment, but maybe it's useful to have it for later - [SANDRO]
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_wifiscanner:
                setTitle("WiFi Scanner");
                wifiScanFrag = new WifiScannerFragment();
                fragmentManager.beginTransaction().replace(
                        R.id.main_fragment, wifiScanFrag).commit();
                break;
            case R.id.nav_settingschecker:
                setTitle("Settings Checker");
                settingCheckFrag = new SettingsCheckerFragment();
                fragmentManager.beginTransaction().replace(
                        R.id.main_fragment, settingCheckFrag).commit();
                break;
            case R.id.nav_usagetracker:
                setTitle("Usage Tracker");
                usageTrackFrag = new UsageTrackerFragment();
                fragmentManager.beginTransaction().replace(
                        R.id.main_fragment, usageTrackFrag).commit();
                break;
            case R.id.nav_theftalarm:
                setTitle("Theft Alarm");
                theftAlarmFrag = new TheftAlarmFragment();
                fragmentManager.beginTransaction().replace(
                        R.id.main_fragment, theftAlarmFrag).commit();
                break;
            case R.id.nav_batterystate:
                setTitle("Battery State");
                batteryStateFrag = new BatteryStateFragment();
                fragmentManager.beginTransaction().replace(
                        R.id.main_fragment, batteryStateFrag).commit();
                break;
            case R.id.nav_aboutapp:
                setTitle("About SysKnife");
                aboutAppFrag = new AboutAppFragment();
                fragmentManager.beginTransaction().replace(
                        R.id.main_fragment, aboutAppFrag).commit();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
