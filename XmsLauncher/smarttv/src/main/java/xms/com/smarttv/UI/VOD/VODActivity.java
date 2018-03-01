package xms.com.smarttv.UI.VOD;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import xms.com.smarttv.R;
import xms.com.smarttv.fragments.BackgroundImageFragment;
import xms.com.smarttv.fragments.SectionMenuFragment;

public class VODActivity extends Activity implements VODHomeFragment.VODHomeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod);

        Fragment backgroundImageFragment = BackgroundImageFragment.newInstance(SectionMenuFragment.HEADER_ID_VOD);
        Fragment vodHomeFragment = new VODHomeFragment();

        getFragmentManager().beginTransaction().add(R.id.background, backgroundImageFragment).commit();
        getFragmentManager().beginTransaction().add(R.id.Main, vodHomeFragment).commit();
    }

    @Override
    public void MovieSelected(Object item) {

    }

    @Override
    public void GenreSelected(Object item) {

    }
}
