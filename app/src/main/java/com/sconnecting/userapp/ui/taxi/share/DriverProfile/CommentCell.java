package com.sconnecting.userapp.ui.taxi.share.DriverProfile;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluejamesbond.text.DocumentView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.data.storages.server.ServerStorage;
import com.sconnecting.userapp.base.ImageHelper;
import com.sconnecting.userapp.base.DateTimeHelper;
import com.sconnecting.userapp.data.models.TravelOrder;
import com.squareup.picasso.Callback;

/**
 * Created by TrungDao on 8/10/16.
 */


public class CommentCell extends RecyclerView.ViewHolder {

    public View cellView;
    public CircularImageView imgAvatar;
    public TextView lblUserName;
    public DocumentView lblComment;
    public TextView lblCreateDate;

    public ImageView imgStar1;
    public ImageView imgStar2;
    public ImageView imgStar3;
    public ImageView imgStar4;
    public ImageView imgStar5;


    public CommentCell(View view) {
        super(view);


        cellView = view;

        imgAvatar = (CircularImageView) view.findViewById(R.id.imgAvatar);
        imgAvatar.setVisibility(View.INVISIBLE);
        lblUserName = (TextView) view.findViewById(R.id.lblUserName);
        lblComment = (DocumentView) view.findViewById(R.id.lblComment);
        lblCreateDate = (TextView) view.findViewById(R.id.lblCreateDate);

        imgStar1 = (ImageView) view.findViewById(R.id.imgStar1);
        imgStar2 = (ImageView) view.findViewById(R.id.imgStar2);
        imgStar3 = (ImageView) view.findViewById(R.id.imgStar3);
        imgStar4 = (ImageView) view.findViewById(R.id.imgStar4);
        imgStar5 = (ImageView) view.findViewById(R.id.imgStar5);

    }


    public void updateWithModel(TravelOrder travelOrder) {


        this.updateAvatar(travelOrder);

        this.updateComment(travelOrder);

        this.updateRating(travelOrder);

        this.updateUserName(travelOrder);

        this.updateCreateTime(travelOrder);
    }

     public void updateUserName(TravelOrder travelOrder ){

        lblUserName.setText(travelOrder.UserName.toUpperCase());
    }

     public void updateAvatar(TravelOrder travelOrder ){

         String url = travelOrder.User != null ? ServerStorage.ServerURL + "/avatar/user/" + travelOrder.User  : "";

         ImageHelper.loadImage(cellView.getContext(), url, R.drawable.avatar, 100, 100, imgAvatar, new Callback() {
             @Override
             public void onSuccess() {

                 imgAvatar.setVisibility(View.VISIBLE);

             }

             @Override
             public void onError() {

                 imgAvatar.setVisibility(View.VISIBLE);

             }
         });


     }

     public void updateRating(TravelOrder travelOrder){

         ImageHelper.loadImage(cellView.getContext(),travelOrder.Rating < 1 ?  R.drawable.graystar : R.drawable.star ,32,32, imgStar1,null );
         ImageHelper.loadImage(cellView.getContext(),travelOrder.Rating < 2 ?  R.drawable.graystar : R.drawable.star ,32,32, imgStar2,null );
         ImageHelper.loadImage(cellView.getContext(),travelOrder.Rating < 3 ?  R.drawable.graystar : R.drawable.star ,32,32, imgStar3,null );
         ImageHelper.loadImage(cellView.getContext(),travelOrder.Rating < 4 ?  R.drawable.graystar : R.drawable.star ,32,32, imgStar4,null );
         ImageHelper.loadImage(cellView.getContext(),travelOrder.Rating < 5 ?  R.drawable.graystar : R.drawable.star ,32,32, imgStar5,null );

    }

     public void updateComment(TravelOrder travelOrder){

         lblComment.setText(travelOrder.Comment);
    }

     public void updateCreateTime(TravelOrder travelOrder){


        if(DateTimeHelper.isNow(travelOrder.createdAt,30)){
            lblCreateDate.setText( "vừa xong");

        }else if(DateTimeHelper.isToday(travelOrder.createdAt)){

            lblCreateDate.setText( "hôm nay");

        } else if(DateTimeHelper.isYesterday(travelOrder.createdAt)){

            lblCreateDate.setText( "hôm qua");

        }else if(DateTimeHelper.isCurrentYear(travelOrder.createdAt)){

            lblCreateDate.setText(DateTimeHelper.toString(travelOrder.createdAt,"dd/MM"));

        }else{

            lblCreateDate.setText( DateTimeHelper.toString(travelOrder.createdAt,"dd/MM/yyyy"));
        }
    }




}