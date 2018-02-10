package xms.com.smarttv.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eliotohme.data.Channel;

import io.realm.Realm;
import xms.com.smarttv.R;
import xms.com.smarttv.UI.ChannelRecyclerViewAdapter;
import xms.com.smarttv.UI.SimpleDividerItemDecoration;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ChannelsListFragment extends Fragment implements ChannelRecyclerViewAdapter.OnChannelClicked  {
    private int savedposition = 0 ;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
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
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        channelRecyclerViewAdapter = new ChannelRecyclerViewAdapter(Realm.getDefaultInstance().where(Channel.class).findAllSorted("number"),mListener,this);
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
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
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

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Channel item);
    }
}
