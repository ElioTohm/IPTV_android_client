package xms.com.smarttv.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.Serializable;

import xms.com.smarttv.Presenter.DetailsDescriptionPresenter;
import xms.com.smarttv.R;
import xms.com.smarttv.models.Card;

public class ItemDetailFragment extends DetailsFragment {
    private static final String ITEM_TAG = "CARD";
    private ArrayObjectAdapter mRowsAdapter;
    private Card card;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        card = (Card)getArguments().getSerializable(ITEM_TAG);

        buildDetails();
    }

    public static ItemDetailFragment newInstance(Serializable object) {
        ItemDetailFragment fragment = new ItemDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ITEM_TAG, object);
        fragment.setArguments(args);
        return fragment;
    }

    private void buildDetails() {
        ClassPresenterSelector selector = new ClassPresenterSelector();
        // Attach your media item details presenter to the row presenter:
        FullWidthDetailsOverviewRowPresenter rowPresenter =
                new FullWidthDetailsOverviewRowPresenter(
                        new DetailsDescriptionPresenter());
        rowPresenter.setBackgroundColor(getResources().getColor(R.color.detail_view_background));
        rowPresenter.setActionsBackgroundColor(getResources().getColor(R.color.detail_view_actionbar_background));

        selector.addClassPresenter(DetailsOverviewRow.class, rowPresenter);
        mRowsAdapter = new ArrayObjectAdapter(selector);

        final DetailsOverviewRow detailsOverview = new DetailsOverviewRow(card);
        RequestOptions options = new RequestOptions()
                .error(R.drawable.default_background)
                .dontAnimate();

        Glide.with(this)
                .asBitmap()
                .load(card.getImageUrl())
                .apply(options)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(
                            @NonNull Bitmap resource,
                            Transition<? super Bitmap> transition) {
                        detailsOverview.setImageBitmap(getActivity(), resource);
                        startEntranceTransition();
                    }
                });

        SparseArrayObjectAdapter adapter = new SparseArrayObjectAdapter();

        adapter.set(1, new Action(1, getResources()
                .getString(R.string.watch_trailer_1),
                getResources().getString(R.string.watch_trailer_2)));
        adapter.set(2, new Action(2, getResources().getString(R.string.rent_1),
                getResources().getString(R.string.rent_2)));
        adapter.set(3, new Action(3, getResources().getString(R.string.buy_1),
                getResources().getString(R.string.buy_2)));
        detailsOverview.setActionsAdapter(adapter);

        // Add images and action buttons to the details view
        mRowsAdapter.add(detailsOverview);
        setAdapter(mRowsAdapter);
    }
}
