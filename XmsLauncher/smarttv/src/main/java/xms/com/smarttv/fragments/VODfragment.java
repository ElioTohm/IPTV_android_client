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
    private VODFragmentListener mListener;
    private final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_LARGE;
    private ArrayObjectAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupAdapter();
    }

    private void setupAdapter() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(ZOOM_FACTOR, false);
        gridPresenter.setNumberOfColumns(COLUMNS);
        setGridPresenter(gridPresenter);

        mAdapter = new ArrayObjectAdapter(new ImageCardViewPresenter(getActivity()));
        setAdapter(mAdapter);

        prepareEntranceTransition();
        mAdapter.addAll(0, Realm.getDefaultInstance().where(Movie.class).findAll());
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
        mListener.onVideoClicked(item);
    }

    public interface VODFragmentListener {
        void onVideoClicked(Object item);
    }
}
