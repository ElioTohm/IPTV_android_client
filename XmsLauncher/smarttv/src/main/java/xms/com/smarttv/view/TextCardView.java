package xms.com.smarttv.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v17.leanback.widget.BaseCardView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import xms.com.smarttv.R;
import xms.com.smarttv.models.Card;

/**
 * Created by elio on 2/3/18.
 */

public class TextCardView extends BaseCardView {

    public TextCardView(Context context) {
        super(context, null, R.style.TextCardStyle);
        LayoutInflater.from(getContext()).inflate(R.layout.text_icon_card, this);
        setFocusable(true);
    }

    public void updateUi(Card card) {
        TextView extraText = (TextView) findViewById(R.id.extra_text);
        TextView primaryText = (TextView) findViewById(R.id.primary_text);
        final ImageView imageView = (ImageView) findViewById(R.id.main_image);

        extraText.setText(card.getExtraText());
        primaryText.setText(card.getTitle());

        // Create a rounded drawable.
        int resourceId = card.getLocalImageResourceId(getContext());
        Bitmap bitmap = BitmapFactory
                .decodeResource(getContext().getResources(), resourceId);
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getContext().getResources(), bitmap);
        drawable.setAntiAlias(true);
        drawable.setCornerRadius(
                Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
        imageView.setImageDrawable(drawable);
    }

}
