package com.sconnecting.userapp.ui.taxi.tripmate.member.searchhost;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.AnimationHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.base.DateTimeHelper;
import com.sconnecting.userapp.base.RegionalHelper;
import com.sconnecting.userapp.data.controllers.QualityServiceTypeController;
import com.sconnecting.userapp.data.controllers.VehicleTypeController;
import com.sconnecting.userapp.data.models.TravelOrder;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by TrungDao on 8/17/16.
 */

public  class SearchHostCell extends RecyclerView.ViewHolder {

    public View cellView;
    public TextView lblPickupPlace;
    public TextView lblDropPlace;
    public TextView lblPickupTime;
    public TextView lblRemainMemberQty;
    public TextView lblSeaterAndQuality;
    public TextView lblMateOrderPrice;
    public TextView lblMateBenifit;
    public TextView lblMateLowestPrice;
    public TextView lblDescMateLowestPrice;

    SearchHostObject hostOrder;

    public SearchHostCell(View view) {
        super(view);

        cellView = view;

        lblPickupPlace = (TextView) view.findViewById(R.id.lblPickupPlace);
        lblDropPlace = (TextView) view.findViewById(R.id.lblDropPlace);
        lblPickupTime = (TextView) view.findViewById(R.id.lblPickupTime);
        lblRemainMemberQty = (TextView) view.findViewById(R.id.lblRemainMemberQty);
        lblSeaterAndQuality = (TextView) view.findViewById(R.id.lblSeaterAndQuality);
        lblMateOrderPrice = (TextView) view.findViewById(R.id.lblMateOrderPrice);
        lblMateBenifit = (TextView) view.findViewById(R.id.lblMateBenifit);
        lblMateLowestPrice = (TextView) view.findViewById(R.id.lblMateLowestPrice);
        lblDescMateLowestPrice = (TextView) view.findViewById(R.id.lblDescMateLowestPrice);

    }


    public interface OnItemClickListener {
        void onItemClick(SearchHostObject item);
    }

    OnItemClickListener mOnItemClickListener;
    public void bind(final SearchHostObject host, final OnItemClickListener listener){

        mOnItemClickListener = listener;
        hostOrder = host;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        mOnItemClickListener.onItemClick(hostOrder);
                    }
                });
            }
        });

    }

    public void updateWithModel(SearchHostObject host){

        hostOrder = host;

        lblPickupPlace.setText(host.OrderPickupPlace != null ?  host.OrderPickupPlace : "");
        lblDropPlace.setText( host.OrderDropPlace != null ?  host.OrderDropPlace : "");
        updatePickupTime(host);
        updateRemainMemberQuality(host);
        updateSeaterAndQuality(host);
        updateEstPrice(host);


    }

    private void updateSeaterAndQuality(SearchHostObject host){

        String vehicleType = "";
        String qualityType = "";

        if(host != null && host.OrderQuality != null)
            qualityType = QualityServiceTypeController.GetLocaleNameFromType(host.OrderQuality);

        if(host != null && host.OrderVehicleType != null)
            vehicleType = VehicleTypeController.GetLocaleNameFromType(host.OrderVehicleType);

        if(vehicleType.isEmpty() == false && qualityType.isEmpty() == true)
            lblSeaterAndQuality.setText(vehicleType );

        else if(vehicleType.isEmpty() == true && qualityType.isEmpty() == false)
            lblSeaterAndQuality.setText(qualityType);

        else if(vehicleType.isEmpty() == false && qualityType.isEmpty() == false)
            lblSeaterAndQuality.setText(vehicleType + " - " + qualityType);
        else
            lblSeaterAndQuality.setText("Chưa rõ");


    }

    public void updatePickupTime(final SearchHostObject host){

        Date date = null;

        if(host.OrderPickupTime != null)
            date = host.OrderPickupTime;



        if(date == null){
            lblPickupTime.setText( "");
            return;
        }

        String strPickupTime = "";
        if(DateTimeHelper.isNow(date,5)){

            strPickupTime = "ngay bây giờ.";

        }else{

            long seconds = (date.getTime() - new Date().getTime()) / 1000;
            long hours =  (seconds / 3600);
            long minutes =  (Math.round((seconds % 3600) / 60));

            if (DateTimeHelper.isToday(date) && hours <= 1 && ((seconds > 0) || ( seconds <= 0 && Math.abs(seconds)<=60))){

                if( hours >= 1){
                    strPickupTime =  String.format("sau %d giờ, %d phút", hours, minutes ) ;
                }else{
                    strPickupTime =  String.format("sau %d phút", minutes );
                }

            }else {


                String strDate =  new SimpleDateFormat("HH:mm").format(date);

                if(DateTimeHelper.isToday(date)){

                    strPickupTime = strDate + " hôm nay";

                }else if(DateTimeHelper.isYesterday(date)){

                    strPickupTime = strDate + " hôm qua";

                }else if(DateTimeHelper.isTomorrow(date)){

                    strPickupTime = strDate + " ngày mai";

                }else{

                    String strDate2 =  new SimpleDateFormat("dd/MM").format(date);
                    strPickupTime = strDate + " ngày " + strDate2;
                }

            }
        }

        lblPickupTime.setText(strPickupTime);
    }

    private void updateRemainMemberQuality(SearchHostObject host){

        int iRemainMin = host.MinRemainMemberQty.intValue();
        if(iRemainMin < 0 )
            iRemainMin = 0;

        int iRemainMax = host.MaxRemainMemberQty.intValue();
        if(iRemainMax < 0 )
            iRemainMax = 0;

        if(iRemainMin > 0 && iRemainMax > 0)
            if(iRemainMin != iRemainMax)
                lblRemainMemberQty.setText(String.format("Còn %d đến %d chỗ", iRemainMin,iRemainMax));
            else
                lblRemainMemberQty.setText(String.format("Còn %d chỗ", iRemainMax));

        else if(iRemainMin == 0 && iRemainMax > 0)
            lblRemainMemberQty.setText(String.format("Còn %d chỗ", iRemainMax));
        else
            lblRemainMemberQty.setText("Hết chỗ");

    }


    public void updateEstPrice(final SearchHostObject host){

        TravelOrder order = SCONNECTING.orderManager.currentOrder;

        lblMateOrderPrice.setText(RegionalHelper.toCurrencyOfCountry(host.MateOrderPrice,order.OrderPickupCountry));
        lblMateBenifit.setText("-" + RegionalHelper.toCurrencyOfCountry(host.MateBenifit,order.OrderPickupCountry));

        if(host.MateLowestPrice != null && host.MateLowestPrice > 0)
            lblMateLowestPrice.setText(RegionalHelper.toCurrencyOfCountry(host.MateLowestPrice, order.OrderPickupCountry));

        lblMateLowestPrice.setVisibility(host.MateLowestPrice != null && host.MateLowestPrice > 0 && host.MateLowestPrice < host.MateOrderPrice ? View.VISIBLE : View.INVISIBLE);
        lblDescMateLowestPrice.setVisibility(lblMateLowestPrice.getVisibility());

    }
}
