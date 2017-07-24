package xms.com.xmsplayer.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import xms.com.xmsplayer.Channel;
import xms.com.xmsplayer.R;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.MyViewHolder>{

    private Context mContext;
    private List<Channel> channelsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
        }
    }

    public ChannelAdapter(Context mContext, List<Channel> channelsList) {
        this.mContext = mContext;
        this.channelsList = channelsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.channel_cardview, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Channel season = channelsList.get(position);
        holder.title.setText(season.getName());
    }

    @Override
    public int getItemCount() {
        return channelsList.size();
    }
}