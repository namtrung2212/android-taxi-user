package com.sconnecting.userapp.ui.taxi.tripmate.host.chatting;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.AnimationHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.base.listener.GetBoolValueListener;
import com.sconnecting.userapp.base.listener.GetDoubleValueListener;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.data.models.TravelOrder;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;
import com.sconnecting.userapp.ui.taxi.tripmate.host.HostScreen;
import com.sconnecting.userapp.ui.taxi.tripmate.host.requestingmembers.RequestingMembersTable;

/**
 * Created by TrungDao on 8/26/16.
 */

public class TripMateChattingView  extends Fragment {


    public HostScreen parentScreen;
    View view;

    public TripMateChattingTable chattingTable;
    public Button btnSend;
    public TextView txtMessage;

    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }


    public TripMateChattingView() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        parentScreen = (HostScreen) context;
        parentScreen.mTripMateChattingView = this;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        try {
            view = inflater.inflate(R.layout.taxi_order_tripmate_host_chatting, container, false);
        } catch (InflateException e) {
        }

        initControls(new Completion() {
            @Override
            public void onCompleted() {
                invalidate(null);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onResume(){
        super.onResume();

        invalidate(null);

    }

    public void initControls(final Completion listener) {


        chattingTable = new TripMateChattingTable((RecyclerView) view.findViewById(R.id.chattingTable),(SwipeRefreshLayout) view.findViewById(R.id.refreshControl));

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


        parentScreen.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        txtMessage = (TextView) view.findViewById(R.id.txtMessage);
        txtMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                parentScreen.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
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
                parentScreen.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
                btnSend.setVisibility(txtMessage.getText().toString().length() > 0 ? View.VISIBLE : View.GONE);

            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                AnimationHelper.hideKeyBoard(v);

                return false;
            }
        });


        if(listener != null)
            listener.onCompleted();

    }


    public void invalidate(final Completion listener){

        if(view == null){

            if(listener != null)
                listener.onCompleted();
            return;
        }

        invalidateUI(parentScreen.isShowChatting,listener);

    }

    public void invalidateUI( final Boolean isShow, final Completion listener){

        view.setVisibility(isShow? View.VISIBLE : View.GONE);

        if(!isShow){

            if(listener != null)
                listener.onCompleted();
            return;
        }

        chattingTable.refreshDataList(new Completion() {
            @Override
            public void onCompleted() {

                view.invalidate();
                if(listener != null)
                    listener.onCompleted();

            }
        });

    }


    public void setReadAll(){

        /*
        TravelOrderController.SetAllMessageToViewedByUser(CurrentOrder().id,null);

        this.parent.mMonitoringView.driverProfileView.invalidateMessageNo(false,0, new GetDoubleValueListener() {
            @Override
            public void onCompleted(Boolean success, Double number) {

                parent.mMonitoringView.driverProfileView.invalidateLastMessage(false,"", null);
            }
        });
*/

    }


}
