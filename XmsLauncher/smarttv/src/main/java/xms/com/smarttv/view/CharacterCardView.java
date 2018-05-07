package xms.com.smarttv.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v17.leanback.widget.BaseCardView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import xms.com.smarttv.R;
import xms.com.smarttv.models.Card;

public class CharacterCardView extends BaseCardView {

    public CharacterCardView(Context context) {
        super(context, null, R.style.DefaultCardTheme);
        LayoutInflater.from(getContext()).inflate(R.layout.character_card, this);
        setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ImageView mainImage = (ImageView) findViewById(R.id.main_image);
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

    public void updateUi(Card card) {
        TextView primaryText = findViewById(R.id.primary_text);
        final ImageView imageView = findViewById(R.id.main_image);

        primaryText.setText(card.getTitle());
        if (card.getLocalImageResourceName() != null) {
            int resourceId = card.getLocalImageResourceId(getContext());
            Bitmap bitmap = BitmapFactory
                    .decodeResource(getContext().getResources(), resourceId);
            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getContext().getResources(), bitmap);
            drawable.setAntiAlias(true);
            drawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
            imageView.setImageDrawable(drawable);
        } else if (card.getImageUrl() != null) {
            Glide.with(getContext())
                .asBitmap()
                .apply(RequestOptions.circleCropTransform())
                .load(card.getImageUrl())
                .into(imageView);
        }
    }


}
