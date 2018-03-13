package com.sconnecting.userapp.ui.taxi.tripmate.host.previewmember;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.widget.IconTextView;
import com.sconnecting.userapp.AppDelegate;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.GetOneListener;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.google.GoogleDirectionHelper;
import com.sconnecting.userapp.base.AnimationHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.base.DateTimeHelper;
import com.sconnecting.userapp.base.RegionalHelper;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.data.models.MateStatus;
import com.sconnecting.userapp.data.models.TravelOrder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by TrungDao on 8/17/16.
 */


public class PreviewMemberScreen extends AppCompatActivity {

    TravelOrder memberOrder;
    TravelOrder hostOrder;
    TravelOrder newHostOrder;

    public Toolbar mainToolbar;
    public IconTextView btnBack;
    public TextView mToolbarTitle;
    protected boolean useToolbar = true;
    public boolean isActive = false;

    public GoogleMap gmsMapView;
    SupportMapFragment mapFragment;
    Marker mHostSourceMarker;
    Marker mHostDestinyMarker;
    Marker mOrderSourceMarker;
    Marker mOrderDestinyMarker;


    public TextView lblPickupPlace;
    public TextView lblDropPlace;
    public TextView lblPickupTime;
    public TextView lblMemberQty;
    public TextView lblMateOrderPrice;
    public TextView lblMateBenifitRatio;
    public TextView lblMateDistanceIncreaseRatio;
    public Button btnAccept;
    public Button btnReject;

    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent myIntent = getIntent();
        memberOrder = Parcels.unwrap(myIntent.getParcelableExtra("Member"));
        hostOrder = Parcels.unwrap(myIntent.getParcelableExtra("Host"));

        setContentView(R.layout.taxi_order_tripmate_host_previewmember);

