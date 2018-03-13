package com.sconnecting.userapp.base.listener;

import com.sconnecting.userapp.data.entity.BaseModel;

public interface GetOneListener<T extends BaseModel>{

    void onGetOne(Boolean success,T item);

}
