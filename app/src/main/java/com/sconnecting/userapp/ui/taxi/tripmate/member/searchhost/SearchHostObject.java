package com.sconnecting.userapp.ui.taxi.tripmate.member.searchhost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Date;

/**
 * Created by TrungDao on 8/7/16.
 */
@Parcel(value = Parcel.Serialization.FIELD,
        analyze = { SearchHostObject.class })
public class SearchHostObject {


    @SerializedName("HostId")
    @Expose
    public String HostId;

    @SerializedName("OrderPickupPlace")
    @Expose
    public String OrderPickupPlace;

    @SerializedName("OrderDropPlace")
    @Expose
    public String OrderDropPlace;

    @SerializedName("OrderQuality")
    @Expose
    public String OrderQuality;

    @SerializedName("OrderVehicleType")
    @Expose
    public String OrderVehicleType;

    @SerializedName("OrderPickupTime")
    @Expose
    public Date OrderPickupTime;

    @SerializedName("MateOrderPrice")
    @Expose
    public Double MateOrderPrice;

    @SerializedName("MateBenifit")
    @Expose
    public Double MateBenifit;

    @SerializedName("MateLowestPrice")
    @Expose
    public Double MateLowestPrice;

    @SerializedName("MinRemainMemberQty")
    @Expose
    public Double MinRemainMemberQty;

    @SerializedName("MaxRemainMemberQty")
    @Expose
    public Double MaxRemainMemberQty;



}
