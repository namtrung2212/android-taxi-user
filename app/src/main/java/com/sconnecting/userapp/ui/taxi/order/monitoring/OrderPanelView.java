package com.sconnecting.userapp.ui.taxi.order.monitoring;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.sconnecting.userapp.AppDelegate;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.GetOneListener;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.base.AnimationHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.base.RegionalHelper;
import com.sconnecting.userapp.ui.taxi.share.DriverProfile.DriverProfileScreen;
import com.sconnecting.userapp.data.models.OrderStatus;
import com.sconnecting.userapp.data.models.TravelOrder;
import com.sconnecting.userapp.ui.taxi.tripmate.host.HostScreen;

import org.parceler.Parcels;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by TrungDao on 9/19/16.
 */

public class OrderPanelView {

    MonitoringView parent;
    View tripMateButtonArea;
    ImageView btnTripMate;
    ImageView redCircleTripMate;
    TextView lblInfoNoTripMate;
    ImageView btnChatTripMate;
    ImageView redCircleTripMateChat;
    TextView lbChatNoTripMate;

    View OrderMonitorArea;
    public View pnlOrderArea;
    public Boolean isCollapsed = false;
    IconTextView btnCollapse;
    TextView lblStatus;
    TextView lblCurrentPrice;
    ImageButton btnPickupIcon;
    ImageButton btnDropIcon;
    IconTextView lblMoreInfoIcon;
    TextView lblPickupLocation;
    TextView lblDropLocation;

    View line1;
    View line2;
    View line3;

