package xms.com.smarttv.UI;

import android.support.v17.leanback.widget.HeaderItem;


public class CustomHeaderItem extends HeaderItem {

    private long ID;
    private static final String TAG = CustomHeaderItem.class.getSimpleName();
    public static final int ICON_NONE = -1;

    /**
     * Hold an icon resource id
     */
    private int mIconResId = ICON_NONE;

    public CustomHeaderItem(long id, String name, int iconResId) {
        super(id, name);
        this.ID = id;
        mIconResId = iconResId;
    }

    public CustomHeaderItem(long id, String name) {
        this(id, name, ICON_NONE);
    }

    public CustomHeaderItem(String name) {
        super(name);
    }

    public int getIconResId() {
        return mIconResId;
    }

    public long getHeaderId() {
        return this.ID;
    }

    public void setIconResId(int iconResId) {
        this.mIconResId = iconResId;
    }
}