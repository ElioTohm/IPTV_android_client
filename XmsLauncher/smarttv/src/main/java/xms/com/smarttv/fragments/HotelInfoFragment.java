package xms.com.smarttv.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import xms.com.smarttv.R;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class HotelInfoFragment extends Fragment {
    public HotelInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_hotel_info, container, false);
        final ImageView imageView = view.findViewById(R.id.ImageViewSlider);

        final int []imageArray={R.drawable.food_01,R.drawable.food_02,R.drawable.food_03,R.drawable.food_04,R.drawable.food_05};
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int i = 0;
            int y = imageArray.length - 1;
            public void run() {
                Glide.with(view.getContext())
                    .load(imageArray[i])
                    .apply(new RequestOptions().placeholder(imageArray[y]))
                    .transition(withCrossFade(700))
                    .into(imageView);
                y = i;
                i++;
                if(i>imageArray.length-1)
                {
                    i=0;
                }
                handler.postDelayed(this, 5000);  //for interval...
            }
        };
        handler.post(runnable);

        return view;
    }

}
