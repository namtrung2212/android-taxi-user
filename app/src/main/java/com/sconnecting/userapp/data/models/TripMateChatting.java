package com.sconnecting.userapp.data.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.data.entity.BaseStringList;
import com.sconnecting.userapp.data.entity.LocationObject;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.TravelOrderChattingRealmProxy;
import io.realm.annotations.PrimaryKey;

@Parcel(value = Parcel.Serialization.FIELD)
public class TripMateChatting implements BaseModel, Serializable {

    public TripMateChatting(){

    }

    @PrimaryKey
    @SerializedName("_id")
    @Expose
    public String id;

    @SerializedName("updatedAt")
    @Expose
    public Date updatedAt;

    @SerializedName("createdAt")
    @Expose
    public Date createdAt;

    public Date retrieveAt;
    public Date useAt;

    public String getId() {
        return id;
    }
    public Date getRetrieveAt() {
        return retrieveAt;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public Date getUpdatedAt() {
        return updatedAt;
    }
    public Date getUsedAt() {
        return useAt;
    }
    public void setRetrieveAt(Date value){ this.retrieveAt = value;}
    public void setUsedAt(Date value){ this.useAt = value;}

    public boolean isNew(){
        return id == null || id.trim().isEmpty();

    }

    @SerializedName("HostOrder")
    @Expose
    public String HostOrder;

    @SerializedName("UserOrder")
    @Expose
    public String UserOrder;

    @SerializedName("User")
    @Expose
    public String User;

    @SerializedName("UserName")
    @Expose
    public String UserName;

    @SerializedName("IsViewedList")
    @Expose
    public BaseStringList IsViewedList = null;



    @SerializedName("Content")
    @Expose
    public String Content;

    @SerializedName("ImageIDs")
    @Expose
    public String ImageIDs;



    @SerializedName("Location")
    @Expose
    public LocationObject Location;




}