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

public class BaseStringListDeserializer implements JsonDeserializer<BaseStringList> {

    @Override
    public BaseStringList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonArray jsonArr = json.getAsJsonArray();

        List<String> arr = new ArrayList<>();

        for (int i = 0; i < jsonArr.size(); i++) {

            arr.add(jsonArr.get(i).getAsString());

        }

        return new BaseStringList(arr);

    }
}


