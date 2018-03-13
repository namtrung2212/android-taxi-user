package com.sconnecting.userapp.ui.taxi.order.creation.custom;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;
import com.sconnecting.userapp.ui.taxi.order.creation.custom.tripmate.TripMateCreateHostView;
import com.sconnecting.userapp.ui.taxi.order.creation.custom.tripmate.TripMateHostInfoByHostView;
import com.sconnecting.userapp.ui.taxi.order.creation.custom.tripmate.TripMateHostInfoByMemberView;
import com.sconnecting.userapp.ui.taxi.order.creation.custom.tripmate.TripMateSearchConfigView;
import com.sconnecting.userapp.data.models.TravelOrder;

/**
 * Created by TrungDao on 10/11/16.
 */


    public class TripMateConfigView extends Fragment {


        View view;
        OrderScreen parentScreen;
        SwipeRefreshLayout reqRefreshControl ;

        public TripMateSearchConfigView mTripMateSearchConfigView;
        public TripMateCreateHostView mTripMateCreateHostView;
        public TripMateHostInfoByHostView mTripMateHostInfoByHostView;
        public TripMateHostInfoByMemberView mTripMateHostInfoByMemberView;

        public TravelOrder CurrentOrder() {

            if( SCONNECTING.orderManager == null)
                return null;

            return SCONNECTING.orderManager.currentOrder;

        }


        public TripMateConfigView() {

        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);

            parentScreen = (OrderScreen) context;
            parentScreen.mCustomOrderView.mTripMateConfigView = this;

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            if (view != null) {
                ViewGroup parent = (ViewGroup) view.getParent();
                if (parent != null)
                    parent.removeView(view);
            }

            try {
                view = inflater.inflate(R.layout.taxi_order_creation_custom_tripmate, container, false);
            } catch (InflateException e) {
            }

            initControls(null);

            return view;
        }

        @Override
        public void onResume(){
            super.onResume();

            invalidate(null);

        }

        public void initControls(final Completion listener) {

            reqRefreshControl = (SwipeRefreshLayout) view.findViewById(R.id.reqRefreshControl);

            reqRefreshControl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {


                    SCONNECTING.orderManager.invalidate(false,true,new Completion() {
                        @Override
                        public void onCompleted() {
                        }
                    });
                    if(reqRefreshControl != null)
                        reqRefreshControl.setRefreshing(false);
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

            invalidateUI(parentScreen.mCustomOrderView.isShowTripMateConfig,listener);

        }


        public void invalidateUI( final Boolean isShow, final Completion listener){

            view.setVisibility(isShow? View.VISIBLE : View.GONE);

            if(!isShow){

                if(listener != null)
                    listener.onCompleted();
                return;
            }


            if(mTripMateSearchConfigView != null)
                mTripMateSearchConfigView.invalidate(null);

            if(mTripMateCreateHostView != null)
                mTripMateCreateHostView.invalidate(null);

            if(mTripMateHostInfoByHostView != null)
                mTripMateHostInfoByHostView.invalidate(null);

            if(mTripMateHostInfoByMemberView != null)
                mTripMateHostInfoByMemberView.invalidate(null);

            if(listener != null)
                listener.onCompleted();
        }

    }
