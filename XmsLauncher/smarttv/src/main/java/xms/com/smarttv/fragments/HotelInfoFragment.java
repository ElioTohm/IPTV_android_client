package xms.com.smarttv.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;

import xms.com.smarttv.R;

public class HotelInfoFragment extends Fragment {
    ViewFlipper imageswitcher;
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
        imageswitcher = view.findViewById(R.id.imageswitcher);

        final String []gallery = {"http://192.168.0.75/storage/hotel/images/hotel1.jpg",
                "http://192.168.0.75/storage/hotel/images/hotel2.jpg","http://192.168.0.75/storage/hotel/images/hotel3.jpg"};

        for (int i = 0; i< gallery.length; i++) {
            ImageView imageView = new ImageView(view.getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageswitcher.addView(imageView);
            Glide.with(view.getContext())
                    .load(gallery[i])
                    .into(imageView);
        }

        return view;
    }

}
