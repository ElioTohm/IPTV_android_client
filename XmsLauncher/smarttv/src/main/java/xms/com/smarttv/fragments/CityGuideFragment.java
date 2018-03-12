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

import com.eliotohme.data.SectionItem;

import io.realm.Realm;
import xms.com.smarttv.Presenter.ImageCardViewPresenter;
import xms.com.smarttv.Presenter.ShadowRowPresenterSelector;

public class CityGuideFragment extends VerticalGridFragment implements OnItemViewClickedListener {
    private ArrayObjectAdapter mAdapter;
    private static final int COLUMNS = 3;
    private final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_LARGE;
    private CityGudieInterface mListener;

    public CityGuideFragment() {
        // Required empty public constructor
        mAdapter = new ArrayObjectAdapter(new ShadowRowPresenterSelector());
        setAdapter(mAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareEntranceTransition();
        createRows();
        startEntranceTransition();
        setOnItemViewClickedListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CityGudieInterface) {
            mListener = (CityGudieInterface) context;
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

    private void createRows() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(ZOOM_FACTOR, false);
        gridPresenter.setNumberOfColumns(COLUMNS);
        setGridPresenter(gridPresenter);

        mAdapter = new ArrayObjectAdapter(new ImageCardViewPresenter(getActivity()));
        setAdapter(mAdapter);

        prepareEntranceTransition();
        mAdapter.addAll(0, Realm.getDefaultInstance().where(SectionItem.class).equalTo("section", 3).findAll());

        startEntranceTransition();
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        mListener.LocationSelected(item);
    }

    public interface CityGudieInterface {
        void LocationSelected(Object item);
    }
}
