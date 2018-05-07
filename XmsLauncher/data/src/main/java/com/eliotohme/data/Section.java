package com.eliotohme.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

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

}
