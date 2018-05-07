package xms.com.smarttv.fragments;

import android.os.Bundle;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.VerticalGridPresenter;

import com.eliotohme.data.SectionItem;

import io.realm.Realm;
import xms.com.smarttv.Presenter.ImageCardViewPresenter;
import xms.com.smarttv.Presenter.ShadowRowPresenterSelector;

public class RestaurantsNBarFragment extends VerticalGridFragment {
    private ArrayObjectAdapter mAdapter;
    private static final int COLUMNS = 3;
    private final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_LARGE;
    private final ArrayObjectAdapter mRowsAdapter;

    public RestaurantsNBarFragment() {
        mRowsAdapter = new ArrayObjectAdapter(new ShadowRowPresenterSelector());
        setAdapter(mRowsAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareEntranceTransition();
        createRows();
        startEntranceTransition();
    }

    private void createRows() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(ZOOM_FACTOR, false);
        gridPresenter.setNumberOfColumns(COLUMNS);
        setGridPresenter(gridPresenter);

        mAdapter = new ArrayObjectAdapter(new ImageCardViewPresenter(getActivity()));
        setAdapter(mAdapter);

        prepareEntranceTransition();
        mAdapter.addAll(0, Realm.getDefaultInstance().where(SectionItem.class).equalTo("section", 1).findAll());

        startEntranceTransition();
    }

}