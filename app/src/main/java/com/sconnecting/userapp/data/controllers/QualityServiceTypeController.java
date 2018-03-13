package com.sconnecting.userapp.data.controllers;

import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.GetItemsListener;
import com.sconnecting.userapp.base.listener.GetListObjectsListener;
import com.sconnecting.userapp.data.entity.BaseController;
import com.sconnecting.userapp.data.models.QualityServiceType;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by TrungDao on 7/26/16.
 */




public class QualityServiceTypeController extends BaseController<QualityServiceType> {


    static ArrayList<String> qualityServiceTypes = new ArrayList<>();
    static ArrayList<String> qualityServiceLocaleTypes = new ArrayList<>();


    public QualityServiceTypeController()
    {
        super(QualityServiceType.class);
    }

    public static void GetActiveTypesByCountry(String countryCode, final GetItemsListener listener){

        new BaseController<>(QualityServiceType.class).get("IsActive=1&Country=" + countryCode, new GetItemsListener() {
            @Override
            public void onGetItems(Boolean success, List list) {

                if(success){

                    qualityServiceTypes = new ArrayList<>();
                    qualityServiceLocaleTypes = new ArrayList<>();

                    for (Object obj : list) {

                        QualityServiceType type = (QualityServiceType) obj;

                        if(qualityServiceTypes.contains(type.Name) == false) {
                            qualityServiceTypes.add(type.Name);
                            qualityServiceLocaleTypes.add(type.LocaleName);
                        }

                    }
                }

                if(listener != null)
                    listener.onGetItems(success,list);
            }
        });
    }

    public static void GetQualityServiceTypes(final GetListObjectsListener listener){

        if(qualityServiceTypes == null || qualityServiceTypes.size() <=0){
            GetActiveTypesByCountry(SCONNECTING.locationHelper.CountryCode, new GetItemsListener() {
                @Override
                public void onGetItems(Boolean success, List list) {

                    if(listener != null)
                        listener.onGetItems(success,qualityServiceTypes);
                }
            });

        }else{

            if(listener != null)
                listener.onGetItems(true,qualityServiceTypes);
        }
    }


    public static void GetQualityServiceLocaleTypes(final GetListObjectsListener listener){

        if(qualityServiceLocaleTypes == null || qualityServiceLocaleTypes.size() <=0){
            GetActiveTypesByCountry(SCONNECTING.locationHelper.CountryCode, new GetItemsListener() {
                @Override
                public void onGetItems(Boolean success, List list) {

                    if(listener != null)
                        listener.onGetItems(success,qualityServiceLocaleTypes);
                }
            });

        }else{

            if(listener != null)
                listener.onGetItems(true,qualityServiceLocaleTypes);
        }
    }



    public static String GetTypeFromLocaleName(String strLocalName){
        for (int i = 0; i < qualityServiceLocaleTypes.size(); i++) {
            if(qualityServiceLocaleTypes.get(i) != null && qualityServiceLocaleTypes.get(i).equals(strLocalName))
                return qualityServiceTypes.get(i);
        }
        return null;
    }



    public static String GetLocaleNameFromType(String strType){
        for (int i = 0; i < qualityServiceTypes.size(); i++) {
            if(qualityServiceTypes.get(i) != null && qualityServiceTypes.get(i).equals(strType))
                return qualityServiceLocaleTypes.get(i);
        }
        return "";
    }

}




