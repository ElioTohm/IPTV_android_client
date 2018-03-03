package xms.com.smarttv.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.app.DetailsFragmentBackgroundController;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.eliotohme.data.Movie;

import java.io.Serializable;

import xms.com.smarttv.Presenter.DetailsDescriptionPresenter;
import xms.com.smarttv.R;

public class VODDetailFragment extends DetailsFragment implements OnActionClickedListener {
    private static final String MOVIE_TAG = "MOVIE";
    private int ACTION_PURCHASE = 1;
    private int ACTION_WATCH = 2;
    private VODDetailFragmentListener listener;
    private ArrayObjectAdapter mRowsAdapter;
    private Movie movie;
    private final DetailsFragmentBackgroundController mDetailsBackground =
            new DetailsFragmentBackgroundController(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movie = (Movie) getArguments().getSerializable(MOVIE_TAG);
        buildDetails();
    }

    public static VODDetailFragment newInstance(Serializable object) {
        VODDetailFragment fragment = new VODDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(MOVIE_TAG, object);
        fragment.setArguments(args);
        return fragment;
    }

    private void buildDetails() {
        ClassPresenterSelector selector = new ClassPresenterSelector();
        // Attach your media item details presenter to the row presenter:
        FullWidthDetailsOverviewRowPresenter rowPresenter = new FullWidthDetailsOverviewRowPresenter(
                new DetailsDescriptionPresenter()) {

            @Override
            protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup parent) {
                // Customize Actionbar and Content by using custom colors.
                RowPresenter.ViewHolder viewHolder = super.createRowViewHolder(parent);

                View actionsView = viewHolder.view.
                        findViewById(R.id.details_overview_actions_background);
                actionsView.setBackgroundColor(getActivity().getResources().
                        getColor(R.color.vod_detail_view_actionbar_background));

                View detailsView = viewHolder.view.findViewById(R.id.details_frame);
                detailsView.setBackgroundColor(
                        getResources().getColor(R.color.vod_detail_view_background));
                return viewHolder;
            }
        };

        selector.addClassPresenter(DetailsOverviewRow.class, rowPresenter);
        mRowsAdapter = new ArrayObjectAdapter(selector);

        final DetailsOverviewRow detailsOverview = new DetailsOverviewRow(movie);
        RequestOptions options = new RequestOptions()
                .error(R.drawable.default_background)
                .dontAnimate();

        Glide.with(this)
            .asBitmap()
            .load(movie.getPoster())
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

        if ( movie.getPrice() > 0 && !movie.isPurchased()) {
            adapter.set(ACTION_PURCHASE, new Action(ACTION_PURCHASE, "Rent"));
        } else {
            adapter.set(ACTION_WATCH, new Action(ACTION_WATCH, "watch"));
        }

        detailsOverview.setActionsAdapter(adapter);

        // Add images and action buttons to the details view
        mRowsAdapter.add(detailsOverview);
        setAdapter(mRowsAdapter);
        initializeBackground();
    }

    @SuppressLint("ResourceAsColor")
    private void initializeBackground() {
        mDetailsBackground.enableParallax();
        mDetailsBackground.setSolidColor(R.color.BlackTransparent);

        RequestOptions options = new RequestOptions()
                .error(R.drawable.default_background)
                .centerCrop();

        Glide.with(this)
                .asBitmap()
                .load(movie.getPoster())
                .apply(options)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(
                            @NonNull Bitmap resource,
                            Transition<? super Bitmap> transition) {
                        mDetailsBackground.setCoverBitmap(resource);
                        startEntranceTransition();
                    }
                });
    }

    @Override
    public void onActionClicked(Action action) {
        if (action.getId() == ACTION_PURCHASE) {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_purshase, PurchaseDialog.newInstance(movie, "Movie"))
                    .commit();
        } else if (action.getId() == ACTION_WATCH) {

        }
    }

    public interface VODDetailFragmentListener {
        void purchase(Movie movie);
        void watch(Movie movie);
    }
}
