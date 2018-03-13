package com.sconnecting.userapp.notification;

import java.util.Map;

/**
 * Created by TrungDao on 8/22/16.
 */

public interface ChatSocketListener {


    void onChatSocketLogged(String socketId);

    void onDriverChatToUser(Map<String,Object>  data);

    void onUserChatToGroup(Map<String,Object>  data);

}

