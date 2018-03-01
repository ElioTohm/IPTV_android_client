package xms.com.smarttv;

import android.content.Context;
import android.support.v17.leanback.widget.BaseCardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.eliotohme.data.Movie;

public class LargeImageCardView extends BaseCardView {

    public LargeImageCardView(Context context) {
        super(context, null, R.style.LargeCardTheme);
        LayoutInflater.from(getContext()).inflate(R.layout.vod_card_view_layout, this);
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
        Glide.with(getContext())
                .asBitmap()
                .load(movie.getPoster())
                .into(imageView);
    }

}
