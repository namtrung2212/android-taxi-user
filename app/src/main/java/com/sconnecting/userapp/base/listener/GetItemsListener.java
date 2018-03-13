package com.sconnecting.userapp.base.listener;

import java.util.List;

import com.sconnecting.userapp.data.entity.BaseModel;

public interface GetItemsListener<T extends BaseModel>{
    void onGetItems(Boolean success,List<T> list);
}


