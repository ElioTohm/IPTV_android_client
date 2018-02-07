package xms.com.smarttv.UI;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.app.RowsFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.PageRow;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.eliotohme.data.Client;
import com.google.gson.Gson;

import io.realm.Realm;
import xms.com.smarttv.CardListRow;
import xms.com.smarttv.Presenter.CardPresenterSelector;
import xms.com.smarttv.Presenter.IconHeaderItemPresenter;
import xms.com.smarttv.Presenter.SettingsIconPresenter;
import xms.com.smarttv.Presenter.ShadowRowPresenterSelector;
import xms.com.smarttv.R;
import xms.com.smarttv.Utils;
import xms.com.smarttv.models.Card;
import xms.com.smarttv.models.CardRow;

public class MainMenu extends BrowseFragment {
    private static final long HEADER_ID_0 = 0;
    private static final String HEADER_NAME_0 = "Room Services";
    private static final long HEADER_ID_1 = 1;
    private static final String HEADER_NAME_1 = "Restaurants & Bars";
    private static final long HEADER_ID_2 = 2;
    private static final String HEADER_NAME_2 = "Spa & Fitness";
    private static final long HEADER_ID_3 = 3;
    private static final String HEADER_NAME_3 = "Special Offers";
    private static final long HEADER_ID_4 = 4;
    private static final String HEADER_NAME_4 = "Weather";
    private static final long HEADER_ID_5 = 5;
    private static final String HEADER_NAME_5 = "City Guide";

