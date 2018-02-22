package xms.com.smarttv.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;

import xms.com.smarttv.R;

public class BackgroundImageFragment extends Fragment {
    private static final String TYPE = "type";


    private int type;
    public BackgroundImageFragment() {
        // Required empty public constructor
    }

    public static BackgroundImageFragment newInstance(int type) {
        BackgroundImageFragment fragment = new BackgroundImageFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt(TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_background_image, container, false);
        ViewFlipper viewflipper = view.findViewById(R.id.viewflipper);
        TextView welcomemessage = view.findViewById(R.id.welcome_message);

        String[] gallery = null;
        switch (type) {
            case SectionMenuFragment.HEADER_ID_RESTOANDBAR:
                gallery = new String[]{
                        "http://192.168.0.75/storage/hotel/images/rest1.png",
                        "http://192.168.0.75/storage/hotel/images/rest2.png",
                };
                break;
            case SectionMenuFragment.HEADER_ID_ACCOUNT:
            case SectionMenuFragment.HEADER_ID_HOTEL_INFO:
                gallery = new String[]{
                        "http://192.168.0.75/storage/hotel/images/hotel1.png",
                        "http://192.168.0.75/storage/hotel/images/hotel2.png",
                        "http://192.168.0.75/storage/hotel/images/hotel3.png"
                };
                break;
            case SectionMenuFragment.HEADER_ID_SPAANDFITNESS:
                gallery = new String[]{
                        "http://192.168.0.75/storage/hotel/images/spa1.png",
//                        "http://192.168.0.75/storage/hotel/images/spa2.png",
//                        "http://192.168.0.75/storage/hotel/images/gym1.png",
                        "http://192.168.0.75/storage/hotel/images/gym2.png"
                };
                break;
            case SectionMenuFragment.HEADER_ID_WEATHER:
                gallery = new String[]{
                        "http://192.168.0.75/storage/hotel/images/weather1.png",
                        "http://192.168.0.75/storage/hotel/images/weather2.png"
                };
                break;
            case SectionMenuFragment.HEADER_ID_CITYGUIDE:
                gallery = new String[]{
                        "http://192.168.0.75/storage/hotel/images/cityguide2.png",
                        "http://192.168.0.75/storage/hotel/images/cityguide1.png"
                };
                break;
            case SectionMenuFragment.HEADER_ID_VOD:
                gallery = new String[]{
                        "http://192.168.0.75/storage/hotel/images/vod1.png",
                        "http://192.168.0.75/storage/hotel/images/vod2.png"
                };
                break;
        }

        for (String imageurl : gallery) {
            ImageView imageView = new ImageView(view.getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewflipper.addView(imageView);
            Glide.with(view.getContext())
                    .load(imageurl)
                    .into(imageView);
        }

        return view;
    }

}
