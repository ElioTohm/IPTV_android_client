/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package xms.com.smarttv.Presenter;

import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import xms.com.smarttv.R;
import xms.com.smarttv.models.Card;
import xms.com.smarttv.objects.ServiceApp;

/*
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an Image CardView
 */
public class CardPresenter extends Presenter {
    private static final String TAG = "CardPresenter";

    private static final int CARD_WIDTH = 200;
    private static final int CARD_HEIGHT = 88;
    private static int sSelectedBackgroundColor;
    private static int sDefaultBackgroundColor;

    private static void updateCardBackgroundColor(ImageCardView view, boolean selected) {
        int color = selected ? sSelectedBackgroundColor : sDefaultBackgroundColor;
        // Both background colors should be set because the view's background is temporarily visible
        // during animations.
        view.setBackgroundColor(color);
        view.findViewById(R.id.info_field).setBackgroundColor(color);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        Log.d(TAG, "onCreateViewHolder");

        sDefaultBackgroundColor = parent.getResources().getColor(R.color.selected_row_item);
        sSelectedBackgroundColor = parent.getResources().getColor(R.color.row_item);

        ImageCardView cardView = new ImageCardView(parent.getContext()) {
            @Override
            public void setSelected(boolean selected) {
                updateCardBackgroundColor(this, selected);
                super.setSelected(selected);
            }
        };

        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        cardView.setMainImageScaleType(ImageView.ScaleType.FIT_XY);
        updateCardBackgroundColor(cardView, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);

        if (item instanceof ServiceApp) {
            ServiceApp serviceApp = (ServiceApp) item;

            if (serviceApp.getCardImageUrl() != null) {
                cardView.setTitleText(serviceApp.getTitle());
                cardView.setContentText(serviceApp.getStudio());
                Glide.with(viewHolder.view.getContext())
                        .load(serviceApp.getSvgimage())
                        .into(cardView.getMainImageView());
                cardView.getMainImageView().setImageResource(serviceApp.getSvgimage());
            }
        } else if (item instanceof Card) {
            Card card = (Card) item;
            cardView.setTag(card);
            cardView.setTitleText(card.getTitle());
            cardView.setContentText(card.getDescription());
            if (card.getLocalImageResourceName() != null) {
                int resourceId = cardView.getContext().getResources()
                        .getIdentifier(card.getLocalImageResourceName(),
                                "drawable", cardView.getContext().getPackageName());
                Glide.with(viewHolder.view.getContext())
                        .load(resourceId)
                        .into(cardView.getMainImageView());
            }
        }
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }
}
