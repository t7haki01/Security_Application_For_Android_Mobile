package scurity.app.securityapplicationforandroidmobile;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class splash_activity extends AppCompatActivity {
    private final int DURATION_SPLASH = 3000; //How long stays on the splash screen. Time in ms
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_activity);

        new Handler().postDelayed(new Runnable(){
            public void run(){
                Intent intent = new Intent(splash_activity.this, MainActivity.class); //We are in splash activity, we go to mainactivity
                startActivity(intent);
                finish();
            };
        }, DURATION_SPLASH);
    } //Then it is necesary to modify android manifest, so the splash screen is shown before the mainactivity screen
}