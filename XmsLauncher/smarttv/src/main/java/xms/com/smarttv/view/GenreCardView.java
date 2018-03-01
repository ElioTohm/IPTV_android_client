package xms.com.smarttv.view;

import android.content.Context;
import android.support.v17.leanback.widget.BaseCardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.eliotohme.data.Genre;

import xms.com.smarttv.R;

public class GenreCardView extends BaseCardView {

    public GenreCardView(Context context) {
        super(context, null, R.style.LargeCardTheme);
        LayoutInflater.from(getContext()).inflate(R.layout.image_text_cardview, this);
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

    public void load(Genre genre) {
        final ImageView imageView = findViewById(R.id.main_image);
        Glide.with(getContext())
                .asBitmap()
                .load("http://192.168.0.75/storage/genres/Action.png")
                .into(imageView);
    }

}
