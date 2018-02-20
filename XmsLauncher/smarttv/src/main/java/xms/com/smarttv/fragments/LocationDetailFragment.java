package xms.com.smarttv.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.Serializable;

import xms.com.smarttv.Presenter.DetailsDescriptionPresenter;
import xms.com.smarttv.R;
import xms.com.smarttv.models.Card;

public class LocationDetailFragment extends DetailsFragment implements OnActionClickedListener{
    private static final String ITEM_TAG = "CARD";
    private ArrayObjectAdapter mRowsAdapter;
    private Card card;
    private LocationDetailFragmentListener mListener;
    private int ACTION_SHOW_ON_MAP = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        card = (Card)getArguments().getSerializable(ITEM_TAG);
        buildDetails();
    }

    public static LocationDetailFragment newInstance(Serializable object) {
        LocationDetailFragment fragment = new LocationDetailFragment();
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
                .circleCrop()
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

        adapter.set(1, new Action(ACTION_SHOW_ON_MAP, "View Location"));

        detailsOverview.setActionsAdapter(adapter);
        rowPresenter.setOnActionClickedListener(this);

        // Add images and action buttons to the details view
        mRowsAdapter.add(detailsOverview);
        setAdapter(mRowsAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LocationDetailFragmentListener) {
            mListener = (LocationDetailFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActionClicked(Action action) {
        if (action.getId() == ACTION_SHOW_ON_MAP) {
            mListener.LoadMap(card.getLattitude(), card.getLongitude(), 13);
        }
    }

    public interface LocationDetailFragmentListener {
        void LoadMap(Double latitude, Double longitude, int zoom);
    }
}

