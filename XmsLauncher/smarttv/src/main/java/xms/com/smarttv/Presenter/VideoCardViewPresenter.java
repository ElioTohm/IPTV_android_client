package xms.com.smarttv.Presenter;

import android.content.Context;
import android.support.v17.leanback.widget.ImageCardView;

import com.bumptech.glide.Glide;

import xms.com.smarttv.models.Card;
import xms.com.smarttv.models.VideoCard;

/**
 * Created by elio on 2/3/18.
 */

public class VideoCardViewPresenter extends ImageCardViewPresenter {

    public VideoCardViewPresenter(Context context, int cardThemeResId) {
        super(context, cardThemeResId);
    }

    public VideoCardViewPresenter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(Card card, final ImageCardView cardView) {
        super.onBindViewHolder(card, cardView);
        VideoCard videoCard = (VideoCard) card;
        Glide.with(getContext())
                .asBitmap()
                .load(videoCard.getImageUrl())
                .into(cardView.getMainImageView());

    }

}
