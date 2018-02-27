package xms.com.smarttv.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eliotohme.data.Channel;

import io.realm.Realm;
import io.realm.RealmResults;
import xms.com.smarttv.R;
import xms.com.smarttv.UI.ChannelRecyclerViewAdapter;
import xms.com.smarttv.UI.SimpleDividerItemDecoration;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link ChannelListFragmentListener}
 * interface.
 */
public class ChannelsListFragment extends Fragment implements ChannelRecyclerViewAdapter.OnChannelClicked  {
    private int savedposition = 0 ;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private ChannelListFragmentListener mListener;
    private ChannelRecyclerViewAdapter channelRecyclerViewAdapter;
    private RecyclerView recyclerView;

    public ChannelsListFragment() {
    }

    @SuppressWarnings("unused")
    public static ChannelsListFragment newInstance(int columnCount) {
        ChannelsListFragment fragment = new ChannelsListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel_list, container, false);
        view.findViewById(R.id.channel_recycler_view);
        // Set the adapter
        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.channel_recycler_view);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        RealmResults realmResults = Realm.getDefaultInstance().where(Channel.class).sort("number").findAll();
        channelRecyclerViewAdapter = new ChannelRecyclerViewAdapter(realmResults, true,mListener,this);
        recyclerView.setAdapter(channelRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        recyclerView.scrollToPosition(savedposition);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChannelListFragmentListener) {
            mListener = (ChannelListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void UpdateLastPosition(int currentposition) {
        this.savedposition = currentposition;
    }

    public interface ChannelListFragmentListener {
        void onChannelSelected(Channel item, boolean flag);
        void onChannelPurchased(Channel item);
    }
}
