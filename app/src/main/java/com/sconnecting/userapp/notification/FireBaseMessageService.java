package com.sconnecting.userapp.notification;

import android.content.Context;
import android.os.PowerManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sconnecting.userapp.base.DeviceHelper;

/**
 * Created by TrungDao on 12/2/16.
 */

public class FireBaseMessageService  extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        sendNotification(remoteMessage);
    }



    private void sendNotification(RemoteMessage message) {

        NotificationHelper.ShowNotification(message,getApplicationContext());

        DeviceHelper.WakeUpFromLockScreen();

    }


}
