package com.sconnecting.userapp.ui.taxi.tripmate.host.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.google.GoogleDirectionHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.ui.taxi.tripmate.host.HostScreen;
import com.sconnecting.userapp.data.models.TravelOrder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


/**
 * Created by TrungDao on 8/1/16.
 */

public class MateMapView extends Fragment{

    View view;

    HostScreen parent;

    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }

    SupportMapFragment mapFragment;
    public GoogleMap gmsMapView;
    Marker mHostSourceMarker;
    Marker mHostDestinyMarker;
    Marker mOrderSourceMarker;
    Marker mOrderDestinyMarker;


    public MateMapView(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        parent = (HostScreen) context;
        parent.mMateMapView = this;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        try {
            view = inflater.inflate(R.layout.taxi_order_tripmate_host_map, container, false);
        } catch (InflateException e) {
        }

        initControls(null);

        return view;
    }


    @SuppressWarnings("MissingPermission")
    public void initControls( final Completion listener) {

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googlemap);
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap map) {

                gmsMapView = map;

                gmsMapView.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {

                        gmsMapView.setMyLocationEnabled(true);
                        gmsMapView.getUiSettings().setMyLocationButtonEnabled(false);
                        gmsMapView.getUiSettings().setScrollGesturesEnabled(true);
                        gmsMapView.getUiSettings().setZoomGesturesEnabled(true);
                        gmsMapView.getUiSettings().setTiltGesturesEnabled(true);
                        gmsMapView.getUiSettings().setRotateGesturesEnabled(true);
                        gmsMapView.getUiSettings().setMapToolbarEnabled(false);
                        gmsMapView.getUiSettings().setZoomControlsEnabled(false);


                        gmsMapView.setMapType(1);

                        invalidate(listener);

                    }
                });
            }
        });


    }

    @SuppressWarnings("MissingPermission")
    public void invalidate(final Completion listener){

        if(gmsMapView != null)
            gmsMapView.setMyLocationEnabled(true);

        loadMarkers();

        loadPolyline(new Completion() {
            @Override
            public void onCompleted() {

                if(listener != null)
                    listener.onCompleted();

            }
        });

    }


    public void loadPolyline(final Completion listener){

        if(gmsMapView == null) {

            if (listener != null)
                listener.onCompleted();
            return;
        }

        if(parent.hostOrder.getId().equals(CurrentOrder().getId()) && CurrentOrder().IsMateHost == 1){
            String[] polylines = parent.hostOrder.HostPolyline.split("TRUNG");
            for (int i = 0; i < polylines.length; i++) {

                String polyline = polylines[i];
                new GoogleDirectionHelper(mapFragment.getContext()).requestPolyline(gmsMapView, polyline, new PolylineOptions().width((float)4).color(Color.rgb(73, 139, 199)), null);


            }
        }else if(CurrentOrder().IsMateHost == 0 && CurrentOrder().HostPolyline != null){
            String[] polylines = CurrentOrder().HostPolyline.split("TRUNG");
            for (int i = 0; i < polylines.length; i++) {

                String polyline = polylines[i];
                new GoogleDirectionHelper(mapFragment.getContext()).requestPolyline(gmsMapView, polyline, new PolylineOptions().width((float)4).color(Color.rgb(73, 139, 199)), null);


            }
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(parent.hostOrder.OrderPickupLoc.getLatLng());
        builder.include(parent.hostOrder.OrderDropLoc.getLatLng());

        LatLngBounds bounds = builder.build();
        gmsMapView.setPadding(200, 200, 200, 200);
        gmsMapView.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        if(listener != null)
            listener.onCompleted();

    }


    private void loadMarkers() {

            Picasso.with(parent).load(R.drawable.sourcepin).resize(50, 50).centerCrop().into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    if(gmsMapView != null){

                        mHostSourceMarker = gmsMapView.addMarker(new MarkerOptions()
                                .position(parent.hostOrder.OrderPickupLoc.getLatLng())
                                .title("Điểm đi")
                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .anchor((float) 0.5,1)
                        );
                        mHostSourceMarker.setVisible(true);
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            });


            Picasso.with(parent).load(R.drawable.destinypin).resize(50, 50).centerCrop().into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    if(gmsMapView != null){
                        mHostDestinyMarker = gmsMapView.addMarker(new MarkerOptions()
                                .position(parent.hostOrder.OrderDropLoc.getLatLng())
                                .title("Điểm đến")
                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .anchor((float) 0.5,1)
                        );
                        mHostDestinyMarker.setVisible(true);

                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            });


        Picasso.with(parent).load(R.drawable.sourcepin).resize(50, 50).centerCrop().into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if(gmsMapView != null){

                    mOrderSourceMarker = gmsMapView.addMarker(new MarkerOptions()
                            .position(CurrentOrder().OrderPickupLoc.getLatLng())
                            .title("Điểm đi")
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            .anchor((float) 0.5,1)
                    );
                    mOrderSourceMarker.setVisible(true);
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });


        Picasso.with(parent).load(R.drawable.destinypin).resize(50, 50).centerCrop().into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if(gmsMapView != null){
                    mOrderDestinyMarker = gmsMapView.addMarker(new MarkerOptions()
                            .position(CurrentOrder().OrderDropLoc.getLatLng())
                            .title("Điểm đến")
                            .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            .anchor((float) 0.5,1)
                    );
                    mOrderDestinyMarker.setVisible(true);

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



}
