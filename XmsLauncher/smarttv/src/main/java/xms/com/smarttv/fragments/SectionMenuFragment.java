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

import java.util.ArrayList;
import java.util.List;

import xms.com.smarttv.R;
import xms.com.smarttv.UI.CustomHeaderItem;
import xms.com.smarttv.UI.SectionRecyclerViewAdapter;
import xms.com.smarttv.UI.SimpleDividerItemDecoration;

public class SectionMenuFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private SectionMenuFragment.OnListFragmentInteractionListener mListener;
    public static final long HEADER_ID_0 = 0;
    private static final String HEADER_NAME_0 = "Hotel Info";
    public static final long HEADER_ID_1 = 1;
    private static final String HEADER_NAME_1 = "Restaurants & Bars";
    public static final long HEADER_ID_2 = 2;
    private static final String HEADER_NAME_2 = "Spa & Fitness";
    public static final long HEADER_ID_3 = 3;
    private static final String HEADER_NAME_3 = "Special Offers";
    public static final long HEADER_ID_4 = 4;
    private static final String HEADER_NAME_4 = "Weather";
    public static final long HEADER_ID_5 = 5;
    private static final String HEADER_NAME_5 = "City Guide";
    public static final long HEADER_ID_6 = 6;
    private static final String HEADER_NAME_6 = "Live Channels";
    public static final long HEADER_ID_7 = 7;
    private static final String HEADER_NAME_7 = "VOD";

    public SectionMenuFragment() {
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
        View view = inflater.inflate(R.layout.fragment_section_list, container, false);

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.channel_recycler_view);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));

        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        CustomHeaderItem info = new CustomHeaderItem(HEADER_ID_0, HEADER_NAME_0, R.drawable.info);
        CustomHeaderItem bar = new CustomHeaderItem(HEADER_ID_1, HEADER_NAME_1, R.drawable.bar);
        CustomHeaderItem fitness = new CustomHeaderItem(HEADER_ID_2, HEADER_NAME_2, R.drawable.gym);
        CustomHeaderItem specialoffers = new CustomHeaderItem(HEADER_ID_3, HEADER_NAME_3, R.drawable.dollar);
        CustomHeaderItem weather = new CustomHeaderItem(HEADER_ID_4, HEADER_NAME_4, R.drawable.weather);
        CustomHeaderItem cityguide = new CustomHeaderItem(HEADER_ID_5, HEADER_NAME_5, R.drawable.compass);
        CustomHeaderItem livechannels = new CustomHeaderItem(HEADER_ID_6, HEADER_NAME_6, R.drawable.live);
        CustomHeaderItem vod = new CustomHeaderItem(HEADER_ID_7, HEADER_NAME_7, R.drawable.vod);
        List<CustomHeaderItem> mRowsAdapter  = new ArrayList<>() ;
        mRowsAdapter.add(info);
        mRowsAdapter.add(livechannels);
        mRowsAdapter.add(vod);
        mRowsAdapter.add(bar);
        mRowsAdapter.add(fitness);
        mRowsAdapter.add(specialoffers);
        mRowsAdapter.add(weather);
        mRowsAdapter.add(cityguide);
        recyclerView.setAdapter(new SectionRecyclerViewAdapter(mRowsAdapter, mListener));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SectionMenuFragment.OnListFragmentInteractionListener) {
            mListener = (SectionMenuFragment.OnListFragmentInteractionListener) context;
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

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(CustomHeaderItem item);
    }
}