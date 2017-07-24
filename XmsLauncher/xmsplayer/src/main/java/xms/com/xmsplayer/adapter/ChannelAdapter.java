package xms.com.xmsplayer.adapter;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import xms.com.xmsplayer.objects.Channel;
import xms.com.xmsplayer.R;

public class ChannelAdapter extends InputTrackingRecyclerViewAdapter<ChannelAdapter.ViewHolder> {

    private List<Channel> channelArrayList;

    private final static int SELECTOR_COLOR = 0xFFBDBDBD;

    public ChannelAdapter(Context context, List<Channel> channelArrayList) {
        super(context);
        this.channelArrayList = channelArrayList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.channel_card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        boolean isSelected = position == getSelectedItem();

        ColorDrawable selectedDrawable = new ColorDrawable(isSelected ? SELECTOR_COLOR : 0X00000000);
        holder.itemView.setBackground(selectedDrawable);
        holder.itemView.setSelected(isSelected);

        holder.textView.setText(this.channelArrayList.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(getSelectedItem());
                setSelectedItem(getRecyclerView().getChildLayoutPosition(v));
                notifyItemChanged(getSelectedItem());
            }
        });
        holder.itemView.setFocusable(true);
    }

    @Override
    public int getItemCount() {
        return this.channelArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        LinearLayout innerLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.text_view);
            innerLayout = (LinearLayout)itemView.findViewById(R.id.inner_llayout);
        }
    }

    public Channel getChannel (int position) {
        return this.channelArrayList.get(position);
    }
}