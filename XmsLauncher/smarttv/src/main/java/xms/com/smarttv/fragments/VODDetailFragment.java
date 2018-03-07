package xms.com.smarttv.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.eliotohme.data.Movie;

import java.io.Serializable;
import java.util.Arrays;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import xms.com.smarttv.Presenter.DetailsDescriptionPresenter;
import xms.com.smarttv.R;

public class VODDetailFragment extends DetailsFragment implements OnItemViewClickedListener {
    private static final String MOVIE_TAG = "MOVIE";
    private int ACTION_RENT = 1;
    private int ACTION_WATCH = 2;
    private VODDetailFragmentListener listener;
    private ArrayObjectAdapter mRowsAdapter;
    private Movie movie;
    DetailsOverviewRow detailsOverview;
    OrderedRealmCollectionChangeListener orderedRealmCollectionChangeListener;
    SparseArrayObjectAdapter adapter;
    RealmResults<Movie> movies;
    private final DetailsFragmentBackgroundController mDetailsBackground =
            new DetailsFragmentBackgroundController(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movie = (Movie) getArguments().getSerializable(MOVIE_TAG);
        buildDetails();
        setOnItemViewClickedListener(this);
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

        detailsOverview = new DetailsOverviewRow(movie);
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

        Realm realm = Realm.getDefaultInstance();

        adapter = new SparseArrayObjectAdapter();

        movies = realm.where(Movie.class).findAll();

        if ( movie.getPrice() > 0 && !movie.isPurchased()) {
            adapter.set(ACTION_RENT, new Action(ACTION_RENT, "Rent"));
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
        orderedRealmCollectionChangeListener = new OrderedRealmCollectionChangeListener<RealmResults<Movie>>() {
            @Override
            public void onChange(RealmResults<Movie> results, OrderedCollectionChangeSet changeSet) {
                Log.e("ELIO", Arrays.toString(changeSet.getInsertions()));
                adapter.clear();
                if ( movie.getPrice() > 0 && !movie.isPurchased()) {
                    adapter.set(ACTION_RENT, new Action(ACTION_RENT, "Rent"));
                } else {
                    adapter.set(ACTION_WATCH, new Action(ACTION_WATCH, "watch"));
                }
                detailsOverview.setActionsAdapter(adapter);
                mRowsAdapter.add(detailsOverview);
                setAdapter(mRowsAdapter);
            }
        };
        movies.addChangeListener(orderedRealmCollectionChangeListener);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof VODDetailFragmentListener) {
            listener = (VODDetailFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach () {
        super.onDetach();
        movies.removeChangeListener(orderedRealmCollectionChangeListener);
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (!(item instanceof Action)) return;
        Action action = (Action) item;
        if (action.getId() == ACTION_RENT) {
            listener.purchase(this.movie);
            // Listeners will be notified when data changes
        } else {
            listener.watch(this.movie);
        }
    }

    public interface VODDetailFragmentListener {
        void purchase(Movie movie);
        void watch(Movie movie);
    }
}