    TextView lblMoreInfo;
    Button btnVoid;



    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }

    public OrderPanelView(MonitoringView monitoringView){

        parent = monitoringView;
    }

    public void initUI(final Completion listener) {

        tripMateButtonArea = parent.view.findViewById(R.id.tripMateButtonArea);
        btnTripMate = (ImageView)parent.view.findViewById(R.id.btnTripMate);
        AnimationHelper.setOnClick(btnTripMate, new Completion() {
            @Override
            public void onCompleted() {

                SCONNECTING.orderManager.reloadHostOrder(new Completion() {
                    @Override
                    public void onCompleted() {

                        Intent intent = new Intent(parent.view.getContext(), HostScreen.class);

                        if(CurrentOrder().IsMateHost == 1) {

                            Parcelable wrappedCurrentOrder = Parcels.wrap(CurrentOrder());
                            intent.putExtra("HostOrder", wrappedCurrentOrder);
                            ((Activity)parent.view.getContext()).startActivity(intent);
                            ((Activity)parent.view.getContext()).overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);


                        }else if (CurrentOrder().isTripMateMember() && SCONNECTING.orderManager.hostOrder != null && CurrentOrder().MateHostOrder.equals(SCONNECTING.orderManager.hostOrder.getId())){

                            Parcelable wrappedCurrentOrder = Parcels.wrap(SCONNECTING.orderManager.hostOrder);
                            intent.putExtra("HostOrder", wrappedCurrentOrder);
                            ((Activity)parent.view.getContext()).startActivity(intent);
                            ((Activity)parent.view.getContext()).overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);

                        }
                    }
                });
            }
        });

        redCircleTripMate = (ImageView)parent.view.findViewById(R.id.redCircleTripMate);

        lblInfoNoTripMate = (TextView)parent.view.findViewById(R.id.lblInfoNoTripMate);

        btnChatTripMate = (ImageView)parent.view.findViewById(R.id.btnChatTripMate);
        AnimationHelper.setOnClick(btnChatTripMate, new Completion() {
            @Override
            public void onCompleted() {

                Intent intent = new Intent(parent.view.getContext(), HostScreen.class);

                if(CurrentOrder().IsMateHost == 1) {

                    Parcelable wrappedCurrentOrder = Parcels.wrap(CurrentOrder());
                    intent.putExtra("HostOrder", wrappedCurrentOrder);
                    ((Activity)parent.view.getContext()).startActivity(intent);
                    ((Activity)parent.view.getContext()).overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);


                }else if (CurrentOrder().isTripMateMember() && SCONNECTING.orderManager.hostOrder != null && CurrentOrder().MateHostOrder.equals(SCONNECTING.orderManager.hostOrder.getId())){

                    Parcelable wrappedCurrentOrder = Parcels.wrap(SCONNECTING.orderManager.hostOrder);
                    intent.putExtra("HostOrder", wrappedCurrentOrder);
                    ((Activity)parent.view.getContext()).startActivity(intent);
                    ((Activity)parent.view.getContext()).overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);

                }
            }
        });

        redCircleTripMateChat = (ImageView)parent.view.findViewById(R.id.redCircleTripMateChat);

        lbChatNoTripMate = (TextView)parent.view.findViewById(R.id.lbChatNoTripMate);


        OrderMonitorArea = (View) parent.view.findViewById(R.id.OrderMonitorArea);
        pnlOrderArea = (View) OrderMonitorArea.findViewById(R.id.pnlOrderArea);
        pnlOrderArea.setVisibility(GONE);
        pnlOrderArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AnimationHelper.hideKeyBoard(v);

                onClickCollapseButton();

                return false;
            }
        });

        btnCollapse = (IconTextView) pnlOrderArea.findViewById(R.id.btnCollapseOrder);
        AnimationHelper.setOnClick(btnCollapse, new Completion() {
            @Override
            public void onCompleted() {

                onClickCollapseButton();
            }
        });


        lblStatus = (TextView) pnlOrderArea.findViewById(R.id.lblStatus);
        lblStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickCollapseButton();
            }
        });
        lblCurrentPrice = (TextView) pnlOrderArea.findViewById(R.id.lblCurrentPrice);
        lblCurrentPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onClickCollapseButton();
            }
        });

        btnPickupIcon = (ImageButton) pnlOrderArea.findViewById(R.id.btnPickupIcon);
        btnPickupIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        if( CurrentOrder().ActPickupLoc != null ){
                            parent.screen.mMapView.moveToLocation(CurrentOrder().ActPickupLoc.getLatLng(),true,null);

                        }else if( CurrentOrder().OrderPickupLoc != null ) {
                            parent.screen.mMapView.moveToLocation(CurrentOrder().OrderPickupLoc.getLatLng(),true, null);
                        }


                    }
                });


            }
        });

        btnDropIcon = (ImageButton) pnlOrderArea.findViewById(R.id.btnDropIcon);
        btnDropIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        if( CurrentOrder().ActDropLoc != null ){
                            parent.screen.mMapView.moveToLocation(CurrentOrder().ActDropLoc.getLatLng(),true,null);

                        }else if( CurrentOrder().OrderDropLoc != null ){
                            parent.screen.mMapView.moveToLocation(CurrentOrder().OrderDropLoc.getLatLng(),true,null);
                        }
                    }
                });

            }
        });


        lblPickupLocation = (TextView) pnlOrderArea.findViewById(R.id.lblPickupLocation);
        lblPickupLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        if( CurrentOrder().ActPickupLoc != null){
                            parent.screen.mMapView.moveToLocation(CurrentOrder().ActPickupLoc.getLatLng(),true,null);
                        }else if( CurrentOrder().OrderPickupLoc != null){
                            parent.screen.mMapView.moveToLocation(CurrentOrder().OrderPickupLoc.getLatLng(),true,null);
                        }
                    }
                });


            }
        });
        lblDropLocation = (TextView) pnlOrderArea.findViewById(R.id.lblDropLocation);
        lblDropLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {


                        if( CurrentOrder().ActDropLoc != null){
                            parent.screen.mMapView.moveToLocation(CurrentOrder().ActDropLoc.getLatLng(),true,null);
                        }else if( CurrentOrder().OrderDropLoc != null){
                            parent.screen.mMapView.moveToLocation(CurrentOrder().OrderDropLoc.getLatLng(),true,null);
                        }
                    }
                });

            }
        });

        lblMoreInfoIcon = (IconTextView) pnlOrderArea.findViewById(R.id.lblMoreInfoIcon);
        lblMoreInfo = (TextView) pnlOrderArea.findViewById(R.id.lblMoreInfo);


        btnVoid = (Button) OrderMonitorArea.findViewById(R.id.btnVoid);
        btnVoid.setText("HỦY CHUYẾN");
        btnVoid.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        new SweetAlertDialog(AppDelegate.CurrentActivity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Bạn muốn hủy hành trình ?")
                                .setContentText("Việc hủy chuyến thường xuyên sẽ được ghi nhận vào hồ sơ của bạn.")
                                .setConfirmText("Hủy")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                        SCONNECTING.orderManager.actionHandler.UserVoidOrder(CurrentOrder(), new GetOneListener() {
                                            @Override
                                            public void onGetOne(Boolean success,BaseModel item) {

                                                if(success) {
                                                    TravelOrder order = (TravelOrder) item;
                                                    if (order != null && order.IsFinishedNotYetPaid()) {

                                                        SCONNECTING.orderManager.reset(order, true, null);

                                                    } else if (order != null && order.Status != null && order.Status.equals(OrderStatus.VoidedBfPickupByUser)) {

                                                        SCONNECTING.orderManager.actionHandler.ResetWhenVoidedBeforePickup(order, null);

                                                    }
                                                }
                                            }
                                        });

                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                })
                                .showCancelButton(true)
                                .setCancelText("Không")
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                        sweetAlertDialog.cancel();
                                    }
                                })
                                .show();

                    }
                });

            }
        });

        line1 =  pnlOrderArea.findViewById(R.id.line1);
        line2 =  pnlOrderArea.findViewById(R.id.line2);
        line3 =  pnlOrderArea.findViewById(R.id.line3);

        if(listener != null)
            listener.onCompleted();

    }

    private void onClickCollapseButton() {
        isCollapsed = !isCollapsed;

        invalidateUI(false,null);


        if( !isCollapsed){

            parent.driverProfileView.isCollapsed = true;
            parent.driverProfileView.invalidateUI(false,null);

        }
    }

    public void invalidate(final Boolean isFirstTime,final Completion listener) {

        Boolean isShow= false;
        if(CurrentOrder() == null)
            isShow = false;

        else if(CurrentOrder().IsMateHost == 1 || (CurrentOrder().IsMateHost == 0 && CurrentOrder().isTripMateMember() == false)) {

            isShow = (CurrentOrder().IsDriverAccepted() || CurrentOrder().IsMonitoring() || CurrentOrder().IsStopped());


        }else if(CurrentOrder().isTripMateMember()){
            isShow = CurrentOrder().Status.equals(OrderStatus.DriverAccepted) || CurrentOrder().Status.equals(OrderStatus.DriverPicking)
                    || CurrentOrder().Status.equals(OrderStatus.VoidedAfPickupByUser) || CurrentOrder().Status.equals(OrderStatus.VoidedAfPickupByDriver)
                    || CurrentOrder().Status.equals(OrderStatus.Finished);
        }


        if(isShow){

            invalidateUI(isFirstTime,new Completion() {
                @Override
                public void onCompleted() {

                    show(true, listener);

                }
            });

        }else{

            show(false,listener);

        }

    }

    public void invalidateUI(final Boolean isFirstTime,final Completion listener) {

        this.btnCollapse.setText(this.isCollapsed ? "{fa-chevron-up}" : "{fa-chevron-down}");

        invalidateTripMateButtons(isFirstTime, new Completion() {
            @Override
            public void onCompleted() {

                invalidateStatus(isFirstTime,new Completion() {
                    @Override
                    public void onCompleted() {

                        invalidatePickupDropLocation(isFirstTime,new Completion() {
                            @Override
                            public void onCompleted() {

                                invalidateMoreInfo(isFirstTime,new Completion() {
                                    @Override
                                    public void onCompleted() {

                                        invalidateButtonArea(isFirstTime,new Completion() {
                                            @Override
                                            public void onCompleted() {

                                                invalidateTripPrice(isFirstTime,listener);

                                            }
                                        });


                                    }
                                });

                            }
                        });
                    }
                });

            }
        });
    }

    public void invalidateTripMateButtons(final Boolean isFirstTime,final Completion listener) {

        tripMateButtonArea.setVisibility(CurrentOrder().IsMateHost == 1 || CurrentOrder().isTripMateMember() ? VISIBLE : GONE);

        redCircleTripMate.setVisibility(GONE);
        lblInfoNoTripMate.setVisibility(GONE);

        redCircleTripMateChat.setVisibility(GONE);
        lbChatNoTripMate.setVisibility(GONE);

        if(listener !=null)
            listener.onCompleted();

    }
    public void invalidateStatus(final Boolean isFirstTime,final Completion listener) {

        String strStatus = "";

        if(CurrentOrder().isTripMateMember())
            strStatus = "Nhóm đi chung";

        else if(this.CurrentOrder().IsDriverAccepted()) {
            strStatus = "Tài xế đang bận, vui lòng chờ.";

        }else if(this.CurrentOrder().IsDriverPicking()){
            strStatus = "Tài xế đang đến, vui lòng chờ.";


            if(this.parent.screen.mapMarkerManager != null && this.parent.screen.mapMarkerManager.currentVehicle() != null && this.parent.screen.mapMarkerManager.currentVehicle().driverId != null
                    && this.parent.screen.mapMarkerManager.currentVehicle().driverId.equals(this.CurrentOrder().Driver)
                    && this.CurrentOrder().OrderPickupLoc != null){

                Double distance = this.parent.screen.mapMarkerManager.currentVehicle().distanceFromLocation(this.CurrentOrder().OrderPickupLoc.getLocation());

                if(distance >= 1000){
                    strStatus = String.format("Đang chờ tài xế. Khoảng cách %.1f Km", distance/1000 );

                }else if(distance > 50) {
                    strStatus =  String.format("Đang chờ tài xế. Khoảng cách %.0f m", distance );

                }else{
                    strStatus = "Tài xế đã đến điểm hẹn.";

                    if( strStatus.toUpperCase().equals(this.lblStatus.getText().toString())== false){

                        new SweetAlertDialog(AppDelegate.CurrentActivity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Tài xế đã đến điểm hẹn.")
                                .setContentText("Chúc quý khách 1 chuyến đi vui vẻ.")
                                .setConfirmText("Đồng ý")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                        if (DriverProfileScreen.instance != null && AppDelegate.CurrentActivity instanceof DriverProfileScreen) {

                                            DriverProfileScreen.instance.invalidateOrder(new Completion() {
                                                @Override
                                                public void onCompleted() {
                                                    DriverProfileScreen.instance.onBackPressed();
                                                }
                                            });

                                        }
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                })
                                .show();


                    }
                }

            }

        }else if(this.CurrentOrder().IsOnTheWay()){

            strStatus = "Đang trong hành trình. ";

        }else if(this.CurrentOrder().IsVoidedByDriver() && this.CurrentOrder().IsFinishedNotYetPaid()){

            strStatus = "Tài xế đã huỷ hành trình. Chưa thanh toán.";

        }else if(this.CurrentOrder().IsVoidedByUser()  && this.CurrentOrder().IsFinishedNotYetPaid()){

            strStatus = "Bạn đã huỷ hành trình. Chưa thanh toán.";

        }else if(this.CurrentOrder().IsFinishedNotYetPaid()){

            strStatus = "Đã đến nơi. Chưa thanh toán.";

        }else if(this.CurrentOrder().IsFinishedAndPaid()){

            strStatus = "Đã thanh toán";

            if(this.CurrentOrder().IsVoidedByDriver()){

                strStatus = "Tài xế huỷ hành trình. Đã thanh toán.";

            }else if(this.CurrentOrder().IsVoidedByUser()){

                strStatus = "Bạn đã huỷ hành trình. Đã thanh toán.";

            }

        }

        this.lblStatus.setText(strStatus.toUpperCase());

        if(listener !=null)
            listener.onCompleted();
    }

    public void invalidatePickupDropLocation(final Boolean isFirstTime,final Completion listener) {


        String strAddress = "";
        if (this.CurrentOrder() != null && this.CurrentOrder().OrderPickupLoc != null && this.CurrentOrder().OrderPickupPlace != null) {
            strAddress =  this.CurrentOrder().OrderPickupPlace.substring(0,  this.CurrentOrder().OrderPickupPlace.lastIndexOf(", "));
        } else if (this.CurrentOrder() != null && this.CurrentOrder().ActPickupLoc != null && this.CurrentOrder().ActPickupPlace != null) {
            strAddress =  this.CurrentOrder().ActPickupPlace.substring(0,  this.CurrentOrder().ActPickupPlace.lastIndexOf(", "));
        }
        this.lblPickupLocation.setText(strAddress);

        strAddress = "";
        if (this.CurrentOrder() != null && this.CurrentOrder().OrderDropLoc != null && this.CurrentOrder().OrderDropPlace != null) {
            strAddress =  this.CurrentOrder().OrderDropPlace.substring(0,  this.CurrentOrder().OrderDropPlace.lastIndexOf(", "));
        } else if (this.CurrentOrder() != null && this.CurrentOrder().ActDropLoc != null && this.CurrentOrder().ActDropPlace != null) {
            strAddress =  this.CurrentOrder().ActDropPlace.substring(0,  this.CurrentOrder().ActDropPlace.lastIndexOf(", "));

        }
        this.lblDropLocation.setText(strAddress);


        this.btnPickupIcon.setVisibility(!this.isCollapsed ? VISIBLE : GONE);
        this.btnDropIcon.setVisibility(!this.isCollapsed ? VISIBLE : GONE);
        this.lblPickupLocation.setVisibility(!this.isCollapsed ? VISIBLE : GONE);
        this.lblDropLocation.setVisibility(!this.isCollapsed ? VISIBLE : GONE);
        this.line1.setVisibility(!this.isCollapsed ? VISIBLE : GONE);
        this.line2.setVisibility(!this.isCollapsed ? VISIBLE : GONE);


        if(listener !=null)
            listener.onCompleted();

    }

    public void invalidateMoreInfo(final Boolean isFirstTime,final Completion listener) {

        if(this.CurrentOrder() != null && (this.CurrentOrder().IsWaitingDriver() || (CurrentOrder().isTripMateMember() && (CurrentOrder().Status.equals(OrderStatus.DriverPicking) || CurrentOrder().Status.equals(OrderStatus.DriverAccepted))))){

            this.lblMoreInfo.setVisibility ( (this.CurrentOrder().OrderDropLoc == null ) || ( this.CurrentOrder().OrderPickupLoc == null ) ? GONE : VISIBLE);

            if(this.lblMoreInfo.getVisibility() == VISIBLE){

                String strPlanning = null;

                if(this.CurrentOrder().OrderDistance > 0 && this.CurrentOrder().OrderDuration > 0) {

                    String strDistance = String.format("%.1f Km", this.CurrentOrder().OrderDistance / 1000);
                    long hours = (long) (this.CurrentOrder().OrderDuration / 3600);
                    long minutes = (long) (Math.round((double)(this.CurrentOrder().OrderDuration % 3600) / 60));

                    String strDuration = "";
                    if (hours > 0) {
                        strDuration = String.format("%d giờ %d phút", hours, minutes);
                    } else {
                        strDuration = String.format("%d phút", minutes);

                    }
                    strPlanning = strDistance + " - " + strDuration;
                }


                if(strPlanning != null){
                    strPlanning = "Dự kiến : " + strPlanning;
                }else{
                    strPlanning = this.CurrentOrder().CompanyName.toUpperCase() + " hân hạnh phục vụ quý khách.";
                }
                this.lblMoreInfo.setText(strPlanning);

            }

        }else if(this.CurrentOrder() != null &&  this.CurrentOrder().IsStopped()){


            this.lblMoreInfo.setVisibility(VISIBLE);

            String strActual = null;

            if(this.CurrentOrder().ActPickupLoc != null && this.CurrentOrder().ActDropLoc != null){

                Location actPickupLoc = this.CurrentOrder().ActPickupLoc.getLocation();
                Location actDropLoc = this.CurrentOrder().ActDropLoc.getLocation();
                Double distance = this.CurrentOrder().ActDistance != null ? this.CurrentOrder().ActDistance : (double)actDropLoc.distanceTo(actPickupLoc);

                String strDistance = String.format("%.1f Km", distance/1000 );
                long timeInterval = this.CurrentOrder().ActDropTime.getTime() - this.CurrentOrder().ActPickupTime.getTime();
                long hours =  (long)(timeInterval / (1000 * 60 * 60));
                long minutes =  (long)(Math.round((double)(timeInterval % (1000 * 60 * 60 )) / (1000 * 60)));

                String strDuration = "";
                if( hours > 0){
                    strDuration =  String.format("%d giờ %d phút", hours, minutes );
                }else{
                    strDuration =  String.format("%d phút", minutes );

                }
                strActual = strDistance + " - " + strDuration;

            }


            if(strActual != null){
                strActual = "Thực tế : " + strActual;
            }else{
                strActual = "";
            }
            this.lblMoreInfo.setText(strActual);


        }else if(this.CurrentOrder() != null &&  this.CurrentOrder().IsOnTheWay()){

            this.lblMoreInfo.setText("");

        }else{

            this.lblMoreInfo.setText("");
        }


        this.lblMoreInfo.setVisibility( (!this.isCollapsed && this.lblMoreInfo.getText().toString().isEmpty() == false) ? VISIBLE : GONE);
        lblMoreInfoIcon.setVisibility(lblMoreInfo.getVisibility());
        this.line3.setVisibility(lblMoreInfo.getVisibility());

        if(listener !=null)
            listener.onCompleted();

    }

    public void invalidateButtonArea(final Boolean isFirstTime,final Completion listener) {

        Boolean isShow= false;
        if(CurrentOrder() == null)
            isShow = false;

        else if(CurrentOrder().IsMateHost == 1 || (CurrentOrder().IsMateHost == 0 && CurrentOrder().isTripMateMember() == false)) {

            isShow = (CurrentOrder().IsWaitingDriver() || CurrentOrder().IsMonitoring());

        }else if(CurrentOrder().isTripMateMember()){
            isShow = CurrentOrder().Status.equals(OrderStatus.DriverAccepted) || CurrentOrder().Status.equals(OrderStatus.DriverPicking)
                    || CurrentOrder().Status.equals(OrderStatus.VoidedAfPickupByUser) || CurrentOrder().Status.equals(OrderStatus.VoidedAfPickupByDriver)
                    || CurrentOrder().Status.equals(OrderStatus.Finished);
        }


        this.btnVoid.setVisibility( (!isCollapsed && isShow) ? VISIBLE: GONE);


        if(listener !=null)
            listener.onCompleted();

    }

    public void invalidateTripPrice(final Boolean isFirstTime, final Completion listener) {


        Double amount = 0.0;

        if(this.CurrentOrder().IsMateHost == 1){

            if(this.CurrentOrder().IsStopped())
                amount = this.CurrentOrder().ActPrice;
            else
                amount = this.CurrentOrder().HostOrderPrice;

         }else if(this.CurrentOrder().isTripMateMember()){

            if(this.CurrentOrder().IsStopped())
                amount = this.CurrentOrder().MateActPrice;
            else
                amount = this.CurrentOrder().MateOrderPrice;

        }else{

            if(this.CurrentOrder().IsStopped())
                amount = this.CurrentOrder().ActPrice;
            else
                amount = this.CurrentOrder().OrderPrice;

        }

        this.lblCurrentPrice.setText((amount >= 0) ?  RegionalHelper.toCurrency(amount, this.CurrentOrder().Currency) :  this.lblCurrentPrice.getText());

        this.lblCurrentPrice.setVisibility(VISIBLE);

        if(listener !=null)
            listener.onCompleted();

    }

    public void show(Boolean isShow , final Completion listener){

        this.OrderMonitorArea.setVisibility(isShow ? VISIBLE : GONE);
        this.pnlOrderArea.setVisibility(isShow ? VISIBLE : GONE);

        if(listener != null )
            listener.onCompleted();
    }

}