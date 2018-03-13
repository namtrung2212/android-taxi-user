package com.sconnecting.userapp.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;

import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sconnecting.userapp.AppDelegate;
import com.sconnecting.userapp.base.listener.GetBoolValueListener;
import com.sconnecting.userapp.base.listener.GetOneListener;
import com.sconnecting.userapp.base.listener.GetStringValueListener;
import com.sconnecting.userapp.base.listener.PostListener;
import com.sconnecting.userapp.data.entity.BaseController;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.data.entity.LocationObject;
import com.sconnecting.userapp.data.storages.server.ServerStorage;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.data.models.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by TrungDao on 8/2/16.
 */

public class UserManager {


    public User CurrentUser;
    public UserSetting CurrentUserSetting;
    public UserStatus CurrentUserStatus;
    public Double CurrentLongtitude;
    public Double CurrentLatitude;


    public static  String Token(){

        return  AppDelegate.getContext().getSharedPreferences("SCONNECTING", Context.MODE_PRIVATE).getString("Token", null);
    }


    public static  String DefaultUserID(){

       return  AppDelegate.getContext().getSharedPreferences("SCONNECTING", Context.MODE_PRIVATE).getString("DefaultUserID", null);
    }


    public void login( String userId, final GetBoolValueListener listener){

        registerNewDevice(userId, new GetBoolValueListener() {
            @Override
            public void onCompleted(Boolean success, Boolean value) {

                if(success){

                    initCurrentUser(listener);

                }else {
                    if(listener != null)
                        listener.onCompleted(false,false);
                }
            }

        });


    }



