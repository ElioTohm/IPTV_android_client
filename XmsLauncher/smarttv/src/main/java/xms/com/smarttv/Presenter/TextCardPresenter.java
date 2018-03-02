package xms.com.smarttv.Presenter;

import android.content.Context;

import com.eliotohme.data.Genre;
import com.eliotohme.data.Movie;

import xms.com.smarttv.models.Card;
import xms.com.smarttv.view.TextCardView;

/**
 * Created by elio on 2/3/18.
 */

public class TextCardPresenter extends AbstractCardPresenter<TextCardView> {

    public TextCardPresenter(Context context) {
        super(context, 0);
    }

    @Override
    protected TextCardView onCreateView() {
        return new TextCardView(getContext());
    }

    @Override
    public void onBindViewHolder(Card card, TextCardView cardView) {
        cardView.updateUi(card);
    }

    @Override
    public void onBindViewHolder(Movie movie, TextCardView cardView) {

    }

    @Override
    public void onBindViewHolder(Genre genre, TextCardView cardView) {

    }

}
