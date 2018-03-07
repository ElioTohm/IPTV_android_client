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

import com.bumptech.glide.Glide;
import com.eliotohme.data.Channel;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import xms.com.smarttv.R;
import xms.com.smarttv.app.Preferences;
import xms.com.smarttv.fragments.ChannelsListFragment;

public class ChannelRecyclerViewAdapter extends RealmRecyclerViewAdapter<Channel, ChannelRecyclerViewAdapter.ViewHolder> {

    private final ChannelsListFragment.ChannelListFragmentListener mListener;
    private OnChannelClicked onChannelClicked;

    public ChannelRecyclerViewAdapter(@Nullable OrderedRealmCollection<Channel> data, boolean autoUpdate,
                                      ChannelsListFragment.ChannelListFragmentListener mListener,
                                      OnChannelClicked onChannelClicked) {
        super(data, autoUpdate);
        setHasStableIds(true);
        this.mListener = mListener;
        this.onChannelClicked = onChannelClicked;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int currentposition = position;
        holder.channel = getItem(position);

        holder.channel_name.setText(holder.channel.getName());
        holder.channel_number.setText(String.valueOf(holder.channel.getNumber()));
        if (holder.channel.getPrice() > 0 && !holder.channel.isPurchased()) {
            Glide.with(holder.mView.getContext())
                    .load(Preferences.getServerUrl() + "/storage/hotel/images/money.png")
                    .into(holder.purchasable_icon);
        } else {
            Glide.with(holder.mView.getContext())
                    .load(R.color.FullTransparent)
                    .into(holder.purchasable_icon);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    if (holder.channel.getPrice() > 0 && !holder.channel.isPurchased()) {
                        mListener.onChannelPurchased(holder.channel);
                    } else {
                        // callback to update player in player activity
                        mListener.onChannelSelected(holder.channel, false);

                        // callback to update position in fragment
                        onChannelClicked.UpdateLastPosition(currentposition);
                    }
                }
            }
        });
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
        return getItem(index).getNumber();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView channel_name;
        TextView channel_number;
        ImageView purchasable_icon;
        Channel channel;

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
