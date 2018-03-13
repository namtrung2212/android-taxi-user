package com.sconnecting.userapp.ui.taxi.order.payment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;
import com.sconnecting.userapp.data.models.TravelOrder;

/**
 * Created by TrungDao on 8/6/16.
 */


public class ChoosePaymentMethodView  extends Fragment {


    View view;

    OrderScreen parent;


    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }


    public ChoosePaymentMethodView() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        parent = (OrderScreen) context;
        parent.mChoosePaymentMethodView = this;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        try {
            view = inflater.inflate(R.layout.taxi_order_payment, container, false);
        } catch (InflateException e) {
        }

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

        view.setVisibility(View.GONE);

        if(listener != null)
            listener.onCompleted();

    }



    public void invalidate(final Boolean isFirstTime,final Completion listener) {

        if(listener != null)
            listener.onCompleted();
    }
}
