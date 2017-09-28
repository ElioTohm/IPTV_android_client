package xms.com.smarttv.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import xms.com.smarttv.R;
import xms.com.smarttv.services.NotificationService;
import xms.com.smarttv.services.onBootService;

public class OnboardingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // init notification intent
        Intent notificationIntent = new Intent(this, NotificationService.class);

        // Starts the IntentService
        this.startService(notificationIntent);

        // init onBootService
        Intent onBootServiceIntent = new Intent(this, onBootService.class);

        //start the onboot service
        this.startService(onBootServiceIntent);
    }
}
