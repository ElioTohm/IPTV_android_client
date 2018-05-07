package com.eliotohme.data;

import android.support.annotation.NonNull;

import com.eliotohme.data.network.ApiInterface;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HotelService extends RealmObject {
    private String name;
    private String action;
    private String tag;


    public HotelService() {
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void getlist_network (ApiInterface apiInterface) {
        Call<List<HotelService >> call = apiInterface.getService();
        call.enqueue(new Callback<List<HotelService >>() {
            @Override
            public void onResponse(@NonNull Call<List<HotelService >> call, @NonNull final Response<List<HotelService >> response) {
                if (response.code() == 200) {
                    Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(@NonNull Realm realm) {
                            Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(@NonNull Realm realm) {
                                    if (realm.where(HotelService .class).findAll().size() >0 ) {
                                        realm.delete(HotelService .class);
                                    }
                                    realm.insertOrUpdate(response.body());
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<HotelService >> call, Throwable t) {

            }
        });
    }

}
