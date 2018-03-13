package com.sconnecting.userapp.ui.taxi.tripmate.member.previewhost;

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
import com.sconnecting.userapp.data.controllers.QualityServiceTypeController;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.data.controllers.VehicleTypeController;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;
import com.sconnecting.userapp.ui.taxi.tripmate.member.searchhost.SearchHostObject;
import com.sconnecting.userapp.data.models.TravelOrder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by TrungDao on 8/17/16.
 */


public class PreviewHostScreen extends AppCompatActivity {

    TravelOrder hostOrder;
    SearchHostObject hostObject;

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
    public TextView lblRemainMemberQty;
    public TextView lblSeaterAndQuality;
    public TextView lblMateOrderPrice;
    public TextView lblMateBenifit;
    public TextView lblMateLowestPrice;
    public TextView lblDescMateLowestPrice;
    public Button btnJoin;

    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent myIntent = getIntent();
        hostObject = Parcels.unwrap(myIntent.getParcelableExtra("Host"));

        setContentView(R.layout.taxi_order_tripmate_member_previewhost);



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
        lblRemainMemberQty = (TextView) findViewById(R.id.lblRemainMemberQty);
        lblSeaterAndQuality = (TextView) findViewById(R.id.lblSeaterAndQuality);
        lblMateOrderPrice = (TextView) findViewById(R.id.lblMateOrderPrice);
        lblMateBenifit = (TextView) findViewById(R.id.lblMateBenifit);
        lblMateLowestPrice = (TextView) findViewById(R.id.lblMateLowestPrice);
        lblDescMateLowestPrice = (TextView) findViewById(R.id.lblDescMateLowestPrice);

        btnJoin = (Button) findViewById(R.id.btnJoin);
        btnJoin.setText("THAM GIA");
        AnimationHelper.setOnClick(btnJoin, new Completion() {
            @Override
            public void onCompleted() {

                TravelOrderController.MemberRequestToJoinTripMate(CurrentOrder().getId(), hostObject.HostId, new GetOneListener() {
                    @Override
                    public void onGetOne(Boolean success, BaseModel item) {

                        if (success && item != null) {

                            SCONNECTING.orderManager.currentOrder = (TravelOrder) item;

                            Intent intent = new Intent(AppDelegate.CurrentActivity, OrderScreen.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("CurrentOrder", Parcels.wrap(CurrentOrder()));
                            startActivity(intent);

                        }
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


                        if(mOrderSourceMarker == null && mOrderDestinyMarker == null) {
                            gmsMapView.clear();
                        }

                        loadData(new Completion() {
                            @Override
                            public void onCompleted() {

                                loadPolyline(new Completion() {
                                    @Override
                                    public void onCompleted() {

                                        loadMarkers();

                                        if(listener != null)
                                            listener.onCompleted();

                                    }
                                });

                            }
                        });


                    }
                });
            }
        });
    }
    public void loadData(final Completion listener){

        TravelOrderController.UserPreviewMateHostOrder(CurrentOrder().id, hostObject.HostId, new GetOneListener() {
            @Override
            public void onGetOne(Boolean success, BaseModel item) {

                if(success)
                    hostOrder = (TravelOrder) item;

                if(listener != null)
                    listener.onCompleted();

            }
        });

    }


