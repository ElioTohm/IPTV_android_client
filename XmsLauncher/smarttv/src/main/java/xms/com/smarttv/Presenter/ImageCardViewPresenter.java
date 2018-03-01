package xms.com.smarttv.Presenter;

import android.content.Context;
import android.support.v17.leanback.widget.ImageCardView;
import android.view.ContextThemeWrapper;

import com.bumptech.glide.Glide;
import com.eliotohme.data.Movie;

import xms.com.smarttv.R;
import xms.com.smarttv.models.Card;

public class ImageCardViewPresenter extends AbstractCardPresenter<ImageCardView> {
    private static int sSelectedBackgroundColor;
    private static int sDefaultBackgroundColor;

    public ImageCardViewPresenter(Context context, int cardThemeResId) {
        super(new ContextThemeWrapper(context, cardThemeResId));
    }

    public ImageCardViewPresenter(Context context) {
        this(context, R.style.DefaultCardTheme);
    }

    private static void updateCardBackgroundColor(ImageCardView view, boolean selected) {
        int color = selected ? sSelectedBackgroundColor : sDefaultBackgroundColor;
        // Both background colors should be set because the view's background is temporarily visible
        // during animations.
        view.setBackgroundColor(color);
        view.findViewById(R.id.info_field).setBackgroundColor(color);
    }

    @Override
    protected ImageCardView onCreateView() {
        sDefaultBackgroundColor = getContext().getResources().getColor(R.color.row_item);
        sSelectedBackgroundColor = getContext().getResources().getColor(R.color.selected_row_item);
        ImageCardView imageCardView = new ImageCardView(getContext()) {
            @Override
            public void setSelected(boolean selected) {
                updateCardBackgroundColor(this, selected);
                super.setSelected(selected);
            }
        };
        updateCardBackgroundColor(imageCardView, false);
        return imageCardView;
    }

    @Override
    public void onBindViewHolder(Card card, final ImageCardView cardView) {
        cardView.setTag(card);
        cardView.setTitleText(card.getTitle());
        cardView.setContentText(card.getDescription());
        if (card.getLocalImageResourceName() != null) {
            int resourceId = getContext().getResources()
                    .getIdentifier(card.getLocalImageResourceName(),
                            "drawable", getContext().getPackageName());
            Glide.with(getContext())
                    .asBitmap()
                    .load(resourceId)
                    .into(cardView.getMainImageView());
        } else if (card.getImageUrl() != null) {
            Glide.with(getContext())
                    .asBitmap()
                    .load(card.getImageUrl())
                    .into(cardView.getMainImageView());
        }
    }

    @Override
    public void onBindViewHolder(Movie movie, ImageCardView cardView) {
        Glide.with(getContext())
            .asBitmap()
            .load(movie.getPoster())
            .into(cardView.getMainImageView());
    }

}

