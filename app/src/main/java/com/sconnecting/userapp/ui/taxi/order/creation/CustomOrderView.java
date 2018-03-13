package com.sconnecting.userapp.ui.taxi.order.creation;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;

import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.GetBoolValueListener;
import com.sconnecting.userapp.base.listener.GetOneListener;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.base.AnimationHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.base.DateTimeHelper;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;
import com.sconnecting.userapp.ui.taxi.order.creation.custom.ChooseDriverView;
import com.sconnecting.userapp.ui.taxi.order.creation.custom.LateConfigView;
import com.sconnecting.userapp.ui.taxi.order.creation.custom.TripMateConfigView;
import com.sconnecting.userapp.data.models.MateStatus;
import com.sconnecting.userapp.data.models.TravelOrder;

import java.util.Date;

import static android.view.Gravity.CENTER;
import static android.view.View.TEXT_ALIGNMENT_CENTER;
import static android.view.View.VISIBLE;

/**
 * Created by TrungDao on 8/6/16.
 */

public class CustomOrderView  extends Fragment {


    View view;

    private FragmentTabHost mTabHost;
    public ChooseDriverView mChooseDriverView;
    public LateConfigView mLateConfigView;
    public TripMateConfigView mTripMateConfigView;

    public Boolean isShowChooseDriver = true;
    public Boolean isShowLateConfig = true;
    public Boolean isShowTripMateConfig = true;

    OrderScreen screen;

    View pnlOrderInfoArea;
    View pnlCustomArea;
    View pnlButtonArea;

