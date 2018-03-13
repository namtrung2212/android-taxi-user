package com.sconnecting.userapp.ui.taxi.order.creation.custom.tripmate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.GetListObjectsListener;
import com.sconnecting.userapp.base.listener.GetOneListener;
import com.sconnecting.userapp.base.listener.PostListener;
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
import com.sconnecting.userapp.base.ui.NTSpinner;

import org.parceler.Parcels;

import java.util.List;

import static android.view.Gravity.CENTER;
import static android.view.View.TEXT_ALIGNMENT_CENTER;

/**
 * Created by TrungDao on 10/11/16.
 */

public class TripMateHostInfoByHostView extends Fragment {

    View view;
    OrderScreen parentScreen;

    NTSpinner cbMemberQty;

    TextView lblPickupDate;
    TextView lblPickupTime;

    TextView lblVehicleType;
    TextView lblQualityType;

    Button btnOpenHostOrder;
    Button btnReadyHostOrder;
    TextView lblDescReadyHostOrder;

    TextView btnMateStatus;
    TextView lblDescMateStatus;

    TextView lblMateTotalMembers;
    TextView lblMateRemainMembers;
    TextView lblOrderPrice;
    View pnlHostOrderPrice;
    TextView lblHostOrderPrice;
    View pnlMateOrderPrice;
    TextView lblMateOrderPrice;
    View pnlBenifitRatio;
    TextView lblBenifitRatio;
    View pnlDistanceIncreaseRatio;
    TextView lblDistanceIncreaseRatio;


    private static final String[] MEMBER_QUANTITIES = new String[] {"1","2","3","4","5","6","7","8","9","10"
    };

