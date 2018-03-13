package com.sconnecting.userapp.ui.taxi.order.map;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.data.entity.LocationObject;
import com.sconnecting.userapp.location.LocationHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Date;

import com.sconnecting.userapp.google.GoogleMapUtil;
import com.sconnecting.userapp.data.models.DriverStatus;

/**
 * Created by TrungDao on 8/2/16.
 */

public class MapVehicleMarker {


    public Marker marker;

    public String driverId;
    public DriverStatus driverStatus;

    Date lastUpdateTimel;
    LatLng lastUpdateLocation ;

    Boolean isShow = true;

    public OrderScreen parent;

    public MapVehicleMarker(OrderScreen parent, String driverId){

        this.parent = parent;
        this.driverId = driverId;
    }

    public void updateStatusFirstTime(DriverStatus status, Completion listener ){

        if(status.Driver.equals(driverId)  && status.Location != null ){

            this.driverStatus = status;
            isShow = true;
            this.invalidate(false,listener);

        }else{

            if(listener != null)
                listener.onCompleted();

        }

    }

    public void updateLocation(Double latitude, Double longitude ,Double degree, Completion listener){

        Boolean isAnimation = true;

        if(this.driverStatus == null){
            this.driverStatus = new DriverStatus();
            this.driverStatus.Driver = this.driverId;
            isAnimation = false;
        }

        if(this.driverStatus.Location == null){
            isAnimation = false;

        }else{
            Location source = LocationHelper.newLocation(latitude , longitude);
            Location destiny =  this.driverStatus.Location.getLocation();
            if( Math.abs(source.distanceTo(destiny)) > 50){
                isAnimation = false;
            }

        }

        this.driverStatus.Location = new LocationObject(latitude,longitude);

        this.driverStatus.Degree = degree;

        this.invalidate(isAnimation,listener);


    }

    public Double distanceFromUser(){
        if( SCONNECTING.locationHelper.getLocation() != null && SCONNECTING.userManager.CurrentUser != null){
            if(this.driverStatus != null && this.driverStatus.Location != null){
                return (double)SCONNECTING.locationHelper.getLocation().distanceTo(this.driverStatus.Location.getLocation());
            }
        }

        return -1.0;
    }


    public Double distanceFromLocation(Location location) {

        if(this.driverStatus != null && this.driverStatus.Location != null){
            return (double)location.distanceTo(this.driverStatus.Location.getLocation());

         }

        return -1.0;
    }



    public void invalidate(final Boolean isAnimation,final Completion listener){

        if(driverStatus == null){

            if(marker != null)
                marker.setVisible(false);

            if(listener != null)
                listener.onCompleted();

            return;
        }
        if(this.marker == null){


            Picasso.with(this.parent).load(R.drawable.car).resize(72, 72).centerCrop().into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    if(marker != null)
                        marker.remove();

                    marker = parent.mMapView.gmsMapView.addMarker(new MarkerOptions()
                            .position(driverStatus.Location.getLatLng())
                            .title(driverStatus.DriverName)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            .anchor((float) 0.5,(float) 0.5)
                    );

                    marker.setVisible(isShow);

                    if (isShow){
                        if (isAnimation) {
                            GoogleMapUtil.animateMarker(parent.mMapView.gmsMapView, marker, driverStatus.Location.getLatLng(), (long) 5000);
                        } else {
                            marker.setPosition(driverStatus.Location.getLatLng());
                        }
                    }


                    if(listener != null)
                        listener.onCompleted();


                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                    if(listener != null)
                        listener.onCompleted();

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            });


        }else{

            marker.setTitle(driverStatus.DriverName);
            marker.setVisible(isShow);

            if(isShow && isAnimation) {
                GoogleMapUtil.animateMarker(parent.mMapView.gmsMapView, marker, driverStatus.Location.getLatLng(),  (long) 5000);
            }else{
                marker.setPosition(driverStatus.Location.getLatLng());
            }
            if(listener != null)
                listener.onCompleted();


        }


         this.rotateVehicle((double)this.parent.mMapView.gmsMapView.getCameraPosition().bearing);

    }


    public void hideVehicle(){

        if( marker != null) {
            if (marker.isVisible())
                marker.setVisible(false);
            isShow = marker.isVisible();
        }
    }

    public void showVehicle(){

        if( marker != null) {
            if ( marker.isVisible() == false)
                marker.setVisible(true);
            isShow = marker.isVisible();
        }

    }

    public void moveToVehicleLocation(){

        if(marker != null) {
            LatLng coordinate = driverStatus.Location.getLatLng();

            this.parent.mMapView.moveToLocation(coordinate, (float)17);
        }
    }

    public void rotateVehicle(Double degree){

        if(this.marker != null && this.driverStatus != null && this.driverStatus.Degree != null)
             this.marker.setRotation((float) (this.driverStatus.Degree - degree));

    }

}
