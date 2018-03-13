package com.sconnecting.userapp.notification;

import java.util.Map;

/**
 * Created by TrungDao on 8/7/16.
 */

public interface TaxiSocketListener {


    void onTaxiSocketLogged(String socketId);

    void onDriverBidding(Map<String,Object>  data);

    void onDriverAccepted(Map<String,Object>  data);

    void onDriverRejected(Map<String,Object>  data);

    void onVehicleUpdateLocation(Map<String,Object>  data);

    void onDriverVoidedBfPickup(Map<String,Object>  data);

    void onDriverStartedTrip(Map<String,Object>  data);

    void onDriverVoidedAfPickup(Map<String,Object>  data);

    void onDriverFinished(Map<String,Object>  data);

    void onDriverReceivedCash(Map<String,Object>  data);

    void onUserShouldInvalidateOrder(Map<String,Object>  data);

    void onCheckAppInForeground(Map<String,Object>  data);

}



