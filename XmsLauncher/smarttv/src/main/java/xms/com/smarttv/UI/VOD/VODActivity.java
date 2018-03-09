package xms.com.smarttv.UI.VOD;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import com.eliotohme.data.Genre;
import com.eliotohme.data.Movie;

import xms.com.smarttv.R;
import xms.com.smarttv.fragments.CameraFragment;
import xms.com.smarttv.fragments.PurchaseDialog;
import xms.com.smarttv.fragments.VODDetailFragment;
import xms.com.smarttv.fragments.VODfragment;


public class VODActivity extends Activity implements VODHomeFragment.VODHomeListener, VODfragment.VODFragmentListener,
        VODDetailFragment.VODDetailFragmentListener{
    Fragment backgroundImageFragment;
    Fragment vodHomeFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod);

//        backgroundImageFragment = BackgroundImageFragment.newInstance(SectionMenuFragment.HEADER_ID_VOD);
//        vodHomeFragment = new VODHomeFragment();

        getFragmentManager().beginTransaction().add(R.id.background, new CameraFragment()).commit();
//        getFragmentManager().beginTransaction().add(R.id.Main, vodHomeFragment).commit();
    }

    @Override
    public void MovieSelected(Movie movie) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.Main, VODDetailFragment.newInstance(movie))
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void GenreSelected(Genre genre) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.Main, VODfragment.newInstance(genre.getId()))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void purchase(Movie movie) {
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container_purshase, PurchaseDialog.newInstance(movie, "Movie"))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void watch(Movie movie) {

    }
}
