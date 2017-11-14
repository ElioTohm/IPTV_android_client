package com.xms.dvb.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.xms.dvb.InstallAppsInfo;
import com.xms.dvb.R;
import com.xms.dvb.presenter.InstalledApplicationPresenter;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends BrowseFragment {
    private static final String TAG = "MainFragment";

    private static final int BACKGROUND_UPDATE_DELAY = 50;
    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;
    private static final int NUM_ROWS = 1;
    private static final int NUM_COLS = 5;

    private final Handler mHandler = new Handler();
    private ArrayObjectAdapter mRowsAdapter;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private URI mBackgroundURI;
    private BackgroundManager mBackgroundManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        prepareBackgroundManager();

        setupUIElements();

        loadRows();

        setupEventListeners();

    }


    private ArrayList<InstallAppsInfo> getInstalledApps() throws PackageManager.NameNotFoundException {
        ArrayList<InstallAppsInfo> res = new ArrayList<InstallAppsInfo>();

        Activity thisactivity = getActivity();

        List<PackageInfo> packs = thisactivity.getPackageManager().getInstalledPackages(0);

        PackageManager pm = thisactivity.getPackageManager();

        for(PackageInfo packinfo : packs) {
            ApplicationInfo ai = pm.getApplicationInfo(packinfo.packageName, 0);

            if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && !packinfo.applicationInfo.loadLabel(thisactivity.getPackageManager()).toString().equals(getString(R.string.app_name))) {
                InstallAppsInfo newInfo = new InstallAppsInfo();
                newInfo.setAppname(packinfo.applicationInfo.loadLabel(thisactivity.getPackageManager()).toString());
                newInfo.setPname(packinfo.packageName);
                newInfo.setVersionName(packinfo.versionName);
                newInfo.setVersionCode(packinfo.versionCode);
                newInfo.setIcon(packinfo.applicationInfo.loadIcon(thisactivity.getPackageManager()));
                res.add(newInfo);
            }
        }
        return res;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
            mBackgroundTimer.cancel();
        }
    }

    private void loadRows() {

        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        ArrayList<InstallAppsInfo> installAppsInfos;
        ArrayObjectAdapter ApplistRowAdapter = new ArrayObjectAdapter(new InstalledApplicationPresenter());

        try {
            installAppsInfos = getInstalledApps();
            for (int j = 0; j < installAppsInfos.size(); j++) {
                ApplistRowAdapter.add(installAppsInfos.get(j));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mRowsAdapter.add(new ListRow(new HeaderItem(0, "Applications"), ApplistRowAdapter));

        HeaderItem gridHeader = new HeaderItem(0, "Preferences");

        GridItemPresenter mGridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
        gridRowAdapter.add("Personal settings");
        mRowsAdapter.add(new ListRow(gridHeader, gridRowAdapter));

        setAdapter(mRowsAdapter);

    }

    private void prepareBackgroundManager() {

        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
        setBadgeDrawable(getActivity().getResources().getDrawable(
                R.drawable.xmsprologosmall));
        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
    }

    private void setupEventListeners() {
        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    protected void updateBackground(String uri) {
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        mBackgroundManager.setDrawable(resource);
                    }
                });
        mBackgroundTimer.cancel();
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof String) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
            } else if (item instanceof InstallAppsInfo) {
                InstallAppsInfo installAppsInfo = (InstallAppsInfo) item;

                Context ctx = getActivity(); // or you can replace **'this'** with your **ActivityName.this**
                Intent i = ctx.getPackageManager().getLaunchIntentForPackage(installAppsInfo.getPname());
                ctx.startActivity(i);

            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
//            if (item instanceof ServiceApp) {
//                mBackgroundURI = ((ServiceApp) item).getBackgroundImageURI();
//                startBackgroundTimer();
//            }
        }
    }

    private class UpdateBackgroundTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mBackgroundURI != null) {
                        updateBackground(mBackgroundURI.toString());
                    }
                }
            });

        }
    }

    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(getResources().getColor(R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }

}