    TextView lblPickupLocation;
    TextView lblDropLocation;
    Button btnCancelOrder;



    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }


    public CustomOrderView() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        screen = (OrderScreen) context;
        screen.mCustomOrderView = this;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        try {
            view = inflater.inflate(R.layout.taxi_order_creation_custom, container, false);
        } catch (InflateException e) {
        }

        initControls(new Completion(){

            @Override
            public void onCompleted() {

                invalidate(true,null);
            }
        });
        return view;
    }

    public void initControls(final Completion listener) {

        pnlOrderInfoArea = (View) view.findViewById(R.id.pnlOrderInfoArea);
        pnlCustomArea = (View) view.findViewById(R.id.pnlCustomArea);
        pnlButtonArea = (View) view.findViewById(R.id.pnlButtonArea);

        lblPickupLocation = (TextView) view.findViewById(R.id.lblPickupLocation);
        lblDropLocation = (TextView) view.findViewById(R.id.lblDropLocation);

        btnCancelOrder = (Button) view.findViewById(R.id.btnCancelOrder);

        btnCancelOrder.setGravity(CENTER);
        btnCancelOrder.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        btnCancelOrder.setText("HỦY YÊU CẦU");
        AnimationHelper.setOnClick(btnCancelOrder, new Completion() {
            @Override
            public void onCompleted() {

                SCONNECTING.orderManager.actionHandler.UserVoidOrder(CurrentOrder(), new GetOneListener() {
                    @Override
                    public void onGetOne(Boolean success, BaseModel item) {
                        if(success)
                            SCONNECTING.orderManager.reset(true, null);
                    }
                });
            }
        });

        mTabHost = (FragmentTabHost)view.findViewById(R.id.tabhost);
        mTabHost.setup(view.getContext(), getChildFragmentManager(), android.R.id.tabcontent);

        View tab = LayoutInflater.from(view.getContext()).inflate(R.layout.taxi_custom_tab, null);
        ((TextView)tab.findViewById(R.id.tab_icon)).setText("{fa-street-view}");
        ((TextView)tab.findViewById(R.id.tab_text)).setText("ĐI NGAY");
        mTabHost.addTab(mTabHost.newTabSpec("ChooseDriver").setIndicator(tab), new ChooseDriverView().getClass(), null);

        tab = LayoutInflater.from(view.getContext()).inflate(R.layout.taxi_custom_tab, null);
        ((TextView)tab.findViewById(R.id.tab_icon)).setText("{fa-clock-o}");
        ((TextView)tab.findViewById(R.id.tab_text)).setText("ĐẶT TRƯỚC");
        mTabHost.addTab(mTabHost.newTabSpec("LateConfig").setIndicator(tab), new LateConfigView().getClass(), null);

        tab = LayoutInflater.from(view.getContext()).inflate(R.layout.taxi_custom_tab, null);
        ((TextView)tab.findViewById(R.id.tab_icon)).setText("{fa-users}");
        ((TextView)tab.findViewById(R.id.tab_text)).setText("ĐI CHUNG");
        mTabHost.addTab(mTabHost.newTabSpec("TripMateConfig").setIndicator(tab), new TripMateConfigView().getClass(), null);

        mTabHost.getTabWidget().setStripEnabled(true);
        mTabHost.getTabWidget().setRightStripDrawable(R.color.transparent);
        mTabHost.getTabWidget().setLeftStripDrawable(R.color.transparent);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                if(mLateConfigView != null && tabId.equals("LateConfig"))
                    mLateConfigView.invalidate(null);

                if(mTripMateConfigView != null && tabId.equals("TripMateConfig"))
                    mTripMateConfigView.invalidate(null);

                if (mChooseDriverView != null && tabId.equals("ChooseDriver"))
                    mChooseDriverView.invalidate(null);


            }
        });

        view.setVisibility(View.GONE);

        if(listener != null)
            listener.onCompleted();

    }


    public void invalidate(final Boolean isFirstTime, final Completion listener){

        final Boolean isShow  = this.screen.isCustomOrderPhase();

        if(isShow) {

            screen.showToolbar(true);

            String strAddress = "";
            if(this.CurrentOrder().OrderPickupPlace != null)
                strAddress =  this.CurrentOrder().OrderPickupPlace.substring(0,  this.CurrentOrder().OrderPickupPlace.lastIndexOf(", "));
            this.lblPickupLocation.setText(strAddress);

            strAddress = "";
            if(this.CurrentOrder().OrderDropPlace != null)
                strAddress =  this.CurrentOrder().OrderDropPlace.substring(0,  this.CurrentOrder().OrderDropPlace.lastIndexOf(", "));
            this.lblDropLocation.setText(strAddress);

        }

        show(isShow,new GetBoolValueListener(){

            @Override
            public void onCompleted(Boolean success, Boolean changed) {

                if( isShow){

                    isShowChooseDriver = true;
                    isShowLateConfig = true;
                    isShowTripMateConfig = true;

                    if(CurrentOrder().OrderPickupLoc == null || CurrentOrder().OrderDropLoc == null){
                        isShowLateConfig = false;
                        isShowTripMateConfig = false;
                    }

                    if(CurrentOrder().IsMateHost == 0) { // member

                        if(CurrentOrder().MateHostOrder != null && CurrentOrder().MateHostOrder.isEmpty() == false) {

                            if (CurrentOrder().MateStatus.equals(MateStatus.Requested) || CurrentOrder().MateStatus.equals(MateStatus.Accepted)|| CurrentOrder().MateStatus.equals(MateStatus.Closed)) {

                                isShowChooseDriver = false;
                                isShowLateConfig = false;

                            }

                        }

                    }else{

                        if(CurrentOrder().MateStatus == null || CurrentOrder().MateStatus.equals(MateStatus.Closed) == false){

                            isShowChooseDriver = false;
                            isShowLateConfig = false;
                        }
                    }

                    if(CurrentOrder().OrderPickupTime != null
                            && (CurrentOrder().OrderPickupTime.getTime() < new Date().getTime())
                            && DateTimeHelper.isNow(CurrentOrder().OrderPickupTime,30) == false )
                        isShowChooseDriver = false;

                    String strCurrentTab = mTabHost.getCurrentTabTag();

                    mTabHost.getTabWidget().getChildAt(0).setVisibility(isShowChooseDriver ? View.VISIBLE : View.GONE);
                    mTabHost.getTabWidget().getChildAt(1).setVisibility(isShowLateConfig ? View.VISIBLE : View.GONE);
                    mTabHost.getTabWidget().getChildAt(2).setVisibility(isShowTripMateConfig ? View.VISIBLE : View.GONE);

                    if(changed){

                        if( isShowChooseDriver)
                            mTabHost.setCurrentTabByTag("ChooseDriver");

                        else if( isShowLateConfig)
                            mTabHost.setCurrentTabByTag("LateConfig");

                        else if ( isShowTripMateConfig)
                            mTabHost.setCurrentTabByTag("TripMateConfig");


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                String strNewTab = mTabHost.getCurrentTabTag();
                                if(mChooseDriverView != null && strNewTab.equals("ChooseDriver"))
                                        mChooseDriverView.invalidate(null);

                                if(mTripMateConfigView != null && strNewTab.equals("TripMateConfig"))
                                        mTripMateConfigView.invalidate(null);

                                if(mLateConfigView != null && strNewTab.equals("LateConfig"))
                                        mLateConfigView.invalidate(null);


                            }
                        }, 3000);

                    }else {

                        if (isShowChooseDriver ) {
                            if (mChooseDriverView == null)
                                mTabHost.setCurrentTabByTag("ChooseDriver");
                            else if(strCurrentTab.equals("ChooseDriver"))
                                mChooseDriverView.invalidate(null);
                        }

                        if (isShowTripMateConfig ) {
                            if (mTripMateConfigView == null)
                                mTabHost.setCurrentTabByTag("TripMateConfig");
                            else if(strCurrentTab.equals("TripMateConfig"))
                                mTripMateConfigView.invalidate(null);
                        }

                        if (isShowLateConfig) {
                            if (mLateConfigView == null)
                                mTabHost.setCurrentTabByTag("LateConfig");
                            else if(strCurrentTab.equals("LateConfig"))
                                mLateConfigView.invalidate(null);
                        }
                    }

                }


                if(listener != null)
                    listener.onCompleted();

            }

        });



    }


    public void show(Boolean show , final GetBoolValueListener listener){


        view.setVisibility(show ? VISIBLE : View.GONE);

        if(show){

            if(this.pnlOrderInfoArea.getVisibility() != VISIBLE){

                this.pnlOrderInfoArea.setVisibility(View.VISIBLE);
                this.pnlCustomArea.setVisibility(View.VISIBLE);

                if(listener != null )
                    listener.onCompleted(true,true);
                return;
            }

        }else{

            if(this.pnlOrderInfoArea.getVisibility() == VISIBLE) {

                this.pnlOrderInfoArea.setVisibility(View.GONE);
                this.pnlCustomArea.setVisibility(View.GONE);

                if(listener != null )
                    listener.onCompleted(true,true);
                return;
            }
        }


        if(listener != null )
            listener.onCompleted(true,false);

    }

}
