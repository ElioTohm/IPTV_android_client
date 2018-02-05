package xms.com.smarttv.Presenter;

import android.content.Context;

import xms.com.smarttv.TextCardView;
import xms.com.smarttv.models.Card;

/**
 * Created by elio on 2/3/18.
 */

public class TextCardPresenter extends AbstractCardPresenter<TextCardView> {

    public TextCardPresenter(Context context) {
        super(context);
    }

    @Override
    protected TextCardView onCreateView() {
        return new TextCardView(getContext());
    }

    @Override
    public void onBindViewHolder(Card card, TextCardView cardView) {
        cardView.updateUi(card);
    }

}
