package com.sconnecting.userapp.ui.taxi.order.creation;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.google.GooglePlaceSearcher;
import com.sconnecting.userapp.google.LocationInfo;
import com.sconnecting.userapp.base.AnimationHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;
import com.sconnecting.userapp.data.models.TravelOrder;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


/**
 * Created by TrungDao on 8/1/16.
 */

public class ChooseLocationView extends Fragment{

    View view;
    ImageView centerLocation;
    ImageButton btnPickupHere;
    ImageButton btnDropHere;


    View pnlOrderArea;

    ImageButton btnPickupIcon;
    ImageButton btnDropIcon;

    ImageButton btnPickupCancel;
    ImageButton btnDropCancel;

    TextView lblPickupLocation;
    TextView lblDropLocation;
    TextView lblPathStatistic;
    View line1;
    View line2;
    View line3;
    Button btnCreateOrder;

    OrderScreen screen;

    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }


    public ChooseLocationView() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        screen = (OrderScreen) context;
        screen.mChooseLocationView = this;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        try {
            view = inflater.inflate(R.layout.taxi_order_creation_chooselocation, container, false);
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

    @Override
    public void onResume(){
        super.onResume();

        invalidate(false,null);

    }

    public void initControls(final Completion listener) {


        centerLocation = (ImageView) view.findViewById(R.id.centerLocation);

        btnPickupHere = (ImageButton) view.findViewById(R.id.btnPickupHere);
        btnPickupHere.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        CurrentOrder().initOrderPickupLoc(screen.mMapView.gmsMapView.getCameraPosition().target);

                        invalidateTravelPath(false,null);

                        screen.mapMarkerManager.loadNearestVehicles(screen.mMapView.gmsMapView.getCameraPosition().target);
                        screen.newPhase = OrderScreen.OrderPhase.ChooseLocation;
                        screen.invalidateUI(false,null);

                        GooglePlaceSearcher searcher = new GooglePlaceSearcher();
                        searcher.getAddress(screen.mMapView.gmsMapView.getCameraPosition().target, new GooglePlaceSearcher.GetPlaceListener() {

                            @Override
                            public void onCompleted(LocationInfo locAddress) {
                                if(locAddress != null ) {
                                    screen.mPlaceSearcher.mSearchView.setSearchText(locAddress.Address);
                                    CurrentOrder().initOrderPickupPlace(locAddress.Address, locAddress.CountryCode);
                                }
                                screen.newPhase = OrderScreen.OrderPhase.ChooseLocation;
                                screen.invalidateUI(false, null);

                            }
                        });
                    }
                });
            }

        });

        btnDropHere = (ImageButton) view.findViewById(R.id.btnDropHere);
        btnDropHere.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        CurrentOrder().initOrderDropLoc(screen.mMapView.gmsMapView.getCameraPosition().target);

                        invalidateTravelPath(false,null);

                        screen.newPhase = OrderScreen.OrderPhase.ChooseLocation;
                        screen.invalidateUI(false,null);

                        GooglePlaceSearcher searcher = new GooglePlaceSearcher();
                        searcher.getAddress(screen.mMapView.gmsMapView.getCameraPosition().target, new GooglePlaceSearcher.GetPlaceListener() {

                            @Override
                            public void onCompleted(LocationInfo locAddress) {

                                if(locAddress != null) {
                                    screen.mPlaceSearcher.mSearchView.setSearchText("");
                                    CurrentOrder().initOrderDropPlace(locAddress.Address);
                                }
                                screen.newPhase = OrderScreen.OrderPhase.ChooseLocation;
                                screen.invalidateUI(false, null);
                            }

                        });
                    }
                });

            }
        });

        pnlOrderArea = (View) view.findViewById(R.id.pnlOrderArea);
        btnCreateOrder = (Button) view.findViewById(R.id.btnCreateOrder);
        btnCreateOrder.setText("TẠO HÀNH TRÌNH");
        AnimationHelper.setOnClick(btnCreateOrder, new Completion() {
            @Override
            public void onCompleted() {
                if(SCONNECTING.userManager != null && SCONNECTING.userManager.CurrentUser != null) {
                    CurrentOrder().User = SCONNECTING.userManager.CurrentUser.getId();
                    CurrentOrder().UserName = SCONNECTING.userManager.CurrentUser.Name;
                }
                CurrentOrder().Device = Build.MODEL;
                CurrentOrder().DeviceID = Build.SERIAL;
                CurrentOrder().OrderLoc = SCONNECTING.locationHelper.getLocationObject();

                SCONNECTING.orderManager.actionHandler.UserCreateOrder(new Completion() {
                    @Override
                    public void onCompleted() {

                        SCONNECTING.orderManager.invalidate(false, true, null);

                    }
                });
            }
        });

        btnPickupIcon = (ImageButton) view.findViewById(R.id.btnPickupIcon);
        btnPickupIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        if( CurrentOrder().OrderPickupLoc != null ){
                            screen.mMapView.moveToLocation(CurrentOrder().OrderPickupLoc.getLatLng(),null);
                        }

                    }
                });


            }
        });

        btnDropIcon = (ImageButton) view.findViewById(R.id.btnDropIcon);
        btnDropIcon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        if( CurrentOrder().OrderDropLoc != null ){
                            screen.mMapView.moveToLocation(CurrentOrder().OrderDropLoc.getLatLng(),null);
                        }
                    }
                });

            }
        });

        IconTextView btnPathStatisticIcon = (IconTextView) view.findViewById(R.id.btnPathStatisticIcon);
        btnPathStatisticIcon.setTextColor(Color.rgb(103,160,212));


        btnPickupCancel = (ImageButton) view.findViewById(R.id.btnPickupCancel);
        btnPickupCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        if( CurrentOrder().OrderPickupLoc != null){

                            CurrentOrder().clearOrderPickupLoc();

                            invalidateTravelPath(false,null);
                            invalidate(false,null);
                            screen.mMapView.invalidate(false,null);

                        }
                    }
                });


            }
        });

        btnDropCancel = (ImageButton) view.findViewById(R.id.btnDropCancel);
        btnDropCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        if( CurrentOrder().OrderDropLoc != null){

                            CurrentOrder().clearOrderDropLoc();

                            invalidateTravelPath(false,null);
                            invalidate(false,null);
                            screen.mMapView.invalidate(false,null);

                        }
                    }
                });
            }
        });

        lblPickupLocation = (TextView) view.findViewById(R.id.lblPickupLocation);
        lblPickupLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        if( CurrentOrder().OrderPickupLoc != null){
                            screen.mMapView.moveToLocation(CurrentOrder().OrderPickupLoc.getLatLng(),null);
                        }
                    }
                });


            }
        });
        lblDropLocation = (TextView) view.findViewById(R.id.lblDropLocation);
        lblDropLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        if( CurrentOrder().OrderDropLoc != null){
                            screen.mMapView.moveToLocation(CurrentOrder().OrderDropLoc.getLatLng(),null);
                        }
                    }
                });

            }
        });

        lblPathStatistic = (TextView) view.findViewById(R.id.lblPathStatistic);


        line1 =  view.findViewById(R.id.line1);
        line2 =  view.findViewById(R.id.line2);
        line3 =  view.findViewById(R.id.line3);

        if(listener != null)
                listener.onCompleted();

    }

    public void showOrderPanel(Boolean show,final Completion listener){

        if(show){

            if(this.pnlOrderArea.getVisibility() != VISIBLE && CurrentOrder() != null && ( CurrentOrder().OrderPickupLoc != null ||   CurrentOrder().OrderDropLoc != null )){

                this.pnlOrderArea.setVisibility(View.VISIBLE);
                this.btnCreateOrder.setVisibility((this.screen.isChoosingLocationPhase() && this.CurrentOrder().OrderPickupLoc != null  ) ? VISIBLE : View.GONE);

            }

        }else{

            if(this.pnlOrderArea.getVisibility() == VISIBLE){

                this.pnlOrderArea.setVisibility(View.GONE);
                this.btnCreateOrder.setVisibility((this.screen.isChoosingLocationPhase() && this.CurrentOrder().OrderPickupLoc != null ) ? VISIBLE : View.GONE);
            }


        }
        if(listener != null )
            listener.onCompleted();
    }

    public void invalidate(final Boolean isFirstTime,final Completion listener){

        Boolean isShow = this.screen.isChoosingLocationPhase();

        view.setVisibility( isShow? VISIBLE : View.GONE);

        if(isShow)
            screen.showToolbar(false);


        this.centerLocation.setVisibility((this.screen.isChoosingLocationPhase() && this.CurrentOrder().IsNewOrder() &&  this.CurrentOrder().IsChoosingLocation()) ? VISIBLE : INVISIBLE) ;
        this.btnPickupHere.setVisibility((this.screen.isChoosingLocationPhase() && this.CurrentOrder().IsNewOrder() && this.CurrentOrder().IsChoosingPickupLocation()) ? VISIBLE : INVISIBLE) ;
        this.btnDropHere.setVisibility((this.screen.isChoosingLocationPhase() && this.CurrentOrder().IsNewOrder() && this.CurrentOrder().IsChoosingDropLocation()) ? VISIBLE : INVISIBLE) ;

        this.invalidateOrderPanel(isFirstTime, new Completion() {
            @Override
            public void onCompleted() {

                view.invalidate();
                if(listener != null )
                    listener.onCompleted();

            }
        });

    }

    public void invalidateOrderPanel(final Boolean isFirstTime,final Completion listener){

        Boolean isShow = this.CurrentOrder().IsNewOrder()  &&  ( (this.screen.isChoosingLocationPhase() && (this.CurrentOrder().OrderPickupLoc != null  || this.CurrentOrder().OrderDropLoc != null  ) )  || this.screen.isCustomOrderPhase() );

        if(isShow){

            this.btnPickupCancel.setVisibility((!this.screen.isCustomOrderPhase() && this.CurrentOrder().OrderPickupLoc != null ) ? VISIBLE : INVISIBLE) ;
            this.btnDropCancel.setVisibility((!this.screen.isCustomOrderPhase() && this.CurrentOrder().OrderDropLoc != null ) ? VISIBLE : INVISIBLE) ;

            String strAddress = "";
            if(this.CurrentOrder().OrderPickupPlace != null)
                strAddress =  this.CurrentOrder().OrderPickupPlace.substring(0,  this.CurrentOrder().OrderPickupPlace.lastIndexOf(", "));
            this.lblPickupLocation.setText(strAddress);
           // this.line1.setVisibility(this.lblPickupLocation.getVisibility());

            strAddress = "";
            if(this.CurrentOrder().OrderDropPlace != null)
                strAddress =  this.CurrentOrder().OrderDropPlace.substring(0,  this.CurrentOrder().OrderDropPlace.lastIndexOf(", "));
            this.lblDropLocation.setText(strAddress);
           // this.line2.setVisibility(this.lblDropLocation.getVisibility());

            this.btnCreateOrder.setVisibility((this.screen.isChoosingLocationPhase() && this.CurrentOrder().OrderPickupLoc != null  ) ? VISIBLE : View.GONE);

            invalidateTravelPath(false,null);

        }

        this.showOrderPanel(isShow,listener);

    }




    public void invalidateTravelPath(final Boolean isFirstTime,final Completion listener){

        if( this.CurrentOrder() == null ){
            if(listener != null )
                listener.onCompleted();
            return;
        }


        if(this.CurrentOrder().OrderDistance > 0 && this.CurrentOrder().OrderDuration > 0){

            final String strDistance = String.format("%.1f Km", this.CurrentOrder().OrderDistance/1000 );
            int hours =  (int)(this.CurrentOrder().OrderDuration / 3600);
            int minutes =  (int)(Math.round((this.CurrentOrder().OrderDuration % 3600) / 60));
            final String strDuration = (hours > 0) ?  String.format("%d giờ %d phút", hours, minutes ) :  String.format("%d phút", minutes );

            lblPathStatistic.setText(strDistance + " - " + strDuration );
            lblPathStatistic.setVisibility(VISIBLE);


        }else{

            this.lblPathStatistic.setText("");
        }

        if(listener != null )
            listener.onCompleted();

    }


}