    public void registerNewDevice(final String userId , final  GetBoolValueListener listener){

        requestNewToken(userId, new GetStringValueListener() {
            @Override
            public void onCompleted(Boolean success, String token) {

                if(!success){
                    if(listener != null)
                        listener.onCompleted(false,false);
                    return;
                }

                new BaseController<>(UserSetting.class).getOneByStringField(true,"User", userId, new GetOneListener() {
                    @Override
                    public void onGetOne(Boolean success,BaseModel item) {

                        if(!success || item == null ){
                            if(listener != null)
                                listener.onCompleted(false,false);
                            return;
                        }

                        final UserSetting newItem = (UserSetting)item;
                        newItem.Device = Build.MODEL;
                        newItem.DeviceID = Build.SERIAL;

                        new BaseController<>(UserSetting.class).update(newItem, new PostListener() {
                            @Override
                            public void onCompleted(Boolean success,BaseModel obj) {

                                if(success && obj != null){

                                    CurrentUserSetting = (UserSetting)obj;

                                    SharedPreferences preferences = AppDelegate.getContext().getSharedPreferences("SCONNECTING", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("DefaultUserID", userId);
                                    editor.commit();

                                    String FCMToken = preferences.getString("UserFCMToken", null);
                                    if(FCMToken != null && FCMToken.isEmpty() == false) {

                                        String url = AppDelegate.ServerURL + "/FCM/updateToken?userId=" + userId + "&FCMToken=" + FCMToken;
                                        new AsyncHttpClient().get(url, new TextHttpResponseHandler() {

                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, String response) {


                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {

                                            }


                                        });

                                    }else {

                                        try {
                                            FirebaseInstanceId.getInstance().deleteInstanceId();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        FirebaseInstanceId.getInstance().getToken();
                                    }


                                }

                                if(listener != null)
                                    listener.onCompleted(success,obj != null);

                            }
                        });
                    }
                });
            }
        });




    }



    public static void isValidDevice(final GetBoolValueListener listener){

        final String userId = DefaultUserID();

        if (userId == null || userId.isEmpty() ) {
            if(listener != null)
                listener.onCompleted(true,false);
        }

        requestNewToken(userId,new GetStringValueListener() {
            @Override
            public void onCompleted(Boolean success, String token) {

                if(!success){
                    if(listener != null)
                        listener.onCompleted(true,false);
                    return;
                }
                new BaseController<>(UserSetting.class).getOneByStringField(true,"User", userId, new GetOneListener() {
                    @Override
                    public void onGetOne(Boolean success, BaseModel item) {

                        UserSetting setting = (UserSetting) item;
                        if(setting != null){

                            Boolean isValid = setting.Device.equals(Build.MODEL)  &&  setting.DeviceID.equals(Build.SERIAL);
                            if(listener != null)
                                listener.onCompleted(true,isValid);
                        }else{

                            if(listener != null)
                                listener.onCompleted(true,false);
                        }

                    }
                });
            }
        });





    }


    public static void requestNewToken(final String userId, final GetStringValueListener listener){

        String url = ServerStorage.ServerURL + "/Authenticate/User/GetNewToken";

        AsyncHttpClient client = new AsyncHttpClient();
        StringEntity entity = null;
        try {
            JSONObject params = new JSONObject();
            params.put("id", userId);
            entity = new StringEntity(params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        client.post(AppDelegate.getContext(),url,entity,"text/plain",new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                if( response.has("success")) {

                    Boolean success = null;
                    try {
                        success = response.getBoolean("success");
                        if (success) {

                            String token = response.getString("token");
                            SharedPreferences preferences = AppDelegate.getContext().getSharedPreferences("SCONNECTING", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("Token", token);
                            editor.commit();

                            if(listener != null)
                                listener.onCompleted(true,token);

                        }else{

                            if(listener != null)
                                listener.onCompleted(false,null);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if(listener != null)
                            listener.onCompleted(false,null);
                    }
                }else{

                    if(listener != null)
                        listener.onCompleted(false,null);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                if(listener != null)
                    listener.onCompleted(false,null);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }



        });
    }
    public static void isValidToken(final String userId, final GetBoolValueListener listener){

        final String token = Token();

        if (token == null || token.isEmpty() ) {
            if(listener != null)
                listener.onCompleted(false,false);
            return;
        }

        String url = ServerStorage.ServerURL + "/Authenticate/User/CheckToken";

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Content-Type" , "application/x-www-form-urlencoded");
        client.setURLEncodingEnabled(true);

        Map<String,String> params = new HashMap<String, String>();
        params.put("id",userId);
        params.put("token",token);

        client.post(url, new RequestParams(params), new JsonHttpResponseHandler() {

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                if (response.has("success")) {

                    Boolean success = null;
                    try {
                        success = response.getBoolean("success");
                        if (listener != null)
                            listener.onCompleted(true, success);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (listener != null)
                            listener.onCompleted(false, false);
                    }
                } else {

                    if (listener != null)
                        listener.onCompleted(false, false);
                }

            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {


                if (listener != null)
                    listener.onCompleted(false, false);

            }

        });



    }

    public void initCurrentUser(final GetBoolValueListener listener){

        final String userId = DefaultUserID();

        if (userId == null || userId.isEmpty() ){
            if(listener != null)
                listener.onCompleted(false, false);
            return;
        }


        new BaseController<>(User.class).getById(true,userId, new GetOneListener() {
            @Override
            public void onGetOne(Boolean success,BaseModel user) {

                CurrentUser = (User) user;

                if(user == null) {

                    if(listener != null)
                        listener.onCompleted(true, false);

                }else{

                    new BaseController<>(UserSetting.class).getOneByStringField(false,"User", userId, new GetOneListener() {
                        @Override
                        public void onGetOne(Boolean success,BaseModel obj) {
                            CurrentUserSetting = (UserSetting)obj;
                        }
                    });

                    new BaseController<>(UserStatus.class).getOneByStringField(false,"User", userId, new GetOneListener() {
                        @Override
                        public void onGetOne(Boolean success,BaseModel obj) {

                            CurrentUserStatus = (UserStatus)obj;

                            if(listener != null)
                                listener.onCompleted(true,CurrentUserStatus != null);
                        }
                    });

                }
            }
        });


    }


    public void invalidateStatus(final Completion listener){

        if(CurrentUser != null){
            new BaseController<>(UserStatus.class).getOneByStringField(true, "User", CurrentUser.getId(), new GetOneListener() {
                @Override
                public void onGetOne(Boolean success, BaseModel item) {

                    CurrentUserStatus = (UserStatus)item;

                    if(listener != null)
                        listener.onCompleted();

                }
            });

        }else{
            if(listener != null)
                listener.onCompleted();
        }

    }

    public void updatePositionHistory(Location location, final Completion listener){

        String userId = CurrentUser.id;

        if ( userId != null && userId.isEmpty() == false && location != null ) {

            UserPosHistory userPos = new UserPosHistory();
            userPos.User = userId.toString();
            userPos.Location = new LocationObject( location.getLatitude() , location.getLongitude() );
            userPos.Speed = (double)location.getSpeed();
            userPos.Device = Build.MODEL;
            userPos.DeviceID = Build.SERIAL;

            new BaseController<>(UserPosHistory.class).create(userPos, new PostListener() {
                @Override
                public void onCompleted(Boolean success, BaseModel item) {

                    if(listener != null)
                        listener.onCompleted();

                }
            });

        }else{

            if(listener != null)
                listener.onCompleted();

        }

    }
}
