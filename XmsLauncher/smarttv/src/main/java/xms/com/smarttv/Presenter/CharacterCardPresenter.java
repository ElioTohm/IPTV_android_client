package xms.com.smarttv.Presenter;

import android.content.Context;

import com.eliotohme.data.Genre;
import com.eliotohme.data.Movie;
import com.eliotohme.data.SectionItem;

import xms.com.smarttv.models.Card;
import xms.com.smarttv.view.CharacterCardView;

/**
 * Created by elio on 2/3/18.
 */

public class CharacterCardPresenter extends AbstractCardPresenter<CharacterCardView> {

    public CharacterCardPresenter(Context context) {
        super(context, 0);
    }

    @Override
    protected CharacterCardView onCreateView() {
        return new CharacterCardView(getContext());
    }

    @Override
    public void onBindViewHolder(Card card, CharacterCardView cardView) {
        cardView.updateUi(card);
    }

    @Override
    public void onBindViewHolder(Movie movie, CharacterCardView cardView) {

    }

    @Override
    public void onBindViewHolder(SectionItem sectionItem, CharacterCardView cardView) {

    }

    @Override
    public void onBindViewHolder(Genre genre, CharacterCardView cardView) {

    }

}
