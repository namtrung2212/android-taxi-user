package com.sconnecting.userapp.ui.taxi.tripmate.host.chatting;

import com.sconnecting.userapp.data.models.TravelOrder;
import com.sconnecting.userapp.data.models.TripMateChatting;
import com.sconnecting.userapp.data.models.TripMateChatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TrungDao on 8/26/16.
 */

public class TripMateChattingObject implements Comparable<TripMateChattingObject> {


    public TravelOrder order;
    public TripMateChatting cellObject;
    public Float preferWidth;

    public TripMateChattingObject(TravelOrder order, TripMateChatting chatting){
        this.order = order;
        this.cellObject = chatting;

    }
    @Override
    public int compareTo(TripMateChattingObject another) {
        long diff = this.cellObject.createdAt.getTime() - another.cellObject.createdAt.getTime();
        if (diff > 0)
            return 1;
        else if (diff < 0)
            return -1;
        else
            return 0;
    }


    public interface GetChatObjectsListener {
        void onGetObjects(Boolean success, List<TripMateChattingObject> list);
    }


    public static void fromArray(TravelOrder order , List<TripMateChatting> arrItems, final GetChatObjectsListener listener) {

        List<TripMateChattingObject> arrObjects = new ArrayList<>();

        if(arrItems != null) {
            for (TripMateChatting chatting : arrItems) {
                arrObjects.add(new TripMateChattingObject(order, chatting));
            }
        }

        if(listener != null)
            listener.onGetObjects(true,arrObjects);
    }

}
