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

import com.bumptech.glide.Glide;

import java.util.List;

import xms.com.smarttv.R;
import xms.com.smarttv.fragments.SectionMenuFragment;

public class SectionRecyclerViewAdapter extends RecyclerView.Adapter<SectionRecyclerViewAdapter.ViewHolder> {

    private final List<CustomHeaderItem> mValues;
    private final SectionMenuFragment.OnListFragmentInteractionListener mListener;

    public SectionRecyclerViewAdapter(List<CustomHeaderItem> items, SectionMenuFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public SectionRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new SectionRecyclerViewAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final SectionRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.section = mValues.get(position);
        holder.section_name.setText(holder.section.getName());
        Glide.with(holder.mView.getContext())
            .load(holder.mView.getContext().getResources().getDrawable(holder.section.getIconResId()))
            .into(holder.imageView);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.section);
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
        final TextView section_name;
        CustomHeaderItem section;
        private ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            section_name = view.findViewById(R.id.item_name);
            imageView = view.findViewById(R.id.item_icon);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + section_name.getText() + "'";
        }
    }
}
