package com.sconnecting.userapp.ui.taxi.order.monitoring;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.AnimationHelper;
import com.sconnecting.userapp.base.listener.GetOneListener;
import com.sconnecting.userapp.data.entity.BaseController;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;
import com.sconnecting.userapp.data.models.Driver;
import com.sconnecting.userapp.data.models.DriverStatus;
import com.sconnecting.userapp.data.models.TravelOrder;
import com.sconnecting.userapp.ui.taxi.tripmate.host.HostScreen;

import org.parceler.Parcels;

/**
 * Created by TrungDao on 8/6/16.
 */

public class MonitoringView extends Fragment {


    OrderScreen screen;
    View view;

    public DriverProfileView driverProfileView;
    public OrderPanelView orderPanelView;



    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }


    public MonitoringView() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        screen = (OrderScreen) context;
        screen.mMonitoringView = this;

        orderPanelView = new OrderPanelView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        try {
            view = inflater.inflate(R.layout.taxi_order_monitoring, container, false);
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

        orderPanelView.initUI(listener);
    }


    public void invalidate(final Boolean isFirstTime,final Completion listener){

        if(this.CurrentOrder().Driver != null && ( this.driverProfileView.driver == null || this.driverProfileView.driver.id == null || this.driverProfileView.driver.id.equals(this.CurrentOrder().Driver) == false)){

           new BaseController<>(Driver.class).getById(false, this.CurrentOrder().Driver, new GetOneListener() {
               @Override
               public void onGetOne(Boolean success,BaseModel item) {

                   if(success)
                       driverProfileView.driver = (Driver)item;

                   new BaseController<>(DriverStatus.class).getOneByStringField(false, "Driver", CurrentOrder().Driver, new GetOneListener() {
                       @Override
                       public void onGetOne(Boolean success,BaseModel item) {

                            if(success)
                                driverProfileView.driverStatus = (DriverStatus) item;

                            invalidateUI(isFirstTime,listener);

                       }
                   });
               }
           });

        }else{

            this.invalidateUI(isFirstTime,listener);

        }



    }

    public void invalidateUI(final Boolean isFirstTime,final Completion listener){

        orderPanelView.invalidate(isFirstTime,new Completion() {
            @Override
            public void onCompleted() {

                driverProfileView.invalidate(isFirstTime,new Completion() {
                    @Override
                    public void onCompleted() {

                        driverProfileView.invalidateAvatar(isFirstTime,listener);
                    }
                });

            }
        });


    }


}
