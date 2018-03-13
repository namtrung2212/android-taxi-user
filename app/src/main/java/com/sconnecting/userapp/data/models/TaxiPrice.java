package com.sconnecting.userapp.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sconnecting.userapp.data.entity.BaseModel;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by TrungDao on 8/6/16.
 */



@RealmClass
public class TaxiPrice extends RealmObject implements BaseModel {


    public TaxiPrice(){

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

    @SerializedName("Company")
    @Expose
    public String Company;

    @SerializedName("Team")
    @Expose
    public String Team;

    @SerializedName("QualityService")
    @Expose
    public String QualityService;

    @SerializedName("Priority")
    @Expose
    public Integer Priority = 0;

    @SerializedName("IsActive")
    @Expose
    public Integer IsActive = 0;

    @SerializedName("FromDateTime")
    @Expose
    public Date FromDateTime ;

    @SerializedName("ToDateTime")
    @Expose
    public Date ToDateTime ;

    @SerializedName("FromKm")
    @Expose
    public Double FromKm = 0.0;

    @SerializedName("Currency")
    @Expose
    public String Currency;

    @SerializedName("PricePerKm")
    @Expose
    public Double PricePerKm = 0.0;

}


