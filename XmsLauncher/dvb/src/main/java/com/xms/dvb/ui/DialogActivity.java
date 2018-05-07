package com.xms.dvb.ui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepFragment;

/**
 * Created by Elio on 12/5/2017.
 */
public class DialogActivity extends Activity {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#21272A")));

        if (savedInstanceState == null) {
            GuidedStepFragment fragment = new XmsDialogFragment();
            GuidedStepFragment.addAsRoot(this, fragment, android.R.id.content);
        }
    }
}

