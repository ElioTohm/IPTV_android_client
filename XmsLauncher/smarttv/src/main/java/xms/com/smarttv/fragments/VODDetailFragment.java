package xms.com.smarttv.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.app.DetailsFragmentBackgroundController;
import android.support.v17.leanback.media.MediaPlayerGlue;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
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

public class VODDetailFragment extends DetailsFragment {
    private static final String MOVIE_TAG = "MOVIE";
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

        adapter.set(1, new Action(1, getResources()
                .getString(R.string.watch_trailer_1),
                getResources().getString(R.string.watch_trailer_2)));
        adapter.set(2, new Action(2, getResources().getString(R.string.rent_1)));

        detailsOverview.setActionsAdapter(adapter);

        // Add images and action buttons to the details view
        mRowsAdapter.add(detailsOverview);
        setAdapter(mRowsAdapter);
        initializeBackground();
    }

    @SuppressLint("RestrictedApi")
    private void initializeBackground() {
        mDetailsBackground.enableParallax();

        MediaPlayerGlue playerGlue = new MediaPlayerGlue(getActivity());
        mDetailsBackground.setupVideoPlayback(playerGlue);

        playerGlue.setTitle(movie.getTitle());
        playerGlue.setArtist(movie.getDescription());
        playerGlue.setVideoUrl(movie.getStream().getVid_stream());
    }
}
