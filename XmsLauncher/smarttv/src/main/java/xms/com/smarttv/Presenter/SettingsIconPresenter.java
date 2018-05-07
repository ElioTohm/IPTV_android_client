package xms.com.smarttv.Presenter;

import android.content.Context;
import android.support.v17.leanback.widget.ImageCardView;
import android.view.View;

import xms.com.smarttv.R;

/**
 * Created by elio on 2/5/18.
 */

public class SettingsIconPresenter extends ImageCardViewPresenter {

    public SettingsIconPresenter(Context context) {
        super(context, R.style.IconCardTheme);
    }

    @Override
    protected ImageCardView onCreateView() {
        final ImageCardView imageCardView = super.onCreateView();
        imageCardView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setImageBackground(imageCardView, R.color.settings_card_background_focussed);
                } else {
                    setImageBackground(imageCardView, R.color.settings_card_background);
                }
            }
        });
        setImageBackground(imageCardView, R.color.settings_card_background);
        return imageCardView;
    }

    private void setImageBackground(ImageCardView imageCardView, int colorId) {
        imageCardView.setBackgroundColor(getContext().getResources().getColor(colorId));
    }
}
