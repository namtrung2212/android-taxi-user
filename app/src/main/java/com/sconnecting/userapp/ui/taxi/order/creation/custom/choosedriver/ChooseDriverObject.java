package com.sconnecting.userapp.ui.taxi.order.creation.custom.choosedriver;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by TrungDao on 8/7/16.
 */

@Parcel(value = Parcel.Serialization.FIELD,
        analyze = { com.sconnecting.userapp.ui.taxi.order.creation.custom.choosedriver.ChooseDriverObject.class })
public class ChooseDriverObject {


    @SerializedName("DriverId")
    @Expose
    public String DriverId;

    @SerializedName("OrderId")
    @Expose
    public String OrderId;

    @SerializedName("DriverName")
    @Expose
    public String DriverName;

    @SerializedName("CompanyName")
    @Expose
    public String CompanyName;

    @SerializedName("QualityService")
    @Expose
    public String QualityService;

    @SerializedName("VehicleType")
    @Expose
    public String VehicleType;

    @SerializedName("Rating")
    @Expose
    public Integer Rating;

    @SerializedName("RateCount")
    @Expose
    public Integer RateCount;

    @SerializedName("ComPrice")
    @Expose
    public Double ComPrice;

    @SerializedName("ComAdjustPrice")
    @Expose
    public Double ComAdjustPrice;

    @SerializedName("ComPromPrice")
    @Expose
    public Double ComPromPrice;

    @SerializedName("SysPromPrice")
    @Expose
    public Double SysPromPrice;

    @SerializedName("FinalPrice")
    @Expose
    public Double FinalPrice;

    @SerializedName("Distance")
    @Expose
    public Double Distance;



}
