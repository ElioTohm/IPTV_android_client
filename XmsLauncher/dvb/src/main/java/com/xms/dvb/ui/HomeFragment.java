package com.xms.dvb.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;

import com.xms.dvb.R;
import com.xms.dvb.data.InstallAppsInfo;
import com.xms.dvb.presenter.InstalledApplicationPresenter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends VerticalGridFragment {

    private ArrayList<InstallAppsInfo> installAppsInfos;
    private ArrayObjectAdapter applistRowAdapter;
    private static final int COLUMNS = 4;
    private static final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Menu");
        setupRowAdapter();
    }

    private void setupRowAdapter() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(ZOOM_FACTOR);
        gridPresenter.setNumberOfColumns(COLUMNS);
        setGridPresenter(gridPresenter);

        applistRowAdapter = new ArrayObjectAdapter(new InstalledApplicationPresenter());

        prepareEntranceTransition();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadRows();
                setOnItemViewClickedListener(new ItemViewClickedListener());
                startEntranceTransition();
            }
        }, 10);

    }

    private ArrayList<InstallAppsInfo> getInstalledApps() throws PackageManager.NameNotFoundException {
        ArrayList<InstallAppsInfo> res = new ArrayList<InstallAppsInfo>();

        Activity thisactivity = getActivity();

        List<PackageInfo> packs = thisactivity.getPackageManager().getInstalledPackages(0);

        PackageManager pm = thisactivity.getPackageManager();

        for(PackageInfo packinfo : packs) {
            ApplicationInfo ai = pm.getApplicationInfo(packinfo.packageName, 0);

            if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) == 0 &&
                    !packinfo.applicationInfo.loadLabel(thisactivity.getPackageManager()).toString().equals(getString(R.string.app_name))) {
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

    private void loadRows() {

        try {
            installAppsInfos = getInstalledApps();
            for (int j = 0; j < installAppsInfos.size(); j++) {
                applistRowAdapter.add(installAppsInfos.get(j));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // add settings icon to access android settings
        InstallAppsInfo settingsapp = new InstallAppsInfo();
        settingsapp.setAppname("Settings");
        settingsapp.setPname("");
        settingsapp.setVersionName("1");
        settingsapp.setVersionCode(1);
        settingsapp.setIcon(getActivity().getResources().getDrawable(R.drawable.ic_settings_settings));
        //add item to list
        applistRowAdapter.add(settingsapp);

        InstallAppsInfo searchChannels = new InstallAppsInfo();
        searchChannels.setAppname("Search for channels");
        searchChannels.setPname("");
        searchChannels.setVersionName("1");
        searchChannels.setVersionCode(1);
        searchChannels.setIcon(getActivity().getResources().getDrawable(R.drawable.lb_ic_in_app_search));
        applistRowAdapter.add(searchChannels);

        setAdapter(applistRowAdapter);

    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

                InstallAppsInfo installAppsInfo = (InstallAppsInfo) item;

                if(installAppsInfo.getAppname().equals("Settings")){
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                } else if (installAppsInfo.getAppname().equals("Search for channels")) {
                    getActivity().startActivity(new Intent(getActivity(), DialogActivity.class));
                } else {
                    Context ctx = getActivity();
                    Intent i = ctx.getPackageManager().getLaunchIntentForPackage(installAppsInfo.getPname());
                    ctx.startActivity(i);
                }
            }
        }

}
