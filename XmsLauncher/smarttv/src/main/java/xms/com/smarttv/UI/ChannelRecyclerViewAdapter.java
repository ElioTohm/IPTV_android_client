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
import xms.com.smarttv.UI.ChannelsListFragment.OnListFragmentInteractionListener;

public class ChannelRecyclerViewAdapter extends RecyclerView.Adapter<ChannelRecyclerViewAdapter.ViewHolder> {

    private final List<Channel> mValues;
    private final OnListFragmentInteractionListener mListener;

    public ChannelRecyclerViewAdapter(List<Channel> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_channel, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.channel = mValues.get(position);
        holder.channel_name.setText( mValues.get(position).getNumber()+ " " + mValues.get(position).getName());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.channel);
                }
            }
        });
        holder.mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundColor(v.getContext().getColor(R.color.Blue));
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
        public final View mView;
        public final TextView channel_name;
        public Channel channel;
        private ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            channel_name = (TextView) view.findViewById(R.id.channel);
            imageView = (ImageView) view.findViewById(R.id.channel_icon);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + channel_name.getText() + "'";
        }
    }
}
