package com.sconnecting.userapp.ui.taxi.order.creation.custom.tripmate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.GetListObjectsListener;
import com.sconnecting.userapp.base.listener.GetOneListener;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.base.AnimationHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.base.RegionalHelper;
import com.sconnecting.userapp.data.controllers.QualityServiceTypeController;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.data.controllers.VehicleTypeController;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;
import com.sconnecting.userapp.ui.taxi.tripmate.host.HostScreen;
import com.sconnecting.userapp.data.models.MateStatus;
import com.sconnecting.userapp.data.models.TravelOrder;

import org.parceler.Parcels;

import java.util.List;

import static android.view.Gravity.CENTER;
import static android.view.View.TEXT_ALIGNMENT_CENTER;

/**
 * Created by TrungDao on 10/22/16.
 */

public class TripMateHostInfoByMemberView  extends Fragment {

    View view;
    OrderScreen parentScreen;
    TravelOrder hostOrder;

    TextView btnMateStatus;
    public TextView lblPickupPlace;
    public TextView lblDropPlace;

    TextView lblPickupDate;
    TextView lblPickupTime;

    TextView lblVehicleType;
    TextView lblQualityType;

    TextView lblMateTotalMembers;
    TextView lblMateRemainMembers;
    TextView lblOrderPrice;
    View pnlMateOrderPrice;
    TextView lblMateOrderPrice;
    View pnlBenifitRatio;
    TextView lblBenifitRatio;
    View pnlDistanceIncreaseRatio;
    TextView lblDistanceIncreaseRatio;


    Button btnOpenHostOrder;
    Button btnLeaveHostOrder;


    private static final String[] MEMBER_QUANTITIES = new String[] {"1","2","3","4","5","6","7","8","9","10"
    };

