package com.sconnecting.userapp.manager;

import android.os.Build;

import com.google.android.gms.maps.model.LatLng;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.GetOneListener;
import com.sconnecting.userapp.base.listener.PostListener;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.location.LocationHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;
import com.sconnecting.userapp.data.models.BusinessCardBudget;
import com.sconnecting.userapp.data.models.DriverBidding;
import com.sconnecting.userapp.data.models.OrderStatus;
import com.sconnecting.userapp.data.models.TravelOrder;
import com.sconnecting.userapp.data.models.UserPayCard;

public class OrderActionHandler{

    OrderManager manager;

    public OrderActionHandler(OrderManager parent){
        this.manager = parent;
    }



    public void UserCreateOrder(final Completion listener){

        manager.currentOrder.User = (SCONNECTING.userManager.CurrentUser != null) ? SCONNECTING.userManager.CurrentUser.getId() : null;
        manager.currentOrder.UserName = (SCONNECTING.userManager.CurrentUser != null) ? SCONNECTING.userManager.CurrentUser.Name : null;
        manager.currentOrder.OrderLoc = SCONNECTING.locationHelper.getLocationObject();
        manager.currentOrder.Device = Build.MODEL;
        manager.currentOrder.DeviceID = Build.SERIAL;

        new TravelOrderController().create(manager.currentOrder,"UserCreateOrder", new PostListener() {
            @Override
            public void onCompleted(Boolean success,BaseModel item) {

                if(success && item != null) {

                    manager.currentOrder = (TravelOrder) item;

                    SCONNECTING.orderScreen.newPhase = OrderScreen.OrderPhase.CustomOrder;
                }

                if(listener != null )
                    listener.onCompleted();

            }
        });

    }

    public void SetDriverRequestingOrderToOpen(TravelOrder order , final GetOneListener listener){

        if(order.Status.equals(OrderStatus.Requested)|| order.Status.equals(OrderStatus.DriverRejected)){

            TravelOrderController.SetDriverRequestingOrderToOpen(order.getId(), new GetOneListener() {
                @Override
                public void onGetOne(Boolean success, BaseModel item) {

                    if(success && item != null)
                        manager.currentOrder = (TravelOrder)item;

                    if(listener != null)
                        listener.onGetOne(success,manager.currentOrder);
                }
            });


        }else{
            if(listener != null )
                listener.onGetOne(true,order);
        }


    }

    public void UserSendRequestToDriver(final TravelOrder order, final String driverId, final GetOneListener listener){

        TravelOrderController.UserSendRequestToDriver(order.getId(), driverId, new GetOneListener() {
            @Override
            public void onGetOne(Boolean success, BaseModel item) {

                if(success && item != null)
                    manager.currentOrder = (TravelOrder)item;

                if(listener != null)
                    listener.onGetOne(success,item);
            }
        });

    }

    public void UserCancelRequestingToDriver(TravelOrder order, final GetOneListener listener){


        TravelOrderController.UserCancelRequestingToDriver(order.getId(), new GetOneListener() {
            @Override
            public void onGetOne(Boolean success, BaseModel item) {

                if(success && item != null)
                    manager.currentOrder = (TravelOrder)item;

                if(listener != null)
                    listener.onGetOne(success,item);
            }
        });

    }

    public void UserAcceptBidding(final DriverBidding bidding, final GetOneListener listener){

        TravelOrderController.UserAcceptBidding(bidding.getId(), new GetOneListener() {
            @Override
            public void onGetOne(Boolean success, BaseModel item) {

                if(success && item != null)
                    manager.currentOrder = (TravelOrder)item;

                if(listener != null)
                    listener.onGetOne(success,item);
            }
        });

    }

    public void UserCancelAcceptingBidding(final DriverBidding bidding, final GetOneListener listener){

        TravelOrderController.UserCancelAcceptingBidding(bidding.getId(), new GetOneListener() {
            @Override
            public void onGetOne(Boolean success, BaseModel item) {

                if(success && item != null)
                    manager.currentOrder = (TravelOrder)item;

                if(listener != null)
                    listener.onGetOne(success,item);
            }
        });

    }

    public void UserVoidOrder(final TravelOrder orderToVoid , final GetOneListener listener){

        final LatLng currentLocation = LocationHelper.newLatLng(SCONNECTING.locationHelper.getLocation());
        TravelOrderController.UserVoidOrder(orderToVoid.getId(), currentLocation, new GetOneListener() {
            @Override
            public void onGetOne(Boolean success, BaseModel item) {

                if (listener != null)
                    listener.onGetOne(success, item);
            }
        });



    }

    public void ResetWhenVoidedBeforePickup(TravelOrder order, final Completion listener){

        if (order != null && order.Status != null && (order.Status.equals(OrderStatus.VoidedBfPickupByDriver) || order.Status.equals(OrderStatus.VoidedBfPickupByUser))) {


            manager.currentOrder = OrderManager.initNewOrder();
            manager.currentOrder.Device = order.Device;
            manager.currentOrder.DeviceID = order.DeviceID;
            manager.currentOrder.Status = OrderStatus.Open;
            manager.currentOrder.User = order.User;
            manager.currentOrder.UserName = order.UserName;
            manager.currentOrder.OrderLoc = order.OrderLoc;
            manager.currentOrder.OrderDropLoc = order.OrderDropLoc;
            manager.currentOrder.OrderDistance = order.OrderDistance;
            manager.currentOrder.OrderDropPlace = order.OrderDropPlace;
            manager.currentOrder.OrderDropRestrict = order.OrderDropRestrict;
            manager.currentOrder.OrderDuration = order.OrderDuration;
            manager.currentOrder.OrderPickupCountry = order.OrderPickupCountry;
            manager.currentOrder.OrderPickupLoc = order.OrderPickupLoc;
            manager.currentOrder.OrderPickupPlace = order.OrderPickupPlace;
            manager.currentOrder.OrderPickupTime = order.OrderPickupTime;
            manager.currentOrder.OrderPrice = order.OrderPrice;
            manager.currentOrder.OrderQuality = order.OrderQuality;
            manager.currentOrder.OrderVehicleType = order.OrderVehicleType;

            UserCreateOrder(new Completion() {
                @Override
                public void onCompleted() {

                    manager.reset(manager.currentOrder , true,listener);

                }
            });

        }else{

            if(listener != null)
                listener.onCompleted();
        }



    }

    public void UserPayByPersonalCard(TravelOrder orderToPay , final UserPayCard userpaycard , final GetOneListener listener){


        if(orderToPay.IsFinishedNotYetPaid() == false ){

            if(listener != null)
                listener.onGetOne(false,orderToPay);
            return;
        }

        TravelOrderController.UserPayByPersonalCard(orderToPay.getId(), userpaycard.getId(), userpaycard.Currency, new GetOneListener() {
            @Override
            public void onGetOne(Boolean success, BaseModel item) {

                if(listener != null)
                    listener.onGetOne(success,item);
            }
        });


    }

    public void UserPayByBusinessCard(TravelOrder orderToPay , final BusinessCardBudget businessCard , final GetOneListener listener){

        if(orderToPay.IsFinishedNotYetPaid() == false ){

            if(listener != null)
                listener.onGetOne(false,orderToPay);
            return;
        }


        TravelOrderController.UserPayByBusinessCard(orderToPay.getId(), businessCard.Card, businessCard.Currency, new GetOneListener() {
            @Override
            public void onGetOne(Boolean success, BaseModel item) {

                if(listener != null)
                    listener.onGetOne(success,item);
            }
        });

    }

}
