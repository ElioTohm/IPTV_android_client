package xms.com.smarttv.models;

import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ObjectAdapter;

public class LargeListRow extends ListRow {

    private static final String TAG = LargeListRow.class.getSimpleName();
    private int mNumRows = 1;

    public LargeListRow(HeaderItem header, ObjectAdapter adapter) {
        super(header, adapter);
    }

    public LargeListRow(long id, HeaderItem header, ObjectAdapter adapter) {
        super(id, header, adapter);
    }

    public LargeListRow(ObjectAdapter adapter) {
        super(adapter);
    }

    public void setNumRows(int numRows) {
        mNumRows = numRows;
    }

    public int getNumRows() {
        return mNumRows;
    }

}