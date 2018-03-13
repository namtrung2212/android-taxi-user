package com.sconnecting.userapp.data.entity;

import android.os.Parcel;

import com.sconnecting.userapp.data.storages.client.RealmDouble;

import org.parceler.ParcelConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TrungDao on 8/11/16.
 */

public class BaseStringListParcelConverter implements ParcelConverter<BaseStringList> {

    @Override
    public void toParcel(BaseStringList input, Parcel parcel) {

        if (input == null) {
            parcel.writeInt(-1);
        }
        else {

            String[] arr = new String[input.getList().size()];

            int i = -1;
            for (String obj : input.getList()) {
                i++;
                arr[i] = obj.toString();
            }

            parcel.writeInt(1);
            parcel.writeInt(arr.length);
            for (int j=0; j<arr.length; j++) {
                parcel.writeString(arr[i]);
            }

        }
    }

    @Override
    public BaseStringList fromParcel(Parcel parcel) {

        int isNull = parcel.readInt();
        if (isNull < 0) return null;

        int iSize = parcel.readInt();

        List<String> list = new ArrayList<>();
        for (int j=0; j<iSize; j++) {
            list.add(parcel.readString());
        }

        return new BaseStringList(list);
    }
}