package com.sconnecting.userapp.ui.taxi.tripmate.host.requestingmembers;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.ui.taxi.tripmate.host.HostScreen;
import com.sconnecting.userapp.data.models.TravelOrder;


/**
 * Created by TrungDao on 8/1/16.
 */

public class RequestingMembersView extends Fragment{

    View view;

    HostScreen parentScreen;
    RequestingMembersTable table;

    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }


    public RequestingMembersView() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        parentScreen = (HostScreen) context;
        parentScreen.mRequestingMembersView = this;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        try {
            view = inflater.inflate(R.layout.taxi_order_tripmate_host_requestingmembers, container, false);
        } catch (InflateException e) {
        }

        initControls(new Completion() {
            @Override
            public void onCompleted() {
                invalidate(null);
            }
        });

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


        table = new RequestingMembersTable((RecyclerView) view.findViewById(R.id.reqTable),(SwipeRefreshLayout) view.findViewById(R.id.reqRefreshControl));

        if(listener != null)
            listener.onCompleted();

    }


    public void invalidate(final Completion listener){

        if(view == null){

            if(listener != null)
                listener.onCompleted();
            return;
        }

        invalidateUI(parentScreen.isShowRequestingMembers,listener);

    }


    public void invalidateUI( final Boolean isShow, final Completion listener){

        view.setVisibility(isShow? View.VISIBLE : View.GONE);

        if(!isShow){

            if(listener != null)
                listener.onCompleted();
            return;
        }

        table.refreshDataList(new Completion() {
            @Override
            public void onCompleted() {

                view.invalidate();
                if(listener != null)
                    listener.onCompleted();

            }
        });

    }


}
