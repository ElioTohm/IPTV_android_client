package xms.com.smarttv.UI.VOD;


import android.content.Context;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;

import com.eliotohme.data.Movie;

import io.realm.Realm;
import xms.com.smarttv.Presenter.FullImageCardPresenter;

public class VODHomeFragment extends BrowseFragment implements OnItemViewClickedListener {
    private ArrayObjectAdapter mRowsAdapter;
    private VODHomeListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareEntranceTransition();
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(
                new FullImageCardPresenter(getActivity()));
        listRowAdapter.addAll(0, Realm.getDefaultInstance().where(Movie.class).findAll());
        HeaderItem header = new HeaderItem("Top Movies");
        mRowsAdapter.add(new ListRow(header, listRowAdapter));
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
//        mListener.LocationSelected(item);
    }

    public interface VODHomeListener {
        void MovieSelected(Object item);
        void GenreSelected(Object item);
    }
}
