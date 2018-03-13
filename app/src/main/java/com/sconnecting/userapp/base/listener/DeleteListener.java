package com.sconnecting.userapp.base.listener;

import com.sconnecting.userapp.data.entity.BaseModel;

/**
 * Created by TrungDao on 7/28/16.
 */

public interface DeleteListener<T extends BaseModel>{

    public void onDeleted(Boolean success,Integer number);

}