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
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eliotohme.data.Channel;

import io.realm.Realm;
import io.realm.RealmQuery;

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

        // Get a Realm instance for this thread
        Realm realm = Realm.getDefaultInstance();

        RealmQuery<Channel> channels = realm.where(Channel.class);
        GridItemPresenter mGridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapterAllChannels = new ArrayObjectAdapter(mGridPresenter);
        gridRowAdapterAllChannels.addAll(0, channels.findAll());

//        RealmQuery<Channel> channelsbundle1 = realm.where(Channel.class).equalTo("bundle_id", 1);
//        GridItemPresenter mGridPresenterbundle1 = new GridItemPresenter();
//        ArrayObjectAdapter gridRowAdapterbundle1 = new ArrayObjectAdapter(mGridPresenterbundle1);
//        gridRowAdapterbundle1.addAll(0, channelsbundle1.findAll());
//
//        RealmQuery<Channel> channelsbundle2 = realm.where(Channel.class).equalTo("bundle_id", 2);
//        GridItemPresenter mGridPresenterbundle2 = new GridItemPresenter();
//        ArrayObjectAdapter gridRowAdapterbundle2 = new ArrayObjectAdapter(mGridPresenterbundle2);
//        gridRowAdapterbundle2.addAll(0, channelsbundle2.findAll());
//
//        RealmQuery<Channel> channelsbundle3 = realm.where(Channel.class).equalTo("bundle_id", 3);
//        GridItemPresenter mGridPresenterbundle3 = new GridItemPresenter();
//        ArrayObjectAdapter gridRowAdapterbundle3 = new ArrayObjectAdapter(mGridPresenterbundle3);
//        gridRowAdapterbundle3.addAll(0, channelsbundle3.findAll());
//
//        RealmQuery<Channel> channelsbundle4 = realm.where(Channel.class).equalTo("bundle_id", 4);
//        GridItemPresenter mGridPresenterbundle4 = new GridItemPresenter();
//        ArrayObjectAdapter gridRowAdapterbundle4 = new ArrayObjectAdapter(mGridPresenterbundle4);
//        gridRowAdapterbundle4.addAll(0, channelsbundle4.findAll());


        mRowsAdapter.add(new ListRow(new HeaderItem(0, "        All Channels"), gridRowAdapterAllChannels));

//        mRowsAdapter.add(new ListRow(new HeaderItem(1, "        " + getString(R.string.BUNDLE_1)), gridRowAdapterbundle1));
//        mRowsAdapter.add(new ListRow(new HeaderItem(2, "        " + getString(R.string.BUNDLE_2)), gridRowAdapterbundle2));
//        mRowsAdapter.add(new ListRow(new HeaderItem(3, "        " + getString(R.string.BUNDLE_3)), gridRowAdapterbundle3));
//        mRowsAdapter.add(new ListRow(new HeaderItem(4, "        " + getString(R.string.BUNDLE_4)), gridRowAdapterbundle4));

        setAdapter(mRowsAdapter);

    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            Log.e("xms", String.valueOf(item));
            if (item instanceof Channel) {
                Channel channel = (Channel) item;
                ((TvPlayer)getActivity()).changeChannel(channel.getId());
            }
        }
    }

}
