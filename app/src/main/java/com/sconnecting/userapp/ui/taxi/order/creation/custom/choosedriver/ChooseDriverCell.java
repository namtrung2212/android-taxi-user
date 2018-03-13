package com.sconnecting.userapp.ui.taxi.order.creation.custom.choosedriver;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.data.storages.server.ServerStorage;
import com.sconnecting.userapp.base.AnimationHelper;
import com.sconnecting.userapp.base.ImageHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.base.RegionalHelper;
import com.sconnecting.userapp.data.controllers.QualityServiceTypeController;
import com.sconnecting.userapp.data.controllers.VehicleTypeController;
import com.squareup.picasso.Callback;

/**
 * Created by TrungDao on 8/7/16.
 */


public class ChooseDriverCell extends RecyclerView.ViewHolder {

    public View cellView;
    public CircularImageView imgAvatar;
    public TextView lblDriverName;
    public TextView lblCompany;
    public TextView lblSeaterAndQuality;
    public TextView lblRateCount;
    public TextView lblEstPrice;
    public TextView lblDistance;
    public ImageView imgStar1;
    public ImageView imgStar2;
    public ImageView imgStar3;
    public ImageView imgStar4;
    public ImageView imgStar5;


    public interface OnItemClickListener {
        void onItemClick(ChooseDriverObject item);
    }

    public ChooseDriverCell(View view) {
        super(view);


        cellView = view;

        imgAvatar = (CircularImageView) view.findViewById(R.id.imgAvatar);
        imgAvatar.setVisibility(View.INVISIBLE);
        lblDriverName = (TextView) view.findViewById(R.id.lblDriverName);
        lblCompany = (TextView) view.findViewById(R.id.lblCompany);
        lblSeaterAndQuality = (TextView) view.findViewById(R.id.lblSeaterAndQuality);
        lblRateCount = (TextView) view.findViewById(R.id.lblRateCount);
        lblEstPrice = (TextView) view.findViewById(R.id.lblEstPrice);
        lblDistance = (TextView) view.findViewById(R.id.lblDistance);
        imgStar1 = (ImageView) view.findViewById(R.id.imgStar1);
        imgStar2 = (ImageView) view.findViewById(R.id.imgStar2);
        imgStar3 = (ImageView) view.findViewById(R.id.imgStar3);
        imgStar4 = (ImageView) view.findViewById(R.id.imgStar4);
        imgStar5 = (ImageView) view.findViewById(R.id.imgStar5);
    }

    public void bind(final ChooseDriverObject item, final OnItemClickListener listener) {

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        listener.onItemClick(item);
                    }
                });
            }
        });
    }


    public  void updateWithModel(ChooseDriverObject obj) {

        lblDriverName.setText( obj.DriverName);


        String url = obj.DriverId != null ? ServerStorage.ServerURL + "/avatar/driver/" + obj.DriverId.toString()  : "";

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


        imgStar1.setVisibility(( obj.Rating >= 1) ? View.VISIBLE : View.INVISIBLE);
        imgStar2.setVisibility(( obj.Rating >= 2) ? View.VISIBLE : View.INVISIBLE);
        imgStar3.setVisibility(( obj.Rating >= 3) ? View.VISIBLE : View.INVISIBLE);
        imgStar4.setVisibility(( obj.Rating >= 4) ? View.VISIBLE : View.INVISIBLE);
        imgStar5.setVisibility(( obj.Rating >= 5) ? View.VISIBLE : View.INVISIBLE);

        lblRateCount.setVisibility(( obj.RateCount >= 0) ? View.VISIBLE : View.INVISIBLE);
        lblRateCount.setText((obj.RateCount != null) ? obj.RateCount.toString() + " lượt" : "");

        lblDriverName.setText((obj.DriverName != null) ? obj.DriverName.toString().toUpperCase() : "");
        lblCompany.setText((obj.CompanyName != null &&  obj.CompanyName != null) ? obj.CompanyName.toString() : "");

        updateSeaterAndQuality(obj);

        lblEstPrice.setText((obj.FinalPrice != null && obj.FinalPrice > 0) ? RegionalHelper.toCurrencyOfCountry(obj.FinalPrice, SCONNECTING.orderManager.currentOrder.OrderPickupCountry) : "");

        lblDistance.setText((obj.Distance  < 1 ) ? String.format("%.0f m", obj.Distance * 1000) :  String.format("%.1f Km", obj.Distance ) );


    }

    private void updateSeaterAndQuality(ChooseDriverObject obj) {

        String vehicleType = "";
        String qualityType = "";

        if(obj.QualityService != null)
            qualityType = QualityServiceTypeController.GetLocaleNameFromType(obj.QualityService);

        if(obj.VehicleType != null)
            vehicleType = VehicleTypeController.GetLocaleNameFromType(obj.VehicleType );

        if(vehicleType != null && vehicleType.isEmpty() == false && qualityType != null && qualityType.isEmpty() == false )
                lblSeaterAndQuality.setText(vehicleType + " - " + qualityType);
        else if(vehicleType != null && vehicleType.isEmpty() == false)
                lblSeaterAndQuality.setText(vehicleType);
        else if(qualityType != null && qualityType.isEmpty() == false)
            lblSeaterAndQuality.setText(qualityType);
        else
            lblSeaterAndQuality.setText("");


    }


}