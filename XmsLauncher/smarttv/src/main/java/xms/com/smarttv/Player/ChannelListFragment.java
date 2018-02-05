package xms.com.smarttv.Player;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.HeadersFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.PageRow;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowHeaderPresenter;

import com.XmsPro.xmsproplayer.XmsPlayer;
import com.eliotohme.data.Channel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import xms.com.smarttv.R;

public class ChannelListFragment extends HeadersFragment {
    private ArrayObjectAdapter mRowsAdapter;
    private RealmResults<Channel> channelRealmResults;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        customSetBackground(R.color.fastlane_background);

        //load rows
        loadRows();

        // set the channel select
        // set click handler for header item / channel
        setOnHeaderClickedListener(new OnHeaderClickedListener() {
            @Override
            public void onHeaderClicked(RowHeaderPresenter.ViewHolder viewHolder, Row row) {
                    XmsPlayer.getPlayerInstance().changeChannel((int) row.getHeaderItem().getId() - 1 );
            }
        });

    }


    private void loadRows() {
        Realm.init(getActivity());
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        // Get a Realm instance for this thread
        final Realm realm = Realm.getDefaultInstance();

        // get all genre
        channelRealmResults = realm.where(Channel.class).findAllSorted("number");

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

    private void customSetBackground(int colorResource) {
        try {
            Class clazz = HeadersFragment.class;
            Method m = clazz.getDeclaredMethod("setBackgroundColor", Integer.TYPE);
            m.setAccessible(true);
            m.invoke(this, getResources().getColor(colorResource));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
