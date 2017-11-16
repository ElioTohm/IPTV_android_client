package com.xms.dvb.fragment;

import android.os.Bundle;
import android.support.v17.leanback.app.HeadersFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.PageRow;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowHeaderPresenter;

import com.XmsPro.xmsproplayer.XmsPlayer;
import com.eliotohme.data.Channel;
import com.xms.dvb.app.Preferences;

import io.realm.Realm;
import io.realm.RealmResults;

public class ChannelGridFragment extends HeadersFragment {
    private ArrayObjectAdapter mRowsAdapter;
    private RealmResults<Channel> channelRealmResults;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //load rows
        loadRows();

        // set the channel select
        if (Preferences.getLastChannel() > 0) {
            setSelectedPosition(Preferences.getLastChannel());
        }

        // set click handler for header item / channel
        setOnHeaderClickedListener(new OnHeaderClickedListener() {
            @Override
            public void onHeaderClicked(RowHeaderPresenter.ViewHolder viewHolder, Row row) {
                XmsPlayer.getPlayerInstance().changeChannel((int) row.getHeaderItem().getId() - 1 );
                Preferences.setLastChannel((int) row.getHeaderItem().getId() - 1);
            }
        });
    }


    private void loadRows() {
        Realm.init(getActivity());
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        // Get a Realm instance for this thread
        final Realm realm = Realm.getDefaultInstance();

        // get all genre
        channelRealmResults = realm.where(Channel.class).findAllSorted("id");

        // loop in result genre to create row genre for channels
        for (Channel channel : channelRealmResults) {

            int channel_id = channel.getId();
            HeaderItem headerItem1 = new HeaderItem(channel_id, channel_id + " " +channel.getName());
            PageRow pageRow1 = new PageRow(headerItem1);
            mRowsAdapter.add(pageRow1);
        }
        setAdapter(mRowsAdapter);
//        channelRealmResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Channel>>() {
//            @Override
//            public void onChange(RealmResults<Channel> channels, @Nullable OrderedCollectionChangeSet orderedCollectionChangeSet) {
//                OrderedCollectionChangeSet.Range[] modifications = orderedCollectionChangeSet.getChangeRanges();
//                for (OrderedCollectionChangeSet.Range range : modifications) {
//                    Log.d("ELIO", "onChange: "+ range.startIndex);
//                    mRowsAdapter.replace(
//                            range.startIndex,
//                            new HeaderItem(
//                                    range.startIndex,
//                                    range.startIndex + " " +
//                                            realm.where(Channel.class)
//                                                    .equalTo("id", range.startIndex)
//                                                    .findFirst().getName())
//
//                    );
//                }
//            }
//        });
    }
}
