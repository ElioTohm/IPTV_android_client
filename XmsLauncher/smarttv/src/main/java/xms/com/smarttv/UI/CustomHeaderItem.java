package xms.com.smarttv.UI;

import android.support.v17.leanback.widget.HeaderItem;


public class CustomHeaderItem extends HeaderItem {

    private long ID;
    private static final String TAG = CustomHeaderItem.class.getSimpleName();
    private String iconUrl;

    public CustomHeaderItem(long id, String name, String iconUrl) {
        super(id, name);
        this.ID = id;
        this.iconUrl = iconUrl;
    }

    public CustomHeaderItem(long id, String name) {
        this(id, name, "");
    }

    public CustomHeaderItem(String name) {
        super(name);
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public long getHeaderId() {
        return this.ID;
    }

    public void setIconResId(String iconResId) {
        this.iconUrl = iconResId;
    }
}