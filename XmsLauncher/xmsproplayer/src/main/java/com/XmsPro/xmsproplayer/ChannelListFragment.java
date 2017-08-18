package com.XmsPro.xmsproplayer;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.XmsPro.xmsproplayer.presenter.ChannelCardPresenter;
import com.eliotohme.data.Channel;
import com.eliotohme.data.Genre;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ChannelListFragment extends BrowseFragment {
    private static final int GRID_ITEM_WIDTH = 175;
    private static final int GRID_ITEM_HEIGHT = 100;
    private ArrayObjectAdapter mRowsAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupUIElements();

        loadRows();

        setOnItemViewClickedListener(new ItemViewClickedListener());

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
            Channel channel = (Channel) item;
            ((TextView) viewHolder.view).setText(channel.getName());
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }

    private void setupUIElements() {
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color
    }

    private void loadRows() {

        Realm.init(getActivity());
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        ChannelCardPresenter channelCardPresenter = new ChannelCardPresenter();

        // Get a Realm instance for this thread
        Realm realm = Realm.getDefaultInstance();

        // Put all channel in all channels group (row)
        RealmQuery<Channel> channels = realm.where(Channel.class);
        ArrayObjectAdapter gridRowAdapterAllChannels = new ArrayObjectAdapter(channelCardPresenter);
        gridRowAdapterAllChannels.addAll(0, channels.findAllSorted("id"));

        mRowsAdapter.add(new ListRow(new HeaderItem(0, "All Channels"), gridRowAdapterAllChannels));

        // get all genre
        RealmQuery<Genre> genreRealmQuery = realm.where(Genre.class);
        RealmResults<Genre> genreRealmResults = genreRealmQuery.findAll();

        // loop in result genre to create row genre for channels
        for (Genre genre : genreRealmResults) {
            RealmQuery<Channel> channelsbundle = realm.where(Channel.class).equalTo("genres.id", genre.getId());
            ArrayObjectAdapter gridRowAdapterbundle = new ArrayObjectAdapter(channelCardPresenter);
            gridRowAdapterbundle.addAll(0, channelsbundle.findAllSorted("id"));
            mRowsAdapter.add(new ListRow(new HeaderItem(1, genre.getName()), gridRowAdapterbundle));

        }

        setAdapter(mRowsAdapter);

    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Channel) {
                ((TvPlayer)getActivity()).changeChannel(((Channel) item).getId() - 1 );
            }
        }
    }

}
