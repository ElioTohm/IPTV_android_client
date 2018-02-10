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
    private static final long HEADER_ID_0 = 0;
    private static final String HEADER_NAME_0 = "Room Services";
    private static final long HEADER_ID_1 = 1;
    private static final String HEADER_NAME_1 = "Restaurants & Bars";
    private static final long HEADER_ID_2 = 2;
    private static final String HEADER_NAME_2 = "Spa & Fitness";
    private static final long HEADER_ID_3 = 3;
    private static final String HEADER_NAME_3 = "Special Offers";
    private static final long HEADER_ID_4 = 4;
    private static final String HEADER_NAME_4 = "Weather";
    private static final long HEADER_ID_5 = 5;
    private static final String HEADER_NAME_5 = "City Guide";

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
        view.findViewById(R.id.channel_recycler_view);
        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.channel_recycler_view);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(context));
        ;
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        CustomHeaderItem headerItem0 = new CustomHeaderItem(HEADER_ID_0, HEADER_NAME_0, R.drawable.lb_ic_fast_forward);
        CustomHeaderItem headerItem1 = new CustomHeaderItem(HEADER_ID_1, HEADER_NAME_1, R.drawable.glass);
        CustomHeaderItem headerItem2 = new CustomHeaderItem(HEADER_ID_2, HEADER_NAME_2, R.drawable.lb_ic_fast_forward);
        CustomHeaderItem headerItem3 = new CustomHeaderItem(HEADER_ID_3, HEADER_NAME_3, R.drawable.tagsale);
        CustomHeaderItem headerItem4 = new CustomHeaderItem(HEADER_ID_4, HEADER_NAME_4, R.drawable.lb_ic_fast_forward);
        List<CustomHeaderItem> mRowsAdapter  = new ArrayList<>() ;
        mRowsAdapter.add(headerItem0);
        mRowsAdapter.add(headerItem1);
        mRowsAdapter.add(headerItem2);
        mRowsAdapter.add(headerItem3);
        mRowsAdapter.add(headerItem4);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(CustomHeaderItem item);
    }
}