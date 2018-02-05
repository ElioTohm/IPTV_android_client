package xms.com.smarttv.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;

import com.eliotohme.data.InstalledApps;

import java.util.List;

import io.realm.Realm;
import xms.com.smarttv.Presenter.InstalledApplicationPresenter;

public class ApplicationsMenu extends GridFragment {
    private static final int COLUMNS = 4;
    private final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_SMALL;
    private ArrayObjectAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupAdapter();
        loadData();
        getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
    }

    private void setupAdapter() {
        VerticalGridPresenter presenter = new VerticalGridPresenter(ZOOM_FACTOR);
        presenter.setNumberOfColumns(COLUMNS);
        setGridPresenter(presenter);

        InstalledApplicationPresenter cardPresenter = new InstalledApplicationPresenter(getActivity());
        mAdapter = new ArrayObjectAdapter(cardPresenter);
        setAdapter(mAdapter);

        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(
                    Presenter.ViewHolder itemViewHolder,
                    Object item,
                    RowPresenter.ViewHolder rowViewHolder,
                    Row row) {
                InstalledApps installApps = (InstalledApps) item;
                Context ctx = getActivity();
                Intent i = ctx.getPackageManager().getLaunchIntentForPackage(installApps.getPname());
                ctx.startActivity(i);
            }
        });
    }

    private void loadData() {
        List<InstalledApps> installedAppsList = Realm.getDefaultInstance().where(InstalledApps.class).findAll();
        mAdapter.addAll(0, installedAppsList);
    }
}