        initControls(new Completion() {
            @Override
            public void onCompleted() {

                invalidate();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppDelegate.CurrentActivity = this;

    }

    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;

    }

    @Override
    public void setContentView(int layoutResID)
    {
        super.setContentView(layoutResID);


        mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);

        if (useToolbar)
        {
            setSupportActionBar(mainToolbar);
            mainToolbar.setTitleTextColor(Color.WHITE);
            mainToolbar.setVisibility(View.VISIBLE);

            mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
            mToolbarTitle.setText(this.getTitle().toString().toUpperCase());

            btnBack = (IconTextView) findViewById(R.id.btnBack);
            btnBack.setTextColor(Color.WHITE);
            AnimationHelper.setOnClick(btnBack, new Completion() {
                @Override
                public void onCompleted() {

                    onBackPressed();
                }
            });

            Window window = getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(Color.rgb(89,145,196));
            }
        }
        else
        {
            mainToolbar.setVisibility(View.GONE);
        }

    }


    @Override

    public void onBackPressed() {

        // Write your code here

        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);


    }


    @SuppressWarnings("MissingPermission")
    public void initControls(final Completion listener) {

        lblPickupPlace = (TextView) findViewById(R.id.lblPickupPlace);
        lblDropPlace = (TextView) findViewById(R.id.lblDropPlace);
        lblPickupTime = (TextView) findViewById(R.id.lblPickupTime);
        lblMemberQty = (TextView) findViewById(R.id.lblMemberQty);
        lblMateOrderPrice = (TextView) findViewById(R.id.lblMateOrderPrice);
        lblMateBenifitRatio = (TextView) findViewById(R.id.lblMateBenifitRatio);
        lblMateDistanceIncreaseRatio = (TextView) findViewById(R.id.lblMateDistanceIncreaseRatio);

        btnAccept = (Button) findViewById(R.id.btnAccept);
        btnAccept.setText("THÊM THÀNH VIÊN");
        AnimationHelper.setOnClick(btnAccept, new Completion() {
            @Override
            public void onCompleted() {

                if(hostOrder.MateSubMembers + memberOrder.MembersQty > hostOrder.MaxSubMembers ){

                    new SweetAlertDialog(AppDelegate.CurrentActivity, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Không đủ chỗ cho thành viên mới !")
                            .setConfirmText("Đồng ý")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    onBackPressed();
                                }
                            })
                            .show();

                    return;
                }

                memberOrder.MateHostOrder = hostOrder.id;
                memberOrder.MateStatus = MateStatus.Accepted;

                 TravelOrderController.HostAcceptMateMember(memberOrder.id, new GetOneListener() {
                     @Override
                     public void onGetOne(Boolean success, BaseModel item) {

                         if (success && item != null)
                             onBackPressed();
                     }
                 });

            }
        });

        btnReject = (Button) findViewById(R.id.btnReject);
        btnReject.setText("TỪ CHỐI");
        AnimationHelper.setOnClick(btnReject, new Completion() {
            @Override
            public void onCompleted() {

                TravelOrderController.HostRejectMateMember(memberOrder.id, new GetOneListener() {
                    @Override
                    public void onGetOne(Boolean success, BaseModel item) {

                        if (success && item != null)
                            onBackPressed();
                    }
                });

            }
        });

        mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.googlemap);
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

                        if(listener != null)
                            listener.onCompleted();



                    }
                });
            }
        });
    }

    public void loadData(final Completion listener){

        if(memberOrder.MateStatus != null && memberOrder.MateStatus.equals(MateStatus.Requested)) {
            TravelOrderController.UserPreviewMateHostOrder(memberOrder.id, hostOrder.id, new GetOneListener() {
                @Override
                public void onGetOne(Boolean success, BaseModel item) {

                    if (success)
                        newHostOrder = (TravelOrder) item;

                    if (listener != null)
                        listener.onCompleted();

                }
            });

        }else{
            newHostOrder = hostOrder;
            if (listener != null)
                listener.onCompleted();
        }

    }

    public void loadPolyline(final Completion listener){

        String[] polylines = hostOrder.HostPolyline.split("TRUNG");
        for (int i = 0; i < polylines.length; i++) {

            String polyline = polylines[i];
            new GoogleDirectionHelper(mapFragment.getContext()).requestPolyline(gmsMapView, polyline, new PolylineOptions().width((float)4).color(Color.rgb(73, 139, 199)), null);

        }

        polylines = newHostOrder.HostPolyline.split("TRUNG");
        for (int i = 0; i < polylines.length; i++) {

            String polyline = polylines[i];
            new GoogleDirectionHelper(mapFragment.getContext()).requestPolyline(gmsMapView, polyline, new PolylineOptions().width((float)2.5).color(Color.rgb(178, 82, 108)), null);

        }


        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(memberOrder.OrderPickupLoc.getLatLng());
        builder.include(memberOrder.OrderDropLoc.getLatLng());
        builder.include(hostOrder.OrderPickupLoc.getLatLng());
        builder.include(hostOrder.OrderDropLoc.getLatLng());

        LatLngBounds bounds = builder.build();
        gmsMapView.setPadding(100, 30, 100, 430);
        gmsMapView.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        if(listener != null)
            listener.onCompleted();

    }

    private void loadMarkers() {

            Picasso.with(this).load(R.drawable.sourcepin).resize(50, 50).centerCrop().into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    if(gmsMapView != null){

                        mHostSourceMarker = gmsMapView.addMarker(new MarkerOptions()
                                .position(hostOrder.OrderPickupLoc.getLatLng())
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


            Picasso.with(this).load(R.drawable.destinypin).resize(50, 50).centerCrop().into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    if(gmsMapView != null){
                        mHostDestinyMarker = gmsMapView.addMarker(new MarkerOptions()
                                .position(hostOrder.OrderDropLoc.getLatLng())
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


            Picasso.with(this).load(R.drawable.sourcepin).resize(50, 50).centerCrop().into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    if(gmsMapView != null){

                        mOrderSourceMarker = gmsMapView.addMarker(new MarkerOptions()
                                .position(memberOrder.OrderPickupLoc.getLatLng())
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


            Picasso.with(this).load(R.drawable.destinypin).resize(50, 50).centerCrop().into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    if(gmsMapView != null){
                        mOrderDestinyMarker = gmsMapView.addMarker(new MarkerOptions()
                                .position(memberOrder.OrderDropLoc.getLatLng())
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

    public void invalidate(){

        loadData(new Completion() {
            @Override
            public void onCompleted() {


                loadPolyline(new Completion() {
                    @Override
                    public void onCompleted() {

                        loadMarkers();

                        lblPickupPlace.setText(memberOrder.OrderPickupPlace != null ?  memberOrder.OrderPickupPlace : "");
                        lblDropPlace.setText( memberOrder.OrderDropPlace != null ?  memberOrder.OrderDropPlace : "");

                        invalidatePickupTime();

                        lblMemberQty.setText(String.format("%d người", memberOrder.MembersQty));

                        invalidateEstPrice();

                        btnAccept.setVisibility(memberOrder.MateStatus.equals(MateStatus.Requested) ? View.VISIBLE : View.GONE);
                        btnReject.setVisibility(memberOrder.MateStatus.equals(MateStatus.Requested) ? View.VISIBLE : View.GONE);


                    }
                });

            }
        });


    }

    public void invalidatePickupTime(){

        Date date = null;

        if(memberOrder.OrderPickupTime != null)
            date = memberOrder.OrderPickupTime;



        if(date == null){
            lblPickupTime.setText( "");
            return;
        }

        String strPickupTime = "";
        if(DateTimeHelper.isNow(date,5)){

            strPickupTime = "ngay bây giờ.";

        }else{

            long seconds = (date.getTime() - new Date().getTime()) / 1000;
            long hours =  (seconds / 3600);
            long minutes =  (Math.round((seconds % 3600) / 60));

            if (DateTimeHelper.isToday(date) && hours <= 1 && ((seconds > 0) || ( seconds <= 0 && Math.abs(seconds)<=60))){

                if( hours >= 1){
                    strPickupTime =  String.format("sau %d giờ, %d phút", hours, minutes ) ;
                }else{
                    strPickupTime =  String.format("sau %d phút", minutes );
                }

            }else {


                String strDate =  new SimpleDateFormat("HH:mm").format(date);

                if(DateTimeHelper.isToday(date)){

                    strPickupTime = strDate + " hôm nay";

                }else if(DateTimeHelper.isYesterday(date)){

                    strPickupTime = strDate + " hôm qua";

                }else if(DateTimeHelper.isTomorrow(date)){

                    strPickupTime = strDate + " ngày mai";

                }else{

                    String strDate2 =  new SimpleDateFormat("dd/MM").format(date);
                    strPickupTime = strDate + " ngày " + strDate2;
                }

            }
        }

        lblPickupTime.setText(strPickupTime);
    }

    public void invalidateEstPrice(){

        TravelOrder hostOrder = SCONNECTING.orderManager.currentOrder;

        lblMateOrderPrice.setText(RegionalHelper.toCurrencyOfCountry(memberOrder.MateOrderPrice,memberOrder.OrderPickupCountry));


        double dBenifitRatio = 0.0;
        if(hostOrder.OrderPrice != null && hostOrder.OrderPrice > 0 &&
                memberOrder.MateOrderPrice != null && memberOrder.MateOrderPrice >= 0){
            dBenifitRatio = memberOrder.MateOrderPrice/hostOrder.OrderPrice;
        }

        dBenifitRatio = dBenifitRatio * 100;
        lblMateBenifitRatio.setText(String.format("%d",(long)dBenifitRatio) + " %");


        double dDistanceIncrease = 0.0;
        if(hostOrder.OrderDistance != null && newHostOrder.HostOrderDistance != null)
            dDistanceIncrease = (newHostOrder.HostOrderDistance - hostOrder.OrderDistance)/hostOrder.OrderDistance;

        dDistanceIncrease = dDistanceIncrease * 100;
        lblMateDistanceIncreaseRatio.setText(String.format("%d",(long)dDistanceIncrease)  + " %");


    }
}