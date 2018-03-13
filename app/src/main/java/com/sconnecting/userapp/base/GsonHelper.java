package com.sconnecting.userapp.base;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.data.entity.BaseStringList;
import com.sconnecting.userapp.data.entity.BaseStringListDeserializer;
import com.sconnecting.userapp.data.entity.BaseStringListSerializer;
import com.sconnecting.userapp.data.entity.DateDeserializer;
import com.sconnecting.userapp.data.entity.DateSerializer;
import com.sconnecting.userapp.data.entity.LocationObjectDeserializer;
import com.sconnecting.userapp.data.entity.LocationObject;
import com.sconnecting.userapp.data.entity.LocationObjectList;
import com.sconnecting.userapp.data.entity.LocationObjectListDeserializer;
import com.sconnecting.userapp.data.entity.LocationObjectListSerializer;
import com.sconnecting.userapp.data.entity.LocationObjectSerializer;

import io.realm.RealmObject;

/**
 * Created by TrungDao on 7/28/16.
 */
public class GsonHelper {

    public static  Gson getGson(){

        GsonBuilder builder = new GsonBuilder();
        builder.setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                });

        builder.registerTypeAdapter(new TypeToken<LocationObject>() {}.getType(), new LocationObjectDeserializer());
        builder.registerTypeAdapter(new TypeToken<LocationObject>() {}.getType(), new LocationObjectSerializer());
        builder.registerTypeAdapter(new TypeToken<LocationObjectList>() {}.getType(), new LocationObjectListDeserializer());
        builder.registerTypeAdapter(new TypeToken<LocationObjectList>() {}.getType(), new LocationObjectListSerializer());
        builder.registerTypeAdapter(new TypeToken<BaseStringList>() {}.getType(), new BaseStringListDeserializer());
        builder.registerTypeAdapter(new TypeToken<BaseStringList>() {}.getType(), new BaseStringListSerializer());
        builder.registerTypeAdapter(Date.class, new DateDeserializer());
        builder.registerTypeAdapter(Date.class, new DateSerializer());

        return builder.create();
    }
    public static <T  extends BaseModel> List<T> toList(final String json, Type arrayType)
    {
        return getGson().fromJson(json, arrayType);
    }


    public static <T  extends BaseModel> T toModel(final String json, Type modelType)
    {
        return getGson().fromJson(json, modelType);
    }
}
