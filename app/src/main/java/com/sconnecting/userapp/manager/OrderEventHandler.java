package com.sconnecting.userapp.manager;

import android.os.Handler;

import com.sconnecting.userapp.AppDelegate;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.DeviceHelper;
import com.sconnecting.userapp.base.listener.GetDoubleValueListener;
import com.sconnecting.userapp.base.listener.GetOneListener;
import com.sconnecting.userapp.data.entity.BaseController;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.notification.TaxiSocketListener;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.base.RegionalHelper;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;
import com.sconnecting.userapp.ui.taxi.share.DriverProfile.DriverProfileScreen;
import com.sconnecting.userapp.data.models.BusinessCardBudget;
import com.sconnecting.userapp.data.models.OrderStatus;
import com.sconnecting.userapp.data.models.TravelOrder;
import com.sconnecting.userapp.data.models.UserPayCard;

import org.json.JSONException;

import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OrderEventHandler implements TaxiSocketListener {

    OrderManager manager;
    SweetAlertDialog alertDialog;

    public OrderEventHandler(OrderManager manager){
        this.manager = manager;
    }


    @Override
    public void onTaxiSocketLogged(final String socketId) {

    }

    @Override
    public void onDriverBidding(final Map<String, Object> data) {

    }

    @Override
    public void onDriverAccepted(final Map<String, Object> data) {

        final String userId = data.get("UserID").toString();
        final String driverId = data.get("DriverID").toString();
        final String orderId = data.get("OrderID").toString();

        if (manager.currentOrder.getId() != null && manager.currentOrder.getId().equals(orderId)) {

            DeviceHelper.playDefaultNotificationSound();

            if (DriverProfileScreen.instance != null && AppDelegate.CurrentActivity instanceof DriverProfileScreen) {
                DriverProfileScreen.instance.invalidateOrder(new Completion() {
                    @Override
                    public void onCompleted() {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                manager.reset(manager.currentOrder,true,null);

                            }
                        }, 3000);

                    }
                });

            }else{

                manager.reset(manager.currentOrder,true,null);

            }

        }

    }

    @Override
    public void onDriverRejected(final Map<String, Object> data) {

        final String userId = data.get("UserID").toString();
        final String driverId = data.get("DriverID").toString();
        final String orderId = data.get("OrderID").toString();

        if (manager.currentOrder.getId() != null && manager.currentOrder.getId().equals(orderId)) {

            DeviceHelper.playDefaultNotificationSound();

            if (DriverProfileScreen.instance != null && AppDelegate.CurrentActivity instanceof DriverProfileScreen) {
                 DriverProfileScreen.instance.invalidateOrder(null);

            }else{

                manager.reset(manager.currentOrder,true,new Completion() {
                    @Override
                    public void onCompleted() {

                        SCONNECTING.orderManager.actionHandler.SetDriverRequestingOrderToOpen(SCONNECTING.orderManager.currentOrder, new GetOneListener() {
                            @Override
                            public void onGetOne(Boolean success,BaseModel item) {

                                if(success)
                                    SCONNECTING.orderManager.currentOrder = (TravelOrder)item;

                                SCONNECTING.orderManager.invalidateUI(false,null);
                            }
                        });

                    }
                });

            }

        }

    }

    @Override
    public void onVehicleUpdateLocation(final Map<String, Object> data) {

        String userId = data.get("UserID").toString();
        String driverId = data.get("DriverID").toString();
        String orderId = data.get("OrderID").toString();

        Double latitude = Double.parseDouble(data.get("latitude").toString());
        Double longitude = Double.parseDouble(data.get("longitude").toString());
        Double degree = Double.parseDouble(data.get("degree").toString());

        if (manager.currentOrder.getId() != null && manager.currentOrder.getId().equals(orderId)) {

            SCONNECTING.orderScreen.mapMarkerManager.invalidateVehicle(driverId, latitude, longitude, degree, true, new Completion() {
                @Override
                public void onCompleted() {

                    SCONNECTING.orderScreen.mMapView.moveToCurrentVehicleLocation();

                }
            });

            if (manager.currentOrder.IsMonitoring()) {

               // manager.invalidateUI(false, null);

                SCONNECTING.orderScreen.newPhase = OrderScreen.OrderPhase.Monitoring;
            }
        }

    }

    public void onDriverVoidedBfPickup(final Map<String, Object> data) {

        String userId = data.get("UserID").toString();
        String driverId = data.get("DriverID").toString();
        final String orderId = data.get("OrderID").toString();

        new TravelOrderController().getById(true, orderId, new GetOneListener() {
            @Override
            public void onGetOne(Boolean success,BaseModel item) {

                final TravelOrder order = (TravelOrder) item;
                if (order != null && order.Status != null && order.Status.equals(OrderStatus.VoidedBfPickupByDriver)) {

                    DeviceHelper.playDefaultNotificationSound();

                    if(alertDialog != null && alertDialog.isShowing())
                        alertDialog.dismissWithAnimation();

                    alertDialog = new SweetAlertDialog(AppDelegate.CurrentActivity, SweetAlertDialog.WARNING_TYPE);
                    alertDialog.setTitleText("Tài xế hủy đón")
                            .setContentText(String.format("Tài xế hủy đón tại địa chỉ : \r\n \n %s", order.OrderPickupPlace))
                            .setConfirmText("Đồng ý")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                    if (manager.currentOrder.getId() != null && manager.currentOrder.getId().equals(orderId)) {

                                        manager.actionHandler.ResetWhenVoidedBeforePickup(order, null);

                                    }

                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .show();

                }
            }
        });


    }

    public void onDriverStartedTrip(final Map<String, Object> data) {

        String userId = data.get("UserID").toString();
        String driverId = data.get("DriverID").toString();
        final String orderId = data.get("OrderID").toString();

        if (manager.currentOrder.getId() != null && manager.currentOrder.getId().equals(orderId)) {

                DeviceHelper.playDefaultNotificationSound();

                manager.reset(manager.currentOrder, true, new Completion() {
                    @Override
                    public void onCompleted() {


                        if(alertDialog != null && alertDialog.isShowing())
                            alertDialog.dismissWithAnimation();

                        alertDialog = new SweetAlertDialog(AppDelegate.CurrentActivity, SweetAlertDialog.WARNING_TYPE);
                        alertDialog.setTitleText("Bắt đầu hành trình")
                                .setContentText(String.format("Tài xế đã đón khách địa chỉ : \r\n \n %s", manager.currentOrder.ActPickupPlace == null ? "" : manager.currentOrder.ActPickupPlace))
                                .setConfirmText("Đồng ý")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                })
                                .show();
                    }
                });

        }

    }

    public void onDriverVoidedAfPickup(final Map<String, Object> data) {

        String userId = data.get("UserID").toString();
        String driverId = data.get("DriverID").toString();
        final String orderId = data.get("OrderID").toString();

        if (manager.currentOrder.getId() != null && manager.currentOrder.getId().equals(orderId)) {

                DeviceHelper.playDefaultNotificationSound();

                manager.reset(manager.currentOrder, true, new Completion() {
                    @Override
                    public void onCompleted() {

                            isShouldPayByCash(manager.currentOrder, new GetDoubleValueListener() {
                                @Override
                                public void onCompleted(Boolean success, Double shouldCash) {


                                    if(alertDialog != null && alertDialog.isShowing())
                                        alertDialog.dismissWithAnimation();

                                    alertDialog = new SweetAlertDialog(AppDelegate.CurrentActivity, SweetAlertDialog.WARNING_TYPE);
                                    alertDialog.setTitleText("Kết thúc hành trình")
                                            .setContentText(shouldCash == 1 ?
                                                    String.format("%s \r \n Xác nhận và thanh toán cước: \r \n %s bằng tiền mặt", manager.currentOrder.ActDropPlace == null ? "" : manager.currentOrder.ActDropPlace, RegionalHelper.toCurrencyOfCountry(manager.currentOrder.ActPrice, manager.currentOrder.OrderPickupCountry)) :
                                                    String.format("%s \r \n Xác nhận và thanh toán cước: \r \n %s", manager.currentOrder.ActDropPlace == null ? "" : manager.currentOrder.ActDropPlace, RegionalHelper.toCurrencyOfCountry(manager.currentOrder.ActPrice, manager.currentOrder.OrderPickupCountry))
                                            )
                                            .setConfirmText("Đồng ý")
                                            .setConfirmClickListener(null)
                                            .show();

                                }
                            });
                    }
                });
        }

    }

    public void onDriverFinished(final Map<String, Object> data) {

        String userId = data.get("UserID").toString();
        String driverId = data.get("DriverID").toString();
        final String orderId = data.get("OrderID").toString();

        if (manager.currentOrder.getId() != null && manager.currentOrder.getId().equals(orderId)) {

            DeviceHelper.playDefaultNotificationSound();

            manager.reset(manager.currentOrder, true, new Completion() {
                @Override
                public void onCompleted() {

                    isShouldPayByCash(manager.currentOrder, new GetDoubleValueListener() {
                        @Override
                        public void onCompleted(Boolean success, Double shouldCash) {


                            if(alertDialog != null && alertDialog.isShowing())
                                alertDialog.dismissWithAnimation();

                            alertDialog = new SweetAlertDialog(AppDelegate.CurrentActivity, SweetAlertDialog.WARNING_TYPE);
                            alertDialog.setTitleText(manager.currentOrder.Status.equals(OrderStatus.VoidedAfPickupByDriver) ? "Tài xế hủy giữa hành trình" : "Bạn đã hủy giữa hành trình")
                                    .setContentText(shouldCash == 1 ?
                                            String.format("%s \r \n Xác nhận và thanh toán cước: \r \n %s bằng tiền mặt", manager.currentOrder.ActDropPlace == null ? "" : manager.currentOrder.ActDropPlace, RegionalHelper.toCurrencyOfCountry(manager.currentOrder.ActPrice, manager.currentOrder.OrderPickupCountry)) :
                                            String.format("%s \r \n Xác nhận và thanh toán cước: \r \n %s", manager.currentOrder.ActDropPlace == null ? "" : manager.currentOrder.ActDropPlace, RegionalHelper.toCurrencyOfCountry(manager.currentOrder.ActPrice, manager.currentOrder.OrderPickupCountry))
                                    )
                                    .setConfirmText("Đồng ý")
                                    .setConfirmClickListener(null)
                                    .show();

                        }
                    });
                }
            });
        }

    }

    public void isShouldPayByCash(final TravelOrder order, final GetDoubleValueListener listener) {

        String query = "User=" + order.User + "&Currency=" + order.Currency + "&IsVerified=1&IsExpired=0&IsLocked=0";

        new BaseController<>(UserPayCard.class).getDouble("count", query, new GetDoubleValueListener() {
            @Override
            public void onCompleted(Boolean success,Double cards) {

                if (success && cards > 0) {
                    if (listener != null)
                        listener.onCompleted(true,0.0);
                } else {


                    String query2 = "AssignedUser=" + order.User + " &Currency=" + order.Currency + "&IsOverBudget=0&IsActivated=1&IsExpired=0&IsLocked=0";
                    new BaseController<>(BusinessCardBudget.class).getDouble("count", query2, new GetDoubleValueListener() {
                        @Override
                        public void onCompleted(Boolean success,Double cards2) {
                            if (listener != null)
                                listener.onCompleted(true, (double) ( (cards2 == null || cards2 == 0.0) ? 1.0 : 0.0));
                        }
                    });

                }
            }
        });

    }

    public void onDriverReceivedCash(final Map<String, Object> data) {

        String userId = data.get("UserID").toString();
        String driverId = data.get("DriverID").toString();
        final String orderId = data.get("OrderID").toString();


        if (manager.currentOrder.getId() != null && manager.currentOrder.getId().equals(orderId)) {

            DeviceHelper.playDefaultNotificationSound();

            manager.reset(manager.currentOrder, true, new Completion() {
                @Override
                public void onCompleted() {

                    isShouldPayByCash(manager.currentOrder, new GetDoubleValueListener() {
                        @Override
                        public void onCompleted(Boolean success, Double shouldCash) {


                            if(alertDialog != null && alertDialog.isShowing())
                                alertDialog.dismissWithAnimation();

                            alertDialog = new SweetAlertDialog(AppDelegate.CurrentActivity, SweetAlertDialog.WARNING_TYPE);
                            alertDialog.setTitleText("Tài xế đã nhận tiền mặt")
                                    .setContentText(String.format("Bạn vừa thanh toán %s tiền mặt. \r\n Cảm ơn đã sử dụng dịch vụ. \r\n Chúc bạn một ngày vui vẻ!", RegionalHelper.toCurrency(manager.currentOrder.PayAmount, manager.currentOrder.PayCurrency)))
                                    .setConfirmText("Xác nhận")
                                    .setConfirmClickListener(null)
                                    .show();

                        }
                    });
                }
            });
        }

    }

    @Override
    public void onUserShouldInvalidateOrder(final Map<String, Object> data) {

        String userId = data.get("UserID").toString();
        final String orderId = data.get("OrderID").toString();

        if (manager.currentOrder.getId() != null && manager.currentOrder.getId().equals(orderId))
            manager.invalidate(false,true, null);

    }

    @Override
    public void onCheckAppInForeground(final Map<String, Object> data) {

        boolean isForeground = DeviceHelper.isAppInForeground();

        if(isForeground){

            try {
                SCONNECTING.notificationHelper.taxiSocket.UserAppInForeground();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            try {
                SCONNECTING.notificationHelper.taxiSocket.UserAppInBackground();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
