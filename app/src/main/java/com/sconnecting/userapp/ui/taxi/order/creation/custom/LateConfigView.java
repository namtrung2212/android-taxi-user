package com.sconnecting.userapp.ui.taxi.order.creation.custom;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import java.util.ArrayList;
import java.util.Calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.GetListObjectsListener;
import com.sconnecting.userapp.base.listener.PostListener;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.base.AnimationHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.data.controllers.QualityServiceTypeController;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.data.controllers.VehicleTypeController;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;
import com.sconnecting.userapp.ui.taxi.order.creation.custom.lateconfig.LateBiddingTable;
import com.sconnecting.userapp.data.models.TravelOrder;
import com.sconnecting.userapp.base.ui.NTSpinner;

import java.util.List;

/**
 * Created by TrungDao on 10/11/16.
 */

public class LateConfigView extends Fragment {


    View view;
    OrderScreen parentScreen;

    LateBiddingTable driverTable;
    NTSpinner cbVehicleType;
    NTSpinner cbQuality;
    private EditText pickupDate;
    private EditText pickupTime;
    TextView lblPickupDate;
    TextView lblPickupTime;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;


    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }


    public LateConfigView() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        parentScreen = (OrderScreen) context;
        parentScreen.mCustomOrderView.mLateConfigView = this;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        try {
            view = inflater.inflate(R.layout.taxi_order_creation_custom_late, container, false);
        } catch (InflateException e) {
        }

        initControls(null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onResume(){
        super.onResume();

        invalidate(null);

    }
    public void initControls(final Completion listener) {

        cbVehicleType = (NTSpinner) view.findViewById(R.id.cbVehicleType);
        cbVehicleType.setHideUnderline(true);
        cbVehicleType.setBackgroundResource(R.drawable.textedit_blue);
        cbVehicleType.setDropDownHeight(490);
        cbVehicleType.setDropDownBackgroundDrawable(new ColorDrawable(0xFFE9E9E9));
        cbVehicleType.setTextColor(Color.WHITE);
        cbVehicleType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AnimationHelper.animateButton(view);
                cbVehicleType.dismissDropDown();

                String strLocaleType = cbVehicleType.getAdapter().getItem(position).toString();

                CurrentOrder().OrderVehicleType = VehicleTypeController.GetTypeFromLocaleName(strLocaleType);

                new TravelOrderController().update(CurrentOrder(),"updateOrder", new PostListener() {
                    @Override
                    public void onCompleted(Boolean success, BaseModel item) {

                        if(success && item!= null)
                            SCONNECTING.orderManager.currentOrder = (TravelOrder)item;

                        parentScreen.invalidateOrderCreation(false,null);
                    }
                });


            }
        });


        cbQuality = (NTSpinner) view.findViewById(R.id.cbQuality);
        cbQuality.setHideUnderline(true);
        cbQuality.setBackgroundResource(R.drawable.textedit_blue);
        cbQuality.setDropDownHeight(370);
        cbQuality.setTextColor(Color.WHITE);
        cbQuality.setDropDownBackgroundDrawable(new ColorDrawable(0xFFE9E9E9));

        cbQuality.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AnimationHelper.animateButton(view);
                cbQuality.dismissDropDown();

                String strLocaleType = cbQuality.getAdapter().getItem(position).toString();

                CurrentOrder().OrderQuality = QualityServiceTypeController.GetTypeFromLocaleName(strLocaleType);

                new TravelOrderController().update(CurrentOrder(),"updateOrder", new PostListener() {
                    @Override
                    public void onCompleted(Boolean success, BaseModel item) {

                        if(success && item!= null)
                            SCONNECTING.orderManager.currentOrder = (TravelOrder)item;
                        parentScreen.invalidateOrderCreation(false,null);
                    }
                });

            }
        });

        pickupDate = (EditText) view.findViewById(R.id.pickupDate);
        pickupDate.setInputType(InputType.TYPE_NULL);
        pickupDate.setTextColor(Color.WHITE);
        pickupDate.setBackgroundResource(R.drawable.textedit_blue);

        pickupDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        lblPickupDate = (TextView) view.findViewById(R.id.lblPickupDate);

        pickupTime = (EditText) view.findViewById(R.id.pickupTime);
        pickupTime.setInputType(InputType.TYPE_NULL);
        pickupTime.setBackgroundResource(R.drawable.textedit_blue);
        pickupTime.setTextColor(Color.WHITE);

        pickupTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });

        lblPickupTime = (TextView) view.findViewById(R.id.lblPickupTime);

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this.getContext(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar cal = Calendar.getInstance();

                if (CurrentOrder().OrderPickupTime == null) {

                    cal.set(year, monthOfYear, dayOfMonth);

                }else {

                    cal.set(year, monthOfYear, dayOfMonth,CurrentOrder().OrderPickupTime.getHours(),CurrentOrder().OrderPickupTime.getMinutes());
                }

                CurrentOrder().OrderPickupTime = cal.getTime();

                new TravelOrderController().update(CurrentOrder(),"updateOrder", new PostListener() {
                    @Override
                    public void onCompleted(Boolean success, BaseModel item) {

                        if(success && item!= null)
                            SCONNECTING.orderManager.currentOrder = (TravelOrder)item;

                        parentScreen.invalidateOrderCreation(false,null);
                    }
                });

            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                CurrentOrder().OrderPickupTime = null;

                new TravelOrderController().update(CurrentOrder(),"updateOrder", new PostListener() {
                    @Override
                    public void onCompleted(Boolean success, BaseModel item) {

                        if(success && item!= null)
                            SCONNECTING.orderManager.currentOrder = (TravelOrder)item;

                        parentScreen.invalidateOrderCreation(false,null);
                    }
                });

            }
        });

        timePickerDialog = new TimePickerDialog(this.getContext(), new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                Calendar cal = Calendar.getInstance();

                if (CurrentOrder().OrderPickupTime != null) {

                    cal.set(CurrentOrder().OrderPickupTime.getYear() + 1900, CurrentOrder().OrderPickupTime.getMonth(), CurrentOrder().OrderPickupTime.getDate(),hourOfDay,minute,0);

                }

                CurrentOrder().OrderPickupTime = cal.getTime();

                new TravelOrderController().update(CurrentOrder(),"updateOrder", new PostListener() {
                    @Override
                    public void onCompleted(Boolean success, BaseModel item) {

                        if(success && item!= null)
                            SCONNECTING.orderManager.currentOrder = (TravelOrder)item;

                        parentScreen.invalidateOrderCreation(false,null);
                    }
                });


            }

        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(this.getContext()));

        timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                CurrentOrder().OrderPickupTime = null;

                new TravelOrderController().update(CurrentOrder(),"updateOrder", new PostListener() {
                    @Override
                    public void onCompleted(Boolean success, BaseModel item) {

                        if(success && item!= null)
                            SCONNECTING.orderManager.currentOrder = (TravelOrder)item;

                        parentScreen.invalidateOrderCreation(false,null);
                    }
                });

            }
        });


        driverTable = new LateBiddingTable((RecyclerView) view.findViewById(R.id.driverTable),(SwipeRefreshLayout) view.findViewById(R.id.refreshControl));

        if(listener != null)
            listener.onCompleted();

    }


    public void invalidate( final Completion listener){

        if(view == null){

            if(listener != null)
                listener.onCompleted();
            return;
        }

        invalidateUI(parentScreen.mCustomOrderView.isShowLateConfig,listener);

    }


    public void invalidateUI( final Boolean isShow, final Completion listener){

        view.setVisibility(isShow? View.VISIBLE : View.GONE);

        if(!isShow){

            if(listener != null)
                listener.onCompleted();
            return;
        }

        invalidateFilterPanel(new Completion() {
            @Override
            public void onCompleted() {

                driverTable.refreshDriverList(new Completion() {
                    @Override
                    public void onCompleted() {

                        view.invalidate();
                        if(listener != null)
                            listener.onCompleted();
                    }
                });
            }
        });
    }


    void invalidateFilterPanel(final Completion listener){


        if (CurrentOrder().OrderPickupTime == null) {
            pickupDate.setText("");
            pickupTime.setText("");

        } else {
            pickupDate.setText(new java.text.SimpleDateFormat("dd/MM/yyyy").format(CurrentOrder().OrderPickupTime));

            if (CurrentOrder().OrderPickupTime.getHours() <= 12)
                pickupTime.setText(String.format("%02d : %02d AM", CurrentOrder().OrderPickupTime.getHours(), CurrentOrder().OrderPickupTime.getMinutes()));
            else
                pickupTime.setText(String.format("%02d : %02d PM", CurrentOrder().OrderPickupTime.getHours() - 12, CurrentOrder().OrderPickupTime.getMinutes()));
        }
        lblPickupDate.setText(pickupDate.getText());
        lblPickupTime.setText(pickupTime.getText());


        pickupDate.setVisibility(CurrentOrder().IsMateHost == 1 ? View.GONE : View.VISIBLE);
        lblPickupDate.setVisibility(CurrentOrder().IsMateHost == 0 ? View.GONE : View.VISIBLE);
        pickupTime.setVisibility(CurrentOrder().IsMateHost == 1 ? View.GONE : View.VISIBLE);
        lblPickupTime.setVisibility(CurrentOrder().IsMateHost == 0 ? View.GONE : View.VISIBLE);

        new VehicleTypeController().GetVehicleLocaleTypes(new GetListObjectsListener() {
            @Override
            public void onGetItems(Boolean success, List list) {

                if(success){

                    ArrayList<String> newList = new ArrayList<>();
                    newList.add(0,getResources().getString(R.string.all));
                    newList.addAll(list);
                    CharSequence[] arr = (CharSequence[]) newList.toArray(new CharSequence[newList.size()]);

                    ArrayAdapter arrayAdapter = new ArrayAdapter(view.getContext(),R.layout.dropdown_item,arr );
                    cbVehicleType.setAdapter(arrayAdapter);


                    if(CurrentOrder().OrderVehicleType == null){

                        cbVehicleType.setText(getResources().getString(R.string.vehicletype),false);

                    }else{

                        String strLocaleName =  VehicleTypeController.GetLocaleNameFromType(CurrentOrder().OrderVehicleType);
                        cbVehicleType.setText(strLocaleName,false);
                    }
                    cbVehicleType.invalidate();

                }

            }
        });


        new QualityServiceTypeController().GetQualityServiceLocaleTypes(new GetListObjectsListener() {
            @Override
            public void onGetItems(Boolean success, List list) {

                if(success){

                    ArrayList<String> newList = new ArrayList<>();
                    newList.add(0,getResources().getString(R.string.all));
                    newList.addAll(list);
                    CharSequence[] arr = (CharSequence[]) newList.toArray(new CharSequence[newList.size()]);

                    ArrayAdapter arrayAdapter = new ArrayAdapter(view.getContext(),R.layout.dropdown_item,arr );
                    cbQuality.setAdapter(arrayAdapter);

                    if(CurrentOrder().OrderQuality == null){

                        cbQuality.setText(getResources().getString(R.string.qualityservice),false);

                    }else{

                        String strLocaleName =  QualityServiceTypeController.GetLocaleNameFromType(CurrentOrder().OrderQuality);
                        cbQuality.setText(strLocaleName,false);
                    }
                    cbQuality.invalidate();


                }

            }
        });

        if(listener != null)
            listener.onCompleted();
    }

}
