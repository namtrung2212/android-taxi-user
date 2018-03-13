package com.sconnecting.userapp.data.controllers;

import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.GetItemsListener;
import com.sconnecting.userapp.base.listener.GetListObjectsListener;
import com.sconnecting.userapp.data.entity.BaseController;
import com.sconnecting.userapp.data.models.VehicleType;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by TrungDao on 7/26/16.
 */




public class VehicleTypeController extends BaseController<VehicleType> {

    static ArrayList<String> vehicleTypes = new ArrayList<>();
    static ArrayList<String> vehicleLocaleTypes = new ArrayList<>();

    public VehicleTypeController()
    {
        super(VehicleType.class);
    }

    public static void GetActiveTypesByCountry(String countryCode, final GetItemsListener listener){

        new BaseController<>(VehicleType.class).get("IsActive=1&Country=" + countryCode, new GetItemsListener() {
            @Override
            public void onGetItems(Boolean success, List list) {

                if(success){

                    vehicleTypes = new ArrayList<>();
                    vehicleLocaleTypes = new ArrayList<>();

                    for (Object obj : list) {

                        VehicleType type = (VehicleType) obj;

                        if(vehicleTypes.contains(type.Name) == false) {
                            vehicleTypes.add(type.Name);
                            vehicleLocaleTypes.add(type.LocaleName);
                        }

                    }
                }

                if(listener != null)
                    listener.onGetItems(success,list);
            }
        });
    }

    public static void GetVehicleTypes(final GetListObjectsListener listener){

        if(vehicleTypes == null || vehicleTypes.size() <=0){
            GetActiveTypesByCountry(SCONNECTING.locationHelper.CountryCode, new GetItemsListener() {
                @Override
                public void onGetItems(Boolean success, List list) {

                    if(listener != null)
                        listener.onGetItems(success,vehicleTypes);
                }
            });

        }else{

            if(listener != null)
                listener.onGetItems(true,vehicleTypes);
        }

    }


    public static void GetVehicleLocaleTypes(final GetListObjectsListener listener){

        if(vehicleLocaleTypes == null || vehicleLocaleTypes.size() <=0){
            GetActiveTypesByCountry(SCONNECTING.locationHelper.CountryCode, new GetItemsListener() {
                @Override
                public void onGetItems(Boolean success, List list) {

                    if(listener != null)
                        listener.onGetItems(success,vehicleLocaleTypes);
                }
            });

        }else{

            if(listener != null)
                listener.onGetItems(true,vehicleLocaleTypes);
        }
    }


    public static String GetTypeFromLocaleName(String strLocalName){
        for (int i = 0; i < vehicleLocaleTypes.size(); i++) {
            if(vehicleLocaleTypes.get(i) != null && vehicleLocaleTypes.get(i).equals(strLocalName))
                return vehicleTypes.get(i);
        }
        return null;
    }



    public static String GetLocaleNameFromType(String strType){
        for (int i = 0; i < vehicleTypes.size(); i++) {
            if(vehicleTypes.get(i) != null && vehicleTypes.get(i).equals(strType))
                return vehicleLocaleTypes.get(i);
        }
        return "";
    }
}




