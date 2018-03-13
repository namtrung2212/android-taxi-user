package com.sconnecting.userapp.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;

import com.sconnecting.userapp.AppDelegate;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.GetBoolValueListener;
import com.sconnecting.userapp.base.listener.GetOneListener;
import com.sconnecting.userapp.data.entity.BaseController;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;
import com.sconnecting.userapp.data.models.OrderStatus;
import com.sconnecting.userapp.data.models.TravelOrder;

import org.parceler.Parcels;

/**
 * Created by TrungDao on 8/2/16.
 */

public class OrderManager {

    public TravelOrder currentOrder;
    public TravelOrder hostOrder;

    Handler autoInvalidateTimer;

    public OrderEventHandler eventHandler = new OrderEventHandler(this);
    public OrderActionHandler actionHandler = new OrderActionHandler(this);
    public OrderChattingHandler chattingHandler = new OrderChattingHandler(this);

    public OrderManager(){

        currentOrder = OrderManager.initNewOrder();
    }


    public OrderManager(TravelOrder order){

        currentOrder = order;

    }

    public void Start(final Completion listener) {

        connectToNotificationServer(listener);

    }



    public void connectToNotificationServer(final Completion listener){

        SCONNECTING.notificationHelper.taxiSocket.connect(eventHandler, new Completion() {
            @Override
            public void onCompleted() {

                SCONNECTING.notificationHelper.chatSocket.connect(chattingHandler, new Completion() {
                    @Override
                    public void onCompleted() {

                        if (Looper.myLooper() != Looper.getMainLooper()) {

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null)
                                        listener.onCompleted();
                                }
                            });

                        }


                    }
                });

            }
        });

    }

    public static TravelOrder initNewOrder(){

        TravelOrder order = new TravelOrder();
        if(SCONNECTING.userManager != null && SCONNECTING.userManager.CurrentUser != null) {
            order.User = SCONNECTING.userManager.CurrentUser.getId();
            order.UserName = SCONNECTING.userManager.CurrentUser.Name;
        }
        order.Status = OrderStatus.Open;
        order.Device = Build.MODEL;
        order.DeviceID = Build.SERIAL;
        return order;
    }

    public void reloadHostOrder(final Completion listener){

        if(currentOrder != null && currentOrder.IsMateHost == 0 && currentOrder.MateHostOrder != null && currentOrder.MateHostOrder.isEmpty() == false){

            new BaseController<>(TravelOrder.class).getById(true, currentOrder.MateHostOrder, new GetOneListener() {
                @Override
                public void onGetOne(Boolean success, BaseModel item) {
                    if(success && item != null)
                        hostOrder = (TravelOrder)item;
                    else
                        hostOrder = null;

                    if(listener != null)
                        listener.onCompleted();

                }
            });

        }else{


            if(listener != null)
                listener.onCompleted();
        }
    }

    public void  invalidate(final Boolean isFirstTime,String orderId,final Boolean updateUI,final Completion listener){

        connectToNotificationServer(null);

        new BaseController<>(TravelOrder.class).getById(true,orderId, new GetOneListener() {

            @Override
            public void onGetOne(Boolean success,BaseModel item) {

                if( success && item != null){

                    currentOrder = (TravelOrder)item;

                    reloadHostOrder(null);

                    if(currentOrder.IsMateHost == 0 && currentOrder.MateHostOrder != null && currentOrder.MateHostOrder.isEmpty() == false){

                        new BaseController<>(TravelOrder.class).getById(true, currentOrder.MateHostOrder, new GetOneListener() {
                            @Override
                            public void onGetOne(Boolean success, BaseModel item) {
                                if(success && item != null)
                                    hostOrder = (TravelOrder)item;
                                else
                                    hostOrder = null;
                            }
                        });

                    }

                    if(updateUI) {
                        invalidateUI(isFirstTime, listener);
                        return;
                    }
                }


                if(listener != null)
                    listener.onCompleted();
            }
        });


    }



    public void invalidate(final Boolean isFirstTime,final Boolean updateUI, final Completion listener){


        if(currentOrder.getId() != null && currentOrder.getId().isEmpty() == false) {

            invalidate(isFirstTime,currentOrder.getId(),updateUI,listener);

        }else{

            connectToNotificationServer(null);

            if(updateUI){
                SCONNECTING.orderScreen.invalidateUI(isFirstTime,listener);
            }else{
                if(listener != null)
                    listener.onCompleted();
            }
        }


    }



    public void invalidateUI(Boolean isFirstTime, final Completion listener){


        SCONNECTING.orderScreen.invalidateUI(isFirstTime, listener);

        startInvalidateTimer(30);

    }



    public void reset(Boolean updateUI, final Completion listener) {

        reset(OrderManager.initNewOrder(), updateUI, new Completion() {
            @Override
            public void onCompleted() {
                SCONNECTING.orderScreen.mapMarkerManager.hideVehicles(null);
                SCONNECTING.orderScreen.mMapView.moveToCurrentLocation((float)14,false);
                if(listener != null)
                    listener.onCompleted();
            }
        });

    }

    public void reset(TravelOrder order, Boolean updateUI, final Completion listener){

        if (AppDelegate.CurrentActivity instanceof OrderScreen && SCONNECTING.orderScreen != null) {

            currentOrder = (TravelOrder)order;

            if(updateUI && order.IsNewOrder())
                SCONNECTING.orderScreen.newPhase = OrderScreen.OrderPhase.ChooseLocation;

            invalidate(false, updateUI,listener);

        } else {

            Intent intent = new Intent(AppDelegate.CurrentActivity, OrderScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            if (order != null) {
                Parcelable wrappedCurrentOrder = Parcels.wrap(order);
                intent.putExtra("CurrentOrder", wrappedCurrentOrder);
            }

            ((Activity) AppDelegate.CurrentActivity).startActivity(intent);
            ((Activity) AppDelegate.CurrentActivity).overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

        }


    }


    void startInvalidateTimer(final int seconds){

        if(autoInvalidateTimer == null){
            autoInvalidateTimer = new Handler(Looper.getMainLooper());
            autoInvalidateTimer.postDelayed(new Runnable() {
                @Override
                public void run() {

                    autoInvalidateTimer.removeCallbacks(this);

                    final Runnable that = this;

                    autoInvalidate(new GetBoolValueListener() {
                        @Override
                        public void onCompleted(Boolean success,Boolean value) {

                            autoInvalidateTimer.postDelayed(that, 1000 * seconds);

                        }
                    });
                }
            }, 1000 * seconds);
        }


    }

    public void autoInvalidate(final GetBoolValueListener listener) {

        invalidate(false, true, new Completion() {
            @Override
            public void onCompleted() {

                if (listener != null)
                    listener.onCompleted(true, true);

            }
        });

    }

    public void tryToOpenLastOpenningOrder(final GetOneListener listener) {

        if (SCONNECTING.userManager.CurrentUser != null) {

            TravelOrderController.GetLastOpenningOrderByUser(SCONNECTING.userManager.CurrentUser.getId(), new GetOneListener() {
                @Override
                public void onGetOne(Boolean success, BaseModel item) {

                    if (item != null)
                        SCONNECTING.orderManager.reset((TravelOrder) item, true, null);
                    else
                        SCONNECTING.orderManager.reset(new TravelOrder(),true, null);


                    if (listener != null)
                        listener.onGetOne(success, item);
                }
            });

        } else {
            if (listener != null)
                listener.onGetOne(false, null);
        }

    }





}
