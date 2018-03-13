package com.sconnecting.userapp.ui.taxi.order.creation.custom.tripmate;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.sconnecting.userapp.AppDelegate;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.GetOneListener;
import com.sconnecting.userapp.base.listener.PostListener;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.base.AnimationHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;
import com.sconnecting.userapp.data.models.MateStatus;
import com.sconnecting.userapp.data.models.TravelOrder;
import com.sconnecting.userapp.base.ui.NTSpinner;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.view.Gravity.CENTER;
import static android.view.View.TEXT_ALIGNMENT_CENTER;

/**
 * Created by TrungDao on 10/11/16.
 */

public class TripMateCreateHostView extends Fragment {

    View view;
    OrderScreen parentScreen;


    NTSpinner cbMinSubMemberQty;
    NTSpinner cbMaxSubMemberQty;
    Button btnCreateHostOrder;

    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }


    private static final String[] MEMBER_QUANTITIES = new String[] {"1","2","3","4","5","6","7","8","9","10"
    };


    public TripMateCreateHostView() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        parentScreen = (OrderScreen) context;
        parentScreen.mCustomOrderView.mTripMateConfigView.mTripMateCreateHostView = this;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        try {
            view = inflater.inflate(R.layout.taxi_order_creation_custom_tripmate_createhost, container, false);
        } catch (InflateException e) {
        }

        initControls(null);

        return view;
    }



    public void initControls(final Completion listener) {


        cbMaxSubMemberQty = (NTSpinner) view.findViewById(R.id.cbMaxSubMemberQty);
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.memberquantities, R.layout.dropdown_item);
        cbMaxSubMemberQty.setAdapter(arrayAdapter);
        cbMaxSubMemberQty.setHideUnderline(true);
        cbMaxSubMemberQty.setBackgroundResource(R.drawable.textedit_blue);
        cbMaxSubMemberQty.setDropDownHeight(490);
        cbMaxSubMemberQty.setDropDownBackgroundDrawable(new ColorDrawable(0xFFE9E9E9));
        cbMaxSubMemberQty.setTextColor(Color.WHITE);
        cbMaxSubMemberQty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AnimationHelper.animateButton(view);
                cbMaxSubMemberQty.dismissDropDown();

                CurrentOrder().MaxSubMembers = Integer.valueOf(MEMBER_QUANTITIES[position]);

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


        cbMinSubMemberQty = (NTSpinner) view.findViewById(R.id.cbMinSubMemberQty);
        arrayAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.memberquantities, R.layout.dropdown_item);
        cbMinSubMemberQty.setAdapter(arrayAdapter);
        cbMinSubMemberQty.setHideUnderline(true);
        cbMinSubMemberQty.setBackgroundResource(R.drawable.textedit_blue);
        cbMinSubMemberQty.setDropDownHeight(490);
        cbMinSubMemberQty.setDropDownBackgroundDrawable(new ColorDrawable(0xFFE9E9E9));
        cbMinSubMemberQty.setTextColor(Color.WHITE);
        cbMinSubMemberQty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AnimationHelper.animateButton(view);
                cbMinSubMemberQty.dismissDropDown();

                CurrentOrder().MinSubMembers = Integer.valueOf(MEMBER_QUANTITIES[position]);

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


        btnCreateHostOrder = (Button) view.findViewById(R.id.btnCreateHostOrder);
        btnCreateHostOrder.setGravity(CENTER);
        btnCreateHostOrder.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        btnCreateHostOrder.setText("TẠO NHÓM MỚI");
        AnimationHelper.setOnClick(btnCreateHostOrder, new Completion() {
            @Override
            public void onCompleted() {

                if(CurrentOrder().MaxSubMembers < CurrentOrder().MinSubMembers){

                    new SweetAlertDialog(AppDelegate.CurrentActivity, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Số người cần thêm không hợp lệ!")
                            .setConfirmText("Đồng ý")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .show();

                    return;
                }

                TravelOrderController.HostCreateTripMate(CurrentOrder().getId(), new GetOneListener() {
                    @Override
                    public void onGetOne(Boolean success, BaseModel item) {

                        if(success && item!= null)
                            SCONNECTING.orderManager.currentOrder = (TravelOrder)item;

                        parentScreen.invalidateOrderCreation(false,null);

                        if(CurrentOrder().IsMateHost == 0){

                            new SweetAlertDialog(AppDelegate.CurrentActivity, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Không thể tạo nhóm !")
                                    .setContentText("Với 3 chuyến hoàn tất gần đây nhất bạn sẽ được phép tạo nhóm mới.")
                                    .setConfirmText("Đồng ý")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                        }
                                    })
                                    .show();
                        }
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


        boolean isRejected = (CurrentOrder().MateStatus != null &&  CurrentOrder().MateStatus.equals(MateStatus.Rejected));
        boolean isVoidedByHost = (CurrentOrder().MateStatus != null &&  CurrentOrder().MateStatus.equals(MateStatus.VoidedByHost));
        boolean isClosed = (CurrentOrder().MateStatus != null &&  CurrentOrder().MateStatus.equals(MateStatus.Closed));

        Boolean isShow = CurrentOrder().IsMateHost == 0 && ( CurrentOrder().MateStatus == null || isRejected || isVoidedByHost );

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
            if(strValue == null && CurrentOrder().MaxSubMembers == null){

                CurrentOrder().MaxSubMembers = 1;
                cbMaxSubMemberQty.setText(cbMaxSubMemberQty.getAdapter().getItem(0).toString(),false);
                break;

            }else if(strValue != null && CurrentOrder().MaxSubMembers != null){
                if(strValue.equals(CurrentOrder().MaxSubMembers.toString())){

                    if(cbMaxSubMemberQty.getAdapter().getCount() > i) {
                        cbMaxSubMemberQty.setText(cbMaxSubMemberQty.getAdapter().getItem(i).toString(),false);
                        break;
                    }
                }
            }

        }

        for (int i = 0; i < MEMBER_QUANTITIES.length; i++) {

            String strValue = MEMBER_QUANTITIES[i];
            if(strValue == null && CurrentOrder().MinSubMembers == null){

                CurrentOrder().MinSubMembers = 1;
                cbMinSubMemberQty.setText(cbMinSubMemberQty.getAdapter().getItem(0).toString(),false);
                break;

            }else if(strValue != null && CurrentOrder().MinSubMembers != null){
                if(strValue.equals(CurrentOrder().MinSubMembers.toString())){

                    if(cbMinSubMemberQty.getAdapter().getCount() > i) {
                        cbMinSubMemberQty.setText(cbMinSubMemberQty.getAdapter().getItem(i).toString(),false);
                        break;
                    }
                }
            }

        }
        if(listener != null)
            listener.onCompleted();
    }

}
