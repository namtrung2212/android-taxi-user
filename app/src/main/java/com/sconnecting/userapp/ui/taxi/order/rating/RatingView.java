package com.sconnecting.userapp.ui.taxi.order.rating;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sconnecting.userapp.AppDelegate;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.AnimationHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.base.listener.GetOneListener;
import com.sconnecting.userapp.base.listener.PostListener;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.data.entity.BaseController;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.data.models.Driver;
import com.sconnecting.userapp.data.models.DriverStatus;
import com.sconnecting.userapp.data.models.OrderStatus;
import com.sconnecting.userapp.data.models.TravelOrder;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;
import com.sconnecting.userapp.ui.taxi.order.monitoring.DriverProfileView;
import com.sconnecting.userapp.ui.taxi.order.monitoring.OrderPanelView;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.view.View.VISIBLE;

/**
 * Created by TrungDao on 8/6/16.
 */

public class RatingView extends Fragment {


    OrderScreen screen;
    View view;
    View ratingArea;

    ImageView imgStar1;
    ImageView imgStar2;
    ImageView imgStar3;
    ImageView imgStar4;
    ImageView imgStar5;

    TextView lblRateDesc;
    EditText txtComment;
    Button btnSubmit;

    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }


    public RatingView() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        screen = (OrderScreen) context;
        screen.mRatingView = this;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        try {
            view = inflater.inflate(R.layout.taxi_order_rating, container, false);
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

        ratingArea = view.findViewById(R.id.ratingArea);
        ratingArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AnimationHelper.hideKeyBoard(v);
            }
        });

        imgStar1 = (ImageView) view.findViewById(R.id.imgStar1);
        imgStar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AnimationHelper.hideKeyBoard(v);
                CurrentOrder().Rating = 1;
                invalidateUI(false,null);

            }
        });
        imgStar2 = (ImageView) view.findViewById(R.id.imgStar2);
        imgStar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AnimationHelper.hideKeyBoard(v);
                CurrentOrder().Rating = 2;
                invalidateUI(false,null);
            }
        });
        imgStar3 = (ImageView) view.findViewById(R.id.imgStar3);
        imgStar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AnimationHelper.hideKeyBoard(v);
                CurrentOrder().Rating = 3;
                invalidateUI(false,null);

            }
        });
        imgStar4 = (ImageView) view.findViewById(R.id.imgStar4);
        imgStar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AnimationHelper.hideKeyBoard(v);
                CurrentOrder().Rating = 4;
                invalidateUI(false,null);
            }
        });
        imgStar5 = (ImageView) view.findViewById(R.id.imgStar5);
        imgStar5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AnimationHelper.hideKeyBoard(v);
                CurrentOrder().Rating = 5;
                invalidateUI(false,null);
            }
        });

        lblRateDesc = (TextView)view.findViewById(R.id.lblRateDesc);

        txtComment = (EditText) view.findViewById(R.id.txtComment);
        txtComment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                SCONNECTING.orderScreen.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
                return false;
            }
        });

        txtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SCONNECTING.orderScreen.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
         
            }
        });


        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        btnSubmit.setText("GỬI ĐÁNH GIÁ");
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                AnimationHelper.hideKeyBoard(v);

                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        if(CurrentOrder() != null) {

                            CurrentOrder().Comment = txtComment.getText().toString();

                            if(CurrentOrder().Rating == 0 )
                                CurrentOrder().Rating = 3;

                            new TravelOrderController().update(CurrentOrder(),"updateOrder", new PostListener() {
                                @Override
                                public void onCompleted(Boolean success, BaseModel item) {

                                    SCONNECTING.orderManager.reset(true,null);
                                }
                            });

                        }


                    }
                });

            }
        });

    }


    public void invalidate(final Boolean isFirstTime,final Completion listener){


        Boolean isShow= CurrentOrder() != null && CurrentOrder().isTripMateMember() == false && CurrentOrder().IsRating();

        if(isShow) {

            screen.showToolbar(true);

            invalidateUI(isFirstTime,new Completion() {
                @Override
                public void onCompleted() {

                    show(true, listener);

                }
            });

        }else{

            show(false,listener);

        }



    }

    public void invalidateUI(final Boolean isFirstTime,final Completion listener){

        if(ratingArea.getVisibility() == View.GONE){

            txtComment.setText("");
        }

        imgStar1.setImageResource( (CurrentOrder() != null &&  CurrentOrder().IsFinishedAndPaid() && ( CurrentOrder().Rating == 0  ||  CurrentOrder().Rating >=1 )) ? R.drawable.star : R.drawable.graystar);
        imgStar2.setImageResource( (CurrentOrder() != null &&  CurrentOrder().IsFinishedAndPaid() && ( CurrentOrder().Rating == 0  ||  CurrentOrder().Rating >=2 )) ? R.drawable.star : R.drawable.graystar);
        imgStar3.setImageResource( (CurrentOrder() != null &&  CurrentOrder().IsFinishedAndPaid() && ( CurrentOrder().Rating == 0  ||  CurrentOrder().Rating >=3 )) ? R.drawable.star : R.drawable.graystar);
        imgStar4.setImageResource( (CurrentOrder() != null &&  CurrentOrder().IsFinishedAndPaid() && CurrentOrder().Rating >=4 ) ? R.drawable.star : R.drawable.graystar);
        imgStar5.setImageResource( (CurrentOrder() != null &&  CurrentOrder().IsFinishedAndPaid() && CurrentOrder().Rating >=5 ) ? R.drawable.star : R.drawable.graystar);

        if( CurrentOrder().Rating == 0 ||  CurrentOrder().Rating == 3)
            lblRateDesc.setText("Hài lòng");
        else if( CurrentOrder().Rating == 1)
            lblRateDesc.setText("Rất tệ");
        else if( CurrentOrder().Rating == 2)
            lblRateDesc.setText("Không hài lòng");
        else if( CurrentOrder().Rating == 4)
            lblRateDesc.setText("Dịch vụ tốt");
        else if( CurrentOrder().Rating == 5)
            lblRateDesc.setText("Rất tốt");

        if(listener != null )
            listener.onCompleted();
    }


    public void show(Boolean isShow , final Completion listener){

        ratingArea.setVisibility(isShow ? VISIBLE : View.GONE);
        view.setVisibility(isShow ? VISIBLE : View.GONE);

        if(listener != null )
            listener.onCompleted();
    }

}
