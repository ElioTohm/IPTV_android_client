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
        holder.section_name.setText(mValues.get(position).getName());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.section);
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
        public final TextView section_name;
        public CustomHeaderItem section;
        private ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            section_name = (TextView) view.findViewById(R.id.channel);
            imageView = (ImageView) view.findViewById(R.id.channel_icon);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + section_name.getText() + "'";
        }
    }
}
