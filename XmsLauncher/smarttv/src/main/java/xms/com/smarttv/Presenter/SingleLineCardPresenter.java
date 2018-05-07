package xms.com.smarttv.Presenter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v17.leanback.widget.ImageCardView;

import xms.com.smarttv.R;
import xms.com.smarttv.models.Card;

/**
 * Created by elio on 2/3/18.
 */

public class SingleLineCardPresenter extends ImageCardViewPresenter {

    public SingleLineCardPresenter(Context context) {
        super(context, R.style.SingleLineCardTheme);
    }

    @Override public void onBindViewHolder(Card card, ImageCardView cardView) {
        super.onBindViewHolder(card, cardView);
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(R.styleable.lbImageCardView);
        android.util.Log.d("SHAAN", "lbImageCardViewType ="+typedArray.getInt(R.styleable.lbImageCardView_lbImageCardViewType, -1));
        cardView.setInfoAreaBackgroundColor(card.getFooterColor());
    }

}
