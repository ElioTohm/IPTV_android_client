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
}
