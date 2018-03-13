package com.sconnecting.userapp.ui.taxi.order.creation.custom.tripmate;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.sconnecting.userapp.base.RegionalHelper;
import com.sconnecting.userapp.data.controllers.QualityServiceTypeController;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.data.controllers.VehicleTypeController;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;
import com.sconnecting.userapp.ui.taxi.tripmate.member.searchhost.SearchHostScreen;
import com.sconnecting.userapp.data.models.MateStatus;
import com.sconnecting.userapp.data.models.TravelOrder;
import com.sconnecting.userapp.base.ui.NTSpinner;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.view.Gravity.CENTER;
import static android.view.View.TEXT_ALIGNMENT_CENTER;

/**
 * Created by TrungDao on 10/22/16.
 */

public class TripMateSearchConfigView extends Fragment {


    View view;
    OrderScreen parentScreen;


    NTSpinner cbMemberQty;

    EditText pickupDate;
    EditText pickupTime;

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;


    NTSpinner cbVehicleType;
    NTSpinner cbQuality;

    TextView lblOrderPrice;

    Button btnSearchHostOrder;


    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }



    private static final String[] MEMBER_QUANTITIES = new String[] {"1","2","3","4","5","6","7","8","9","10"
    };

    private static final String[] PICKUP_TIMES = new String[] {"Now","Later"
    };


    public TripMateSearchConfigView() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        parentScreen = (OrderScreen) context;
        parentScreen.mCustomOrderView.mTripMateConfigView.mTripMateSearchConfigView = this;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        try {
            view = inflater.inflate(R.layout.taxi_order_creation_custom_tripmate_searchconfig, container, false);
        } catch (InflateException e) {
        }


        initControls(null);

        return view;
    }


    public void initControls(final Completion listener) {

        cbMemberQty = (NTSpinner) view.findViewById(R.id.cbMemberQty);
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.memberquantities, R.layout.dropdown_item);
        cbMemberQty.setAdapter(arrayAdapter);
        cbMemberQty.setHideUnderline(true);
        cbMemberQty.setBackgroundResource(R.drawable.textedit_blue);
        cbMemberQty.setDropDownHeight(490);
        cbMemberQty.setDropDownBackgroundDrawable(new ColorDrawable(0xFFE9E9E9));
        cbMemberQty.setTextColor(Color.WHITE);
        cbMemberQty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AnimationHelper.animateButton(view);
                cbMemberQty.dismissDropDown();

                CurrentOrder().MembersQty = Integer.valueOf(MEMBER_QUANTITIES[position]);

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

        lblOrderPrice = (TextView)view.findViewById(R.id.lblOrderPrice);

        btnSearchHostOrder = (Button) view.findViewById(R.id.btnSearchHostOrder);
        btnSearchHostOrder.setGravity(CENTER);
        btnSearchHostOrder.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        btnSearchHostOrder.setText("TÌM NHÓM PHÙ HỢP");
        AnimationHelper.setOnClick(btnSearchHostOrder, new Completion() {
            @Override
            public void onCompleted() {


                Intent intent = new Intent(view.getContext(), SearchHostScreen.class);

                Parcelable wrappedCurrentOrder = Parcels.wrap(CurrentOrder());
                intent.putExtra("CurrentOrder",wrappedCurrentOrder);

                ((Activity)view.getContext()).startActivity(intent);
                ((Activity)view.getContext()).overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);

            }
        });



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

        if(listener != null)
            listener.onCompleted();

    }


    public void invalidate(final Completion listener){

        if(view == null){

            if(listener != null)
                listener.onCompleted();
            return;
        }

        boolean isRejected = (CurrentOrder().MateStatus != null &&  CurrentOrder().MateStatus.equals(MateStatus.Rejected));
        boolean isVoidedByHost = (CurrentOrder().MateStatus != null &&  CurrentOrder().MateStatus.equals(MateStatus.VoidedByHost));
        boolean isClosed = (CurrentOrder().MateStatus != null &&  CurrentOrder().MateStatus.equals(MateStatus.Closed));

        Boolean isShow = CurrentOrder().IsMateHost == 0 && ( CurrentOrder().MateStatus == null || isRejected || isVoidedByHost );

        invalidateUI(isShow,listener);

    }


    public void invalidateUI(Boolean isShow , final Completion listener){

        view.setVisibility(isShow? View.VISIBLE : View.GONE);

        if(!isShow){

            if(listener != null)
                listener.onCompleted();
            return;
        }

        for (int i = 0; i < MEMBER_QUANTITIES.length; i++) {

            String strValue = MEMBER_QUANTITIES[i];
            if(strValue == null && CurrentOrder().MembersQty == null){

                CurrentOrder().MembersQty = 1;
                cbMemberQty.setText(cbMemberQty.getAdapter().getItem(0).toString(),false);
                break;

            }else if(strValue != null && CurrentOrder().MembersQty != null){
                if(strValue.equals(CurrentOrder().MembersQty.toString())){

                    if(cbMemberQty.getAdapter().getCount() > i) {
                        cbMemberQty.setText(cbMemberQty.getAdapter().getItem(i).toString(),false);
                        break;
                    }
                }
            }

        }


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

                        cbVehicleType.setText(getResources().getString(R.string.all),false);

                    }else{

                        String strLocaleName =  VehicleTypeController.GetLocaleNameFromType(CurrentOrder().OrderVehicleType);
                        cbVehicleType.setText(strLocaleName,false);
                    }

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

                        cbQuality.setText(getResources().getString(R.string.all),false);

                    }else{

                        String strLocaleName =  QualityServiceTypeController.GetLocaleNameFromType(CurrentOrder().OrderQuality);
                        cbQuality.setText(strLocaleName,false);
                    }
                }

            }
        });


        lblOrderPrice.setText((CurrentOrder().OrderPrice != null && CurrentOrder().OrderPrice >= 0) ? RegionalHelper.toCurrency(CurrentOrder().OrderPrice,CurrentOrder().Currency) : "");

        if(listener != null)
            listener.onCompleted();
    }
}
