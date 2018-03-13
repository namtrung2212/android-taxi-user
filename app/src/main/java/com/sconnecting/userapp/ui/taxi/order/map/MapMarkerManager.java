package com.sconnecting.userapp.ui.taxi.order.map;

import com.google.android.gms.maps.model.LatLng;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sconnecting.userapp.base.listener.GetItemsListener;
import com.sconnecting.userapp.data.controllers.DriverController;
import com.sconnecting.userapp.data.models.DriverStatus;

/**
 * Created by TrungDao on 8/2/16.
 */

public class MapMarkerManager {

    Map<String,MapVehicleMarker> carList = new HashMap<String,MapVehicleMarker>();

    public MapVehicleMarker currentVehicle() {

        if (SCONNECTING.orderManager.currentOrder.IsMateHost == 0 && (SCONNECTING.orderManager.currentOrder.MateHostOrder == null || SCONNECTING.orderManager.currentOrder.MateHostOrder.isEmpty())) {

            if (SCONNECTING.orderManager.currentOrder.Driver != null && SCONNECTING.orderManager.currentOrder.Driver.isEmpty() == false)
                return this.carList.get(SCONNECTING.orderManager.currentOrder.Driver);
            else
                return null;

        } else if (SCONNECTING.orderManager.currentOrder.isTripMateMember()) {

            if (SCONNECTING.orderManager.hostOrder != null && SCONNECTING.orderManager.hostOrder.Driver != null && SCONNECTING.orderManager.hostOrder.Driver.isEmpty() == false)
                return this.carList.get(SCONNECTING.orderManager.hostOrder.Driver);
            else
                return null;

        } else if (SCONNECTING.orderManager.currentOrder.IsMateHost == 1) {

            if (SCONNECTING.orderManager.currentOrder.Driver != null && SCONNECTING.orderManager.currentOrder.Driver.isEmpty() == false)
                return this.carList.get(SCONNECTING.orderManager.currentOrder.Driver);
            else
                return null;

        }
        return  null;
    }

    OrderScreen parent;

    public MapMarkerManager(OrderScreen parent){

        this.parent = parent;
    }


    public void loadNearestVehicles(LatLng coordinate){

        DriverController.GetNearestDrivers(coordinate,null,null,null,null, new GetItemsListener(){

            @Override
            public void onGetItems(Boolean success,List drivers) {
                if(success)
                    loadNearestVehicles( drivers);
            }
        });



    }

    public void loadNearestVehicles(List<DriverStatus> drivers ){

       // hideVehicles(null);
        for (DriverStatus driverStatus : drivers) {

            if(driverStatus.Driver != null  && driverStatus.Driver.isEmpty() == false && driverStatus.Location != null ){

                MapVehicleMarker marker =  carList.get(driverStatus.Driver);

                if(marker == null){

                    marker = new MapVehicleMarker(parent, driverStatus.Driver);
                    carList.put(driverStatus.Driver, marker);

                }

                marker.updateStatusFirstTime(driverStatus,null);

            }
        }


    }


    public void invalidateVehicle(String driverId , Double latitude , Double longitude, Double degree, Boolean hideOther, Completion listener){

        if(hideOther){
            this.hideVehicles(driverId);
        }

        MapVehicleMarker marker =  carList.get(driverId);

        if(marker == null){
            marker = new MapVehicleMarker(parent, driverId);
            carList.put(driverId, marker);
        }

        marker.showVehicle();
        marker.updateLocation(latitude, longitude, degree,listener);

    }

    public void hideVehicles(String exceptId){

        for (String id : carList.keySet()) {

            if(exceptId == null || exceptId.isEmpty() || exceptId.equals(id) == false){
                carList.get(id).hideVehicle();
            }
        }
    }

    public void showVehicles(){

        for (MapVehicleMarker marker : carList.values()) {
            marker.showVehicle();
        }
    }

    public void rotateVehicles(Double degree ){

        for (MapVehicleMarker marker : carList.values()) {
            marker.rotateVehicle(degree);
        }

    }

}
