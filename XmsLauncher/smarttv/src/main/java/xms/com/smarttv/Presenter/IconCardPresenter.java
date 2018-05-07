package xms.com.smarttv.Presenter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.view.View;
import android.widget.ImageView;

import xms.com.smarttv.R;

/**
 * Created by elio on 2/3/18.
 */

public class IconCardPresenter extends ImageCardViewPresenter {
    private static final int ANIMATION_DURATION = 200;

    public IconCardPresenter(Context context) {
        super(context, R.style.IconCardTheme);
    }

    @Override
    protected ImageCardView onCreateView() {
        final ImageCardView imageCardView = super.onCreateView();
        final ImageView image = imageCardView.getMainImageView();
        image.setBackgroundResource(R.drawable.icon_focused);
        image.getBackground().setAlpha(0);
        imageCardView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                animateIconBackground(image.getBackground(), hasFocus);
            }
        });
        return imageCardView;
    }

    private void animateIconBackground(Drawable drawable, boolean hasFocus) {
        if (hasFocus) {
            ObjectAnimator.ofInt(drawable, "alpha", 0, 255).setDuration(ANIMATION_DURATION).start();
        } else {
            ObjectAnimator.ofInt(drawable, "alpha", 255, 0).setDuration(ANIMATION_DURATION).start();
        }
    }
}
