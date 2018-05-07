package com.eliotohme.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.eliotohme.data.network.ApiInterface;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Section extends RealmObject {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("section_item")
    @Expose
    private RealmList<SectionItem> sectionItem = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public Section() {
    }

    /**
     *
     * @param updatedAt
     * @param id
     * @param icon
     * @param createdAt
     * @param name
     * @param sectionItem
     */
    public Section(Integer id, String name, String icon, String createdAt, String updatedAt, RealmList<SectionItem> sectionItem) {
        super();
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.sectionItem = sectionItem;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Object getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Object getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public RealmList<SectionItem> getSectionItem() {
        return sectionItem;
    }

    public void setSectionItem(RealmList<SectionItem> sectionItem) {
        this.sectionItem = sectionItem;
    }


    public void getlist_network (ApiInterface apiInterface) {
        Call<List<Section>> channelCall = apiInterface.getSections();
        channelCall.enqueue(new Callback<List<Section>>() {
            @Override
            public void onResponse(@NonNull Call<List<Section>> call, @NonNull final Response<List<Section>> response) {
                Realm backgroundRealm = Realm.getDefaultInstance();
                backgroundRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (response.code() == 200) {
                            if (realm.where(Section.class).findAll().size() > 0 ) {
                                realm.delete(Section.class);
                            }
                            if (realm.where(SectionItem.class).findAll().size() > 0 ) {
                                realm.delete(SectionItem.class);
                            }
                            realm.insertOrUpdate(response.body());
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<List<Section>> call, @NonNull Throwable t) {}
        });
    }

}
