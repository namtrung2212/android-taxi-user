package com.sconnecting.userapp.base.listener;

import com.sconnecting.userapp.data.entity.BaseModel;

public interface PostListener<T extends BaseModel>{

    public void onCompleted(Boolean success,T item);


}
