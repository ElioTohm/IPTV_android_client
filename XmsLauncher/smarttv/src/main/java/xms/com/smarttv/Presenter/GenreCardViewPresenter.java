package xms.com.smarttv.Presenter;

import android.content.Context;

import com.eliotohme.data.Genre;
import com.eliotohme.data.Movie;

import xms.com.smarttv.models.Card;
import xms.com.smarttv.view.GenreCardView;

public class GenreCardViewPresenter extends AbstractCardPresenter<GenreCardView> {

    public GenreCardViewPresenter(Context context) {
        super(context, 50);
    }

    @Override
    protected GenreCardView onCreateView() {
        return new GenreCardView(getContext());
    }

    @Override
    public void onBindViewHolder(Card card, GenreCardView cardView) {

    }

    @Override
    public void onBindViewHolder(Movie movie, GenreCardView cardView) {

    }

    @Override
    public void onBindViewHolder(Genre genre, GenreCardView cardView) {
            cardView.load(genre);
    }

}
