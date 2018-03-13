package com.sconnecting.userapp.ui.leftmenu;

import android.view.View;
import android.widget.TextView;

import com.sconnecting.userapp.R;

/**
 * Created by TrungDao on 8/16/16.
 */


public class LeftMenuCellGroup extends LeftMenuCell {

    public View cellView;

    public TextView leftIcon;
    public TextView lblTitle;


    public LeftMenuCellGroup(View view) {
        super(view);

        cellView = view;
        leftIcon = (TextView) view.findViewById(R.id.leftIcon);
        lblTitle = (TextView) view.findViewById(R.id.lblTitle);

    }

    @Override
    public void bind(LeftMenuObject item, OnItemClickListener listener) {

    }



    @Override
    public void updateWithModel(LeftMenuObject item) {


        lblTitle.setText(item.title.toUpperCase());

        if(item.leftIcon == null){

            leftIcon.setVisibility(View.GONE);

        }else {

            leftIcon.setVisibility(View.VISIBLE);

            if (item.section == 0) {
                leftIcon.setText("{fa-car}");

            } else if (item.section == 1) {
                leftIcon.setText("{fa-users}");

            } else if (item.section == 2) {
                leftIcon.setText(" ");

            }

        }


    }


}