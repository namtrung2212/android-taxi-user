package com.sconnecting.userapp.data.entity;



import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by TrungDao on 8/9/16.
 */


@Parcel(value = Parcel.Serialization.FIELD,
        converter = BaseStringListParcelConverter.class)
public class BaseStringList  {

    private ArrayList<String> arrayList= new ArrayList<>();


    public List<String> getList(){
        return arrayList;
    }

    public BaseStringList(){}


    public BaseStringList(List<String> list) {

        if (list != null) {

            for (String obj : list) {

                arrayList.add(new String(obj));

            }

        }
    }


}