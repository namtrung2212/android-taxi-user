package com.sconnecting.userapp.ui.taxi.tripmate.host.chatting;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.ImageHelper;
import com.sconnecting.userapp.data.storages.server.ServerStorage;
import com.squareup.picasso.Callback;

import static android.view.View.LAYOUT_DIRECTION_LTR;
import static android.view.View.LAYOUT_DIRECTION_RTL;

/**
 * Created by TrungDao on 8/26/16.
 */

public class TripMateChattingCell extends RecyclerView.ViewHolder {

    public View cellView;
    public CircularImageView imgAvatar;
    public TextView lblContent;
    public LinearLayout linearlayout;
    public View backgroundPanel;


    public TripMateChattingCell(View view) {
        super(view);


        cellView = view;

        linearlayout = (LinearLayout) view.findViewById(R.id.linearlayout);
        backgroundPanel =  view.findViewById(R.id.backgroundPanel);

        imgAvatar = (CircularImageView) view.findViewById(R.id.imgAvatar);
        imgAvatar.setVisibility(View.INVISIBLE);
        lblContent = (TextView) view.findViewById(R.id.lblContent);

    }


    public void updateWithModel(TripMateChattingObject obj) {


        this.updateAvatar(obj);

        this.updateContent(obj);

        Boolean isCurrentUser = (obj.cellObject.User != null && obj.cellObject.User.equals(SCONNECTING.userManager.CurrentUser.getId()));

        GradientDrawable draw = (GradientDrawable) backgroundPanel.getBackground();
        draw.setColor(Color.parseColor(isCurrentUser == false ? "#F7F7F7" : "#7DBBE9"));
        draw.setStroke(1 , Color.parseColor(isCurrentUser == false ? "#DADAD9" : "#7DBBE9"));

        linearlayout.setLayoutDirection(isCurrentUser == false ? LAYOUT_DIRECTION_LTR : LAYOUT_DIRECTION_RTL);

    }


    public void updateAvatar(TripMateChattingObject obj ){

        String url = obj.cellObject.User != null ? ServerStorage.ServerURL + "/avatar/user/" + obj.cellObject.User  : "";

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


    public void updateContent(TripMateChattingObject obj){

        lblContent.setText(obj.cellObject.Content);

    }



}