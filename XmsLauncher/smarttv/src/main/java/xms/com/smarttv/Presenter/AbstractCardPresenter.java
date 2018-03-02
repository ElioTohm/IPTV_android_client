package xms.com.smarttv.Presenter;

import android.content.Context;
import android.support.v17.leanback.widget.BaseCardView;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;

import com.eliotohme.data.Genre;
import com.eliotohme.data.Movie;

import xms.com.smarttv.R;
import xms.com.smarttv.models.Card;

/**
 * Created by elio on 2/3/18.
 */

public abstract class AbstractCardPresenter<T extends BaseCardView> extends Presenter {

    private static final String TAG = "AbstractCardPresenter";
    private final Context mContext;
    private int padding = 0;

    /**
     * @param context The current context.
     */
    public AbstractCardPresenter(Context context, int padding) {
        this.mContext = context;
        this.padding = padding;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent) {
        if (this.padding > 0) {
            HorizontalGridView horizontalGridView = parent.findViewById(R.id.row_content);
            horizontalGridView.setItemSpacing(this.padding); // You can set item margin here.
        }
        T cardView = onCreateView();
        return new ViewHolder(cardView);
    }

    @Override
    public final void onBindViewHolder(ViewHolder viewHolder, Object item) {
        if (item instanceof Card) {
            Card card = (Card) item;
            onBindViewHolder(card, (T) viewHolder.view);
        } else if (item instanceof Movie) {
            Movie movie = (Movie) item;
            onBindViewHolder(movie, (T) viewHolder.view);
        } else if (item instanceof Genre) {
            Genre genre = (Genre) item;
            onBindViewHolder(genre, (T) viewHolder.view);
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
    public abstract void onBindViewHolder(Card card, T cardView);
    public abstract void onBindViewHolder(Movie movie, T cardView);
    public abstract void onBindViewHolder(Genre genre, T cardView);

}