    public void loadPolyline(final Completion listener){

        int iStartLine = 0;
        int iEndLine = 0;
        int line = 0;

        for (int i = 0; i < hostOrder.HostPoints.getList().size()-3; i+=2) {

            line++;

            Double sourceLat = hostOrder.HostPoints.getList().get(i).getDoubleValue();
            Double sourceLong = hostOrder.HostPoints.getList().get(i+1).getDoubleValue();
            Double destLat = hostOrder.HostPoints.getList().get(i+2).getDoubleValue();
            Double destLong = hostOrder.HostPoints.getList().get(i+3).getDoubleValue();

            if(sourceLat == CurrentOrder().OrderPickupLoc.getLatLng().latitude && sourceLong == CurrentOrder().OrderPickupLoc.getLatLng().longitude){
                iStartLine = line;
            }

            if(destLat == CurrentOrder().OrderDropLoc.getLatLng().latitude && destLong == CurrentOrder().OrderDropLoc.getLatLng().longitude){
                iEndLine = line;
            }
        }


        String[] polylines = hostOrder.HostPolyline.split("TRUNG");
        for (int i = 0; i < polylines.length; i++) {

            String polyline = polylines[i];
            new GoogleDirectionHelper(mapFragment.getContext()).requestPolyline(gmsMapView, polyline, new PolylineOptions().width((float)4).color(Color.rgb(73, 139, 199)), null);

            if(i+1>=iStartLine && i+1<=iEndLine)
                new GoogleDirectionHelper(mapFragment.getContext()).requestPolyline(gmsMapView, polyline, new PolylineOptions().width((float)2.5).color(Color.rgb(178, 82, 108)), null);


        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(CurrentOrder().OrderPickupLoc.getLatLng());
        builder.include(CurrentOrder().OrderDropLoc.getLatLng());
       // builder.include(hostOrder.OrderPickupLoc.getLatLng());
       // builder.include(hostOrder.OrderDropLoc.getLatLng());

        LatLngBounds bounds = builder.build();
        gmsMapView.setPadding(100, 30, 100, 430);
        gmsMapView.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        if(listener != null)
            listener.onCompleted();

    }


    private void loadMarkers() {
/*
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
*/

            Picasso.with(this).load(R.drawable.sourcepin).resize(50, 50).centerCrop().into(new Target() {
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


            Picasso.with(this).load(R.drawable.destinypin).resize(50, 50).centerCrop().into(new Target() {
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


    public void invalidate(){

        lblPickupPlace.setText(hostObject.OrderPickupPlace != null ?  hostObject.OrderPickupPlace : "");
        lblDropPlace.setText( hostObject.OrderDropPlace != null ?  hostObject.OrderDropPlace : "");
        invalidatePickupTime();
        invalidateRemainMemberQuality();
        invalidateSeaterAndQuality();
        invalidateEstPrice();


    }

    private void invalidateSeaterAndQuality(){

        String vehicleType = "";
        String qualityType = "";

        if(hostObject != null && hostObject.OrderQuality != null)
            qualityType = QualityServiceTypeController.GetLocaleNameFromType(hostObject.OrderQuality);

        if(hostObject != null && hostObject.OrderVehicleType != null)
            vehicleType = VehicleTypeController.GetLocaleNameFromType(hostObject.OrderVehicleType);

        if(vehicleType.isEmpty() == false && qualityType.isEmpty() == true)
            lblSeaterAndQuality.setText(vehicleType );

        else if(vehicleType.isEmpty() == true && qualityType.isEmpty() == false)
            lblSeaterAndQuality.setText(qualityType);

        else if(vehicleType.isEmpty() == false && qualityType.isEmpty() == false)
            lblSeaterAndQuality.setText(vehicleType + " - " + qualityType);
        else
            lblSeaterAndQuality.setText("Chưa rõ");


    }


    public void invalidatePickupTime(){

        Date date = null;

        if(hostObject.OrderPickupTime != null)
            date = hostObject.OrderPickupTime;



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

    private void invalidateRemainMemberQuality(){

        int iRemainMin = hostObject.MinRemainMemberQty.intValue();
        if(iRemainMin < 0 )
            iRemainMin = 0;

        int iRemainMax = hostObject.MaxRemainMemberQty.intValue();
        if(iRemainMax < 0 )
            iRemainMax = 0;

        if(iRemainMin > 0 && iRemainMax > 0)
            if(iRemainMin != iRemainMax)
                lblRemainMemberQty.setText(String.format("Còn %d đến %d chỗ", iRemainMin,iRemainMax));
            else
                lblRemainMemberQty.setText(String.format("Còn %d chỗ", iRemainMax));

        else if(iRemainMin == 0 && iRemainMax > 0)
            lblRemainMemberQty.setText(String.format("Còn %d chỗ", iRemainMax));
        else
            lblRemainMemberQty.setText("Hết chỗ");

    }


    public void invalidateEstPrice(){

        TravelOrder order = SCONNECTING.orderManager.currentOrder;

        lblMateOrderPrice.setText(RegionalHelper.toCurrencyOfCountry(hostObject.MateOrderPrice,order.OrderPickupCountry));
        lblMateBenifit.setText("-" + RegionalHelper.toCurrencyOfCountry(hostObject.MateBenifit,order.OrderPickupCountry));


        if(hostObject.MateLowestPrice != null && hostObject.MateLowestPrice > 0)
            lblMateLowestPrice.setText(RegionalHelper.toCurrencyOfCountry(hostObject.MateLowestPrice,order.OrderPickupCountry));

        lblMateLowestPrice.setVisibility(hostObject.MateLowestPrice != null && hostObject.MateLowestPrice > 0 && hostObject.MateLowestPrice < hostObject.MateOrderPrice ? View.VISIBLE : View.GONE);
        lblDescMateLowestPrice.setVisibility(lblMateLowestPrice.getVisibility());
    }
}