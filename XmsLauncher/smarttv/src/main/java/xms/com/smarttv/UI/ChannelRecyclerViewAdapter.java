package xms.com.smarttv.UI;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eliotohme.data.Channel;

import java.util.List;

import xms.com.smarttv.R;
import xms.com.smarttv.fragments.ChannelsListFragment.OnListFragmentInteractionListener;

public class ChannelRecyclerViewAdapter extends RecyclerView.Adapter<ChannelRecyclerViewAdapter.ViewHolder> {

    private final List<Channel> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final OnChannelClicked OnChannelClicked;

    public ChannelRecyclerViewAdapter(List<Channel> items, OnListFragmentInteractionListener listener, OnChannelClicked OnChannelClicked) {
        mValues = items;
        mListener = listener;
        this.OnChannelClicked = OnChannelClicked;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.channel = mValues.get(position);
        holder.channel_name.setText(holder.channel.getName());
        holder.channel_number.setText(String.valueOf(holder.channel.getNumber()));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // callback to update player in player activity
                    mListener.onListFragmentInteraction(holder.channel);

                    // callback to update position in fragment
                    OnChannelClicked.UpdateLastPosition(position);
                }
            }
        });
        holder.mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                v.setBackgroundColor(v.getContext().getResources().getColor(R.color.selected_row_item));
            } else {
                v.setBackgroundColor(0x00000000);
            }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView channel_name;
        final TextView channel_number;
        Channel channel;
        private ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            channel_name = view.findViewById(R.id.item_name);
            channel_number = view.findViewById(R.id.item_number);
            imageView = view.findViewById(R.id.item_icon);
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
