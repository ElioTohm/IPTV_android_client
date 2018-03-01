package xms.com.smarttv.Presenter;

import android.content.Context;

import com.eliotohme.data.Movie;

import xms.com.smarttv.LargeImageCardView;
import xms.com.smarttv.models.Card;

public class FullImageCardPresenter extends AbstractCardPresenter<LargeImageCardView> {

    public FullImageCardPresenter(Context context) {
        super(context);
    }

    @Override
    protected LargeImageCardView onCreateView() {
        return new LargeImageCardView(getContext());
    }

    @Override
    public void onBindViewHolder(Card card, LargeImageCardView cardView) {
    }

    @Override
    public void onBindViewHolder(Movie movie, LargeImageCardView cardView) {
        cardView.updateUi(movie);
    }

}
