package com.sconnecting.userapp.ui.taxi.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.sconnecting.userapp.AppDelegate;
import com.sconnecting.userapp.base.BaseActivity;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.DeviceHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.manager.OrderManager;
import com.sconnecting.userapp.notification.NotificationHelper;
import com.sconnecting.userapp.notification.NotificationType;
import com.sconnecting.userapp.ui.taxi.order.map.*;

import com.sconnecting.userapp.ui.taxi.order.monitoring.MonitoringView;
import com.sconnecting.userapp.ui.taxi.order.payment.ChoosePaymentMethodView;
import com.sconnecting.userapp.ui.taxi.order.placesearcher.PlaceSearcher;
import com.sconnecting.userapp.ui.taxi.order.creation.*;
import com.sconnecting.userapp.data.models.OrderStatus;
import com.sconnecting.userapp.data.models.TravelOrder;
import com.sconnecting.userapp.ui.taxi.order.rating.RatingView;
import com.sconnecting.userapp.ui.taxi.tripmate.host.HostScreen;

import org.parceler.Parcels;


/**
 * Created by TrungDao on 8/1/16.
 */


public class OrderScreen extends BaseActivity implements  ActivityCompat.OnRequestPermissionsResultCallback {

    public enum OrderPhase {
        ChooseLocation, CustomOrder, Monitoring, Payment, Rating
    }

    public MapView mMapView;
    public MapLocation mMapLocation;
    public MapMarkerManager mapMarkerManager;

    public PlaceSearcher mPlaceSearcher;

    public ChooseLocationView mChooseLocationView;
    public CustomOrderView mCustomOrderView;
    public MonitoringView mMonitoringView;
    public RatingView mRatingView;
    public ChoosePaymentMethodView mChoosePaymentMethodView;

    public boolean isMapReady =false;

    public interface OnMapReadyListener {
        void onReady();
    }

