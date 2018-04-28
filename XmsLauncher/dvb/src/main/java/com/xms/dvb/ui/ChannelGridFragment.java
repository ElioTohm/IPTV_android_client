package com.xms.dvb.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.HeadersFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.PageRow;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowHeaderPresenter;

import com.XmsPro.xmsproplayer.RTPPlayer;
import com.XmsPro.xmsproplayer.XmsPlayer;
import com.eliotohme.data.Channel;
import com.xms.dvb.app.Preferences;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
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
                if (XmsPlayer.getPlayerInstance() != null) {
                    Preferences.setLastChannel((int) row.getHeaderItem().getId() - 1);
                } else if (RTPPlayer.getPlayerInstance() != null) {
                    RTPPlayer.getPlayerInstance().SetChannel((int) row.getHeaderItem().getId());
                    Preferences.setLastChannel((int) row.getHeaderItem().getId());
                }
            }
        });
    }


    private void loadRows() {
        Realm.init(getActivity());
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        // Get a Realm instance for this thread
        final Realm realm = Realm.getDefaultInstance();

        // get all genre
        channelRealmResults = realm.where(Channel.class).findAll().sort("number");

        // loop in result genre to create row genre for channels
        for (Channel channel : channelRealmResults) {

            int channel_id = channel.getNumber();
            HeaderItem headerItem1 = new HeaderItem(channel_id, channel_id + " " +channel.getName());
            PageRow pageRow1 = new PageRow(headerItem1);
            mRowsAdapter.add(pageRow1);
        }
        setAdapter(mRowsAdapter);

        channelRealmResults.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Channel>>() {
            @Override
            public void onChange(RealmResults<Channel> channels, @Nullable OrderedCollectionChangeSet orderedCollectionChangeSet) {
                OrderedCollectionChangeSet.Range[] modifications = orderedCollectionChangeSet.getChangeRanges();
                for (OrderedCollectionChangeSet.Range range : modifications) {
                    int index = range.startIndex + 1 ;
                    mRowsAdapter.replace(
                            range.startIndex,
                            new PageRow(new HeaderItem(range.startIndex,
                                    index + " " + realm.where(Channel.class)
                                                            .equalTo("number", index)
                                                            .findFirst().getName())
                            )
                    );
                }
            }
        });

    }
}