    private static final String[] PICKUP_TIMES = new String[] {"Now","Later"
    };


    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }


    public TripMateHostInfoByHostView() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        parentScreen = (OrderScreen) context;
        parentScreen.mCustomOrderView.mTripMateConfigView.mTripMateHostInfoByHostView = this;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        try {
            view = inflater.inflate(R.layout.taxi_order_creation_custom_tripmate_hostinfo_byhost, container, false);
        } catch (InflateException e) {
        }

        initControls(null);

        return view;
    }

    public void initControls(final Completion listener) {


        btnMateStatus = (TextView) view.findViewById(R.id.btnMateStatus);
        lblDescMateStatus = (TextView) view.findViewById(R.id.lblDescMateStatus);

        cbMemberQty = (NTSpinner) view.findViewById(R.id.cbMemberQty);
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.memberquantities, R.layout.dropdown_item);
        cbMemberQty.setAdapter(arrayAdapter);
        cbMemberQty.setHideUnderline(true);
        cbMemberQty.setBackgroundResource(R.drawable.textedit_blue);
        cbMemberQty.setDropDownHeight(490);
        cbMemberQty.setDropDownBackgroundDrawable(new ColorDrawable(0xFFE9E9E9));
        cbMemberQty.setTextColor(Color.WHITE);
        cbMemberQty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AnimationHelper.animateButton(view);
                cbMemberQty.dismissDropDown();

                CurrentOrder().MembersQty = Integer.valueOf(MEMBER_QUANTITIES[position]);

                new TravelOrderController().update(CurrentOrder(),"updateOrder", new PostListener() {
                    @Override
                    public void onCompleted(Boolean success, BaseModel item) {

                        if(success && item!= null)
                            SCONNECTING.orderManager.currentOrder = (TravelOrder)item;

                        parentScreen.invalidateOrderCreation(false,null);
                    }
                });


            }
        });

        lblPickupDate = (TextView) view.findViewById(R.id.lblPickupDate);
        lblPickupTime = (TextView) view.findViewById(R.id.lblPickupTime);

        lblVehicleType = (TextView) view.findViewById(R.id.lblVehicleType);
        lblQualityType = (TextView) view.findViewById(R.id.lblQualityType);

        btnOpenHostOrder = (Button) view.findViewById(R.id.btnOpenHostOrder);
        btnOpenHostOrder.setGravity(CENTER);
        btnOpenHostOrder.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        btnOpenHostOrder.setText("<<   CHI TIẾT   >>");
        AnimationHelper.setOnClick(btnOpenHostOrder, new Completion() {
            @Override
            public void onCompleted() {

                Intent intent = new Intent(view.getContext(), HostScreen.class);

                Parcelable wrappedCurrentOrder = Parcels.wrap(CurrentOrder());
                intent.putExtra("HostOrder",wrappedCurrentOrder);

                ((Activity)view.getContext()).startActivity(intent);
                ((Activity)view.getContext()).overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);

            }
        });
        btnReadyHostOrder = (Button) view.findViewById(R.id.btnReadyHostOrder);
        btnReadyHostOrder.setGravity(CENTER);
        btnReadyHostOrder.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        btnReadyHostOrder.setText("CHỐT NHÓM");
        AnimationHelper.setOnClick(btnReadyHostOrder, new Completion() {
            @Override
            public void onCompleted() {

                TravelOrderController.HostCloseTripMate(CurrentOrder().getId(), new GetOneListener() {
                    @Override
                    public void onGetOne(Boolean success, BaseModel item) {

                        if (success && item != null)
                            SCONNECTING.orderManager.currentOrder = (TravelOrder) item;

                        parentScreen.invalidateOrderCreation(false, null);
                    }
                });

            }
        });

        lblMateTotalMembers = (TextView)view.findViewById(R.id.lblMateTotalMembers);
        lblMateRemainMembers = (TextView)view.findViewById(R.id.lblMateRemainMembers);
        lblOrderPrice = (TextView)view.findViewById(R.id.lblOrderPrice);
        lblHostOrderPrice = (TextView)view.findViewById(R.id.lblHostOrderPrice);
        lblMateOrderPrice = (TextView)view.findViewById(R.id.lblMateOrderPrice);
        lblBenifitRatio = (TextView)view.findViewById(R.id.lblBenifitRatio);
        lblDistanceIncreaseRatio = (TextView)view.findViewById(R.id.lblDistanceIncreaseRatio);
        pnlHostOrderPrice = view.findViewById(R.id.pnlHostOrderPrice);
        pnlMateOrderPrice = view.findViewById(R.id.pnlMateOrderPrice);
        pnlBenifitRatio = view.findViewById(R.id.pnlBenifitRatio);
        pnlDistanceIncreaseRatio = view.findViewById(R.id.pnlDistanceIncreaseRatio);
        lblDescReadyHostOrder  = (TextView)view.findViewById(R.id.lblDescReadyHostOrder);


        if(listener != null)
            listener.onCompleted();

    }
    public void invalidate(final Completion listener){

        if(view == null){

            if(listener != null)
                listener.onCompleted();
            return;
        }

        Boolean isShow = (CurrentOrder().IsMateHost == 1 && CurrentOrder().MateHostOrder == null );

        invalidateUI(isShow,listener);
    }

    void invalidateUI(final Boolean isShow,final Completion listener){

        view.setVisibility( isShow ? View.VISIBLE : View.GONE);

        if(!isShow){

            if(listener != null)
                listener.onCompleted();
            return;
        }


        for (int i = 0; i < MEMBER_QUANTITIES.length; i++) {

            String strValue = MEMBER_QUANTITIES[i];
            if(strValue == null && CurrentOrder().MembersQty == null){

                CurrentOrder().MembersQty = 1;
                cbMemberQty.setText(cbMemberQty.getAdapter().getItem(0).toString(),false);
                break;

            }else if(strValue != null && CurrentOrder().MembersQty != null){
                if(strValue.equals(CurrentOrder().MembersQty.toString())){

                    if(cbMemberQty.getAdapter().getCount() > i) {
                        cbMemberQty.setText(cbMemberQty.getAdapter().getItem(i).toString(),false);
                        break;
                    }
                }
            }

        }

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

                    if(CurrentOrder().OrderVehicleType == null){

                        lblVehicleType.setText(getResources().getString(R.string.all));

                    }else{

                        String strLocaleName =  VehicleTypeController.GetLocaleNameFromType(CurrentOrder().OrderVehicleType);
                        lblVehicleType.setText(strLocaleName);
                    }

                }

            }
        });


        new QualityServiceTypeController().GetQualityServiceLocaleTypes(new GetListObjectsListener() {
            @Override
            public void onGetItems(Boolean success, List list) {

                if(success){

                    if(CurrentOrder().OrderQuality == null){

                        lblQualityType.setText(getResources().getString(R.string.all));

                    }else{

                        String strLocaleName =  QualityServiceTypeController.GetLocaleNameFromType(CurrentOrder().OrderQuality);
                        lblQualityType.setText(strLocaleName);
                    }
                }

            }
        });


        int iTotalMembers = CurrentOrder().MembersQty;
        if(CurrentOrder().MateSubMembers != null)
            iTotalMembers += CurrentOrder().MateSubMembers;
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
        else if(iRemainMinMembers >0 && iRemainMaxMembers > 0){

            if(iRemainMinMembers != iRemainMaxMembers)
                lblMateRemainMembers.setText(String.format("từ %d đến %d người", iRemainMinMembers, iRemainMaxMembers));
            else
                lblMateRemainMembers.setText(String.format("%d người", iRemainMinMembers));
        }

        lblOrderPrice.setText((CurrentOrder().OrderPrice != null && CurrentOrder().OrderPrice >= 0) ? RegionalHelper.toCurrency(CurrentOrder().OrderPrice,CurrentOrder().Currency) : "");

        lblHostOrderPrice.setVisibility(CurrentOrder().MateSubMembers != null && CurrentOrder().MateSubMembers > 0 ? View.VISIBLE : View.GONE);
        pnlHostOrderPrice.setVisibility(lblHostOrderPrice.getVisibility());
        lblHostOrderPrice.setText(( CurrentOrder().HostOrderPrice != null && CurrentOrder().HostOrderPrice >= 0) ? RegionalHelper.toCurrency(CurrentOrder().HostOrderPrice,CurrentOrder().Currency) : "");

        lblMateOrderPrice.setVisibility(CurrentOrder().MateSubMembers != null && CurrentOrder().MateSubMembers > 0 ? View.VISIBLE : View.GONE);
        pnlMateOrderPrice.setVisibility(lblMateOrderPrice.getVisibility());
        lblMateOrderPrice.setText((CurrentOrder().MateOrderPrice != null && CurrentOrder().MateOrderPrice >= 0) ? RegionalHelper.toCurrency(CurrentOrder().MateOrderPrice,CurrentOrder().Currency) : "");

        double dBenifitRatio = 0.0;
        if(CurrentOrder().OrderPrice != null && CurrentOrder().OrderPrice > 0 &&
                CurrentOrder().MateOrderPrice != null && CurrentOrder().MateOrderPrice >= 0){
            dBenifitRatio = (CurrentOrder().OrderPrice - CurrentOrder().MateOrderPrice)/CurrentOrder().OrderPrice;
        }

        dBenifitRatio = dBenifitRatio * 100;
        lblBenifitRatio.setVisibility(CurrentOrder().MateSubMembers != null && CurrentOrder().MateSubMembers > 0 ? View.VISIBLE : View.GONE);
        pnlBenifitRatio.setVisibility(lblBenifitRatio.getVisibility());
        lblBenifitRatio.setText(String.format("%d",(long)dBenifitRatio) + " %");


        double dDistanceIncrease = 0.0;
        if(CurrentOrder().OrderDistance != null && CurrentOrder().HostOrderDistance != null)
            dDistanceIncrease = (CurrentOrder().HostOrderDistance - CurrentOrder().OrderDistance)/CurrentOrder().OrderDistance;

        dDistanceIncrease = dDistanceIncrease * 100;
        lblDistanceIncreaseRatio.setVisibility(CurrentOrder().MateSubMembers != null && CurrentOrder().MateSubMembers > 0 ? View.VISIBLE : View.GONE);
        pnlDistanceIncreaseRatio.setVisibility(lblDistanceIncreaseRatio.getVisibility());
        lblDistanceIncreaseRatio.setText(String.format("%d",(long)dDistanceIncrease)  + " %");

        Boolean isShowReadyBtn = true;
        if(CurrentOrder().MateStatus != null && CurrentOrder().MateStatus.equals(MateStatus.Closed))
            isShowReadyBtn = false;

        if(CurrentOrder().MateSubMembers < CurrentOrder().MinSubMembers)
            isShowReadyBtn = false;

        btnReadyHostOrder.setVisibility(isShowReadyBtn ? View.VISIBLE : View.GONE );
        lblDescReadyHostOrder.setVisibility(btnReadyHostOrder.getVisibility());


        if(CurrentOrder().MateStatus!= null && CurrentOrder().MateStatus.equals(MateStatus.Closed)) {
            btnMateStatus.setText("ĐÃ CHỐT THÀNH VIÊN");
            lblDescMateStatus.setText("Bạn có thể gọi xe ĐI NGAY hoặc ĐẶT TRƯỚC để tài xế quét thấy.");
            lblDescMateStatus.setVisibility(View.VISIBLE);
        }else{

            lblDescMateStatus.setVisibility(View.GONE);
            if(iRemainMinMembers == 0 && iRemainMaxMembers == 0)
                btnMateStatus.setText("ĐÃ ĐỦ NGƯỜI ");

            else if(iRemainMinMembers == 0 && iRemainMaxMembers > 0)
                btnMateStatus.setText(String.format("CÓ THỂ THÊM TỐI ĐA %d", iRemainMaxMembers));
            else
                btnMateStatus.setText("");
        }



        if(listener != null)
            listener.onCompleted();
    }


}
