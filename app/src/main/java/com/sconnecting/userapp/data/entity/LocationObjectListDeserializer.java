package com.sconnecting.userapp.data.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TrungDao on 8/9/16.
 */

public class LocationObjectListDeserializer implements JsonDeserializer<LocationObjectList> {

    @Override
    public LocationObjectList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonArray jsonArr = json.getAsJsonArray();

        List<Double> arr = new ArrayList<>();

        for (int i = 0; i < jsonArr.size(); i++) {

            arr.add(jsonArr.get(i).getAsDouble());

        }

        return new LocationObjectList(arr);

    }
}


