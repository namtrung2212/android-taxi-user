package com.sconnecting.userapp.ui.taxi.order.creation.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

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
import com.sconnecting.userapp.ui.taxi.order.creation.custom.choosedriver.ChooseDriverTable;
import com.sconnecting.userapp.data.models.TravelOrder;
import com.sconnecting.userapp.base.ui.NTSpinner;

import java.util.ArrayList;
import java.util.List;

import static java.lang.StrictMath.round;


/**
 * Created by TrungDao on 8/1/16.
 */

public class ChooseDriverView extends Fragment{

    View view;

    OrderScreen parentScreen;
    ChooseDriverTable driverTable;
    NTSpinner cbVehicleType;
    NTSpinner cbQuality;

    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }


    public ChooseDriverView() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        parentScreen = (OrderScreen) context;
        parentScreen.mCustomOrderView.mChooseDriverView = this;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        try {
            view = inflater.inflate(R.layout.taxi_order_creation_custom_choosedriver, container, false);
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



        driverTable = new ChooseDriverTable((RecyclerView) view.findViewById(R.id.driverTable),(SwipeRefreshLayout) view.findViewById(R.id.refreshControl));

        if(listener != null)
            listener.onCompleted();

    }


    public void invalidate(final Completion listener){

        if(view == null){

            if(listener != null)
                listener.onCompleted();
            return;
        }

        invalidateUI(parentScreen.mCustomOrderView.isShowChooseDriver,listener);

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

                }

            }
        });


        if(listener != null)
            listener.onCompleted();
    }




}
