package com.eliotohme.data;

import android.support.annotation.NonNull;

import com.eliotohme.data.network.ApiInterface;
import com.eliotohme.data.network.ApiService;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Movie extends RealmObject implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("poster")
    @Expose
    private String poster;
    @SerializedName("price")
    @Expose
    private int price;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("genres")
    @Expose
    private RealmList<Genre> genres = null;
    @SerializedName("stream")
    @Expose
    private Stream stream;

    private boolean purchased = false;

    public Movie() {
    }

    public Movie(Integer id, String title, String poster, String createdAt,
                 String updatedAt, RealmList<Genre> genres, Stream stream, int price) {
        super();
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.genres = genres;
        this.stream = stream;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(RealmList<Genre> genres) {
        this.genres = genres;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public void getlist_network (ApiInterface apiInterface) {
        Call<List<Movie>> movieCall = apiInterface.getMovies();
        movieCall.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(@NonNull Call<List<Movie>> call, @NonNull final Response<List<Movie>> response) {
                if (response.code() == 200) {
                    Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            if (realm.where(Movie.class).findAll().size() >0 ) {
                                realm.delete(Movie.class);
                                realm.delete(Genre.class);
                            }
                            realm.insertOrUpdate(response.body());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {

            }
        });
    }
}