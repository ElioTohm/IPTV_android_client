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
    public static final int HEADER_ID_HOTEL_INFO = 0;
    private static final String HEADER_NAME_HOTEL_INFO = "Hotel Info";
    public static final int HEADER_ID_RESTOANDBAR = 1;
    private static final String HEADER_NAME_RESTOANDBAR = "Restaurants & Bars";
    public static final int HEADER_ID_SPAANDFITNESS = 2;
    private static final String HEADER_NAME_SPAANDFITNESS = "Spa & Fitness";
    public static final int HEADER_ID_OFFERS = 3;
    private static final String HEADER_NAME_OFFERS  = "Special Offers";
    public static final int HEADER_ID_WEATHER = 4;
    private static final String HEADER_NAME_WEATHER = "Weather";
    public static final int HEADER_ID_CITYGUIDE = 5;
    private static final String HEADER_NAME_CITYGUIDE = "City Guide";
    public static final int HEADER_ID_CHANNELS = 6;
    private static final String HEADER_NAME_CHANNELS = "Live TV";
    public static final int HEADER_ID_VOD = 7;
    private static final String HEADER_NAME_VOD = "VOD";
    public static final int HEADER_ID_ACCOUNT = 3;
    private static final String HEADER_NAME_ACCOUNT = "Your Account";
    public static final int HEADER_ID_APPS = 8;
    private static final String HEADER_NAME_APPS = "Applications";


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
        CustomHeaderItem info = new CustomHeaderItem(HEADER_ID_HOTEL_INFO, HEADER_NAME_HOTEL_INFO, R.drawable.info);
        CustomHeaderItem bar = new CustomHeaderItem(HEADER_ID_RESTOANDBAR, HEADER_NAME_RESTOANDBAR, R.drawable.bar);
        CustomHeaderItem fitness = new CustomHeaderItem(HEADER_ID_SPAANDFITNESS, HEADER_NAME_SPAANDFITNESS, R.drawable.gym);
        CustomHeaderItem weather = new CustomHeaderItem(HEADER_ID_WEATHER, HEADER_NAME_WEATHER, R.drawable.weather);
        CustomHeaderItem cityguide = new CustomHeaderItem(HEADER_ID_CITYGUIDE, HEADER_NAME_CITYGUIDE, R.drawable.compass);
        CustomHeaderItem livechannels = new CustomHeaderItem(HEADER_ID_CHANNELS, HEADER_NAME_CHANNELS, R.drawable.live);
        CustomHeaderItem vod = new CustomHeaderItem(HEADER_ID_VOD, HEADER_NAME_VOD, R.drawable.vod);
        CustomHeaderItem account = new CustomHeaderItem(HEADER_ID_ACCOUNT, HEADER_NAME_ACCOUNT, R.drawable.account);
        CustomHeaderItem apps = new CustomHeaderItem(HEADER_ID_APPS, HEADER_NAME_APPS, R.drawable.apps);
        List<CustomHeaderItem> mRowsAdapter  = new ArrayList<>() ;
        mRowsAdapter.add(info);
        mRowsAdapter.add(livechannels);
        mRowsAdapter.add(vod);
        mRowsAdapter.add(bar);
        mRowsAdapter.add(fitness);
        mRowsAdapter.add(weather);
        mRowsAdapter.add(cityguide);
        mRowsAdapter.add(apps);
        mRowsAdapter.add(account);
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