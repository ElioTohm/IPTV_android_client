package xms.com.smarttv.Presenter;

import android.content.Context;
import android.support.v17.leanback.widget.BaseCardView;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eliotohme.data.Genre;
import com.eliotohme.data.Movie;
import com.eliotohme.data.SectionItem;

import xms.com.smarttv.R;
import xms.com.smarttv.models.Card;

/**
 * Created by elio on 2/3/18.
 */

public class SideInfoCardPresenter extends AbstractCardPresenter<BaseCardView> {

    public SideInfoCardPresenter(Context context) {
        super(context, 0);
    }

    @Override
    protected BaseCardView onCreateView() {
        final BaseCardView cardView = new BaseCardView(getContext(), null,
                R.style.SideInfoCardStyle);
        cardView.setFocusable(true);
        cardView.addView(LayoutInflater.from(getContext()).inflate(R.layout.side_info_card, null));
        return cardView;
    }

    @Override
    public void onBindViewHolder(Card card, BaseCardView cardView) {
        ImageView imageView = (ImageView) cardView.findViewById(R.id.main_image);
        if (card.getLocalImageResourceName() != null) {
            int width = (int) getContext().getResources()
                    .getDimension(R.dimen.sidetext_image_card_width);
            int height = (int) getContext().getResources()
                    .getDimension(R.dimen.sidetext_image_card_height);
            int resourceId = getContext().getResources()
                    .getIdentifier(card.getLocalImageResourceName(),
                            "drawable", getContext().getPackageName());
            RequestOptions myOptions = new RequestOptions()
                    .override(width, height);
            Glide.with(getContext())
                    .asBitmap()
                    .load(resourceId)
                    .apply(myOptions)
                    .into(imageView);
        }

        TextView primaryText = (TextView) cardView.findViewById(R.id.primary_text);
        primaryText.setText(card.getTitle());

        TextView secondaryText = (TextView) cardView.findViewById(R.id.secondary_text);
        secondaryText.setText(card.getDescription());

        TextView extraText = (TextView) cardView.findViewById(R.id.extra_text);
        extraText.setText(card.getExtraText());
    }

    @Override
    public void onBindViewHolder(Movie movie, BaseCardView cardView) {

    }

    @Override
    public void onBindViewHolder(SectionItem sectionItem, BaseCardView cardView) {

    }

    @Override
    public void onBindViewHolder(Genre genre, BaseCardView cardView) {

    }

}
