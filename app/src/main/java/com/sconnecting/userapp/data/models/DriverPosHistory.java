
package com.sconnecting.userapp.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.data.entity.LocationObject;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by TrungDao on 8/6/16.
 */

@RealmClass
public class DriverPosHistory extends RealmObject implements BaseModel {

    public DriverPosHistory(){

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


    @SerializedName("Driver")
    @Expose
    public String Driver;

    @SerializedName("WorkingPlan")
    @Expose
    public String WorkingPlan;

    @SerializedName("Company")
    @Expose
    public String Company;

    @SerializedName("Team")
    @Expose
    public String Team;

    @SerializedName("CountryCode")
    @Expose
    public String Country;

    @SerializedName("Car")
    @Expose
    public String Car;

    @SerializedName("Location")
    @Expose
    public LocationObject Location;

    @SerializedName("Vehicle")
    @Expose
    public String Vehicle;

    @SerializedName("VehicleType")
    @Expose
    public String VehicleType;

    @SerializedName("Address")
    @Expose
    public String Address;

    @SerializedName("Degree")
    @Expose
    public Double Degree;

    @SerializedName("Speed")
    @Expose
    public Double Speed;

    @SerializedName("Device")
    @Expose
    public String Device;

    @SerializedName("DeviceID")
    @Expose
    public String DeviceID;


}
