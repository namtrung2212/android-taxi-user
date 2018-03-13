package com.sconnecting.userapp;

import com.sconnecting.userapp.base.InternetHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.notification.NotificationHelper;
import com.sconnecting.userapp.data.storages.client.ClientCachingConfig;
import com.sconnecting.userapp.data.storages.client.ClientStorage;
import com.sconnecting.userapp.manager.OrderManager;

import com.sconnecting.userapp.location.*;
import com.sconnecting.userapp.manager.UserManager;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;

import com.sconnecting.userapp.data.models.*;

/**
 * Created by TrungDao on 8/1/16.
 */

public class SCONNECTING {

    public static UserManager userManager;
    public static LocationHelper locationHelper;
    public static NotificationHelper notificationHelper;
    public static OrderManager orderManager;

    public static OrderScreen orderScreen;


    public static void Init(final Completion listener){


        userManager = new UserManager();
        locationHelper = new LocationHelper(AppDelegate.getContext());
        notificationHelper = new NotificationHelper();
        orderManager = new OrderManager();
        orderManager.currentOrder = OrderManager.initNewOrder();

        SetupCachingTime(listener);
    }

    public static void Start(final Completion listener){

        if(orderManager == null){
            if(listener != null)
                listener.onCompleted();
            return;
        }
        orderManager.Start(listener);

    }



    public static void SetupCachingTime(final Completion listener){

/*
        ClientCachingConfig.register( "Vehicle",  60 * 24,  10);
        new ClientStorage<>(Vehicle.class).cleanUpIfNeeded();

        ClientCachingConfig.register( "VehicleStatus", 1,  10);
        new ClientStorage<>(VehicleStatus.class).cleanUpIfNeeded();

        ClientCachingConfig.register( "CellStatistic", 5,  10);
        new ClientStorage<>(CellStatistic.class).cleanUpIfNeeded();

        ClientCachingConfig.register( "Company", 60 * 24,  10);
        new ClientStorage<>(Company.class).cleanUpIfNeeded();
*/
        ClientCachingConfig.register( "Driver", 60 * 24,  10);
        new ClientStorage<>(Driver.class).cleanUpIfNeeded();

        /*
        ClientCachingConfig.register( "DriverActivity", 60 * 24,  10);
        new ClientStorage<>(DriverActivity.class).cleanUpIfNeeded();

        ClientCachingConfig.register( "DriverPosHistory", 2,  10);
        new ClientStorage<>(DriverPosHistory.class).cleanUpIfNeeded();

        ClientCachingConfig.register( "DriverSetting", 60 * 24,  10);
        new ClientStorage<>(DriverSetting.class).cleanUpIfNeeded();
*/
        ClientCachingConfig.register( "DriverStatus", 2,  10);
        new ClientStorage<>(DriverStatus.class).cleanUpIfNeeded();
/*
        ClientCachingConfig.register( "ExchangeRate", 60 * 24,  10);
        new ClientStorage<>(ExchangeRate.class).cleanUpIfNeeded();
*/
        ClientCachingConfig.register( "TaxiAveragePrice", 60 * 24 * 10,  30);
        new ClientStorage<>(TaxiAveragePrice.class).cleanUpIfNeeded();
/*
        ClientCachingConfig.register( "TaxiDiscount", 60 * 24,  10);
        new ClientStorage<>(TaxiDiscount.class).cleanUpIfNeeded();
*/
        ClientCachingConfig.register( "TaxiPrice", 60 * 24,  10);
        new ClientStorage<>(TaxiPrice.class).cleanUpIfNeeded();
/*
        ClientCachingConfig.register( "Team", 60 * 24,  10);
        new ClientStorage<>(Team.class).cleanUpIfNeeded();
*/
        /*
        ClientCachingConfig.register( "TravelOrder", 1,  10);
        new ClientStorage<>(TravelOrder.class).cleanUpIfNeeded();
*/
        ClientCachingConfig.register( "User", 60 * 24,  10);
        new ClientStorage<>(User.class).cleanUpIfNeeded();
/*
        ClientCachingConfig.register( "UserActivity", 60 * 24,  10);
        new ClientStorage<>(UserActivity.class).cleanUpIfNeeded();
*/
        ClientCachingConfig.register( "UserPosHistory", 2,  10);
        new ClientStorage<>(UserPosHistory.class).cleanUpIfNeeded();

        ClientCachingConfig.register( "UserSetting", 60 * 24,  10);
        new ClientStorage<>(UserSetting.class).cleanUpIfNeeded();

        ClientCachingConfig.register( "UserStatus", 1,  10);
        new ClientStorage<>(UserStatus.class).cleanUpIfNeeded();
/*
        ClientCachingConfig.register( "WorkingPlan", 60 * 1,  10);
        new ClientStorage<>(WorkingPlan.class).cleanUpIfNeeded();
*/

        if(listener != null)
            listener.onCompleted();
    }





}
