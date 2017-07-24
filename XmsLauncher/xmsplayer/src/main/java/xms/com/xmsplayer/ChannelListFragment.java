package xms.com.xmsplayer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xms.com.xmsplayer.objects.Channel;

public class ChannelListFragment extends BrowseFragment {
    private static final int GRID_ITEM_WIDTH = 200;
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
        setHeadersState(HEADERS_DISABLED);
        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }

    private void loadRows() {
        List<Channel> list = new ArrayList<>();
        String[] uriStrings = getActivity().getIntent().getStringArrayExtra("uri_list");

        String[] channelname = {"LBCI", "OTV", "El Jadid", "MTV", "Manar"};

        for (int i = 0; i < uriStrings.length; i++) {
            Channel channel = new Channel(Uri.parse(uriStrings[i]), channelname[i], "", i);
            list.add(channel);
        }

        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        GridItemPresenter mGridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);

        for (int i=0;  i < channelname.length; i++) {
            gridRowAdapter.add(list.get(i));
        }

        mRowsAdapter.add(new ListRow(gridRowAdapter));

        setAdapter(mRowsAdapter);

    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            Log.e("xms", String.valueOf(item));
            if (item instanceof Channel) {
                Channel channel = (Channel) item;
                ((PlayerActivity)getActivity()).itemClicked(channel);
            }
        }
    }

}
