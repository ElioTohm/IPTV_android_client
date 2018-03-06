package xms.com.smarttv.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;

import com.eliotohme.data.Channel;
import com.eliotohme.data.Client;
import com.eliotohme.data.Movie;
import com.eliotohme.data.Purchase;
import com.eliotohme.data.PurchaseForm;
import com.eliotohme.data.User;
import com.eliotohme.data.network.ApiInterface;
import com.eliotohme.data.network.ApiService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xms.com.smarttv.app.Preferences;

/**
 * Leanback Purchase Dialog
 */

public class PurchaseDialog extends GuidedStepFragment {
    private static final String ARG_ITEM = "item";
    private static final String ARG_TYPE = "item_type";
    private static Object Purchasableitem;
    private String type;
    private static final int ACTION_ID_PURCHASE = 1;
    private static final int ACTION_ID_CANCEL_PURCHASE = ACTION_ID_PURCHASE + 1;

    /**
     * Create Fragment while passing Item and Type
     */
    public static PurchaseDialog newInstance(Object item, String type) {
        PurchaseDialog fragment = new PurchaseDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ITEM, (Serializable) item);
        args.putSerializable(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.Purchasableitem= getArguments().getSerializable(ARG_ITEM);
            this.type = getArguments().getString(ARG_TYPE);
        }
    }

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        GuidanceStylist.Guidance guidance = null;
        if (Purchasableitem instanceof Channel){
            guidance = new GuidanceStylist.Guidance(((Channel)this.Purchasableitem).getName(),
                    "Price : " + ((Channel)this.Purchasableitem).getPrice(),
                    "Purchase " + this.type, null);
            return guidance;
        } else {
            guidance = new GuidanceStylist.Guidance(((Movie)this.Purchasableitem).getTitle(),
                    "Price : " + ((Movie)this.Purchasableitem).getPrice(),
                    "Purchase " + this.type, null);
        }
        return guidance;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        GuidedAction action = new GuidedAction.Builder()
                .id(ACTION_ID_PURCHASE)
                .title("Purchase").build();
        actions.add(action);
        action = new GuidedAction.Builder(getContext())
                .id(ACTION_ID_CANCEL_PURCHASE)
                .title("Cancel").build();
        actions.add(action);
    }

    /**
     * @param action
     * on purchase send request to purchase to server
     * update the client info
     * and set the purchased item price to 0 locally
     * to unlock usability
     */
    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        if (ACTION_ID_PURCHASE == action.getId()) {
            int item_id = 0;
            if (Purchasableitem instanceof Channel) {
                item_id = ((Channel) Purchasableitem).getId();
            } else {
                item_id = ((Movie) Purchasableitem).getId();
            }
            final int id = item_id;
            Realm realm = Realm.getDefaultInstance();
            User user = realm.where(User.class).findFirst();
            int clientID = realm.where(Client.class).findFirst().getId();

            ArrayList<Purchase> purchases = new ArrayList<>();
            purchases.add(new Purchase(this.type, id));

            ApiInterface apiInterface = ApiService.createService(ApiInterface.class,
                                            Preferences.getServerUrl(),
                                            user.getToken_type(),
                                            user.getAccess_token());

            Call<Client> userCall = apiInterface.purchaseItem(new PurchaseForm(clientID, purchases));
            userCall.enqueue(new Callback<Client>() {
                @Override
                public void onResponse(@NonNull Call<Client> call, @NonNull final Response<Client> response) {
                    if (response.code() == 200 && response.body() != null) {
                        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(@NonNull Realm realm) {
                                realm.insertOrUpdate(response.body());
                                realm.where(Channel.class).equalTo("id", id).findFirst().setPurchased(true);
                            }
                        });
                    }
                }
                @Override
                public void onFailure(Call<Client> call, Throwable t) {}
            });
        }
        getFragmentManager().beginTransaction().remove(this).commit();
    }
}