    public OnMapReadyListener mapReadyListener;


    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;
    }


    public OrderPhase newPhase = OrderPhase.ChooseLocation;

    public Boolean isChoosingLocationPhase() {

        return this.CurrentOrder().IsNewOrder() && newPhase.equals(OrderPhase.ChooseLocation);

    }


    public Boolean isCustomOrderPhase() {

        if(this.CurrentOrder().IsNewOrder()){

            if(newPhase.equals(OrderPhase.CustomOrder))
                return true;

        }else {

            if (this.CurrentOrder().IsMateHost == 0) {

                if (this.CurrentOrder().isTripMateRequestingMember())
                    return true;

                if (this.CurrentOrder().isTripMateAcceptedMember())
                    return true;

                if (this.CurrentOrder().IsNotYetChooseDriver())
                    return true;

            } else {

                if (this.CurrentOrder().IsNotYetChooseDriver())
                    return true;

            }
        }

        return false;

    }

    public Boolean isMonitoringPhase() {
        return !this.CurrentOrder().IsNewOrder() && this.CurrentOrder().IsMonitoring();
    }

    public Boolean isPaymentPhase() {

        return !this.CurrentOrder().IsNewOrder() && this.CurrentOrder().IsFinishedNotYetPaid();
    }

    public Boolean isRatingPhase() {
        return !this.CurrentOrder().IsNewOrder() && this.CurrentOrder().IsFinishedAndPaid();
    }


    public OrderScreen.OrderPhase getPhase() {

        if(CurrentOrder().isNew())
            return this.newPhase;


        if(CurrentOrder().Status.equals(OrderStatus.DriverAccepted)
                || CurrentOrder().Status.equals(OrderStatus.DriverPicking)
                || CurrentOrder().Status.equals(OrderStatus.Pickuped)){
            return OrderScreen.OrderPhase.Monitoring;

        }else if(CurrentOrder().IsFinishedNotYetPaid()){
            return OrderScreen.OrderPhase.Payment;

        }else if(CurrentOrder().IsFinishedAndPaid()){
            return OrderScreen.OrderPhase.Rating;

        }else if(CurrentOrder().Status.equals(OrderStatus.Requested)
                ||  CurrentOrder().Status.equals(OrderStatus.BiddingAccepted)
                ||  CurrentOrder().Status.equals(OrderStatus.Open)
                ||  CurrentOrder().Status.equals(OrderStatus.DriverRejected)){
            return OrderPhase.CustomOrder;

        }

        return this.newPhase;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(mMapView != null)
            mMapView.initMap();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SCONNECTING.orderScreen = this;

        initControls(new Completion() {
            @Override
            public void onCompleted() {

                SCONNECTING.locationHelper.getLocation();

            }
        });


        reloadOrderFirstTime();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppDelegate.CurrentActivity = this;

    }

    @Override
    public void showToolbar(boolean show) {

        super.showToolbar(show);

        if (this.mPlaceSearcher != null && this.mPlaceSearcher.mSearchView != null)
            this.mPlaceSearcher.mSearchView.setVisibility(show ? View.GONE : View.VISIBLE);

    }
    public void initControls(final Completion listener) {

        mapReadyListener = new OnMapReadyListener() {
            @Override
            public void onReady() {

                isMapReady = true;
                reloadOrderFirstTime();

            }
        };

        setContentView(R.layout.taxi_order);

        mPlaceSearcher = new PlaceSearcher(this);
        mMapLocation = new MapLocation(this);
        mapMarkerManager = new MapMarkerManager(this);

        showToolbar(false);

        if(listener != null)
            listener.onCompleted();
    }


    public void reloadOrderFirstTime(){

        if(isMapReady) {

            TravelOrder order = Parcels.unwrap(getIntent().getParcelableExtra("CurrentOrder"));

            if(order == null) {
                order = OrderManager.initNewOrder();
            }

            SCONNECTING.orderManager.reset(order, true, new Completion() {
                @Override
                public void onCompleted() {

                    if(CurrentOrder() != null){

                        if(isChoosingLocationPhase()){
                            mMapView.moveToCurrentLocation((float)15,false);

                        }else if(isMonitoringPhase()){
                            mMapView.moveToCurrentVehicleLocation();
                        }

                        String notifyType = getIntent().getStringExtra(NotificationHelper.NOTIFICATION_TYPE);
                        if(CurrentOrder().IsMateHost == 1 && notifyType!=null &&
                                (notifyType.equals(NotificationType.MateMemberSentRequest)
                                        || notifyType.equals(NotificationType.MemberLeaveFromTripMate)
                                        || notifyType.equals(NotificationType.UserChatToGroup))){

                            Intent intent = new Intent(mMapView.getContext(), HostScreen.class);

                            Parcelable wrappedCurrentOrder = Parcels.wrap(CurrentOrder());
                            intent.putExtra("HostOrder",wrappedCurrentOrder);
                            intent.putExtra("NotifyType",notifyType);

                            ((Activity)mMapView.getContext()).startActivity(intent);
                            ((Activity)mMapView.getContext()).overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);

                        }


                    }

                }
            });


        }


    }

    public void invalidateUI(final Boolean isFirstTime, final Completion listener) {

        boolean isForeground = DeviceHelper.isAppInForeground();

        if(isForeground == false || AppDelegate.CurrentActivity != this ||  mMapView == null || mMapView.gmsMapView == null){
            if(listener != null)
                listener.onCompleted();
            return;
        }

        final OrderPhase oldPhase = this.newPhase;
        this.newPhase = getPhase();
/*
        if(isFirstTime){

            invalidateOrderCreation(isFirstTime,new Completion() {
                @Override
                public void onCompleted() {
                    invalidateOrderMonitoring(isFirstTime,new Completion() {
                        @Override
                        public void onCompleted() {
                            invalidateOrderPayment(isFirstTime,new Completion() {
                                @Override
                                public void onCompleted() {
                                    invalidateOrderRating(isFirstTime, new Completion() {
                                        @Override
                                        public void onCompleted() {
                                            invalidateMapView(isFirstTime,listener);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });

            return;

        }

        if(oldPhase.equals(OrderPhase.ChooseLocation) || oldPhase.equals(OrderPhase.CustomOrder) ){

            if(newPhase.equals(OrderPhase.ChooseLocation) || newPhase.equals(OrderPhase.CustomOrder)){

                    invalidateOrderCreation(oldPhase == newPhase,new Completion() {
                        @Override
                        public void onCompleted() {

                            invalidateMapView(oldPhase == newPhase,listener);
                        }
                    });

                    return;

            }else  if(newPhase.equals(OrderPhase.Monitoring)){

                invalidateOrderCreation(false,new Completion() {
                    @Override
                    public void onCompleted() {

                        invalidateOrderMonitoring(true,new Completion() {
                            @Override
                            public void onCompleted() {

                                invalidateMapView(false,listener);
                            }
                        });
                    }
                });

                return;


            }else  if(newPhase.equals(OrderPhase.Payment)){

                invalidateOrderCreation(false,new Completion() {
                    @Override
                    public void onCompleted() {

                        invalidateOrderMonitoring(true,new Completion() {
                            @Override
                            public void onCompleted() {

                                invalidateOrderPayment(isFirstTime,new Completion() {
                                    @Override
                                    public void onCompleted() {

                                        invalidateMapView(isFirstTime,listener);

                                    }
                                });
                            }
                        });
                    }
                });

                return;

            }else  if(newPhase.equals(OrderPhase.Rating)){

                invalidateOrderCreation(false,new Completion() {
                    @Override
                    public void onCompleted() {

                        invalidateOrderMonitoring(true,new Completion() {
                            @Override
                            public void onCompleted() {

                                invalidateOrderRating(isFirstTime, new Completion() {
                                    @Override
                                    public void onCompleted() {
                                        invalidateMapView(isFirstTime,listener);
                                    }
                                });

                            }
                        });
                    }
                });

                return;
            }

        }


        if(oldPhase.equals(OrderPhase.Monitoring)){

            if (newPhase.equals(OrderPhase.Monitoring)){

                invalidateOrderMonitoring(false,new Completion() {
                    @Override
                    public void onCompleted() {

                        invalidateMapView(false,listener);
                    }
                });

                return;

            }else if (newPhase.equals(OrderPhase.Payment)){

                invalidateOrderMonitoring(false,new Completion() {
                    @Override
                    public void onCompleted() {

                        invalidateOrderPayment(true,new Completion() {
                            @Override
                            public void onCompleted() {

                                invalidateMapView(false,listener);
                            }
                        });
                    }
                });

                return;

            }
        }



        if(oldPhase.equals(OrderPhase.Payment)){

            if (newPhase.equals(OrderPhase.Payment)){

                invalidateOrderPayment(false,new Completion() {
                    @Override
                    public void onCompleted() {

                        invalidateMapView(false,listener);
                    }
                });

                return;

            }else if (newPhase.equals(OrderPhase.Rating)){

                invalidateOrderPayment(false,new Completion() {
                    @Override
                    public void onCompleted() {

                        invalidateOrderRating(false, new Completion() {
                            @Override
                            public void onCompleted() {

                                invalidateMapView(false,listener);
                            }
                        });
                    }
                });

                return;
            }
        }

*/

        invalidateOrderCreation(isFirstTime,new Completion() {
            @Override
            public void onCompleted() {
                invalidateOrderMonitoring(isFirstTime,new Completion() {
                    @Override
                    public void onCompleted() {
                        invalidateOrderPayment(isFirstTime,new Completion() {
                            @Override
                            public void onCompleted() {
                                invalidateOrderRating(isFirstTime, new Completion() {
                                    @Override
                                    public void onCompleted() {
                                        invalidateMapView(isFirstTime,listener);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

    }


    public void invalidateMapView( Boolean isFirstTime, final Completion listener){

        mMapView.invalidate(isFirstTime,listener);
    }

    public void invalidateOrderCreation(final Boolean isFirstTime, final Completion listener){

        mChooseLocationView.invalidate( isFirstTime, new Completion() {
            @Override
            public void onCompleted() {
                mCustomOrderView.invalidate( isFirstTime,listener);
            }
        });
    }

    public void invalidateOrderMonitoring( Boolean isFirstTime, final Completion listener){

        mMonitoringView.invalidate(isFirstTime,listener);

    }

    public void invalidateOrderPayment(Boolean isFirstTime,  final Completion listener){

        mChoosePaymentMethodView.invalidate(isFirstTime,listener);
    }


    public void invalidateOrderRating(Boolean isFirstTime,  final Completion listener){

        mRatingView.invalidate(isFirstTime,listener);
    }
}