    private ArrayObjectAdapter mRowsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUi();
        loadData();
        BackgroundManager mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        getMainFragmentRegistry().registerFragment(PageRow.class,
                new PageRowFragmentFactory(mBackgroundManager));
    }

    private void setupUi() {
        setHeadersState(HEADERS_ENABLED);
        setTitle("Welcome " + Realm.getDefaultInstance().where(Client.class).findFirst().getName());
        setHeadersTransitionOnBackEnabled(true);
        setHeaderPresenterSelector(new PresenterSelector() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public Presenter getPresenter(Object item) {
                return new IconHeaderItemPresenter();
            }
        });
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        prepareEntranceTransition();
    }

    private void loadData() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(mRowsAdapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                createRows();
                startEntranceTransition();
            }
        }, 2000);
    }

    private void createRows() {
        CustomHeaderItem headerItem0 = new CustomHeaderItem(HEADER_ID_0, HEADER_NAME_0, R.drawable.lb_ic_fast_forward);
        PageRow pageRow0 = new PageRow(headerItem0);
        mRowsAdapter.add(pageRow0);

        CustomHeaderItem headerItem1 = new CustomHeaderItem(HEADER_ID_1, HEADER_NAME_1, R.drawable.glass);
        PageRow pageRow1 = new PageRow(headerItem1);
        mRowsAdapter.add(pageRow1);

        CustomHeaderItem headerItem2 = new CustomHeaderItem(HEADER_ID_2, HEADER_NAME_2, R.drawable.lb_ic_fast_forward);
        PageRow pageRow2 = new PageRow(headerItem2);
        mRowsAdapter.add(pageRow2);

        CustomHeaderItem headerItem3 = new CustomHeaderItem(HEADER_ID_3, HEADER_NAME_3, R.drawable.tagsale);
        PageRow pageRow3 = new PageRow(headerItem3);
        mRowsAdapter.add(pageRow3);

        CustomHeaderItem headerItem4 = new CustomHeaderItem(HEADER_ID_4, HEADER_NAME_4, R.drawable.lb_ic_fast_forward);
        PageRow pageRow4 = new PageRow(headerItem4);
        mRowsAdapter.add(pageRow4);
    }

    private static class PageRowFragmentFactory extends BrowseFragment.FragmentFactory {
        private final BackgroundManager mBackgroundManager;

        PageRowFragmentFactory(BackgroundManager backgroundManager) {
            this.mBackgroundManager = backgroundManager;
        }

        @Override
        public Fragment createFragment(Object rowObj) {
            Row row = (Row)rowObj;
            mBackgroundManager.setDrawable(null);
            if (row.getHeaderItem().getId() == HEADER_ID_0) {
                return new ApplicationsMenu();
            } else if (row.getHeaderItem().getId() == HEADER_ID_1) {
                return new ApplicationsMenu();
            } else if (row.getHeaderItem().getId() == HEADER_ID_2) {
                return new SampleFragmentB();
            } else if (row.getHeaderItem().getId() == HEADER_ID_3) {
                return new SettingsFragment();
            } else if (row.getHeaderItem().getId() == HEADER_ID_4) {
                return new WebViewFragment();
            }

            throw new IllegalArgumentException(String.format("Invalid row %s", rowObj));
        }
    }

    /**
     * Page fragment embeds a rows fragment.
     */
    public static class SampleFragmentB extends RowsFragment {
        private final ArrayObjectAdapter mRowsAdapter;

        public SampleFragmentB() {
            mRowsAdapter = new ArrayObjectAdapter(new ShadowRowPresenterSelector());

            setAdapter(mRowsAdapter);
            setOnItemViewClickedListener(new OnItemViewClickedListener() {
                @Override
                public void onItemClicked(
                        Presenter.ViewHolder itemViewHolder,
                        Object item,
                        RowPresenter.ViewHolder rowViewHolder,
                        Row row) {
                }
            });
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            createRows();
        }

        private void createRows() {
            String json = Utils.inputStreamToString(getResources().openRawResource(
                    R.raw.page_row_example));
            CardRow[] rows = new Gson().fromJson(json, CardRow[].class);
            for (CardRow row : rows) {
                if (row.getType() == CardRow.TYPE_DEFAULT) {
                    mRowsAdapter.add(createCardRow(row));
                }
            }
        }

        private Row createCardRow(CardRow cardRow) {
            PresenterSelector presenterSelector = new CardPresenterSelector(getActivity());
            ArrayObjectAdapter adapter = new ArrayObjectAdapter(presenterSelector);
            for (Card card : cardRow.getCards()) {
                adapter.add(card);
            }

            CustomHeaderItem headerItem = new CustomHeaderItem(cardRow.getTitle());
            return new CardListRow(headerItem, adapter, cardRow);
        }
    }

    public static class SettingsFragment extends RowsFragment {
        private final ArrayObjectAdapter mRowsAdapter;

        public SettingsFragment() {
            ListRowPresenter selector = new ListRowPresenter();
            selector.setNumRows(2);
            mRowsAdapter = new ArrayObjectAdapter(selector);
            setAdapter(mRowsAdapter);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadData();
                }
            }, 200);
        }

        private void loadData() {
            if (isAdded()) {
                String json = Utils.inputStreamToString(getResources().openRawResource(
                        R.raw.icon_example));
                CardRow cardRow = new Gson().fromJson(json, CardRow.class);
                mRowsAdapter.add(createCardRow(cardRow));
                getMainFragmentAdapter().getFragmentHost().notifyDataReady(
                        getMainFragmentAdapter());
            }
        }

        private ListRow createCardRow(CardRow cardRow) {
            SettingsIconPresenter iconCardPresenter = new SettingsIconPresenter(getActivity());
            ArrayObjectAdapter adapter = new ArrayObjectAdapter(iconCardPresenter);
            for(Card card : cardRow.getCards()) {
                adapter.add(card);
            }

            CustomHeaderItem headerItem = new CustomHeaderItem(cardRow.getTitle());
            return new CardListRow(headerItem, adapter, cardRow);
        }
    }

    public static class WebViewFragment extends Fragment implements MainFragmentAdapterProvider {
        private MainFragmentAdapter mMainFragmentAdapter = new MainFragmentAdapter(this);
        private WebView mWebview;
        public static  final String LON = "55.207660";
        public static  final String LAT = "25.082351";
        public static final String WEATHER_WEB_URL = String.format("http://forecast.io/embed/#lat=%s&lon=%s&name=Wellington color=#00aaff&font=Arial&units=ca", LAT, LON);

        @Override
        public MainFragmentAdapter getMainFragmentAdapter() {
            return mMainFragmentAdapter;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getMainFragmentAdapter().getFragmentHost().showTitleView(false);
        }

        @Override
        public View onCreateView(
                LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            FrameLayout root = new FrameLayout(getActivity());
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            lp.setMarginStart(32);
            mWebview = new WebView(getActivity());
            mWebview.setWebViewClient(new WebViewClient());
            mWebview.getSettings().setJavaScriptEnabled(true);
            root.addView(mWebview, lp);
            return root;
        }

        @Override
        public void onResume() {
            super.onResume();
            mWebview.loadUrl(WEATHER_WEB_URL);
            getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
        }
    }
}
