package com.sconnecting.userapp.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.google.firebase.messaging.RemoteMessage;
import com.sconnecting.userapp.AppDelegate;
import com.sconnecting.userapp.AppRootActivity;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.base.RegionalHelper;
import com.sconnecting.userapp.base.listener.GetBoolValueListener;
import com.sconnecting.userapp.base.listener.GetOneListener;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.app.Notification.VISIBILITY_PUBLIC;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.view.View.GONE;

/**
 * Created by TrungDao on 8/7/16.
 */

public class NotificationHelper {


    public TaxiSocket taxiSocket;
    public ChatSocket chatSocket;

    public NotificationHelper(){

        taxiSocket = new TaxiSocket();
        chatSocket = new ChatSocket();
    }

    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    public static final String NOTIFICATION_DATA = "NOTIFICATION_DATA";
    public static final String NOTIFICATION_TYPE = "NOTIFICATION_TYPE";
    public static final String NOTIFICATION_ACTION = "NOTIFICATION_ACTION";


    //----------------------------------------------- NOTIFICATION CREATION ----------------------------------------------

    public static PendingIntent getIntent(int notifyId, HashMap<String,String> data, String notifyAction, Context context) {

        String notifyType = data.get("Type");

        Intent intent = new Intent(context, AppRootActivity.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(NOTIFICATION_ID, notifyId);
        intent.putExtra(NOTIFICATION_DATA, new HashMap<String,String>( data));
        intent.putExtra(NOTIFICATION_TYPE, (CharSequence) notifyType);
        intent.putExtra(NOTIFICATION_ACTION, (CharSequence) notifyAction);
        intent.setAction(String.valueOf(notifyAction));

        PendingIntent dismissIntent = PendingIntent.getActivity(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        return dismissIntent;

    }

    public static PendingIntent getOPENIntent(int notifyId, HashMap<String,String> data, Context context) {

        return getIntent(notifyId,data,NotificationActionType.OPEN,context);

    }

    public static void ShowNotification(RemoteMessage message, Context context) {


        Map<String,String> msgData = message.getData();
        HashMap<String, String> data = (msgData instanceof HashMap) ? (HashMap) msgData : new HashMap<String, String>(msgData);
        String notifyType = data.get("Type");

        String orderId = data.get("OrderID");
        if(orderId != null && orderId.isEmpty() == false) {

            final int notifyId = orderId.hashCode();

            PendingIntent piOpen = NotificationHelper.getOPENIntent(notifyId,data, context);

            if(notifyType!= null && ( notifyType.equals(NotificationType.DriverChatToUser) ||  notifyType.equals(NotificationType.UserChatToGroup))) {

                ShowChatNotificationByOrder(message, context, notifyId, piOpen);

            }else {

                ShowDefaultNotificationByOrder(message, context, notifyId, piOpen);

            }

        }else{

            final int notifyId = new Random().nextInt();
        }
        
    }

    private static void ShowDefaultNotificationByOrder(RemoteMessage message, Context context, int notifyId, PendingIntent piOpen) {

        Map<String,String> msgData = message.getData();
        HashMap<String, String> data = (msgData instanceof HashMap) ? (HashMap) msgData : new HashMap<String, String>(msgData);
        String orderId = data.get("OrderID");
        String notifyType = data.get("Type");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notifyId);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.centerlogo)
                        .setDefaults( Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND)
                        .setPriority(NotificationCompat.PRIORITY_MAX) //must give priority to High, Max which will considered as heads-up notification
                        .setVisibility(VISIBILITY_PUBLIC)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000})
                        .setContentIntent(piOpen)
                        .setAutoCancel(true)
                        .setShowWhen(true)
                        .setOnlyAlertOnce(true);


        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notify_default);
        remoteViews.setImageViewResource(R.id.imgLogo,R.drawable.centerlogo);
        remoteViews.setImageViewResource(R.id.line1,R.drawable.line);
        remoteViews.setImageViewResource(R.id.line2,R.drawable.line);
        remoteViews.setTextColor(R.id.txtTitle, Color.DKGRAY);
        remoteViews.setTextColor(R.id.txtDesc, Color.DKGRAY);
        remoteViews.setTextColor(R.id.txtSentTime, Color.DKGRAY);
        remoteViews.setTextColor(R.id.txtBelowDesc, Color.DKGRAY);

        Notification notification = builder.build();
        notification.bigContentView = remoteViews;
        notification.contentView = remoteViews;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.headsUpContentView = remoteViews;
        }

        String strSentTime = new SimpleDateFormat("h:mm a").format(new Date(message.getSentTime()));
        remoteViews.setTextViewText(R.id.txtSentTime,strSentTime);

        if(notifyType!= null) {

            remoteViews.setTextViewText(R.id.txtDesc,data.get("PickupPlace"));
            remoteViews.setTextViewText(R.id.txtBelowDesc,"ẤN ĐỂ XEM CHI TIẾT");

            String strAmount = data.get("Amount");
            if(strAmount != null) {

                Double amount = Double.valueOf(data.get("Amount"));
                String strAmountInVND = RegionalHelper.toCurrency(amount, "VND");
                remoteViews.setTextViewText(R.id.txtPrice, strAmountInVND);

            }else{

                remoteViews.setTextViewText(R.id.txtPrice, "");
            }

            if (notifyType.equals(NotificationType.DriverAccepted)) {

                remoteViews.setTextViewText(R.id.txtTitle,"TÀI XẾ ĐỒNG Ý PHỤC VỤ ");
                notificationManager.notify(notifyId,notification );
                AutoRemoveNotification(notifyId,1,context);


            }else if (notifyType.equals(NotificationType.DriverRejected)) {

                remoteViews.setTextViewText(R.id.txtTitle,"TÀI XẾ TỪ CHỐI PHỤC VỤ ");
                notificationManager.notify(notifyId,notification );
                AutoRemoveNotification(notifyId,1,context);

            }else if (notifyType.equals(NotificationType.DriverVoidedBfPickup)) {

                remoteViews.setTextViewText(R.id.txtTitle,"TÀI XẾ HỦY ĐÓN");
                notificationManager.notify(notifyId,notification );
                AutoRemoveNotification(notifyId,60*3,context);

            }else if (notifyType.equals(NotificationType.DriverStartPicking)) {

                remoteViews.setTextViewText(R.id.txtTitle,"TÀI KẾ ĐANG ĐẾN ĐÓN");
                notificationManager.notify(notifyId,notification );
                AutoRemoveNotification(notifyId,60*3,context);

            }else if (notifyType.equals(NotificationType.DriverStartedTrip)) {

                remoteViews.setTextViewText(R.id.txtTitle,"BẮT ĐẦU HÀNH TRÌNH");
                remoteViews.setTextViewText(R.id.txtBelowDesc,"Chúc quý khách 1 chuyến đi vui vẻ.");
                notificationManager.notify(notifyId,notification );
                AutoRemoveNotification(notifyId,60*3,context);

            }else if (notifyType.equals(NotificationType.DriverVoidedAfPickup)) {

                remoteViews.setTextViewText(R.id.txtTitle,"TÀI XẾ HỦY HÀNH TRÌNH");
                notificationManager.notify(notifyId,notification );
                AutoRemoveNotification(notifyId,60*3,context);

            }else if (notifyType.equals(NotificationType.DriverFinished)) {

                remoteViews.setTextViewText(R.id.txtTitle,"KẾT THÚC HÀNH TRÌNH");
                notificationManager.notify(notifyId,notification );
                AutoRemoveNotification(notifyId,60*3,context);

            }else if (notifyType.equals(NotificationType.DriverReceivedCash)) {

                remoteViews.setTextViewText(R.id.txtTitle,"TÀI XẾ ĐÃ NHẬN TIỀN MẶT");
                notificationManager.notify(notifyId,notification );
                AutoRemoveNotification(notifyId,60*3,context);


            }else if (notifyType.equals(NotificationType.MateMemberSentRequest)) {

                remoteViews.setTextViewText(R.id.txtTitle,"YÊU CẦU ĐI CHUNG");
                notificationManager.notify(notifyId,notification );
                AutoRemoveNotification(notifyId,60*3,context);


            }else if (notifyType.equals(NotificationType.HostAcceptMateMember)) {

                remoteViews.setTextViewText(R.id.txtTitle,"YÊU CẦU ĐI CHUNG ĐƯỢC CHẤP NHẬN");
                notificationManager.notify(notifyId,notification );
                AutoRemoveNotification(notifyId,60*3,context);


            }else if (notifyType.equals(NotificationType.HostRejectMateMember)) {

                remoteViews.setTextViewText(R.id.txtTitle,"YÊU CẦU ĐI CHUNG BỊ TỪ CHỐI");
                notificationManager.notify(notifyId,notification );
                AutoRemoveNotification(notifyId,60*3,context);


            }else if (notifyType.equals(NotificationType.MemberLeaveFromTripMate)) {

                remoteViews.setTextViewText(R.id.txtTitle,"THÀNH VIÊN RỜI KHỎI NHÓM");
                notificationManager.notify(notifyId,notification );
                AutoRemoveNotification(notifyId,60*3,context);


            }else if (notifyType.equals(NotificationType.HostCloseTripMate)) {

                remoteViews.setTextViewText(R.id.txtTitle,"NHÓM ĐI CHUNG ĐÃ CHỐT");
                notificationManager.notify(notifyId,notification );
                AutoRemoveNotification(notifyId,60*3,context);


            }else if (notifyType.equals(NotificationType.HostVoidTripMate)) {

                remoteViews.setTextViewText(R.id.txtTitle,"NHÓM ĐI CHUNG ĐÃ HỦY");
                notificationManager.notify(notifyId,notification );
                AutoRemoveNotification(notifyId,60*3,context);


            }else if (notifyType.equals(NotificationType.DriverVoidTripMate)) {

                remoteViews.setTextViewText(R.id.txtTitle,"TÀI XẾ HỦY HÀNH TRÌNH");
                notificationManager.notify(notifyId,notification );
                AutoRemoveNotification(notifyId,60*3,context);


            }

        }


    }

    private static void ShowChatNotificationByOrder(RemoteMessage message, Context context, int notifyId, PendingIntent piOpen) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notifyId);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.centerlogo)
                        .setDefaults( Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND)
                        .setPriority(NotificationCompat.PRIORITY_MAX) //must give priority to High, Max which will considered as heads-up notification
                        .setVisibility(VISIBILITY_PUBLIC)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000})
                        .setContentIntent(piOpen)
                        .setAutoCancel(true)
                        .setShowWhen(true)
                        .setOnlyAlertOnce(true);


        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notify_default);
        remoteViews.setImageViewResource(R.id.imgLogo,R.drawable.centerlogo);
        remoteViews.setImageViewResource(R.id.line1,R.drawable.line);
        remoteViews.setImageViewResource(R.id.line2,R.drawable.line);
        remoteViews.setTextColor(R.id.txtTitle, Color.DKGRAY);
        remoteViews.setTextColor(R.id.txtDesc, Color.DKGRAY);
        remoteViews.setTextColor(R.id.txtSentTime, Color.DKGRAY);
        remoteViews.setTextColor(R.id.txtBelowDesc, Color.DKGRAY);

        Notification notification = builder.build();
        notification.bigContentView = remoteViews;
        notification.contentView = remoteViews;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.headsUpContentView = remoteViews;
        }

        Map<String,String> msgData = message.getData();
        HashMap<String, String> data = (msgData instanceof HashMap) ? (HashMap) msgData : new HashMap<String, String>(msgData);
        String orderId = data.get("OrderID");

        String strSentTime = new SimpleDateFormat("h:mm a").format(new Date(message.getSentTime()));
        remoteViews.setTextViewText(R.id.txtSentTime,strSentTime);

        String notifyType = data.get("Type");
        if(notifyType.equals(NotificationType.UserChatToGroup))
            remoteViews.setTextViewText(R.id.txtTitle,data.get("UserName"));
        else
            remoteViews.setTextViewText(R.id.txtTitle,data.get("DriverName"));

        remoteViews.setTextViewText(R.id.txtDesc,data.get("Message"));
        remoteViews.setTextViewText(R.id.txtBelowDesc,data.get("PickupPlace"));
        remoteViews.setViewVisibility(R.id.txtBelowDesc,GONE);
        remoteViews.setViewVisibility(R.id.txtPrice,GONE);

        notificationManager.notify(notifyId,notification );
        AutoRemoveNotification(notifyId,30,context);




    }


    private static void AutoRemoveNotification(final int notificationId, int minutes,final Context context) {

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(notificationId);

            }
        },1000*60*minutes);
    }

    //----------------------------------------------- OPEN ACTIVITIES ----------------------------------------------

    public static Boolean isNotification(Intent rootIntent){

        int notifyId = rootIntent.getIntExtra(NOTIFICATION_ID, -1);

        return notifyId != -1;

    }

    public static void TryToForwardFromNotification(final Intent rootIntent,final Activity rootContext, final GetBoolValueListener listener){

        if(isNotification(rootIntent) == false) {

            if(listener!=null)
                listener.onCompleted(true,false);

            return;
        }

        NotificationManager manager = (NotificationManager) AppDelegate.getContext().getSystemService(NOTIFICATION_SERVICE);
        int notifyId = rootIntent.getIntExtra(NOTIFICATION_ID, -1);
        manager.cancel(notifyId);

        String notifyType = rootIntent.getStringExtra(NOTIFICATION_TYPE);

        if(notifyType != null ){

            OpenOrderFromNotification(rootIntent, rootContext, new GetBoolValueListener() {
                @Override
                public void onCompleted(Boolean success, Boolean value) {

                    if(listener!=null)
                        listener.onCompleted(success,value);
                }
            });

        }else{

            if(listener!=null)
                listener.onCompleted(true,false);
        }


    }

    static void OpenOrderFromNotification(final Intent rootIntent, final Activity rootContext, final GetBoolValueListener listener){

        final HashMap<String, String> data = (HashMap<String, String>)rootIntent.getSerializableExtra(NOTIFICATION_DATA);

        String orderId = data.get("OrderID") ;


        new TravelOrderController().getById(true, orderId, new GetOneListener() {
            @Override
            public void onGetOne(Boolean success, BaseModel item) {

                if(item != null){

                    Intent intent = new Intent(rootContext, OrderScreen.class);
                    Parcelable wrappedCurrentOrder = Parcels.wrap(item);
                    intent.putExtra("CurrentOrder",wrappedCurrentOrder);

                    String notifyType = rootIntent.getStringExtra(NOTIFICATION_TYPE);
                    String notifyAction = rootIntent.getStringExtra(NOTIFICATION_ACTION);
                    intent.putExtra(NOTIFICATION_TYPE, (CharSequence) notifyType);
                    intent.putExtra(NOTIFICATION_ACTION, (CharSequence) notifyAction);
                    intent.putExtra(NOTIFICATION_DATA, data);

                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    rootContext.startActivity(intent);
                    rootContext.overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);
                }

                if(listener!=null)
                    listener.onCompleted(success,item != null);
            }
        });

    }
}
