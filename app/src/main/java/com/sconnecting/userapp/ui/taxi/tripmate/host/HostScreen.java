package com.sconnecting.userapp.ui.taxi.tripmate.host;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.sconnecting.userapp.AppDelegate;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.GetOneListener;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.base.AnimationHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.notification.NotificationType;
import com.sconnecting.userapp.ui.taxi.tripmate.host.chatting.TripMateChattingView;
import com.sconnecting.userapp.ui.taxi.tripmate.host.map.MateMapView;
import com.sconnecting.userapp.ui.taxi.tripmate.host.members.MembersView;
import com.sconnecting.userapp.ui.taxi.tripmate.host.requestingmembers.RequestingMembersView;
import com.sconnecting.userapp.data.models.MateStatus;
import com.sconnecting.userapp.data.models.TravelOrder;
import org.parceler.Parcels;

/**
 * Created by TrungDao on 8/17/16.
 */


public class HostScreen extends AppCompatActivity {

    public TravelOrder hostOrder;

    public Toolbar mainToolbar;
    public IconTextView btnBack;
    public TextView mToolbarTitle;
    protected boolean useToolbar = true;
    public boolean isActive = false;

    private FragmentTabHost mTabHost;
    public MateMapView mMateMapView;
    public RequestingMembersView mRequestingMembersView;
    public MembersView mMembersView;
    public TripMateChattingView mTripMateChattingView;


    public Boolean isShowRequestingMembers = true;
    public Boolean isShowMembers = true;
    public Boolean isShowMap = true;
    public Boolean isShowChatting = true;


    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent myIntent = getIntent();
        hostOrder = Parcels.unwrap(myIntent.getParcelableExtra("HostOrder"));

        setContentView(R.layout.taxi_order_tripmate_host);



