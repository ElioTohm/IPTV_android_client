package xms.com.smarttv.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eliotohme.data.Client;

import io.realm.Realm;
import xms.com.smarttv.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HotelInfoFragment extends Fragment {

    TextView welcomemessage, personalmesage;

    public HotelInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hotel_info, container, false);
        welcomemessage = view.findViewById(R.id.welcome_message);
        personalmesage = view.findViewById(R.id.personal_message);
        String message = "";
        String clientName = "";

        Client client = Realm.getDefaultInstance().where(Client.class).findFirst();
        if (client != null) {
            message = client.getWelcomeMessage();
            clientName = client.getName();
        }
        welcomemessage.setText(String.format("%s %s", "Welcome", clientName));
        personalmesage.setText(message);
        return view;
    }

}
