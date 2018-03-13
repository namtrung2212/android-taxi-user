package com.sconnecting.userapp.ui.taxi.share.DriverProfile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.sconnecting.userapp.AppDelegate;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.GetDoubleValueListener;
import com.sconnecting.userapp.base.listener.GetOneListener;
import com.sconnecting.userapp.data.entity.BaseController;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.data.storages.server.ServerStorage;
import com.sconnecting.userapp.base.AnimationHelper;
import com.sconnecting.userapp.base.ImageHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.base.RegionalHelper;
import com.sconnecting.userapp.data.controllers.QualityServiceTypeController;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.data.controllers.VehicleTypeController;
import com.sconnecting.userapp.data.models.*;
import com.squareup.picasso.Callback;

import org.parceler.Parcels;

import static android.view.Gravity.CENTER;
import static android.view.View.TEXT_ALIGNMENT_CENTER;

/**
 * Created by TrungDao on 8/7/16.
 */

public class DriverProfileScreen extends AppCompatActivity {

    public Toolbar mainToolbar;
    public IconTextView btnBack;
    public TextView mToolbarTitle;


    protected boolean useToolbar = true;
    public boolean isActive = false;


    public static DriverProfileScreen instance;

    public String DriverID;

    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;
    }

    public DriverBidding driverBidding;
    public Driver driver;
    public DriverStatus driverStatus;


    public CircularImageView imgAvatar;

    public TextView lblRateCountValue;
    public ImageView imgStar1;
    public ImageView imgStar2;
    public ImageView imgStar3;
    public ImageView imgStar4;
    public ImageView imgStar5;

    public TextView lblCitizenIDValue;
    public TextView lblCompanyValue;
    public TextView lblVehicleNoValue;
    public TextView lblVehicleTypeValue;
    public TextView lblVehicleQualityValue;
    public TextView lblServedQtyValue;
    public TextView lblVoidedBfPickupByDriverValue;

    public TextView  lblEstPrice;
    public TextView  lblStatus;

    public Button btnSendRequest;
    public Button btnCancelRequest;
    public Button btnAcceptBidding;
    public Button btnCancelBidding;

    public CommentTable commentView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        Intent myIntent = getIntent();
        this.DriverID = myIntent.getStringExtra("DriverID");
        this.driverBidding = Parcels.unwrap(myIntent.getParcelableExtra("DriverBidding"));

        setContentView(R.layout.taxi_share_driverprofile);

        initControls();

    }

    @Override
    protected void onResume() {
        super.onResume();

        AppDelegate.CurrentActivity = this;
        invalidate(null);

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
        instance = null;

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

        if(SCONNECTING.orderManager.currentOrder != null){
            SCONNECTING.orderManager.actionHandler.SetDriverRequestingOrderToOpen(SCONNECTING.orderManager.currentOrder, new GetOneListener() {
                @Override
                public void onGetOne(Boolean success,BaseModel item) {

                }
            });
        }

        instance = null;
    }


    public void initControls(){

        imgAvatar = (CircularImageView) findViewById(R.id.imgAvatar);
        imgAvatar.setVisibility(View.INVISIBLE);

        imgStar1 = (ImageView) findViewById(R.id.imgStar1);
        imgStar2 = (ImageView) findViewById(R.id.imgStar2);
        imgStar3 = (ImageView) findViewById(R.id.imgStar3);
        imgStar4 = (ImageView) findViewById(R.id.imgStar4);
        imgStar5 = (ImageView) findViewById(R.id.imgStar5);
        lblRateCountValue = (TextView) findViewById(R.id.lblRateCountValue);

        lblCitizenIDValue = (TextView) findViewById(R.id.lblCitizenIDValue);
        lblCompanyValue = (TextView) findViewById(R.id.lblCompanyValue);
        lblVehicleNoValue = (TextView) findViewById(R.id.lblVehicleNoValue);
        lblVehicleTypeValue = (TextView) findViewById(R.id.lblVehicleSeaterValue);
        lblVehicleQualityValue = (TextView) findViewById(R.id.lblVehicleQualityValue);
        lblServedQtyValue = (TextView) findViewById(R.id.lblServedQtyValue);
        lblVoidedBfPickupByDriverValue = (TextView) findViewById(R.id.lblVoidedBfPickupByDriverValue);

        lblEstPrice = (TextView) findViewById(R.id.lblEstPrice);
        lblStatus = (TextView) findViewById(R.id.lblStatus);

        btnSendRequest = (Button)findViewById(R.id.btnSendRequest);
        btnSendRequest.setGravity(CENTER);
        btnSendRequest.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        btnSendRequest.setText("ĐỀ NGHỊ ĐÓN");
        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {
                        
                        if(driverBidding == null && CurrentOrder() != null ){

                            invalidateOrder(new Completion() {
                                @Override
                                public void onCompleted() {

                                    if(driverStatus.IsReady == 1){

                                        SCONNECTING.orderManager.actionHandler.UserSendRequestToDriver(CurrentOrder(), driver.id, new GetOneListener() {
                                            @Override
                                            public void onGetOne(Boolean success,BaseModel item) {

                                                invalidateOrderControls(null);
                                            }
                                        });

                                    }else{

                                        invalidateOrderControls(null);

                                    }
                                }
                            });

                        }

                    }
                });


            }
        });
        btnCancelRequest = (Button)findViewById(R.id.btnCancelRequest);
        btnCancelRequest.setGravity(CENTER);
        btnCancelRequest.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        btnCancelRequest.setText("HỦY ĐÓN");
        btnCancelRequest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {


                        if(driverBidding == null && CurrentOrder() != null ){

                            SCONNECTING.orderManager.actionHandler.UserCancelRequestingToDriver(CurrentOrder(), new GetOneListener() {
                                @Override
                                public void onGetOne(Boolean success,BaseModel item) {

                                    invalidateOrderControls(null);
                                }
                            });

                        }


                    }
                });


            }
        });
        btnAcceptBidding = (Button)findViewById(R.id.btnAcceptBidding);
        btnAcceptBidding.setGravity(CENTER);
        btnAcceptBidding.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        btnAcceptBidding.setText("CHẤP NHẬN TÀI XẾ");
        btnAcceptBidding.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        if(driverBidding != null && CurrentOrder() != null ){

                            SCONNECTING.orderManager.actionHandler.UserAcceptBidding(driverBidding, new GetOneListener() {
                                @Override
                                public void onGetOne(Boolean success,BaseModel item) {

                                    invalidateOrderControls(null);
                                }
                            });

                        }

                    }
                });


            }
        });
        btnCancelBidding = (Button)findViewById(R.id.btnCancelBidding);
        btnCancelBidding.setGravity(CENTER);
        btnCancelBidding.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        btnCancelBidding.setText("KHÔNG CHỜ");
        btnCancelBidding.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        if(driverBidding != null && CurrentOrder() != null ){

                            SCONNECTING.orderManager.actionHandler.UserCancelAcceptingBidding(driverBidding, new GetOneListener() {
                                @Override
                                public void onGetOne(Boolean success,BaseModel item) {

                                    invalidateOrderControls(null);
                                }
                            });

                        }
                    }
                });


            }
        });


        commentView = new CommentTable((RecyclerView) findViewById(R.id.commentView),this.DriverID );
    }


    public void invalidate(String driverId,  DriverBidding bidding,final Completion listener){

        this.DriverID = driverId;
        this.driverBidding = bidding;
        invalidate(listener);

    }
    public void invalidate(final Completion listener){

        new BaseController<>(DriverStatus.class).getOneByStringField(false, "Driver", this.DriverID, new GetOneListener() {
            @Override
            public void onGetOne(Boolean success,BaseModel item) {

                if(!success){
                    if(listener !=null)
                        listener.onCompleted();
                    return;
                }

                driverStatus = (DriverStatus)item;

                new BaseController<>(Driver.class).getById(false, driverStatus.Driver, new GetOneListener() {
                    @Override
                    public void onGetOne(Boolean success,BaseModel newDriver) {

                        if(success) {

                            driver = (Driver) newDriver;

                            invalidateProfile();

                            commentView.refreshObjectList(new Completion() {
                                @Override
                                public void onCompleted() {
                                    invalidateOrder(listener);
                                }
                            });

                        }else{

                            if(listener !=null)
                                listener.onCompleted();
                        }

                    }
                });

            }
        });

    }

    public void invalidateProfile(){

        mToolbarTitle.setText(this.driver != null ? this.driver.Name.toUpperCase() : "" );


        String url = this.driver.id != null ? ServerStorage.ServerURL + "/avatar/driver/" + this.driver.id  : "";

        ImageHelper.loadImage(this, url, R.drawable.avatar, 250, 250, imgAvatar, new Callback() {
            @Override
            public void onSuccess() {

                imgAvatar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onError() {

                imgAvatar.setVisibility(View.VISIBLE);

            }
        });


        this.lblCitizenIDValue.setText( this.driver != null && this.driver.CitizenID != null ? this.driver.CitizenID.toUpperCase() : "" );
        this.lblCompanyValue.setText( this.driverStatus != null && this.driverStatus.CompanyName != null ? this.driverStatus.CompanyName.toUpperCase() : "" );
        this.lblVehicleNoValue.setText(this.driverStatus != null && this.driverStatus.VehicleNo != null ? this.driverStatus.VehicleNo.toUpperCase() : "" );

        String qualityType = "";
        if(driverStatus != null && driverStatus.QualityService != null)
            qualityType = QualityServiceTypeController.GetLocaleNameFromType(driverStatus.QualityService);
        lblVehicleQualityValue.setText(qualityType);

        String vehicleType = "";
        if(driverStatus != null && driverStatus.VehicleType != null)
            vehicleType = VehicleTypeController.GetLocaleNameFromType(driverStatus.VehicleType );
        lblVehicleTypeValue.setText(vehicleType);


        this.lblServedQtyValue.setText( this.driverStatus != null ? String.format("%d chuyến",this.driverStatus.ServedQty) : " 0 chuyến" );
        this.lblVoidedBfPickupByDriverValue.setText(this.driverStatus != null ?  String.format("%d chuyến",this.driverStatus.VoidedBfPickupByDriver) : " 0 chuyến" );
        this.lblRateCountValue.setText( this.driverStatus != null ?  String.format("%d đánh giá",this.driverStatus.RateCount)  : " 0 đánh giá" );

        ImageHelper.loadImage(this,((this.driverStatus == null) || (this.driverStatus.Rating < 1)) ?  R.drawable.graystar : R.drawable.star ,32,32, imgStar1,null );
        ImageHelper.loadImage(this,((this.driverStatus == null) || (this.driverStatus.Rating < 2)) ?  R.drawable.graystar : R.drawable.star ,32,32, imgStar2,null );
        ImageHelper.loadImage(this,((this.driverStatus == null) || (this.driverStatus.Rating < 3)) ?  R.drawable.graystar : R.drawable.star ,32,32, imgStar3,null );
        ImageHelper.loadImage(this,((this.driverStatus == null) || (this.driverStatus.Rating < 4)) ?  R.drawable.graystar : R.drawable.star ,32,32, imgStar4,null );
        ImageHelper.loadImage(this,((this.driverStatus == null) || (this.driverStatus.Rating < 5)) ?  R.drawable.graystar : R.drawable.star ,32,32, imgStar5,null );

    }


    public void invalidateOrder(final Completion listener){

        new BaseController<>(DriverStatus.class).getOneByStringField(true, "Driver", this.DriverID, new GetOneListener(){
            @Override
            public void onGetOne(Boolean success,BaseModel item) {

                if(success)
                    driverStatus = (DriverStatus)item;

                if(CurrentOrder() != null && CurrentOrder().isNew() == false){

                    new BaseController<>(TravelOrder.class).getById(true, CurrentOrder().id, new GetOneListener() {
                        @Override
                        public void onGetOne(Boolean success,BaseModel item) {
                            if(success)
                                SCONNECTING.orderManager.currentOrder = (TravelOrder)item;
                            invalidateOrderControls(listener);
                        }
                    });


                }else{
                    invalidateOrderControls(listener);
                }
            }
        });



    }

    public void invalidateOrderControls(final Completion listener){

        if(CurrentOrder() != null){

            TravelOrderController.TryToCalculateTripPrice(this.CurrentOrder().id,DriverID, new GetDoubleValueListener() {
                @Override
                public void onCompleted(Boolean success,Double value) {

                    if(success) {
                        lblEstPrice.setText((value != null) ? RegionalHelper.toCurrencyOfCountry(value, CurrentOrder().OrderPickupCountry) : "");
                        lblEstPrice.setVisibility((value != null && value > 0) ? View.VISIBLE : View.GONE);
                    }
                    if(listener != null)
                        listener.onCompleted();

                }
            });
        }

        lblEstPrice.setVisibility((this.driverBidding != null || (CurrentOrder() != null && CurrentOrder().OrderDistance > 0) )  ? View.VISIBLE : View.GONE);

        this.btnSendRequest.setVisibility((this.driverStatus.IsReady == 1 &&  this.driverBidding == null && CurrentOrder() != null && CurrentOrder().Status.equals(OrderStatus.Open)) ? View.VISIBLE : View.GONE);
        this.btnCancelRequest.setVisibility((this.driverBidding == null && CurrentOrder() != null && CurrentOrder().Status.equals(OrderStatus.Requested))  ? View.VISIBLE : View.GONE);
        this.btnAcceptBidding.setVisibility((this.driverBidding != null && CurrentOrder() != null && CurrentOrder().Status.equals(OrderStatus.Open)) ? View.VISIBLE : View.GONE);

        this.btnCancelBidding.setVisibility((this.driverBidding != null && CurrentOrder() != null && CurrentOrder().Status.equals(OrderStatus.BiddingAccepted)) ? View.VISIBLE : View.GONE);
        this.lblStatus.setVisibility(View.GONE);

        if( CurrentOrder() != null)
            this.invalidateOrderStatus(CurrentOrder().Status);

    }

    public void invalidateOrderStatus(String status ) {

        if( CurrentOrder() == null)
            return;

        CurrentOrder().Status = status;

        Boolean isShow = CurrentOrder() != null && (CurrentOrder().Status != null) && (CurrentOrder().Status.isEmpty() == false);
        this.lblStatus.setVisibility( isShow? View.VISIBLE : View.GONE);

        Boolean isEnableBack = !( (CurrentOrder() != null) && (CurrentOrder().Status.equals(OrderStatus.Requested) || CurrentOrder().Status.equals(OrderStatus.BiddingAccepted)));
        this.btnBack.setVisibility(isEnableBack ? View.VISIBLE : View.GONE);

        if (isShow) {

            if (this.driverStatus.IsReady == 0) {
                if(SCONNECTING.orderManager.currentOrder.id == CurrentOrder().id && SCONNECTING.orderManager.currentOrder.Status.equals(OrderStatus.DriverAccepted))
                     this.lblStatus.setText("Tài xế đã chấp nhận yêu cầu.");
                else
                     this.lblStatus.setText("Tài xế đang bận.");

                this.lblStatus.setVisibility(View.VISIBLE);

            } else {

                this.lblStatus.setVisibility(((CurrentOrder().Status != null) && (CurrentOrder().Status.isEmpty() == false)) ? View.VISIBLE : View.GONE);

                if (CurrentOrder().Status != null) {

                    if (CurrentOrder().Status.equals(OrderStatus.Requested) || CurrentOrder().Status.equals(OrderStatus.BiddingAccepted)) {
                        this.lblStatus.setText("Chờ phản hồi từ tài xế . . .");

                    } else if (CurrentOrder().Status.equals(OrderStatus.DriverAccepted)) {
                        this.lblStatus.setText("Tài xế sẵn sàng phục vụ. Vui lòng chờ.");

                    } else if (CurrentOrder().Status.equals(OrderStatus.DriverRejected)) {
                        this.lblStatus.setText("Tài xế từ chối phục vụ.");
                    } else {
                        this.lblStatus.setText("");
                        this.lblStatus.setVisibility(View.GONE);
                    }
                }
            }
        }
    }





}
