package xms.com.smarttv.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eliotohme.data.Client;
import com.eliotohme.data.HotelService;
import com.eliotohme.data.Purchase;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import xms.com.smarttv.Presenter.AccountActionsAdapter;
import xms.com.smarttv.R;
import xms.com.smarttv.UI.PurchasesAdapter;
import xms.com.smarttv.app.Preferences;
import xms.com.smarttv.models.Card;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClientAccountFragmentListener} interface
 * to handle interaction events.
 * Use the {@link ClientAccountFragment} factory method to
 * create an instance of this fragment.
 */
public class ClientAccountFragment extends Fragment {
    private ClientAccountFragmentListener mListener;

    public ClientAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Client client = Realm.getDefaultInstance().where(Client.class).findFirst();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_client_account, container, false);
        TextView clientname = view.findViewById(R.id.client_name);
        TextView balance = view.findViewById(R.id.balance);

        RecyclerView actionsRecyclerView = view.findViewById(R.id.action_recycler_view);
        actionsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));

        RecyclerView purchases = view.findViewById(R.id.purchase_recyclerview);
        purchases.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        purchases.setAdapter(new PurchasesAdapter(Realm.getDefaultInstance().where(Purchase.class).findAll(), false));

        if (client != null) {
            clientname.setText(String.format("%s's Account",client.getName()));
            balance.setText(Integer.toString(client.getBalance()));

        } else {
            clientname.setText("No Checked in Client Yet");
            balance.setText("");
        }

        List<Card> mRowsAdapter  = new ArrayList<>() ;
        List<HotelService> hotelServices = Realm.getDefaultInstance().where(HotelService.class).findAll();
        for (HotelService hotelService : hotelServices) {
            Card roomservice = new Card();
            roomservice.setTitle(hotelService.getName());
            roomservice.setImageUrl(hotelService.getIcon());
            mRowsAdapter.add(roomservice);
        }
        actionsRecyclerView.setAdapter(new AccountActionsAdapter(mRowsAdapter, mListener));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ClientAccountFragmentListener) {
            mListener = (ClientAccountFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ClientAccountFragmentListener {
        void ServiceClicked(Card card);
    }
}
