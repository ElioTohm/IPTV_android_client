package xms.com.smarttv.Presenter;

import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;

import xms.com.smarttv.models.CardListRow;
import xms.com.smarttv.models.CardRow;

public class ShadowRowPresenterSelector extends PresenterSelector {

    private ListRowPresenter mShadowEnabledRowPresenter = new ListRowPresenter();
    private ListRowPresenter mShadowDisabledRowPresenter = new ListRowPresenter();

    public ShadowRowPresenterSelector() {
        mShadowEnabledRowPresenter.setNumRows(1);
        mShadowDisabledRowPresenter.setShadowEnabled(false);
    }

    @Override public Presenter getPresenter(Object item) {
        if (!(item instanceof CardListRow)) return mShadowDisabledRowPresenter;
        CardListRow listRow = (CardListRow) item;
        CardRow row = listRow.getCardRow();
        if (row.useShadow()) return mShadowEnabledRowPresenter;
        return mShadowDisabledRowPresenter;
    }

    @Override
    public Presenter[] getPresenters() {
        return new Presenter [] {
                mShadowDisabledRowPresenter,
                mShadowEnabledRowPresenter
        };
    }
}
