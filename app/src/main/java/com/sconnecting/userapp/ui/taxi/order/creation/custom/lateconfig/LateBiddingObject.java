package com.sconnecting.userapp.ui.taxi.order.creation.custom.lateconfig;

import com.sconnecting.userapp.base.listener.GetDoubleValueListener;
import com.sconnecting.userapp.base.listener.GetOneListener;
import com.sconnecting.userapp.data.entity.BaseController;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.base.listener.CompletionWithObject;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.data.models.Driver;
import com.sconnecting.userapp.data.models.DriverBidding;
import com.sconnecting.userapp.data.models.DriverStatus;
import com.sconnecting.userapp.data.models.TravelOrder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TrungDao on 8/7/16.
 */

public class LateBiddingObject {


    public TravelOrder order;
    public Driver driver;
    public String driverId;
    public DriverStatus driverStatus;
    public DriverBidding driverBidding;
    public Double estPrice;

    public LateBiddingObject(TravelOrder order , DriverBidding bidding){

        driverBidding = bidding;
        this.order = order;

    }

    public void loadDriver(final GetOneListener listener) {

        if(this.driverBidding != null){
            this.driverId = this.driverBidding.Driver;

        }

        if(this.driverId == null){
            if(listener != null)
                listener.onGetOne(false, null);
            return;
        }

        new BaseController<>(Driver.class).getById(false,driverId, new GetOneListener() {
            @Override
            public void onGetOne(Boolean success,BaseModel item) {

                if(success) {
                    driver = (Driver) item;

                    loadDriverStatus(new Completion() {
                        @Override
                        public void onCompleted() {

                            if (listener != null)
                                listener.onGetOne(true, driver);
                        }
                    });
                }else{

                    if (listener != null)
                        listener.onGetOne(false, null);
                }

            }
        });

    }

    public void loadDriverStatus(final Completion listener) {

        new BaseController<>(DriverStatus.class).getOneByStringField(false,"Driver", driverId, new GetOneListener() {
            @Override
            public void onGetOne(Boolean success,BaseModel item) {

                if(success)
                    driverStatus = (DriverStatus) item;

                if (listener != null)
                    listener.onCompleted();

            }
        });

    }

    public void loadEstPrice(final Completion listener){

        if(this.estPrice == null){

            if(this.order != null){
                TravelOrderController.TryToCalculateTripPrice(order.getId(),driverId, new GetDoubleValueListener() {
                    @Override
                    public void onCompleted(Boolean success,Double value) {

                        if(success)
                            estPrice = value;
                        if(listener != null)
                            listener.onCompleted();
                    }
                });
            }

        }else{

            if(listener != null)
                listener.onCompleted();

        }

    }

    public static void fromArray(TravelOrder order , final List<DriverBidding> arrBidding, final CompletionWithObject listener) {

        final Map<String,LateBiddingObject> arrObjects = new HashMap<>();

        final Integer[] iCount = {0};

        if (arrBidding != null && arrBidding.size() > 0) {
            for (DriverBidding bidding : arrBidding) {

                final LateBiddingObject obj = new LateBiddingObject(order, bidding);

                obj.loadDriver(new GetOneListener() {
                    @Override
                    public void onGetOne(Boolean success,BaseModel item) {

                        if (success && item != null) {

                            String driverId = ((Driver) item).id;
                            arrObjects.put(driverId, obj);
                        }

                        iCount[0] += 1;
                        if (iCount[0] == arrBidding.size()) {

                            if (listener != null)
                                listener.onCompleted(Arrays.asList(arrObjects.values().toArray()));
                        }

                    }
                });

            }


        }else{

            if (listener != null)
                listener.onCompleted(Arrays.asList(arrObjects.values().toArray()));
        }

    }


}
