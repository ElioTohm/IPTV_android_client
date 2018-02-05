package xms.com.smarttv.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.eliotohme.data.InstalledApps;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import xms.com.smarttv.R;

public class GetInstalledAppService extends IntentService {
//    // TODO: Rename actions, choose action names that describe tasks that this
//    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
//    private static final String ACTION_FOO = "xms.com.smarttv.services.action.FOO";
//    private static final String ACTION_BAZ = "xms.com.smarttv.services.action.BAZ";
//
//    // TODO: Rename parameters
//    private static final String EXTRA_PARAM1 = "xms.com.smarttv.services.extra.PARAM1";
//    private static final String EXTRA_PARAM2 = "xms.com.smarttv.services.extra.PARAM2";

    public GetInstalledAppService() {
        super("GetInstalledAppService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            final ArrayList<InstalledApps> installedApps = getInstalledApps();
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    realm.insertOrUpdate(installedApps);
                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<InstalledApps> getInstalledApps() throws PackageManager.NameNotFoundException {
        ArrayList<InstalledApps> res = new ArrayList<InstalledApps>();


        List<PackageInfo> packs = getApplication().getPackageManager().getInstalledPackages(0);

        PackageManager pm = getApplication().getPackageManager();

        for(PackageInfo packinfo : packs) {
            ApplicationInfo ai = pm.getApplicationInfo(packinfo.packageName, 0);

            if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && !packinfo.applicationInfo.loadLabel(getApplication().getPackageManager()).toString().equals(getString(R.string.app_name))) {
                InstalledApps installedApps = new InstalledApps();
                installedApps.setAppname(packinfo.applicationInfo.loadLabel(getApplication().getPackageManager()).toString());
                installedApps.setPname(packinfo.packageName);
                installedApps.setVersionName(packinfo.versionName);
                installedApps.setVersionCode(packinfo.versionCode);
                res.add(installedApps);
            }
        }
        return res;
    }

//    /**
//     * Handle action Foo in the provided background thread with the provided
//     * parameters.
//     */
//    private void handleActionFoo(String param1, String param2) {
//        // TODO: Handle action Foo
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    /**
//     * Handle action Baz in the provided background thread with the provided
//     * parameters.
//     */
//    private void handleActionBaz(String param1, String param2) {
//        // TODO: Handle action Baz
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
}
