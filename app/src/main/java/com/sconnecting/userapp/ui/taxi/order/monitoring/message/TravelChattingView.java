package com.sconnecting.userapp.ui.taxi.order.monitoring.message;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.GetBoolValueListener;
import com.sconnecting.userapp.base.listener.GetDoubleValueListener;
import com.sconnecting.userapp.base.AnimationHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;
import com.sconnecting.userapp.data.models.TravelOrder;

/**
 * Created by TrungDao on 8/26/16.
 */

public class TravelChattingView {


    public OrderScreen parent;
    public View chattingView;
    public TravelChattingTable chattingTable;
    public Button btnSend;
    public TextView txtMessage;

    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }

    public TravelChattingView(final OrderScreen parent, View view) {

        chattingView= view;
        this.parent = parent;
        chattingTable = new TravelChattingTable((RecyclerView) view.findViewById(R.id.chattingTable),(SwipeRefreshLayout) view.findViewById(R.id.refreshControl));


        btnSend = (Button) view.findViewById(R.id.btnSend);
        AnimationHelper.setOnClick(btnSend, new Completion() {
            @Override
            public void onCompleted() {

                chattingTable.createNewItem(txtMessage.getText().toString(), new Completion() {
                    @Override
                    public void onCompleted() {
                        txtMessage.setText("");
                    }
                });
            }
        });


        parent.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        txtMessage = (TextView) view.findViewById(R.id.txtMessage);
        txtMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                parent.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
               btnSend.setVisibility(txtMessage.getText().toString().length() > 0 ? View.VISIBLE : View.GONE);

                return false;
            }
        });

        txtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                parent.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
                btnSend.setVisibility(txtMessage.getText().toString().length() > 0 ? View.VISIBLE : View.GONE);

            }
        });

        chattingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                AnimationHelper.hideKeyBoard(v);

                return false;
            }
        });




    }

    public void showChattingPanel(Boolean show, final GetBoolValueListener listener){

        if(show){

            if(chattingView.getVisibility() != View.VISIBLE ){

                chattingView.setVisibility(View.VISIBLE);
                if(listener != null)
                    listener.onCompleted(true,true);

            }else{
                if(listener != null)
                    listener.onCompleted(true,false);
            }

        }else{

           // self.chattingInputVIew.txtMessage.endEditing(true)
            if(chattingView.getVisibility() == View.VISIBLE ){

                chattingView.setVisibility(View.GONE);
                if(listener != null)
                    listener.onCompleted(true,true);


            }else{
                if(listener != null)
                    listener.onCompleted(true,false);
            }

        }
    }



    public void invalidate(final Boolean isShow,final GetBoolValueListener listener){


        showChattingPanel(isShow, new GetBoolValueListener() {
            @Override
            public void onCompleted(Boolean success, Boolean changed) {

                if( isShow && changed){

                    chattingTable.refreshObjectList(null);

                }

                if(listener != null)
                    listener.onCompleted(true,changed);
            }
        });


    }


    public void setReadAll(){

        TravelOrderController.SetAllMessageToViewedByUser(CurrentOrder().id,null);

        this.parent.mMonitoringView.driverProfileView.invalidateMessageNo(false,0, new GetDoubleValueListener() {
            @Override
            public void onCompleted(Boolean success, Double number) {

                parent.mMonitoringView.driverProfileView.invalidateLastMessage(false,"", null);
            }
        });


    }


}