        initControls(new Completion() {
            @Override
            public void onCompleted() {

                invalidate();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppDelegate.CurrentActivity = this;

        invalidate();

    }

    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;

    }

    @Override
    public void setContentView(int layoutResID)
    {
        super.setContentView(layoutResID);


        mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);

        if (useToolbar)
        {
            setSupportActionBar(mainToolbar);
            mainToolbar.setTitleTextColor(Color.WHITE);
            mainToolbar.setVisibility(View.VISIBLE);

            mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
            mToolbarTitle.setText(this.getTitle().toString().toUpperCase());

            btnBack = (IconTextView) findViewById(R.id.btnBack);
            btnBack.setTextColor(Color.WHITE);
            AnimationHelper.setOnClick(btnBack, new Completion() {
                @Override
                public void onCompleted() {

                    onBackPressed();
                }
            });

            Window window = getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(Color.rgb(89,145,196));
            }
        }
        else
        {
            mainToolbar.setVisibility(View.GONE);
        }

    }


    @Override

    public void onBackPressed() {

        // Write your code here

        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);


    }


    @SuppressWarnings("MissingPermission")
    public void initControls(final Completion listener) {

        mTabHost = (FragmentTabHost)findViewById(R.id.tabhost);
        mTabHost.setup(this, this.getSupportFragmentManager(), android.R.id.tabcontent);

        View tab = LayoutInflater.from(this).inflate(R.layout.taxi_custom_tab, null);
        ((TextView)tab.findViewById(R.id.tab_icon)).setText("{fa-globe}");
        ((TextView)tab.findViewById(R.id.tab_icon)).setTextSize(20);
        ((TextView)tab.findViewById(R.id.tab_text)).setText("");
        mTabHost.addTab(mTabHost.newTabSpec("MateMapView").setIndicator(tab), new MateMapView().getClass(), null);

        tab = LayoutInflater.from(this).inflate(R.layout.taxi_custom_tab, null);
        ((TextView)tab.findViewById(R.id.tab_icon)).setText("{fa-users}");
        ((TextView)tab.findViewById(R.id.tab_icon)).setTextSize(20);
        ((TextView)tab.findViewById(R.id.tab_text)).setText("");
        mTabHost.addTab(mTabHost.newTabSpec("Members").setIndicator(tab), new MembersView().getClass(), null);

        tab = LayoutInflater.from(this).inflate(R.layout.taxi_custom_tab, null);
        ((TextView)tab.findViewById(R.id.tab_icon)).setText("{fa-reply-all}");
        ((TextView)tab.findViewById(R.id.tab_icon)).setTextSize(20);
        ((TextView)tab.findViewById(R.id.tab_text)).setText("");
        mTabHost.addTab(mTabHost.newTabSpec("RequestingMembers").setIndicator(tab), new RequestingMembersView().getClass(), null);

        tab = LayoutInflater.from(this).inflate(R.layout.taxi_custom_tab, null);
        ((TextView)tab.findViewById(R.id.tab_icon)).setText("{fa-comments}");
        ((TextView)tab.findViewById(R.id.tab_icon)).setTextSize(20);
        ((TextView)tab.findViewById(R.id.tab_text)).setText("");
        mTabHost.addTab(mTabHost.newTabSpec("Chatting").setIndicator(tab), new TripMateChattingView().getClass(), null);

        mTabHost.getTabWidget().setStripEnabled(true);
        mTabHost.getTabWidget().setRightStripDrawable(R.color.transparent);
        mTabHost.getTabWidget().setLeftStripDrawable(R.color.transparent);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                if(mMembersView != null && tabId.equals("Members"))
                    mMembersView.invalidate(null);

                if(mRequestingMembersView != null && tabId.equals("RequestingMembers"))
                    mRequestingMembersView.invalidate(null);

                if (mMateMapView != null && tabId.equals("MateMapView"))
                    mMateMapView.invalidate(null);

                if (mTripMateChattingView != null && tabId.equals("Chatting"))
                    mTripMateChattingView.invalidate(null);

            }
        });


        isShowRequestingMembers = true;
        isShowMembers = true;
        isShowMap = true;
        isShowChatting= true;

        if(CurrentOrder().IsMateHost == 1) {
            isShowRequestingMembers = true;
            isShowMembers = true;
            isShowChatting= true;
        }else{

            if(hostOrder.getId().equals(SCONNECTING.orderManager.currentOrder.getId()) == false){
                isShowRequestingMembers = false;
            }
            if(CurrentOrder().MateHostOrder == null || CurrentOrder().MateHostOrder.equals(hostOrder.getId())  == false
                    || CurrentOrder().MateStatus == null
                    || (CurrentOrder().MateStatus.equals(MateStatus.Accepted) == false && CurrentOrder().MateStatus.equals(MateStatus.Closed) == false) ){
                isShowMembers = false;
                isShowRequestingMembers = false;
                isShowChatting= false;
            }
        }

        if(CurrentOrder().MateStatus != null && CurrentOrder().MateStatus.equals(MateStatus.Closed))
            isShowMap = false;

        mTabHost.getTabWidget().getChildAt(0).setVisibility(isShowMap ? View.VISIBLE : View.GONE);
        mTabHost.getTabWidget().getChildAt(1).setVisibility(isShowMembers ? View.VISIBLE : View.GONE);
        mTabHost.getTabWidget().getChildAt(2).setVisibility(isShowRequestingMembers ? View.VISIBLE : View.GONE);
        mTabHost.getTabWidget().getChildAt(3).setVisibility(isShowChatting ? View.VISIBLE : View.GONE);

        String strNotifyType = getIntent().getStringExtra("NotifyType");
        if(strNotifyType!= null && strNotifyType.equals(NotificationType.UserChatToGroup))
            mTabHost.setCurrentTab(3);
        else if(strNotifyType!= null && strNotifyType.equals(NotificationType.MateMemberSentRequest))
            mTabHost.setCurrentTab(2);
        else if(strNotifyType!= null && strNotifyType.equals(NotificationType.MemberLeaveFromTripMate))
            mTabHost.setCurrentTab(1);
        else
            mTabHost.setCurrentTab(1);


        if(listener != null)
            listener.onCompleted();

    }



    public void loadData(final Completion listener) {

        if (hostOrder != null) {

            new TravelOrderController().getById(true, hostOrder.getId(), new GetOneListener() {
                @Override
                public void onGetOne(Boolean success, BaseModel item) {

                    if (success && item != null)
                        hostOrder = (TravelOrder) item;


                    new TravelOrderController().getById(true, CurrentOrder().getId(), new GetOneListener() {
                        @Override
                        public void onGetOne(Boolean success, BaseModel item) {

                            if (success && item != null)
                                SCONNECTING.orderManager.currentOrder = (TravelOrder) item;

                            if(listener != null)
                                listener.onCompleted();

                        }
                    });

                }
            });

        }else{

            if(listener != null)
                listener.onCompleted();
        }
    }

    public void invalidate(){

        loadData(new Completion() {
            @Override
            public void onCompleted() {

                String tabId = mTabHost.getCurrentTabTag();
                if(mMembersView != null && tabId.equals("Members"))
                    mMembersView.invalidate(null);

                if(mRequestingMembersView != null && tabId.equals("RequestingMembers"))
                    mRequestingMembersView.invalidate(null);

                if (mMateMapView != null && tabId.equals("MateMapView"))
                    mMateMapView.invalidate(null);

                if (mTripMateChattingView != null && tabId.equals("Chatting"))
                    mTripMateChattingView.invalidate(null);
            }
        });


    }

}