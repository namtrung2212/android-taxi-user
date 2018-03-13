package com.sconnecting.userapp.ui.taxi.order.creation.custom.lateconfig;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.data.storages.server.ServerStorage;
import com.sconnecting.userapp.base.AnimationHelper;
import com.sconnecting.userapp.base.ImageHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.base.RegionalHelper;
import com.squareup.picasso.Callback;

/**
 * Created by TrungDao on 8/7/16.
 */


public class LateBiddingCell extends RecyclerView.ViewHolder {

    public View cellView;
    public CircularImageView imgAvatar;
    public TextView lblDriverName;
    public TextView lblCompany;
    public TextView lblSeaterAndQuality;
    public TextView lblRateCount;
    public TextView lblEstPrice;
    public ImageView imgStar1;
    public ImageView imgStar2;
    public ImageView imgStar3;
    public ImageView imgStar4;
    public ImageView imgStar5;


    public interface OnItemClickListener {
        void onItemClick(LateBiddingObject item);
    }

    public LateBiddingCell(View view) {
        super(view);


        cellView = view;

        imgAvatar = (CircularImageView) view.findViewById(R.id.imgAvatar);
        imgAvatar.setVisibility(View.INVISIBLE);
        lblDriverName = (TextView) view.findViewById(R.id.lblDriverName);
        lblCompany = (TextView) view.findViewById(R.id.lblCompany);
        lblSeaterAndQuality = (TextView) view.findViewById(R.id.lblSeaterAndQuality);
        lblRateCount = (TextView) view.findViewById(R.id.lblRateCount);
        lblEstPrice = (TextView) view.findViewById(R.id.lblEstPrice);
        imgStar1 = (ImageView) view.findViewById(R.id.imgStar1);
        imgStar2 = (ImageView) view.findViewById(R.id.imgStar2);
        imgStar3 = (ImageView) view.findViewById(R.id.imgStar3);
        imgStar4 = (ImageView) view.findViewById(R.id.imgStar4);
        imgStar5 = (ImageView) view.findViewById(R.id.imgStar5);
    }

    public void bind(final LateBiddingObject item, final OnItemClickListener listener) {

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


    public  void updateWithModel(LateBiddingObject obj) {

        lblDriverName.setText( obj.driver.Name);


        String url = obj.driverId != null ? ServerStorage.ServerURL + "/avatar/driver/" + obj.driverId.toString()  : "";

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


        imgStar1.setVisibility(( obj.driverStatus != null && obj.driverStatus.Rating >= 1) ? View.VISIBLE : View.INVISIBLE);
        imgStar2.setVisibility(( obj.driverStatus != null && obj.driverStatus.Rating >= 2) ? View.VISIBLE : View.INVISIBLE);
        imgStar3.setVisibility(( obj.driverStatus != null && obj.driverStatus.Rating >= 3) ? View.VISIBLE : View.INVISIBLE);
        imgStar4.setVisibility(( obj.driverStatus != null && obj.driverStatus.Rating >= 4) ? View.VISIBLE : View.INVISIBLE);
        imgStar5.setVisibility(( obj.driverStatus != null && obj.driverStatus.Rating >= 5) ? View.VISIBLE : View.INVISIBLE);

        lblRateCount.setVisibility(( obj.driverStatus != null && obj.driverStatus.RateCount >= 0) ? View.VISIBLE : View.INVISIBLE);
        lblRateCount.setText((obj.driverStatus != null) ? obj.driverStatus.RateCount.toString() + " lượt" : "");

        lblDriverName.setText((obj.driver != null) ? obj.driver.Name.toString().toUpperCase() : "");
        lblCompany.setText((obj.driverStatus != null &&  obj.driverStatus.CompanyName != null) ? obj.driverStatus.CompanyName.toString() : "");

        String qualityText = "";
        if( obj.driverStatus != null){

            switch (obj.driverStatus.QualityService ){
                case "Popular" : qualityText = "Phổ Thông";
                    break;
                case "Economic" : qualityText = "Giá Rẻ";
                    break;
                case "Luxury" : qualityText = "Chất Lượng Cao";
                    break;
                default: qualityText = "chưa rõ";
            }

        }

        if(obj.driverStatus != null && obj.driverStatus.VehicleType != null){

            switch (obj.driverStatus.VehicleType){
                case "Car4" :
                    lblSeaterAndQuality.setText("4 chỗ - " + qualityText);
                    break;
                case "Car7" :
                    lblSeaterAndQuality.setText("7 chỗ - " + qualityText);
                    break;
                case "Car8" :
                    lblSeaterAndQuality.setText("8 chỗ - " + qualityText);
                    break;
                case "Car9" :
                    lblSeaterAndQuality.setText("9 chỗ - " + qualityText);
                    break;
                case "Car11" :
                    lblSeaterAndQuality.setText("11 chỗ - " + qualityText);
                    break;
                case "Car16" :
                    lblSeaterAndQuality.setText("16 chỗ - " + qualityText);
                    break;
                default:
                    lblSeaterAndQuality.setText("");
            }
        }else{

            lblSeaterAndQuality.setText("");
        }


        updateEstPrice(obj);

    }

    public void updateEstPrice(final LateBiddingObject obj ){
        if(obj.estPrice == null){

            obj.loadEstPrice(new Completion() {
                @Override
                public void onCompleted() {

                    lblEstPrice.setText((obj.estPrice != null && obj.estPrice > 0) ? RegionalHelper.toCurrencyOfCountry(obj.estPrice,obj.driver.Country) : "");
                }
            });

        }
        lblEstPrice.setText((obj.estPrice != null && obj.estPrice > 0) ? RegionalHelper.toCurrencyOfCountry(obj.estPrice,obj.driver.Country) : "");
    }

}