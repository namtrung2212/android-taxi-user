package com.sconnecting.userapp.ui.taxi.order.map;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;

/**
 * Created by TrungDao on 8/2/16.
 */

public class MapLocation {


    OrderScreen parent;
    Marker currentLocationDot;

    public Boolean shouldToMoveToCurrentLocaton = false;

    public MapLocation(OrderScreen scr) {
        parent = scr;
        shouldToMoveToCurrentLocaton = true;

    }

    @SuppressWarnings("MissingPermission")
    public void onLocationAuthorized() {

        this.parent.mMapView.gmsMapView.setMyLocationEnabled(true);
    }

    public void changeLocation(Location location) {

        if(location == null)
             return;

        LatLng loc = new LatLng( location.getLatitude(), location.getLongitude());

        if( this.parent.mMapView != null && this.parent.mMapView.gmsMapView != null){

            if(this.parent.isMapReady && this.shouldToMoveToCurrentLocaton)
            {

                    LatLngBounds bounds = new LatLngBounds.Builder().include(loc).build();
                    int padding = 5;
                    this.parent.mMapView.gmsMapView.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

                    // searchResultController.autocompleteBounds = bounds //TRUNG NOTED

            }

            this.shouldToMoveToCurrentLocaton = false;
        }

       // updateDotLcation(location);
    }
    public void updateDotLcation(Location location) {

        LatLng loc = new LatLng( location.getLatitude(), location.getLongitude());

        if(currentLocationDot == null){

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(loc);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            currentLocationDot = this.parent.mMapView.gmsMapView.addMarker(markerOptions);
        }else {
            currentLocationDot.setPosition(loc);
        }
    }



}
