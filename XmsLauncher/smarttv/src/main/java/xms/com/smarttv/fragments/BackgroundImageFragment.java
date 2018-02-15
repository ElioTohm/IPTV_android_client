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
import com.eliotohme.data.Client;

import io.realm.Realm;
import xms.com.smarttv.R;

public class BackgroundImageFragment extends Fragment {
    private static final String TYPE = "type";
    public static final int HOTEL = 1;
    public static final int SPA = 2;
    public static final int RESTAURANTNBAR = 3;
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
        final View view = inflater.inflate(R.layout.fragment_hotel_info, container, false);
        ViewFlipper viewflipper = view.findViewById(R.id.viewflipper);
        TextView welcomemessage = view.findViewById(R.id.welcome_message);

        String[] gallery = null;
        switch (type) {
            case RESTAURANTNBAR:
                gallery = new String[]{
                        "http://192.168.0.75/storage/hotel/images/rest1.jpg",
                        "http://192.168.0.75/storage/hotel/images/rest2.jpg",
                };
                break;
            case HOTEL:
                gallery = new String[]{
                        "http://192.168.0.75/storage/hotel/images/hotel1.jpg",
                        "http://192.168.0.75/storage/hotel/images/hotel2.jpg",
                        "http://192.168.0.75/storage/hotel/images/hotel3.jpg"
                };
                break;
            case SPA:
                gallery = new String[]{
                        "http://192.168.0.75/storage/hotel/images/spa1.jpg",
                        "http://192.168.0.75/storage/hotel/images/spa2.jpg",
                        "http://192.168.0.75/storage/hotel/images/gym1.jpg",
                        "http://192.168.0.75/storage/hotel/images/gym2.jpg"
                };
                break;
        }

        Client client = Realm.getDefaultInstance().where(Client.class).findFirst();

        String welcome = "";
        String clientName = "";
        if (client != null) {
            welcome = client.getWelcomeMessage();
            clientName = client.getName();
        }
        welcomemessage.setText(String.format("%s %s", welcome, clientName));

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
