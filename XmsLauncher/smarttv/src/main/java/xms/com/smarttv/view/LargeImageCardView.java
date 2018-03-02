package xms.com.smarttv.view;

import android.content.Context;
import android.support.v17.leanback.widget.BaseCardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eliotohme.data.Movie;

import xms.com.smarttv.R;

public class LargeImageCardView extends BaseCardView {
    ImageView mImageView;
    public LargeImageCardView(Context context) {
        super(context, null, R.style.LargeCardTheme);

        LayoutInflater.from(getContext()).inflate(R.layout.vod_card_view_layout, this);
        mImageView = findViewById(R.id.main_image);
        setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                View container = findViewById(R.id.container);
                if (hasFocus) {
                    container.setBackgroundColor(R.drawable.character_focused);
                } else {
                    container.setBackgroundResource(R.drawable.character_not_focused_padding);
                }
            }
        });
        setFocusable(true);
    }

    public void updateUi(Movie movie) {
        final ImageView imageView = findViewById(R.id.main_image);
        final TextView title = findViewById(R.id.title);
        title.setText(movie.getTitle());
        Glide.with(getContext())
                .asBitmap()
                .load(movie.getPoster())
                .into(imageView);
    }
    /**
     * Returns the main image view.
     */
    public final ImageView getMainImageView() {
        return mImageView;
    }
}
