package com.sconnecting.userapp.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;

import com.sconnecting.userapp.AppDelegate;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.DeviceHelper;
import com.sconnecting.userapp.base.listener.GetDoubleValueListener;
import com.sconnecting.userapp.base.listener.GetOneListener;
import com.sconnecting.userapp.base.listener.PostListener;
import com.sconnecting.userapp.data.entity.BaseController;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.data.models.TripMateChatting;
import com.sconnecting.userapp.data.storages.client.ClientStorage;
import com.sconnecting.userapp.data.storages.server.ServerStorage;
import com.sconnecting.userapp.notification.ChatSocketListener;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.data.models.TravelOrderChatting;
import com.sconnecting.userapp.ui.taxi.tripmate.host.HostScreen;

import org.parceler.Parcels;

import java.util.Date;
import java.util.Map;

/**
 * Created by TrungDao on 9/19/16.
 */

public class OrderChattingHandler implements ChatSocketListener {

    OrderManager manager;

    public OrderChattingHandler(OrderManager manager){
        this.manager = manager;
    }


    public void UserChatToDriver(final String content, final GetOneListener listener) {

        TravelOrderChatting obj = new TravelOrderChatting();

        obj.Order = manager.currentOrder.id;
        obj.User = manager.currentOrder.User;
        obj.UserName = manager.currentOrder.UserName;
        obj.Driver = manager.currentOrder.Driver;
        obj.DriverName = manager.currentOrder.DriverName;
        obj.Vehicle = manager.currentOrder.Vehicle;
        obj.VehicleNo = manager.currentOrder.VehicleNo;
        obj.CitizenID = manager.currentOrder.CitizenID;
        // obj.Location = locationManager.currentLocation!.Location
        obj.IsUser = 1;
        obj.IsViewed = 0;
        obj.Content = content;

        obj.createdAt = new Date();
        obj.updatedAt = new Date();

        new BaseController<>(TravelOrderChatting.class).create(obj,"UserChatToDriver", new PostListener() {
            @Override
            public void onCompleted(Boolean success,BaseModel item) {

                if(listener != null)
                    listener.onGetOne(true,item);
            }
        });
    }


    public void UserChatToGroup(final String content, final GetOneListener listener) {

        TripMateChatting obj = new TripMateChatting();

        if( manager.currentOrder.IsMateHost == 1)
            obj.HostOrder = manager.currentOrder.id;
        else
            obj.HostOrder = manager.currentOrder.MateHostOrder;

        obj.UserOrder = manager.currentOrder.id;
        obj.User = manager.currentOrder.User;
        obj.UserName = manager.currentOrder.UserName;
        obj.IsViewedList = null;
        obj.Content = content;

        obj.createdAt = new Date();
        obj.updatedAt = new Date();


        new ServerStorage(TripMateChatting.class).create(obj,"UserChatToGroup", new PostListener() {
            @Override
            public void onCompleted(Boolean success,BaseModel item) {

                if(listener != null)
                    listener.onGetOne(true,item);

            }
        });
    }


    @Override
    public void onChatSocketLogged(final String socketId) {

        if (Looper.myLooper() != Looper.getMainLooper()) {

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onChatSocketLogged(socketId);
                }
            });

        } else {

        }
    }

    @Override
    public void onDriverChatToUser(final Map<String, Object> data) {

        if (Looper.myLooper() != Looper.getMainLooper()) {

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onDriverChatToUser(data);
                }
            });

        } else {

            // let arrData = data[0] as! [String: AnyObject]

            String userId = data.get("UserID").toString();
            String driverId = data.get("DriverID").toString();
            final String orderId = data.get("OrderID").toString();
            final String contentId = data.get("ContentID").toString();
            final String content = data.get("Content").toString();

            if (manager.currentOrder != null && manager.currentOrder.getId() != null && manager.currentOrder.getId().equals(orderId)) {

                DeviceHelper.playDefaultNotificationSound();

                SCONNECTING.orderScreen.mMonitoringView.driverProfileView.increaseMessageNo(1, new GetDoubleValueListener() {
                    @Override
                    public void onCompleted(Boolean success, Double number) {


                    }
                });

                SCONNECTING.orderScreen.mMonitoringView.driverProfileView.invalidateLastMessage(false,content, new Completion() {
                    @Override
                    public void onCompleted() {


                    }
                });

                SCONNECTING.orderScreen.mMonitoringView.driverProfileView.chattingView.chattingTable.addItemFromDriver(contentId,content,new Completion() {
                    @Override
                    public void onCompleted() {

                    }
                });

                //if(SCONNECTING.orderScreen.mMonitoringView.isCollapsedProfile == false) {
                SCONNECTING.orderScreen.mMonitoringView.driverProfileView.chattingView.chattingTable.loadNewData(new Completion() {
                    @Override
                    public void onCompleted() {

                    }
                });
                //}
            }
        }
    }


    @Override
    public void onUserChatToGroup(final Map<String, Object> data) {

        if (Looper.myLooper() != Looper.getMainLooper()) {

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onUserChatToGroup(data);
                }
            });

        } else {


            final String orderId = data.get("OrderID").toString();
            final String contentId = data.get("ContentID").toString();
            final String content = data.get("Content").toString();

            if (manager.currentOrder != null &&
                    ((manager.currentOrder.getId() != null && manager.currentOrder.getId().equals(orderId))
                            || (manager.currentOrder.MateHostOrder != null && manager.currentOrder.MateHostOrder.equals(orderId))) ) {

                if(AppDelegate.CurrentActivity instanceof HostScreen){

                    if(((HostScreen)AppDelegate.CurrentActivity).mTripMateChattingView != null &&
                            ((HostScreen)AppDelegate.CurrentActivity).mTripMateChattingView.chattingTable != null) {
                        ((HostScreen) AppDelegate.CurrentActivity).mTripMateChattingView.chattingTable.loadNewData(new Completion() {
                            @Override
                            public void onCompleted() {

                            }
                        });
                    }
                }else{
                }
            }
        }
    }

}