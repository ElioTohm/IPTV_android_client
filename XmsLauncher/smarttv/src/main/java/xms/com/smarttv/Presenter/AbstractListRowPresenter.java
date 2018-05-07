package xms.com.smarttv.Presenter;

import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import xms.com.smarttv.view.LargeListRowView;

public abstract class AbstractListRowPresenter<T extends LinearLayout> extends Presenter {

    private static final String TAG = "AbstractCardPresenter";
    private final Context mContext;

    /**
     * @param context The current context.
     */
    public AbstractListRowPresenter(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent) {
        T cardView = onCreateView();
        return new ViewHolder(cardView);
    }

    @Override
    public final void onBindViewHolder(ViewHolder viewHolder, Object item) {
        if (item instanceof LargeListRowView) {
            LargeListRowView listRow = (LargeListRowView) item;
            onBindViewHolder(listRow, (T) viewHolder.view);
        }

    }

    @Override
    public final void onUnbindViewHolder(ViewHolder viewHolder) {
        onUnbindViewHolder((T) viewHolder.view);
    }

    public void onUnbindViewHolder(T cardView) {
        // Nothing to clean up. Override if necessary.
    }

    /**
     * Invoked when a new view is created.
     *
     * @return Returns the newly created view.
     */
    protected abstract T onCreateView();

    /**
     * Implement this method to update your card's view with the data bound to it.
     *
     * @param card The model containing the data for the card.
     * @param cardView The view the card is bound to.
     * @see Presenter#onBindViewHolder(Presenter.ViewHolder, Object)
     */
    public abstract void onBindViewHolder(LargeListRowView card, T cardView);

}
