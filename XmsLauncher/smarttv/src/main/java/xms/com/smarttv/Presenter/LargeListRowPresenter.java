package xms.com.smarttv.Presenter;

import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.RowPresenter;

import xms.com.smarttv.models.LargeListRow;


public class LargeListRowPresenter extends ListRowPresenter {

    private static final String TAG = LargeListRowPresenter.class.getSimpleName();

    public LargeListRowPresenter() {
        super();
    }

    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        /* This two line codes changes the number of rows of ListRow */
        int numRows = ((LargeListRow) item).getNumRows();
        ((ListRowPresenter.ViewHolder) holder).getGridView().setNumRows(numRows);

        super.onBindRowViewHolder(holder, item);
    }

    @Override
    protected void initializeRowViewHolder(RowPresenter.ViewHolder holder) {
        super.initializeRowViewHolder(holder);

        /* Disable Shadow */
        // setShadowEnabled(false);
    }
}

