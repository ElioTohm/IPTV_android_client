package xms.com.smarttv.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;

import com.eliotohme.data.Movie;

import io.realm.Realm;
import xms.com.smarttv.Presenter.ImageCardViewPresenter;

public class VODfragment extends VerticalGridFragment implements OnItemViewClickedListener {
    private static final int COLUMNS = 5;
    private static final String FILTER = "FILTER";
    int filter_param = 0;
    private VODFragmentListener mListener;
    private final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_LARGE;
    private ArrayObjectAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filter_param = getArguments().getInt(FILTER);
        setupAdapter();
    }
    public static VODfragment newInstance(int genre_id) {
        VODfragment fragment = new VODfragment();
        Bundle args = new Bundle();
        args.putSerializable(FILTER, genre_id);
        fragment.setArguments(args);
        return fragment;
    }

    private void setupAdapter() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(ZOOM_FACTOR, false);
        gridPresenter.setNumberOfColumns(COLUMNS);
        setGridPresenter(gridPresenter);

        mAdapter = new ArrayObjectAdapter(new ImageCardViewPresenter(getActivity()));
        setAdapter(mAdapter);

        prepareEntranceTransition();
        if ( this.filter_param >0 ) {
            mAdapter.addAll(0, Realm.getDefaultInstance().where(Movie.class).equalTo("genres.id", this.filter_param).findAll());
        } else {
            mAdapter.addAll(0, Realm.getDefaultInstance().where(Movie.class).findAll());
        }

        startEntranceTransition();
        setOnItemViewClickedListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof VODFragmentListener) {
            mListener = (VODFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }


    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        mListener.MovieSelected((Movie)item);
    }

    public interface VODFragmentListener {
        void MovieSelected(Movie item);
    }
}
