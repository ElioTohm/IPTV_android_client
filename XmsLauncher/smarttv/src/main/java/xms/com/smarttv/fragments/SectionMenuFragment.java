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

import com.eliotohme.data.Section;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import xms.com.smarttv.R;
import xms.com.smarttv.UI.CustomHeaderItem;
import xms.com.smarttv.UI.SectionRecyclerViewAdapter;
import xms.com.smarttv.UI.SimpleDividerItemDecoration;

public class SectionMenuFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private SectionMenuFragmentListener mListener;
    public static final int HEADER_ID_HOTEL_INFO = 1;
    private static final String HEADER_NAME_HOTEL_INFO = "Hotel Info";
    public static final int HEADER_ID_RESTOANDBAR = 2;
    private static final String HEADER_NAME_RESTOANDBAR = "Restaurants & Bars";
    public static final int HEADER_ID_SPAANDFITNESS = 3;
    private static final String HEADER_NAME_SPAANDFITNESS = "Spa & Fitness";
    public static final int HEADER_ID_OFFERS = 4;
    private static final String HEADER_NAME_OFFERS  = "Special Offers";
    public static final int HEADER_ID_WEATHER = 5;
    private static final String HEADER_NAME_WEATHER = "Weather";
    public static final int HEADER_ID_CITYGUIDE = 6;
    private static final String HEADER_NAME_CITYGUIDE = "City Guide";
    public static final int HEADER_ID_CHANNELS = 7;
    private static final String HEADER_NAME_CHANNELS = "Live TV";
    public static final int HEADER_ID_VOD = 8;
    private static final String HEADER_NAME_VOD = "VOD";
    public static final int HEADER_ID_ACCOUNT = 9;
    private static final String HEADER_NAME_ACCOUNT = "Your Account";
    public static final int HEADER_ID_APPS = 10;
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

        List<CustomHeaderItem> mRowsAdapter  = new ArrayList<>() ;
        Realm realm = Realm.getDefaultInstance();
        List<Section> sections = realm.where(Section.class).findAll();
        for (Section section : sections) {
            mRowsAdapter.add(new CustomHeaderItem(section.getId(), section.getName(), section.getIcon()));
        }
        recyclerView.setAdapter(new SectionRecyclerViewAdapter(mRowsAdapter, mListener));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SectionMenuFragment.SectionMenuFragmentListener) {
            mListener = (SectionMenuFragment.SectionMenuFragmentListener) context;
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

    public interface SectionMenuFragmentListener {
        void onSectionClicked(CustomHeaderItem item);
    }
}