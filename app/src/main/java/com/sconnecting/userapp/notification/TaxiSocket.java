package com.sconnecting.userapp.notification;

import android.os.Handler;
import android.os.Looper;

import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.CollectionHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.base.listener.GetObjectListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by TrungDao on 8/7/16.
 */

public class TaxiSocket {


    public static String ServerURL = "";

    public Socket socket;

    public TaxiSocketListener socketListener;

    public TaxiSocket(){

        IO.Options opts = new IO.Options();
        opts.reconnection = true;
        opts.forceNew = true;
      //  opts.query = "auth_token=" + authToken;


        try {
            socket = IO.socket(ServerURL , opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if(socket != null)
            addHandlers();
    }

    public void connect(TaxiSocketListener newSocketListener, final Completion listener){

        socketListener = newSocketListener;

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                try {
                    loggin();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(listener != null)
                    listener.onCompleted();
            }

        });


        socket.connect();

    }
public  void processEvent(final GetObjectListener listener, Object... args){

    if (socketListener != null) {
        Map<String, Object> data = null;
        try {
            data = CollectionHelper.toMap((JSONObject) args[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (Looper.myLooper() != Looper.getMainLooper()) {

            final Map<String, Object>  finalData = data;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                    if(listener != null)
                        listener.onGetOne(true,finalData);
                }
            });

        } else {

            if(listener != null)
                listener.onGetOne(true,data);
        }
    }else{

        if(listener != null)
            listener.onGetOne(false,null);
    }
}
    public void addHandlers() {

        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                socket.connect();
            }

        }).on("UserLogged", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                if (socketListener != null) {
                    String data = null;
                    try {
                        data = args[0].toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    if (Looper.myLooper() != Looper.getMainLooper()) {

                        final String finalData = data;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {

                                socketListener.onTaxiSocketLogged(finalData);
                            }
                        });

                    } else {

                        socketListener.onTaxiSocketLogged(data);
                    }
                }
            }
        }).on("DriverBidding", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                processEvent(new GetObjectListener() {
                    @Override
                    public void onGetOne(Boolean success, Object item) {

                        if(success)
                            socketListener.onDriverBidding((Map<String, Object>)item);

                    }
                }, args);

            }
        }).on("DriverAccepted", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                processEvent(new GetObjectListener() {
                    @Override
                    public void onGetOne(Boolean success, Object item) {

                        if(success)
                            socketListener.onDriverAccepted((Map<String, Object>)item);

                    }
                }, args);

            }
        }).on("DriverRejected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                processEvent(new GetObjectListener() {
                    @Override
                    public void onGetOne(Boolean success, Object item) {

                        if(success)
                            socketListener.onDriverRejected((Map<String, Object>)item);

                    }
                }, args);

            }
        }).on("VehicleUpdateLocation", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                processEvent(new GetObjectListener() {
                    @Override
                    public void onGetOne(Boolean success, Object item) {

                        if(success)
                            socketListener.onVehicleUpdateLocation((Map<String, Object>)item);

                    }
                }, args);

            }
        }).on("DriverVoidedBfPickup", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                processEvent(new GetObjectListener() {
                    @Override
                    public void onGetOne(Boolean success, Object item) {

                        if(success)
                            socketListener.onDriverVoidedBfPickup((Map<String, Object>)item);

                    }
                }, args);

            }
        }).on("DriverStartedTrip", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                processEvent(new GetObjectListener() {
                    @Override
                    public void onGetOne(Boolean success, Object item) {

                        if(success)
                            socketListener.onDriverStartedTrip((Map<String, Object>)item);

                    }
                }, args);

            }
        }).on("DriverVoidedAfPickup", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                processEvent(new GetObjectListener() {
                    @Override
                    public void onGetOne(Boolean success, Object item) {

                        if(success)
                            socketListener.onDriverVoidedAfPickup((Map<String, Object>)item);

                    }
                }, args);

            }
        }).on("DriverFinished", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                processEvent(new GetObjectListener() {
                    @Override
                    public void onGetOne(Boolean success, Object item) {

                        if(success)
                            socketListener.onDriverFinished((Map<String, Object>)item);

                    }
                }, args);

            }
        }).on("DriverReceivedCash", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                processEvent(new GetObjectListener() {
                    @Override
                    public void onGetOne(Boolean success, Object item) {

                        if(success)
                            socketListener.onDriverReceivedCash((Map<String, Object>)item);

                    }
                }, args);

            }
        }).on("UserShouldInvalidateOrder", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                processEvent(new GetObjectListener() {
                    @Override
                    public void onGetOne(Boolean success, Object item) {

                        if(success)
                            socketListener.onUserShouldInvalidateOrder((Map<String, Object>)item);

                    }
                }, args);

            }
        }).on("CheckAppInForeground", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                processEvent(new GetObjectListener() {
                    @Override
                    public void onGetOne(Boolean success, Object item) {

                        if(success)
                            socketListener.onCheckAppInForeground((Map<String, Object>)item);

                    }
                }, args);
            }
        });
    }

    public void loggin() throws JSONException {


        JSONObject obj = new JSONObject();
        obj.put("UserID", SCONNECTING.userManager.CurrentUser.id);

        socket.emit("UserLogin", obj);
    }

    public void UserAppInForeground() throws JSONException{

        JSONObject obj = new JSONObject();
        obj.put("UserID", SCONNECTING.userManager.CurrentUser.getId());

        socket.emit("UserAppInForeground", obj);

    }

    public void UserAppInBackground() throws JSONException{

        JSONObject obj = new JSONObject();
        obj.put("UserID", SCONNECTING.userManager.CurrentUser.getId());

        socket.emit("UserAppInBackground", obj);

    }


}
