package com.xms.dvb.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;

import com.eliotohme.data.Channel;
import com.xms.dvb.app.Preferences;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Elio on 12/5/2017.
 */

public class XmsDialogFragment extends GuidedStepFragment {

    private static final int ACTION_ID_POSITIVE = 1;
    private static final int ACTION_ID_NEGATIVE = ACTION_ID_POSITIVE + 1;

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        GuidanceStylist.Guidance guidance = new GuidanceStylist.Guidance("Search Channels",
                "are you sure you want to search channels again ?",
                "", null);
        return guidance;
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        GuidedAction action = new GuidedAction.Builder()
                .id(ACTION_ID_POSITIVE)
                .title("Search").build();
        actions.add(action);
        action = new GuidedAction.Builder()
                .id(ACTION_ID_NEGATIVE)
                .title("Cancel").build();
        actions.add(action);
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        if (ACTION_ID_POSITIVE == action.getId()) {
            Realm realm = Realm.getDefaultInstance();
            realm.removeAllChangeListeners();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.delete(Channel.class);
                }
            });
            Preferences.setStartingUrl("");
            Preferences.setServerUrl("");
            Preferences.setLastChannel(0);
//            Intent intent = new Intent(getActivity(), SplashScreen.class);
            Intent intent = getActivity().getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName() );
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            Realm.getDefaultInstance().close();
        }
        getActivity().finish();
    }
}