    private static final String[] PICKUP_TIMES = new String[] {"Now","Later"
    };


    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }


    public TripMateHostInfoByMemberView() {

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        parentScreen = (OrderScreen) context;
        parentScreen.mCustomOrderView.mTripMateConfigView.mTripMateHostInfoByMemberView = this;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        try {
            view = inflater.inflate(R.layout.taxi_order_creation_custom_tripmate_hostinfo_bymember, container, false);
        } catch (InflateException e) {
        }

        initControls(null);

        return view;
    }



    public void initControls(final Completion listener) {


        btnMateStatus = (TextView) view.findViewById(R.id.btnMateStatus);
        lblPickupPlace = (TextView) view.findViewById(R.id.lblPickupPlace);
        lblDropPlace = (TextView) view.findViewById(R.id.lblDropPlace);

        lblPickupDate = (TextView) view.findViewById(R.id.lblPickupDate);
        lblPickupTime = (TextView) view.findViewById(R.id.lblPickupTime);

        lblVehicleType = (TextView) view.findViewById(R.id.lblVehicleType);
        lblQualityType = (TextView) view.findViewById(R.id.lblQualityType);

        lblMateTotalMembers = (TextView)view.findViewById(R.id.lblMateTotalMembers);
        lblMateRemainMembers = (TextView)view.findViewById(R.id.lblMateRemainMembers);
        lblOrderPrice = (TextView)view.findViewById(R.id.lblOrderPrice);
        lblMateOrderPrice = (TextView)view.findViewById(R.id.lblMateOrderPrice);
        lblBenifitRatio = (TextView)view.findViewById(R.id.lblBenifitRatio);
        lblDistanceIncreaseRatio = (TextView)view.findViewById(R.id.lblDistanceIncreaseRatio);
        pnlMateOrderPrice = view.findViewById(R.id.pnlMateOrderPrice);
        pnlBenifitRatio = view.findViewById(R.id.pnlBenifitRatio);
        pnlDistanceIncreaseRatio = view.findViewById(R.id.pnlDistanceIncreaseRatio);

        btnOpenHostOrder = (Button) view.findViewById(R.id.btnOpenHostOrder);
        btnOpenHostOrder.setGravity(CENTER);
        btnOpenHostOrder.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        btnOpenHostOrder.setText("<<   CHI TIẾT   >>");
        AnimationHelper.setOnClick(btnOpenHostOrder, new Completion() {
            @Override
            public void onCompleted() {

                Intent intent = new Intent(view.getContext(), HostScreen.class);

                Parcelable wrappedCurrentOrder = Parcels.wrap(hostOrder);
                intent.putExtra("HostOrder",wrappedCurrentOrder);

                ((Activity)view.getContext()).startActivity(intent);
                ((Activity)view.getContext()).overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);
            }
        });

        btnLeaveHostOrder = (Button) view.findViewById(R.id.btnLeaveHostOrder);
        btnLeaveHostOrder.setGravity(CENTER);
        btnLeaveHostOrder.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        if(CurrentOrder().MateStatus != null && CurrentOrder().MateStatus.equals(MateStatus.Accepted))
            btnLeaveHostOrder.setText("RỜI KHỎI NHÓM");
        else
            btnLeaveHostOrder.setText("HỦY ĐỀ NGHỊ");

        AnimationHelper.setOnClick(btnLeaveHostOrder, new Completion() {
            @Override
            public void onCompleted() {

                TravelOrderController.MemberLeaveFromTripMate(CurrentOrder().id, new GetOneListener() {
                    @Override
                    public void onGetOne(Boolean success, BaseModel item) {

                        if(success && item!= null)
                            SCONNECTING.orderManager.currentOrder = (TravelOrder)item;

                        parentScreen.invalidateOrderCreation(false,null);
                    }
                });

            }
        });

        if(listener != null)
            listener.onCompleted();

    }
    public void invalidate( final Completion listener){

        if(view == null){

            if(listener != null)
                listener.onCompleted();
            return;
        }

        Boolean isShow = CurrentOrder().IsMateHost == 0  && CurrentOrder().MateHostOrder != null;

        if( isShow){

            if(hostOrder == null || hostOrder.id.equals(CurrentOrder().MateHostOrder) == false){
                new TravelOrderController().getById(true, CurrentOrder().MateHostOrder, new GetOneListener() {
                    @Override
                    public void onGetOne(Boolean success, BaseModel item) {
                        if(success) {
                            hostOrder = (TravelOrder) item;

                            invalidateUI(true,listener);
                        }else{
                            if(listener != null)
                                listener.onCompleted();
                        }

                    }
                });
            }else{

                invalidateUI(true,listener);
            }

        }else{
            invalidateUI(false,listener);
        }

    }

    void invalidateUI(final Boolean isShow,final Completion listener){

        view.setVisibility( isShow ? View.VISIBLE : View.GONE);

        if(!isShow || hostOrder == null){

            if(listener != null)
                listener.onCompleted();
            return;
        }

        if(CurrentOrder().MateStatus!= null){

            if(CurrentOrder().MateStatus.equals(MateStatus.Requested))
                btnMateStatus.setText("CHỜ PHẢN HỒI");

            else if(CurrentOrder().MateStatus.equals(MateStatus.Accepted))
                btnMateStatus.setText("ĐÃ ĐƯỢC CHẤP NHẬN");

            else if(CurrentOrder().MateStatus.equals(MateStatus.Rejected))
                btnMateStatus.setText("ĐÃ BỊ TỪ CHỐI");

            else if(CurrentOrder().MateStatus.equals(MateStatus.Closed))
                btnMateStatus.setText("ĐÃ CHỐT THÀNH VIÊN");

            else if(CurrentOrder().MateStatus.equals(MateStatus.VoidedByHost))
                btnMateStatus.setText("NHÓM ĐI CHUNG ĐÃ BỊ HỦY");
            else
                btnMateStatus.setText("");

        }else{

            btnMateStatus.setText("");
        }

        lblPickupPlace.setText(hostOrder.OrderPickupPlace != null ?  hostOrder.OrderPickupPlace : "");
        lblDropPlace.setText( hostOrder.OrderDropPlace != null ?  hostOrder.OrderDropPlace : "");


        if (CurrentOrder().OrderPickupTime == null) {
            lblPickupDate.setText("");
            lblPickupTime.setText("");

        } else {
            lblPickupDate.setText(new java.text.SimpleDateFormat("dd/MM/yyyy").format(CurrentOrder().OrderPickupTime));

            if (CurrentOrder().OrderPickupTime.getHours() <= 12)
                lblPickupTime.setText(String.format("%02d : %02d AM", CurrentOrder().OrderPickupTime.getHours(), CurrentOrder().OrderPickupTime.getMinutes()));
            else
                lblPickupTime.setText(String.format("%02d : %02d PM", CurrentOrder().OrderPickupTime.getHours() - 12, CurrentOrder().OrderPickupTime.getMinutes()));
        }


        new VehicleTypeController().GetVehicleLocaleTypes(new GetListObjectsListener() {
            @Override
            public void onGetItems(Boolean success, List list) {

                if(success){

                    if(hostOrder.OrderVehicleType == null){

                        lblVehicleType.setText(getResources().getString(R.string.all));

                    }else{

                        String strLocaleName =  VehicleTypeController.GetLocaleNameFromType(hostOrder.OrderVehicleType);
                        lblVehicleType.setText(strLocaleName);
                    }

                }

            }
        });


        new QualityServiceTypeController().GetQualityServiceLocaleTypes(new GetListObjectsListener() {
            @Override
            public void onGetItems(Boolean success, List list) {

                if(success){

                    if(hostOrder.OrderQuality == null){

                        lblQualityType.setText(getResources().getString(R.string.all));

                    }else{

                        String strLocaleName =  QualityServiceTypeController.GetLocaleNameFromType(hostOrder.OrderQuality);
                        lblQualityType.setText(strLocaleName);
                    }
                }

            }
        });



        int iTotalMembers = hostOrder.MembersQty;
        if(hostOrder.MateSubMembers != null)
            iTotalMembers += hostOrder.MateSubMembers;
        lblMateTotalMembers.setText(String.format("%d người",iTotalMembers));


        int iRemainMinMembers = CurrentOrder().MinSubMembers;
        if(CurrentOrder().MateSubMembers != null)
            iRemainMinMembers -= CurrentOrder().MateSubMembers;
        if(iRemainMinMembers<0)
            iRemainMinMembers = 0;

        int iRemainMaxMembers = CurrentOrder().MaxSubMembers;
        if(CurrentOrder().MateSubMembers != null)
            iRemainMaxMembers -= CurrentOrder().MateSubMembers;
        if(iRemainMaxMembers<0)
            iRemainMaxMembers = 0;


        if(iRemainMinMembers == 0 && iRemainMaxMembers == 0)
            lblMateRemainMembers.setText("đã đủ người");
        else if(iRemainMinMembers == 0 && iRemainMaxMembers > 0)
            lblMateRemainMembers.setText(String.format("tối đa %d người",iRemainMaxMembers));
        else if(iRemainMinMembers >0 && iRemainMaxMembers > 0) {

            if(iRemainMinMembers != iRemainMaxMembers)
                lblMateRemainMembers.setText(String.format("từ %d đến %d người", iRemainMinMembers, iRemainMaxMembers));
            else
                lblMateRemainMembers.setText(String.format("%d người", iRemainMinMembers));
        }


        lblOrderPrice.setText((CurrentOrder().OrderPrice != null && CurrentOrder().OrderPrice >= 0) ? RegionalHelper.toCurrency(CurrentOrder().OrderPrice,CurrentOrder().Currency) : "");

        boolean isRejected = (CurrentOrder().MateStatus.equals(MateStatus.Rejected));
        boolean isVoidedByHost = (CurrentOrder().MateStatus.equals(MateStatus.VoidedByHost));
        boolean isClosed = (CurrentOrder().MateStatus.equals(MateStatus.Closed));

        lblMateOrderPrice.setVisibility(iRemainMaxMembers > 0 && isRejected == false ? View.VISIBLE : View.GONE);
        pnlMateOrderPrice.setVisibility(lblMateOrderPrice.getVisibility());
        lblMateOrderPrice.setText((CurrentOrder().MateOrderPrice != null && CurrentOrder().MateOrderPrice >= 0) ? RegionalHelper.toCurrency(CurrentOrder().MateOrderPrice,CurrentOrder().Currency) : "");

        double dBenifitRatio = 0.0;
        if(CurrentOrder().OrderPrice != null && CurrentOrder().OrderPrice > 0 &&
                CurrentOrder().MateOrderPrice != null && CurrentOrder().MateOrderPrice >= 0){
            dBenifitRatio = (CurrentOrder().OrderPrice - CurrentOrder().MateOrderPrice)/CurrentOrder().OrderPrice;
        }
        dBenifitRatio = dBenifitRatio * 100;
        lblBenifitRatio.setVisibility(iRemainMaxMembers > 0 && isRejected == false ? View.VISIBLE : View.GONE);
        pnlBenifitRatio.setVisibility(lblBenifitRatio.getVisibility());
        lblBenifitRatio.setText(String.format("%d",(long)dBenifitRatio) + " %");


        double dDistanceIncrease = 0.0;
        if(CurrentOrder().OrderDistance != null && CurrentOrder().MateOrderDistance != null)
            dDistanceIncrease = (CurrentOrder().MateOrderDistance - CurrentOrder().OrderDistance)/CurrentOrder().OrderDistance;
        if(dDistanceIncrease <0)
            dDistanceIncrease = 0.0;

        dDistanceIncrease = dDistanceIncrease * 100;
        lblDistanceIncreaseRatio.setVisibility(iRemainMaxMembers > 0 && isRejected == false ? View.VISIBLE : View.GONE);
        pnlDistanceIncreaseRatio.setVisibility(lblDistanceIncreaseRatio.getVisibility());
        lblDistanceIncreaseRatio.setText(String.format("%d",(long)dDistanceIncrease)  + " %");

        btnLeaveHostOrder.setVisibility( (isRejected || isVoidedByHost || isClosed || ( hostOrder.MateStatus != null && hostOrder.MateStatus.equals(MateStatus.Closed))) ? View.GONE : View.VISIBLE);

        if(listener != null)
            listener.onCompleted();
    }
}
