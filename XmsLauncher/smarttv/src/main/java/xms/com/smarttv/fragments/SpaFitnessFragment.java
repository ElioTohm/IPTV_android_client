package xms.com.smarttv.fragments;

import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.Row;

import com.google.gson.Gson;

import xms.com.smarttv.CardListRow;
import xms.com.smarttv.Presenter.CardPresenterSelector;
import xms.com.smarttv.Presenter.ShadowRowPresenterSelector;
import xms.com.smarttv.R;
import xms.com.smarttv.UI.CustomHeaderItem;
import xms.com.smarttv.Utils;
import xms.com.smarttv.models.Card;
import xms.com.smarttv.models.CardRow;


public class SpaFitnessFragment extends BrowseFragment {
    private final ArrayObjectAdapter mRowsAdapter;

    public SpaFitnessFragment() {
        mRowsAdapter = new ArrayObjectAdapter(new ShadowRowPresenterSelector());
        setAdapter(mRowsAdapter);
        setHeadersState(HEADERS_DISABLED);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareEntranceTransition();
        createRows();
        startEntranceTransition();
    }

    private void createRows() {
        String json = Utils.inputStreamToString(getResources().openRawResource(
                R.raw.fitnesssection));
        CardRow[] rows = new Gson().fromJson(json, CardRow[].class);
        for (CardRow row : rows) {
            if (row.getType() == CardRow.TYPE_DEFAULT) {
                mRowsAdapter.add(createCardRow(row));
            }
        }
    }

    private Row createCardRow(CardRow cardRow) {
        PresenterSelector presenterSelector = new CardPresenterSelector(getActivity());
        ArrayObjectAdapter adapter = new ArrayObjectAdapter(presenterSelector);
        for (Card card : cardRow.getCards()) {
            adapter.add(card);
        }

        CustomHeaderItem headerItem = new CustomHeaderItem(cardRow.getTitle());
        return new CardListRow(headerItem, adapter, cardRow);
    }
}
