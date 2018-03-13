package com.sconnecting.userapp.ui.taxi.order.map;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.InternetHelper;
import com.sconnecting.userapp.google.GoogleDirectionHelper;
import com.sconnecting.userapp.google.GooglePlaceSearcher;
import com.sconnecting.userapp.google.LocationInfo;
import com.sconnecting.userapp.location.LocationHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;

import com.sconnecting.userapp.data.models.TravelOrder;


import com.daimajia.androidanimations.library.*;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by TrungDao on 8/1/16.
 */

public class MapView extends Fragment{

    SupportMapFragment mapFragment;
    View view;


    OrderScreen parent;

    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }

    LatLng currentSourceLoc;
    LatLng currentDestLoc;

    List<Polyline> polylines = new ArrayList<Polyline>();

    public String currentEncodedPolyline;

    public GoogleMap gmsMapView;
    Marker mSourceMarker;
    Marker mDestinyMarker;


    public MapView(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        parent = (OrderScreen) context;
        parent.mMapView = this;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        try {
            view = inflater.inflate(R.layout.taxi_order_map, container, false);
        } catch (InflateException e) {
        }

        initControls();

        return view;
    }


    public void initControls() {


        if (! InternetHelper.checkConnectingToInternet()) {
            return;
        }


        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googlemap);
        mapFragment.getMapAsync(new OnMapReadyCallback(){

            @Override
            public void onMapReady(GoogleMap map) {

                gmsMapView = map;

                gmsMapView.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {

                        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},2212);

                            return;
                        }

                        initMap();
                    }
                });
            }

        });


        Button button = (Button) view.findViewById(R.id.btnMyLocation);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                YoYo.with(Techniques.ZoomIn).duration(300).playOn(v);

                moveToCurrentLocation(null,false);
            }
        });




    }

    @SuppressWarnings("MissingPermission")
    public void initMap(){


        gmsMapView.setMyLocationEnabled(true);
        gmsMapView.getUiSettings().setMyLocationButtonEnabled(false);
        gmsMapView.getUiSettings().setScrollGesturesEnabled(true);
        gmsMapView.getUiSettings().setZoomGesturesEnabled(true);
        gmsMapView.getUiSettings().setTiltGesturesEnabled(true);
        gmsMapView.getUiSettings().setRotateGesturesEnabled(true);
        gmsMapView.getUiSettings().setMapToolbarEnabled(false);
        gmsMapView.getUiSettings().setZoomControlsEnabled(false);


        gmsMapView.setMapType(1);
        gmsMapView.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                GooglePlaceSearcher searcher = new GooglePlaceSearcher();
                searcher.getAddress(gmsMapView.getCameraPosition().target, new GooglePlaceSearcher.GetPlaceListener(){

                    @Override
                    public void onCompleted(LocationInfo locAddress) {
                        if(locAddress != null && CurrentOrder().IsChoosingLocation()) {

                            int endIndex =   locAddress.Address.lastIndexOf(", ");
                            String strAddress = endIndex != -1 ? locAddress.Address.substring(0, endIndex) : locAddress.Address;
                            parent.mPlaceSearcher.mSearchView.setSearchText(strAddress);
                        }
                    }

                });
            }
        });

        Picasso.with(parent).load(R.drawable.sourcepin).resize(80, 80).centerCrop().into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if(gmsMapView != null){

                    mSourceMarker = gmsMapView.addMarker(new MarkerOptions()
                            .position(new LatLng(0,0))
                            .title("Điểm đi")
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            .anchor((float) 0.5,1)
                    );

                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });


        Picasso.with(parent).load(R.drawable.destinypin).resize(80, 80).centerCrop().into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if(gmsMapView != null){
                    mDestinyMarker = gmsMapView.addMarker(new MarkerOptions()
                            .position(new LatLng(0,0))
                            .title("Điểm đến")
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            .anchor((float) 0.5,1)
                    );
                    mDestinyMarker.setVisible(false);

                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });

        if(parent.mapReadyListener != null)
            parent.mapReadyListener.onReady();

    }
    @SuppressWarnings("MissingPermission")
    public void invalidate(Boolean isFirstTime,  final Completion listener){

        if(gmsMapView != null)
            gmsMapView.setMyLocationEnabled(!this.parent.isMonitoringPhase());


        if(this.CurrentOrder() == null || this.CurrentOrder().IsStopped()){
           this.parent.mapMarkerManager.hideVehicles(null);
        }


        if(this.CurrentOrder().OrderPickupLoc == null && this.CurrentOrder().OrderLoc == null){
            this.mSourceMarker = null ;
            this.mDestinyMarker = null;
            gmsMapView.clear();

            for(Polyline line : polylines)
                line.remove();
            polylines.clear();
        }

        invalidatePath(isFirstTime,new Completion() {
            @Override
            public void onCompleted() {

                invalidateRouteMarkers(listener);
            }
        });

    }


    public void invalidatePath(Boolean isFirstTime,final Completion listener) {

        LatLng sourceLoc = null;
        LatLng destinyLoc = null;

        if( this.CurrentOrder().OrderPickupLoc != null ){
            sourceLoc = this.CurrentOrder().OrderPickupLoc.getLatLng();
        }

        if( this.CurrentOrder().ActPickupLoc != null){
            sourceLoc = this.CurrentOrder().ActPickupLoc.getLatLng();
        }

        if( this.CurrentOrder().OrderDropLoc != null){
            destinyLoc = this.CurrentOrder().OrderDropLoc.getLatLng();
        }

        if( this.CurrentOrder().ActDropLoc != null ){
            destinyLoc = this.CurrentOrder().ActDropLoc.getLatLng();
        }


        if( sourceLoc == null  ||  destinyLoc == null ){

            for(Polyline line : polylines)
                line.remove();

            polylines.clear();

            this.currentSourceLoc = null;
            this.currentDestLoc = null;

            moveToCurrentLocation(null,false);

            if(listener != null)
                listener.onCompleted();

        }else{

            if(CurrentOrder().isNew())
                addNewOrderPathPolyLine(false,sourceLoc,destinyLoc,listener);
            else
                addCurrentOrderPolyLine(isFirstTime,listener);

        }

    }

    void invalidateRouteMarkers(final Completion listener) {

        LatLng sourceLoc = null;
        LatLng destinyLoc = null;

        if( this.CurrentOrder().OrderPickupLoc != null ){
            sourceLoc = this.CurrentOrder().OrderPickupLoc.getLatLng();
        }

        if( this.CurrentOrder().ActPickupLoc != null){
            sourceLoc = this.CurrentOrder().ActPickupLoc.getLatLng();
        }

        if( this.CurrentOrder().OrderDropLoc != null){
            destinyLoc = this.CurrentOrder().OrderDropLoc.getLatLng();
        }

        if( this.CurrentOrder().ActDropLoc != null ){
            destinyLoc = this.CurrentOrder().ActDropLoc.getLatLng();
        }

        if(this.mSourceMarker == null){

            final LatLng sourceLoc2 = sourceLoc;
            Picasso.with(parent).load(R.drawable.sourcepin).resize(80, 80).centerCrop().into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    if(gmsMapView != null){

                        if(mSourceMarker != null)
                            mSourceMarker.remove();

                        mSourceMarker = gmsMapView.addMarker(new MarkerOptions()
                                .position(new LatLng(0,0))
                                .title("Điểm đi")
                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .anchor((float) 0.5,1)
                        );

                        mSourceMarker.setVisible( sourceLoc2 != null );
                        if( mSourceMarker.isVisible()){

                            mSourceMarker.setPosition(sourceLoc2);

                        }

                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            });

        }
        if(this.mSourceMarker != null){
            this.mSourceMarker.setVisible( sourceLoc != null );
            if( this.mSourceMarker.isVisible()){

                this.mSourceMarker.setPosition(sourceLoc);

            }
        }

        if(this.mDestinyMarker == null) {

            final LatLng destinyLoc2 = destinyLoc;
            Picasso.with(parent).load(R.drawable.destinypin).resize(80, 80).centerCrop().into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    if(gmsMapView != null){

                        if(mDestinyMarker != null)
                            mDestinyMarker.remove();

                        mDestinyMarker = gmsMapView.addMarker(new MarkerOptions()
                                .position(new LatLng(0,0))
                                .title("Điểm đến")
                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .anchor((float) 0.5,1)
                        );
                        mDestinyMarker.setVisible( destinyLoc2 != null );
                        if( mDestinyMarker.isVisible()){

                            mDestinyMarker.setPosition(destinyLoc2);

                        }

                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            });

        }
        if(this.mDestinyMarker != null) {
            this.mDestinyMarker.setVisible(destinyLoc != null);
            if (this.mDestinyMarker.isVisible()) {

                this.mDestinyMarker.setPosition(destinyLoc);

            }
        }

        if(listener != null)
            listener.onCompleted();

    }


    public void addCurrentOrderPolyLine(final Boolean isFirstTime,final Completion listener) {

        if (CurrentOrder().IsMateHost == 0 && (CurrentOrder().MateHostOrder == null || CurrentOrder().MateHostOrder.isEmpty())) {

            addCurrentNormalOrderPolyLine(isFirstTime,listener);

        }else if(CurrentOrder().IsMateHost == 0 && CurrentOrder().MateHostOrder != null && CurrentOrder().MateHostOrder.isEmpty() == false) {

            addCurrentMateMemberOrderPolyLine(isFirstTime,listener);

        }else if(CurrentOrder().IsMateHost == 1){

            addCurrentMateHostOrderPolyLine(isFirstTime,listener);
        }
    }

    public void addCurrentNormalOrderPolyLine(final Boolean isFirstTime,final Completion listener) {


            String strEncodedPolyline = null;

            if (CurrentOrder().OrderPolyline != null)
                strEncodedPolyline = CurrentOrder().OrderPolyline;

            if (CurrentOrder().ActPolyline != null)
                strEncodedPolyline = CurrentOrder().ActPolyline;


            final String encoded = strEncodedPolyline;
            if (encoded != null && (currentEncodedPolyline == null || currentEncodedPolyline.equals(encoded) == false)) {

                for(Polyline line : polylines)
                    line.remove();
                polylines.clear();

                new GoogleDirectionHelper(this.getContext()).requestPolyline(gmsMapView, strEncodedPolyline, new PolylineOptions().width(3).color(Color.rgb(73, 139, 199)), new GoogleDirectionHelper.RequestPolylineResult() {
                    @Override
                    public void onCompleted(Polyline polyline) {

                        if (polyline != null) {
                            polylines.add(polyline);
                            currentEncodedPolyline = encoded;
                        }

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (LatLng pos : polyline.getPoints()) {
                            builder.include(pos);
                        }
                        LatLngBounds bounds = builder.build();

                        Location loc1 = LocationHelper.newLocation( gmsMapView.getCameraPosition().target);
                        Location loc2 = LocationHelper.newLocation( bounds.getCenter());
                        float dist = loc1.distanceTo(loc2);

                        gmsMapView.setPadding(100, 230, 100, 430);
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                        if (isFirstTime || dist > 10000000)
                            gmsMapView.moveCamera(cameraUpdate);
                        else
                            gmsMapView.animateCamera(cameraUpdate);

                        if (listener != null)
                            listener.onCompleted();
                    }
                });

            } else {

                if (listener != null)
                    listener.onCompleted();
            }

    }

    public void addCurrentMateMemberOrderPolyLine(final Boolean isFirstTime,final Completion listener){

        if (CurrentOrder().HostPolyline != null && CurrentOrder().HostPoints != null && (currentEncodedPolyline == null || currentEncodedPolyline.equals(CurrentOrder().HostPolyline) == false)) {

            for (Polyline line : polylines)
                line.remove();
            polylines.clear();

            int iStartLine = 0;
            int iEndLine = 0;
            int line = 0;

            for (int i = 0; i < CurrentOrder().HostPoints.getList().size() - 3; i += 2) {

                line++;

                Double sourceLat = CurrentOrder().HostPoints.getList().get(i).getDoubleValue();
                Double sourceLong = CurrentOrder().HostPoints.getList().get(i + 1).getDoubleValue();
                Double destLat = CurrentOrder().HostPoints.getList().get(i + 2).getDoubleValue();
                Double destLong = CurrentOrder().HostPoints.getList().get(i + 3).getDoubleValue();

                if (sourceLat == CurrentOrder().OrderPickupLoc.getLatLng().latitude && sourceLong == CurrentOrder().OrderPickupLoc.getLatLng().longitude) {
                    iStartLine = line;
                }

                if (destLat == CurrentOrder().OrderDropLoc.getLatLng().latitude && destLong == CurrentOrder().OrderDropLoc.getLatLng().longitude) {
                    iEndLine = line;
                }
            }


            String[] polylineList = CurrentOrder().HostPolyline.split("TRUNG");
            for (int i = 0; i < polylineList.length; i++) {

                String strPolyline = polylineList[i];
                new GoogleDirectionHelper(mapFragment.getContext()).requestPolyline(gmsMapView, strPolyline, new PolylineOptions().width((float) 4).color(Color.rgb(73, 139, 199)), new GoogleDirectionHelper.RequestPolylineResult() {
                    @Override
                    public void onCompleted(Polyline polyline) {

                        if (polyline != null) {
                            polylines.add(polyline);
                        }
                    }
                });

                if (i + 1 >= iStartLine && i + 1 <= iEndLine)
                    new GoogleDirectionHelper(mapFragment.getContext()).requestPolyline(gmsMapView, strPolyline, new PolylineOptions().width((float) 2.5).color(Color.rgb(178, 82, 108)), new GoogleDirectionHelper.RequestPolylineResult() {
                        @Override
                        public void onCompleted(Polyline polyline) {

                            if (polyline != null) {
                                polylines.add(polyline);
                            }
                        }
                    });


            }

            currentEncodedPolyline = CurrentOrder().HostPolyline;

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(CurrentOrder().OrderPickupLoc.getLatLng());
            builder.include(CurrentOrder().OrderDropLoc.getLatLng());

            LatLngBounds bounds = builder.build();

            Location loc1 = LocationHelper.newLocation( gmsMapView.getCameraPosition().target);
            Location loc2 = LocationHelper.newLocation( bounds.getCenter());
            float dist = loc1.distanceTo(loc2);

            gmsMapView.setPadding(100, 230, 100, 430);
            if (isFirstTime || dist > 10000000)
                gmsMapView.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
            else
                gmsMapView.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        }

        if(listener != null)
            listener.onCompleted();

    }

    public void addCurrentMateHostOrderPolyLine(final Boolean isFirstTime,final Completion listener){

        if (CurrentOrder().HostPolyline != null && (currentEncodedPolyline == null || currentEncodedPolyline.equals(CurrentOrder().HostPolyline) == false)) {

            for (Polyline line : polylines)
                line.remove();
            polylines.clear();


            String[] polylineList = CurrentOrder().HostPolyline.split("TRUNG");
            for (int i = 0; i < polylineList.length; i++) {

                String strPolyline = polylineList[i];
                new GoogleDirectionHelper(mapFragment.getContext()).requestPolyline(gmsMapView, strPolyline, new PolylineOptions().width((float) 4).color(Color.rgb(73, 139, 199)), new GoogleDirectionHelper.RequestPolylineResult() {
                    @Override
                    public void onCompleted(Polyline polyline) {

                        if (polyline != null) {
                            polylines.add(polyline);
                        }
                    }
                });

            }

            currentEncodedPolyline = CurrentOrder().HostPolyline;

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(CurrentOrder().OrderPickupLoc.getLatLng());
            builder.include(CurrentOrder().OrderDropLoc.getLatLng());

            LatLngBounds bounds = builder.build();

            Location loc1 = LocationHelper.newLocation( gmsMapView.getCameraPosition().target);
            Location loc2 = LocationHelper.newLocation( bounds.getCenter());
            float dist = loc1.distanceTo(loc2);

            gmsMapView.setPadding(100, 230, 100, 430);

            if (isFirstTime|| dist > 10000000)
                gmsMapView.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
            else
                gmsMapView.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        }

        if(listener != null)
            listener.onCompleted();

    }

    public void addNewOrderPathPolyLine(final Boolean isFirstTime,final LatLng srcLocation , final LatLng destLocation,final Completion listener){

        if(this.currentSourceLoc == null || this.currentDestLoc == null
                || this.currentSourceLoc.latitude != srcLocation.latitude || this.currentSourceLoc.longitude != srcLocation.longitude
                || this.currentDestLoc.latitude != destLocation.latitude || this.currentDestLoc.longitude != destLocation.longitude ){


            for (Polyline line : polylines)
                line.remove();
            polylines.clear();

            new GoogleDirectionHelper(this.getContext()).requestPolyline(gmsMapView, srcLocation, destLocation, new PolylineOptions().width(3).color(Color.rgb(73, 139, 199)), new GoogleDirectionHelper.RequestDirectionResult() {
                @Override
                public void onCompleted(Polyline polyline, double distance, double duration) {

                    if (polyline != null) {

                        polylines.add(polyline);

                        CurrentOrder().initRoadPath(distance, duration);

                        currentSourceLoc = srcLocation;
                        currentDestLoc = destLocation;


                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (LatLng pos : polyline.getPoints()) {
                            builder.include(pos);
                        }

                        LatLngBounds bounds = builder.build();
                        gmsMapView.setPadding(100, 270, 100, 400);
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0);
                        gmsMapView.animateCamera(cameraUpdate);

                        parent.mChooseLocationView.invalidateTravelPath(isFirstTime, null);

                    }
                    if(listener != null)
                        listener.onCompleted();
                }
            });


        }else{


            if(listener != null)
                listener.onCompleted();
        }

    }


    public void moveToLocation(LatLng loc, Float zoom) {

        if(loc == null)
            return;

        moveToLocation(loc,null,zoom);

    }

    public void moveToLocation(LatLng target,Boolean isAnimate,Float zoom ){

        moveToLocation(target,isAnimate,zoom,null);

    }


    public void moveToLocation(LatLng target,Boolean isAnimate,Float zoom,Double bearing ){

        CameraPosition.Builder builder =  new CameraPosition.Builder();
        builder = builder.target(target);

        if(bearing != null)
            builder = builder.bearing(bearing.floatValue());


        if( zoom == null && ( this.gmsMapView.getCameraPosition().zoom >14 || this.gmsMapView.getCameraPosition().zoom <=8) )
            zoom = (float) 14.0;

        if(zoom == null)
            zoom = this.gmsMapView.getCameraPosition().zoom;


        if(zoom != null)
            builder = builder.zoom(zoom);

        if(isAnimate != null && isAnimate == true){

            this.gmsMapView.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()), null);
            return;
        }

        Location source = LocationHelper.newLocation(target);
        Location destiny =  LocationHelper.newLocation(this.gmsMapView.getCameraPosition().target);
        Float distance = Math.abs(source.distanceTo(destiny));

        if(distance > 300 || (isAnimate != null && isAnimate == false)){

            this.gmsMapView.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));

        }else{

            this.gmsMapView.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()), null);

        }
    }


    public void moveToCurrentLocation(Float zoom,Boolean isAnimate) {

        if (gmsMapView == null)
            return;

        Location location = SCONNECTING.locationHelper.getLocation();

        if (location != null) {
            LatLng target = new LatLng(location.getLatitude(), location.getLongitude());

            moveToLocation(target, isAnimate, zoom);

            //noinspection MissingPermission
            gmsMapView.setMyLocationEnabled(true);

            //parent.mMapLocation.updateDotLcation(SCONNECTING.locationHelper.getLocation());
        }

    }


    public void moveToCurrentVehicleLocation(){

        if(parent.mapMarkerManager.currentVehicle() != null){
            parent.mapMarkerManager.currentVehicle().moveToVehicleLocation();
        }
    }



}
