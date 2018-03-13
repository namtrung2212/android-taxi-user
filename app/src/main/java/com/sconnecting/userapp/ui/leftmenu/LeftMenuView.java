package com.sconnecting.userapp.ui.leftmenu;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sconnecting.userapp.base.BaseActivity;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.data.storages.server.ServerStorage;
import com.sconnecting.userapp.base.ImageHelper;
import com.sconnecting.userapp.base.listener.Completion;

/**
 * Created by TrungDao on 8/1/16.
 */

public class LeftMenuView extends Fragment {

    View view;

    BaseActivity parent;
    LeftMenuList menuList;

    ImageView imgAvatar;
    TextView lblUserName;


    public LeftMenuView() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        parent = (BaseActivity) context;
        parent.mLeftMenuView = this;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.base_leftmenu, container, false);

        initControls(new Completion(){

            @Override
            public void onCompleted() {

            }
        });
        return view;
    }


    public void initControls(final Completion listener) {

        view.setVisibility(View.VISIBLE);


        lblUserName = (TextView) view.findViewById(R.id.lblUserName);
        imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);

        if(SCONNECTING.userManager != null && SCONNECTING.userManager.CurrentUser != null && SCONNECTING.userManager.CurrentUser.id != null){

            String url = ServerStorage.ServerURL + "/avatar/user/" + SCONNECTING.userManager.CurrentUser.id;
            ImageHelper.loadImage(parent, url, R.drawable.avatar, 250, 250, imgAvatar,null);

        }

        if(SCONNECTING.userManager != null && SCONNECTING.userManager.CurrentUser != null && SCONNECTING.userManager.CurrentUser.Name != null){
            lblUserName.setText(SCONNECTING.userManager.CurrentUser.Name.toUpperCase());
        }


        menuList = new LeftMenuList((RecyclerView) view.findViewById(R.id.mainMenu));


        if(listener != null)
            listener.onCompleted();

    }


    public void reloadMenu( ) {

        menuList.reloadData();
    }


}

