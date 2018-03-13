package com.sconnecting.userapp.ui.taxi.order.monitoring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.GetBoolValueListener;
import com.sconnecting.userapp.base.listener.GetDoubleValueListener;
import com.sconnecting.userapp.base.listener.GetOneListener;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.data.storages.server.ServerStorage;
import com.sconnecting.userapp.base.AnimationHelper;
import com.sconnecting.userapp.base.ImageHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.data.controllers.QualityServiceTypeController;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.data.controllers.VehicleTypeController;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;
import com.sconnecting.userapp.ui.taxi.order.monitoring.message.TravelChattingObject;
import com.sconnecting.userapp.ui.taxi.order.monitoring.message.TravelChattingView;
import com.sconnecting.userapp.ui.taxi.share.DriverProfile.DriverProfileScreen;
import com.sconnecting.userapp.data.models.Driver;
import com.sconnecting.userapp.data.models.DriverStatus;
import com.sconnecting.userapp.data.models.TravelOrder;
import com.sconnecting.userapp.data.models.TravelOrderChatting;
import com.squareup.picasso.Callback;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class DriverProfileView extends Fragment {


    public OrderScreen screen;
    public View view;
    MonitoringView parent;

    View pnlProfileArea;
    public Boolean isCollapsed = true;
    IconTextView btnCollapse;
    CircularImageView imgAvatar;
    ImageView redCircle;
    TextView lblMessageNo;

    TextView lblDriverName;
    TextView lblSeaterAndQuality;
    TextView lblLastMessage;
    TextView lblVehicleNo;
    TextView lblRateCount;
    ImageView imgStar1;
    ImageView imgStar2;
    ImageView imgStar3;
    ImageView imgStar4;
    ImageView imgStar5;


    public TravelChattingView chattingView;

    public Driver driver;
    public DriverStatus driverStatus;

    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }


    public DriverProfileView(){

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        screen = (OrderScreen) context;
        screen.mMonitoringView.driverProfileView = this;

        parent = screen.mMonitoringView;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        view = inflater.inflate(R.layout.taxi_order_monitoring_driverprofile, container, false);

        initControls(new Completion(){

            @Override
            public void onCompleted() {

                invalidate(true,null);
            }
        });
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        invalidate(false,null);

    }


    public void initControls(final Completion listener) {


        pnlProfileArea = (View) view.findViewById(R.id.pnlProfileArea);
        pnlProfileArea.setVisibility(View.GONE);
        pnlProfileArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AnimationHelper.hideKeyBoard(v);

                onClickCollapseButton();

                return true;
            }
        });

        btnCollapse = (IconTextView) pnlProfileArea.findViewById(R.id.btnCollapseProfile);
        AnimationHelper.setOnClick(btnCollapse, new Completion() {
            @Override
            public void onCompleted() {

                onClickCollapseButton();
            }
        });

        imgAvatar = (CircularImageView) pnlProfileArea.findViewById(R.id.imgAvatar);
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AnimationHelper.animateButton(view, new Completion() {
                    @Override
                    public void onCompleted() {

                        Intent intent = new Intent(view.getContext(), DriverProfileScreen.class);
                        intent.putExtra("DriverID",driver.getId());

                        ((Activity)view.getContext()).startActivity(intent);
                        ((Activity)view.getContext()).overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);

                    }
                });



            }
        });
        redCircle = (ImageView) pnlProfileArea.findViewById(R.id.redCircle);
        redCircle.setVisibility(View.GONE);
        lblMessageNo = (TextView) pnlProfileArea.findViewById(R.id.lblMessageNo);
        lblMessageNo.setVisibility(View.GONE);

        lblDriverName = (TextView) pnlProfileArea.findViewById(R.id.lblDriverName);
        lblDriverName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickCollapseButton();
            }
        });
        lblSeaterAndQuality = (TextView) pnlProfileArea.findViewById(R.id.lblSeaterAndQuality);
        lblSeaterAndQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickCollapseButton();
            }
        });
        lblLastMessage = (TextView) pnlProfileArea.findViewById(R.id.lblLastMessage);
        lblLastMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickCollapseButton();
            }
        });
        lblVehicleNo = (TextView) pnlProfileArea.findViewById(R.id.lblVehicleNo);
        lblVehicleNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickCollapseButton();
            }
        });
        lblRateCount = (TextView) pnlProfileArea.findViewById(R.id.lblRateCount);
        lblRateCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickCollapseButton();
            }
        });


        imgStar1 = (ImageView) pnlProfileArea.findViewById(R.id.imgStar1);
        imgStar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickCollapseButton();
            }
        });
        imgStar2 = (ImageView) pnlProfileArea.findViewById(R.id.imgStar2);
        imgStar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickCollapseButton();
            }
        });
        imgStar3 = (ImageView) pnlProfileArea.findViewById(R.id.imgStar3);
        imgStar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickCollapseButton();
            }
        });
        imgStar4 = (ImageView) pnlProfileArea.findViewById(R.id.imgStar4);
        imgStar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickCollapseButton();
            }
        });
        imgStar5 = (ImageView) pnlProfileArea.findViewById(R.id.imgStar5);
        imgStar5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickCollapseButton();
            }
        });

        chattingView = new TravelChattingView( this.screen,pnlProfileArea.findViewById(R.id.chattingView));

        if(listener != null)
            listener.onCompleted();

    }

    private void onClickCollapseButton() {

        isCollapsed = !isCollapsed;

        if(!isCollapsed)
            chattingView.setReadAll();

        invalidateUI(false,new Completion() {
            @Override
            public void onCompleted() {

            }
        });

        if( !isCollapsed){

            parent.orderPanelView.isCollapsed = true;
            parent.orderPanelView.invalidateUI(false,null);

        }
    }


    Boolean shouldToShow(){

        Boolean isShow  = (this.driverStatus != null) && CurrentOrder().isTripMateMember() == false && (  CurrentOrder().IsWaitingDriver() ||  CurrentOrder().IsMonitoring() ||  CurrentOrder().IsFinishedNotYetPaid() ) ;

        return isShow;
    }

    public void show(Boolean show, final GetBoolValueListener listener){

        if(show){

            if(this.pnlProfileArea.getVisibility() != View.VISIBLE ){

                this.pnlProfileArea.setVisibility(VISIBLE);

                if(listener != null )
                    listener.onCompleted(true,true);
            }else{

                if(listener != null )
                    listener.onCompleted(true,false);
            }


        }else{

            if(this.pnlProfileArea.getVisibility() == View.VISIBLE){

                this.pnlProfileArea.setVisibility(View.GONE);

                if(listener != null )
                    listener.onCompleted(true,true);
            }else{

                if(listener != null )
                    listener.onCompleted(true,false);
            }

        }
    }

    public void invalidate(final Boolean isFirstTime,final Completion listener) {

        if(shouldToShow()){

            screen.showToolbar(true);
            if (this.screen.mPlaceSearcher != null && this.screen.mPlaceSearcher.mSearchView != null)
                this.screen.mPlaceSearcher.mSearchView.setVisibility(GONE);

            show(true, new GetBoolValueListener() {
                @Override
                public void onCompleted(Boolean success, Boolean changed) {

                    invalidateUI(changed,listener);

                }
            });



        }else{

            show(false, new GetBoolValueListener() {
                @Override
                public void onCompleted(Boolean success, Boolean value) {

                    if(listener != null )
                        listener.onCompleted();
                }
            });

        }

    }

    public void invalidateAvatar(final Boolean isFirstTime,final Completion listener) {

        if(isFirstTime && CurrentOrder() != null) {
            String url = this.CurrentOrder().Driver != null ? ServerStorage.ServerURL + "/avatar/driver/" + this.CurrentOrder().Driver : "";

            if (url.isEmpty()) {
                if (listener != null)
                    listener.onCompleted();
                return;
            }
            ImageHelper.loadImage(parent.screen, url, R.drawable.avatar, 140, 140, imgAvatar, new Callback() {
                @Override
                public void onSuccess() {

                    imgAvatar.setVisibility(View.VISIBLE);

                }

                @Override
                public void onError() {

                    imgAvatar.setVisibility(View.VISIBLE);

                }
            });
        }
        if(listener != null )
            listener.onCompleted();
    }



    public void increaseMessageNo(Integer increase , final GetDoubleValueListener listener){

        if(increase == null) {

            invalidateMessageNo(false,null,listener);
            return;
        }

        String strNo = lblMessageNo.getText().toString();
        if(strNo.isEmpty() == false) {

            Integer count =  Integer.valueOf(strNo);
            if(count != null)
                count += increase;

            invalidateMessageNo(false,count, listener);
        }else {

            invalidateMessageNo(false,null, listener);
        }


    }

    public void invalidateMessageNo(final Boolean isFirstTime,Integer count , final GetDoubleValueListener listener){

        if(count != null && isFirstTime == false){

            this.redCircle.setVisibility( (!this.shouldToShow()  || !this.isCollapsed || count <= 0) ? View.GONE : VISIBLE);
            this.lblMessageNo.setVisibility(  this.redCircle.getVisibility());
            this.lblMessageNo.setText(String.valueOf(count));
            if(listener != null)
                listener.onCompleted(true, Double.valueOf(count));
            return;

        }

        if(isFirstTime == false){
            if(listener != null)
                listener.onCompleted(false, null);
            return;
        }

        TravelOrderController.CountNotYetViewedMessageByUser(CurrentOrder().getId(), new GetDoubleValueListener() {
            @Override
            public void onCompleted(Boolean success, Double number) {

                redCircle.setVisibility( (!shouldToShow()  || !isCollapsed || number == null || number <= 0) ? View.GONE : VISIBLE);
                lblMessageNo.setVisibility(  redCircle.getVisibility());
                lblMessageNo.setText( number != null ? String.valueOf(number.intValue()) : "");
                if(listener != null)
                    listener.onCompleted(true,  number != null ? Double.valueOf(number) : 0);

            }
        });

    }


    public void invalidateLastMessage(final Boolean isFirstTime,String message , final Completion listener){

        this.lblLastMessage.setVisibility( (!this.shouldToShow()  || !this.isCollapsed) ? View.GONE : VISIBLE);

        if( message != null){
            this.lblLastMessage.setText(message);
            if(listener != null)
                listener.onCompleted();
            return;
        }


        if(isFirstTime == false && this.chattingView != null && this.chattingView.chattingTable != null && this.chattingView.chattingTable.dataSource.size() > 0){

            TravelChattingObject lastObj = this.chattingView.chattingTable.dataSource.size() > 0 ? this.chattingView.chattingTable.dataSource.get(this.chattingView.chattingTable.dataSource.size() - 1) : null;

            if( lastObj != null && lastObj.cellObject != null && lastObj.cellObject.IsUser == 1 && lastObj.cellObject.Content != null ){
                lblLastMessage.setText(lastObj.cellObject.Content);
            }else{
                lblLastMessage.setText("");
            }

            if(listener != null)
                listener.onCompleted();
        }else{

            TravelOrderController.GetLastChattingMessage(CurrentOrder().getId(), new GetOneListener() {
                @Override
                public void onGetOne(Boolean success, BaseModel item) {

                    TravelOrderChatting chatting = (TravelOrderChatting)item;
                    if(chatting != null  && chatting.IsUser == 0  && chatting.Content != null){
                        lblLastMessage.setText(chatting.Content);
                    }else{
                        lblLastMessage.setText("");
                    }
                    if(listener != null)
                        listener.onCompleted();
                }
            });
        }


    }


    public void invalidateUI(final Boolean isFirstTime,final Completion listener) {

        if (CurrentOrder() != null){

            this.invalidateAvatar(isFirstTime, null);
            this.btnCollapse.setText(this.isCollapsed ? "{fa-chevron-down}" : "{fa-chevron-up}");

            this.imgStar1.setVisibility( !( this.driverStatus != null && this.driverStatus.Rating >= 1) ? GONE : VISIBLE);
            this.imgStar2.setVisibility( !( this.driverStatus != null && this.driverStatus.Rating >= 2) ? GONE : VISIBLE);
            this.imgStar3.setVisibility( !( this.driverStatus != null && this.driverStatus.Rating >= 3) ? GONE : VISIBLE);
            this.imgStar4.setVisibility( !( this.driverStatus != null && this.driverStatus.Rating >= 4) ? GONE : VISIBLE);
            this.imgStar5.setVisibility( !( this.driverStatus != null && this.driverStatus.Rating >= 5) ? GONE : VISIBLE);

            this.lblRateCount.setText( (this.driverStatus != null) ? this.driverStatus.RateCount.toString() + " lượt" : "" );
            this.lblRateCount.setVisibility(VISIBLE);

            this.lblDriverName.setText( (this.driverStatus != null) ? this.driverStatus.DriverName.toString().toUpperCase() : "" );
            this.lblDriverName.setVisibility(VISIBLE);

            this.lblVehicleNo.setText( (this.driverStatus != null) ? this.driverStatus.VehicleNo.toString() : "" );


            String vehicleType = "";
            String qualityType = "";


            if( this.driverStatus != null && this.driverStatus.QualityService != null)
                qualityType = QualityServiceTypeController.GetLocaleNameFromType(this.driverStatus.QualityService);

            if(this.driverStatus != null && this.driverStatus.VehicleType != null)
                vehicleType = VehicleTypeController.GetLocaleNameFromType(this.driverStatus.VehicleType);

            String strCompany = "";
            if(this.driverStatus != null && this.driverStatus.CompanyName != null)
                strCompany = this.driverStatus.CompanyName.toString();


            if( strCompany.isEmpty() == false  && vehicleType.isEmpty() == false && qualityType.isEmpty() == false )
                lblSeaterAndQuality.setText(strCompany  + " - " + vehicleType + " - " + qualityType);
            else if( vehicleType.isEmpty() == false)
                lblSeaterAndQuality.setText(strCompany  + " - " + vehicleType);
            else if( qualityType.isEmpty() == false)
                lblSeaterAndQuality.setText(strCompany  + " - " + qualityType);
            else
                lblSeaterAndQuality.setText("");

            this.lblSeaterAndQuality.setVisibility(VISIBLE);


            this.chattingView.invalidate(!this.isCollapsed, new GetBoolValueListener() {
                @Override
                public void onCompleted(Boolean success, Boolean changed) {

                    invalidateMessageNo(isFirstTime,null, new GetDoubleValueListener() {
                        @Override
                        public void onCompleted(Boolean success, Double number) {

                            invalidateLastMessage( isFirstTime, null,listener);
                        }
                    });
                }
            });


            chattingView.chattingView.setVisibility(this.isCollapsed ? GONE : VISIBLE);
        }

    }

}
