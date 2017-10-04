package xms.com.smarttv.UI;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import xms.com.smarttv.R;

public class OnboardingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        OnboardingFragment onboardingFragment = new OnboardingFragment();
        fragmentTransaction.add(R.id.onboarding_fragment_root, onboardingFragment );

        Bundle args = new Bundle();
        args.putString("name", getIntent().getExtras().getString("name"));
        args.putString("welcome_message", getIntent().getExtras().getString("welcome_message"));
        args.putString("welcome_image", getIntent().getExtras().getString("welcome_image"));
        onboardingFragment.setArguments(args);
        fragmentTransaction.commit();
    }
}
