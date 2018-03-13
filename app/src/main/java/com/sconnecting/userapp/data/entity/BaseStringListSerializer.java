package com.sconnecting.userapp.data.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.sconnecting.userapp.data.storages.client.RealmDouble;

import java.lang.reflect.Type;

public class BaseStringListSerializer implements JsonSerializer<BaseStringList> {

    @Override
    public JsonElement serialize(BaseStringList src, Type typeOfSrc, JsonSerializationContext context) {


        JsonArray jArray = new JsonArray();

        if(src == null)
            return jArray;

        for (String obj : src.getList()) {

            jArray.add(new JsonPrimitive(obj.toString()));

        }

        return jArray;

    }
}
