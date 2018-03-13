package com.sconnecting.userapp.ui.taxi.tripmate.host.requestingmembers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.data.storages.server.ServerStorage;
import com.sconnecting.userapp.base.AnimationHelper;
import com.sconnecting.userapp.base.ImageHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.base.DateTimeHelper;
import com.sconnecting.userapp.base.RegionalHelper;
import com.sconnecting.userapp.data.models.TravelOrder;
import com.squareup.picasso.Callback;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by TrungDao on 8/17/16.
 */

public  class RequestingMemberCell extends RecyclerView.ViewHolder {

    public View cellView;
    public CircularImageView imgAvatar;
    public TextView lblUserName;
    public TextView lblPickupPlace;
    public TextView lblDropPlace;
    public TextView lblPickupTime;
    public TextView lblMemberQty;
    public TextView lblMateOrderPrice;
    public TextView lblMateBenifitRatio;
    public TextView lblMateDistanceIncreaseRatio;

    TravelOrder memberOrder;

    public RequestingMemberCell(View view) {
        super(view);

        cellView = view;

        imgAvatar = (CircularImageView) view.findViewById(R.id.imgAvatar);
        imgAvatar.setVisibility(View.INVISIBLE);
        lblUserName = (TextView) view.findViewById(R.id.lblUserName);
        lblPickupPlace = (TextView) view.findViewById(R.id.lblPickupPlace);
        lblDropPlace = (TextView) view.findViewById(R.id.lblDropPlace);
        lblPickupTime = (TextView) view.findViewById(R.id.lblPickupTime);
        lblMemberQty = (TextView) view.findViewById(R.id.lblMemberQty);
        lblMateOrderPrice = (TextView) view.findViewById(R.id.lblMateOrderPrice);
        lblMateBenifitRatio = (TextView) view.findViewById(R.id.lblMateBenifitRatio);
        lblMateDistanceIncreaseRatio = (TextView) view.findViewById(R.id.lblMateDistanceIncreaseRatio);

    }


    public interface OnItemClickListener {
        void onItemClick(TravelOrder item);
    }

    OnItemClickListener mOnItemClickListener;
    public void bind(final TravelOrder order, final OnItemClickListener listener){

        mOnItemClickListener = listener;
        memberOrder = order;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        mOnItemClickListener.onItemClick(memberOrder);
                    }
                });
            }
        });

    }

    public void updateWithModel(TravelOrder order){

        memberOrder = order;

        lblUserName.setText( memberOrder.UserName);


        String url = memberOrder.User != null ? ServerStorage.ServerURL + "/avatar/user/" + memberOrder.User.toString()  : "";

        ImageHelper.loadImage(cellView.getContext(), url, R.drawable.avatar, 250, 250, imgAvatar, new Callback() {
            @Override
            public void onSuccess() {

                imgAvatar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onError() {

                imgAvatar.setVisibility(View.VISIBLE);

            }
        });

        lblPickupPlace.setText(memberOrder.OrderPickupPlace != null ?  memberOrder.OrderPickupPlace : "");
        lblDropPlace.setText( memberOrder.OrderDropPlace != null ?  memberOrder.OrderDropPlace : "");
        lblMemberQty.setText(String.format("%d người",memberOrder.MembersQty));
        updatePickupTime();
        updateEstPrice();


    }


    public void updatePickupTime(){

        Date date = null;

        if(memberOrder.OrderPickupTime != null)
            date = memberOrder.OrderPickupTime;



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

    public void updateEstPrice(){

        TravelOrder hostOrder = SCONNECTING.orderManager.currentOrder;

        lblMateOrderPrice.setText(RegionalHelper.toCurrencyOfCountry(memberOrder.MateOrderPrice,memberOrder.OrderPickupCountry));


        double dBenifitRatio = 0.0;
        if(hostOrder.OrderPrice != null && hostOrder.OrderPrice > 0 &&
                memberOrder.MateOrderPrice != null && memberOrder.MateOrderPrice >= 0){
            dBenifitRatio = memberOrder.MateOrderPrice/hostOrder.OrderPrice;
        }

        dBenifitRatio = dBenifitRatio * 100;
        lblMateBenifitRatio.setText(String.format("%d",(long)dBenifitRatio) + " %");


        double dDistanceIncrease = 0.0;
        if(hostOrder.OrderDistance != null && memberOrder.HostOrderDistance != null)
            dDistanceIncrease = (memberOrder.HostOrderDistance - hostOrder.OrderDistance)/hostOrder.OrderDistance;

        dDistanceIncrease = dDistanceIncrease * 100;
        lblMateDistanceIncreaseRatio.setText(String.format("%d",(long)dDistanceIncrease)  + " %");


    }
}
