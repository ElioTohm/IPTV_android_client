package xms.com.smarttv.UI.VOD;


import android.content.Context;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;

import com.eliotohme.data.Genre;
import com.eliotohme.data.Movie;

import io.realm.Realm;
import xms.com.smarttv.Presenter.FullImageCardPresenter;
import xms.com.smarttv.Presenter.GenreCardViewPresenter;
import xms.com.smarttv.app.Preferences;
import xms.com.smarttv.models.LargeListRow;

public class VODHomeFragment extends BrowseFragment implements OnItemViewClickedListener {
    private ArrayObjectAdapter mRowsAdapter;
    private VODHomeListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareEntranceTransition();

        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        ArrayObjectAdapter topmoviesarrayadapter = new ArrayObjectAdapter(
                new FullImageCardPresenter(getActivity()));
        topmoviesarrayadapter.addAll(0, Realm.getDefaultInstance().where(Movie.class).findAll());
        HeaderItem header = new HeaderItem("Top Movies");
        mRowsAdapter.add(new LargeListRow(header, topmoviesarrayadapter));

        ArrayObjectAdapter genresRowAdapter = new ArrayObjectAdapter(
                new GenreCardViewPresenter(getActivity()));

        genresRowAdapter.add(0, new Genre(0, "A-Z", Preferences.getServerUrl() + "/storage/genres/all.png"));
        genresRowAdapter.addAll(0, Realm.getDefaultInstance().where(Genre.class).findAll());

        header = new HeaderItem("Genres");
        mRowsAdapter.add(new LargeListRow(header, genresRowAdapter ));

        setAdapter(mRowsAdapter);
        setHeadersState(HEADERS_DISABLED);
        startEntranceTransition();
        setOnItemViewClickedListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof VODHomeListener) {
            mListener = (VODHomeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Movie) {
            mListener.MovieSelected((Movie) item);
        } else if (item instanceof Genre) {
            mListener.GenreSelected((Genre) item);
        }

    }

    public interface VODHomeListener {
        void MovieSelected(Movie movie);
        void GenreSelected(Genre genre);
    }
}
