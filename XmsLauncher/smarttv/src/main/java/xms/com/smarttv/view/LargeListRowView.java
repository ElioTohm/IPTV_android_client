package xms.com.smarttv.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import xms.com.smarttv.R;

public class LargeListRowView extends LinearLayout {

    private HorizontalGridView mGridView;

    public LargeListRowView(Context context) {
        this(context, null);
    }

    public LargeListRowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("RestrictedApi")
    public LargeListRowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.lb_list_row, this);
        mGridView = findViewById(R.id.row_content);
        // since we use WRAP_CONTENT for height in lb_list_row, we need set fixed size to false
        mGridView.setHasFixedSize(false);

        // Uncomment this to experiment with page-based scrolling.
        mGridView.setFocusScrollStrategy(HorizontalGridView.FOCUS_SCROLL_PAGE);
        setOrientation(LinearLayout.VERTICAL);
        setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
    }
    /**
     * Returns the HorizontalGridView.
     */
    public HorizontalGridView getGridView() {
        return mGridView;
    }
}
