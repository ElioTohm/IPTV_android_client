package xms.com.smarttv.Presenter;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import xms.com.smarttv.R;
import xms.com.smarttv.fragments.ClientAccountFragment;
import xms.com.smarttv.models.Card;

public class AccountActionsAdapter extends RecyclerView.Adapter<AccountActionsAdapter.ViewHolder> {
    private final List<Card> mValues;
    private ClientAccountFragment.ClientAccountFragmentListener listener;

    public AccountActionsAdapter(List<Card> items, ClientAccountFragment.ClientAccountFragmentListener listener) {
        mValues = items;
        this.listener = listener;
    }

    @Override
    public AccountActionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.account_action, parent, false);
        return new AccountActionsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AccountActionsAdapter.ViewHolder holder, int position) {
        holder.section = mValues.get(position);
        holder.actionname.setText(holder.section.getTitle());
        Glide.with(holder.mView.getContext())
                .load(holder.section.getImageUrl())
                .into(holder.actionicon);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.ServiceClicked(holder.section);
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
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView actionname;
        final ImageView actionicon;
        Card section;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            actionname = view.findViewById(R.id.action_name);
            actionicon = view.findViewById(R.id.action_icon);
        }
    }
}
