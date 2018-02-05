package xms.com.smarttv.Presenter;

import android.content.Context;

import xms.com.smarttv.CharacterCardView;
import xms.com.smarttv.models.Card;

/**
 * Created by elio on 2/3/18.
 */

public class CharacterCardPresenter extends AbstractCardPresenter<CharacterCardView> {

    public CharacterCardPresenter(Context context) {
        super(context);
    }

    @Override
    protected CharacterCardView onCreateView() {
        return new CharacterCardView(getContext());
    }

    @Override
    public void onBindViewHolder(Card card, CharacterCardView cardView) {
        cardView.updateUi(card);
    }

}
