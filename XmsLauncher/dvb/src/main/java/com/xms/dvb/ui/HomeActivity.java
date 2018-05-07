package com.xms.dvb.ui;

import android.app.Activity;
import android.os.Bundle;

import com.xms.dvb.R;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
