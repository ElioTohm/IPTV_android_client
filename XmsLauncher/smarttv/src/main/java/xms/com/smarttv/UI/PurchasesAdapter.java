package xms.com.smarttv.UI;

import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eliotohme.data.Channel;
import com.eliotohme.data.Movie;
import com.eliotohme.data.Purchase;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import xms.com.smarttv.R;

public class PurchasesAdapter extends RealmRecyclerViewAdapter<Purchase, PurchasesAdapter.ViewHolder> {

    public PurchasesAdapter(@Nullable OrderedRealmCollection<Purchase> data, boolean autoUpdate) {
        super(data, autoUpdate);
        setHasStableIds(true);
    }

    @Override
    public PurchasesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.purchased_item_adapter_view, parent, false);
        return new PurchasesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PurchasesAdapter.ViewHolder holder, int position) {
        holder.purchase = getItem(position);
        if (holder.purchase.getPurchasableType().equals("App\\Movie")) {
            holder.channel_name.setText(Realm.getDefaultInstance()
                    .where(Movie.class)
                    .equalTo("id", holder.purchase.getPurchasableId())
                    .findFirst()
                    .getTitle());
            holder.channel_number.setText(
                    String.valueOf(Realm.getDefaultInstance()
                            .where(Movie.class)
                            .equalTo("id", holder.purchase.getPurchasableId())
                            .findFirst()
                            .getPrice() + " Units"));
        } else if (holder.purchase.getPurchasableType().equals("App\\Channel")) {
            holder.channel_name.setText(
                    Realm.getDefaultInstance()
                            .where(Channel.class)
                            .equalTo("id", holder.purchase.getPurchasableId())
                            .findFirst().getName());
            holder.channel_number.setText(String.valueOf(
                    Realm.getDefaultInstance()
                            .where(Channel.class)
                            .equalTo("id", holder.purchase.getPurchasableId())
                            .findFirst()
                            .getPrice() + " Units"));
        }
        holder.mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundColor(v.getContext().getResources().getColor(R.color.selected_row_header));
                } else {
                    v.setBackgroundColor(0x00000000);
                }
            }
        });
    }

    @Override
    public long getItemId(int index) {
        return getItem(index).getId();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView channel_name;
        TextView channel_number;
        ImageView purchasable_icon;
        Purchase purchase;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            channel_name = view.findViewById(R.id.item_name);
            channel_number = view.findViewById(R.id.item_number);
            purchasable_icon = view.findViewById(R.id.purchasable_icon);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + channel_name.getText() + "'";
        }
    }

    public interface OnChannelClicked {
        void UpdateLastPosition(int currentposition);
    }
}
