package com.sconnecting.userapp.ui.activation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.GetBoolValueListener;
import com.sconnecting.userapp.base.listener.GetOneListener;
import com.sconnecting.userapp.base.listener.GetStringValueListener;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.base.AnimationHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.data.controllers.UserController;
import com.sconnecting.userapp.data.models.User;

/**
 * Created by TrungDao on 8/6/16.
 */


public class ActivationScreen extends AppCompatActivity {


    EditText txtUserName;
    EditText txtPhoneNo;
    EditText txtActivationCode;
    Button btnGetCode;
    Button btnCancel;
    Button btnCommitCode;
    Button btnDone;
    TextView lblStatus;


    String currentRequestID;
    String UserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.other_activation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(txtPhoneNo.getVisibility() == View.VISIBLE) {
            txtPhoneNo.selectAll();
            final InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(txtPhoneNo, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void setContentView(int layoutResID)
    {

        super.setContentView(layoutResID);

        findViewById(R.id.constraintLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService( getApplicationContext().INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);

            }
        });


        txtUserName =(EditText) findViewById(R.id.txtUserName);
        txtPhoneNo =(EditText) findViewById(R.id.txtPhoneNo);
        txtActivationCode =(EditText) findViewById(R.id.txtActivationCode);
        lblStatus =(TextView) findViewById(R.id.lblStatus);

        btnGetCode =(Button) findViewById(R.id.btnGetCode);
        btnCancel =(Button) findViewById(R.id.btnCancel);
        btnCommitCode =(Button) findViewById(R.id.btnCommitCode);
        btnDone =(Button) findViewById(R.id.btnDone);


        txtUserName.setVisibility(View.GONE);
        txtPhoneNo.setVisibility(View.VISIBLE);
        txtActivationCode.setVisibility(View.GONE);
        btnGetCode.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.GONE);
        btnCommitCode.setVisibility(View.GONE);
        btnDone.setVisibility(View.GONE);

        final InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService( getApplicationContext().INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(txtPhoneNo, InputMethodManager.SHOW_IMPLICIT);


        txtPhoneNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    txtPhoneNo.selectAll();
                    final InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService( getApplicationContext().INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(txtPhoneNo, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        txtActivationCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    txtActivationCode.selectAll();
                    final InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService( getApplicationContext().INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(txtActivationCode, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        txtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    txtUserName.selectAll();
                    final InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService( getApplicationContext().INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(txtUserName, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });



        btnGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        txtPhoneNo.clearFocus();
                        txtActivationCode.clearFocus();
                        txtUserName.clearFocus();

                        txtPhoneNo.setVisibility(View.GONE);
                        btnGetCode.setVisibility(View.GONE);
                        btnCancel.setVisibility(View.GONE);
                        btnCommitCode.setVisibility(View.VISIBLE);
                        txtActivationCode.setVisibility(View.VISIBLE);

                        txtActivationCode.setText("");
                        txtActivationCode.requestFocus();

                        String phoneNo = txtPhoneNo.getText().toString();

                        if(phoneNo.isEmpty() == false){

                            UserController.RequestForActivationCode(phoneNo, "VN", new GetStringValueListener() {
                                @Override
                                public void onCompleted(Boolean success, String requestId) {

                                    if(requestId != null && requestId.isEmpty() == false){
                                        currentRequestID = requestId;
                                        lblStatus.setText("Đã gửi mã kích hoạt đến số điện thoại của bạn.");

                                    }else{

                                        lblStatus.setText("Gửi mã kích hoạt không thành công.");

                                    }

                                }
                            });

                        }else{
                            lblStatus.setText("Vui lòng nhập số điện thoại của bạn.");
                        }

                    }
                });
            }
        });


        btnCommitCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        txtPhoneNo.clearFocus();
                        txtActivationCode.clearFocus();
                        txtUserName.clearFocus();

                        if(currentRequestID != null){

                            lblStatus.setText("Đang kiểm tra mã kích hoạt...");
                            btnCommitCode.setVisibility(View.GONE);


                            String phoneNo = txtPhoneNo.getText().toString().trim();

                            String activateCode = txtActivationCode.getText().toString().trim();


                            new UserController().CheckForActivationCode(currentRequestID, activateCode, phoneNo, new GetStringValueListener() {
                                @Override
                                public void onCompleted(Boolean success,final String userId) {

                                    if(!success || userId == null) {
                                        lblStatus.setText("Mã kích hoạt không đúng, vui lòng kiểm tra lại!");
                                        btnCommitCode.setVisibility(View.VISIBLE);
                                        return;
                                    }

                                    SCONNECTING.userManager.login(userId, new GetBoolValueListener() {
                                            @Override
                                            public void onCompleted(Boolean success, Boolean isLogged) {

                                                btnGetCode.setVisibility(View.GONE);
                                                btnCommitCode.setVisibility(View.GONE);
                                                txtActivationCode.setVisibility(View.GONE);

                                                if(!isLogged){

                                                    lblStatus.setText("Kết nối không hợp lệ.");
                                                    btnCancel.setVisibility(View.VISIBLE);
                                                    btnDone.setVisibility(View.GONE);
                                                    return;

                                                }

                                                btnCancel.setVisibility(View.GONE);
                                                btnDone.setVisibility(View.VISIBLE);
                                                txtUserName.setVisibility(View.VISIBLE);

                                                lblStatus.setText("Kích hoạt thành công.");

                                                new UserController().getById(true,userId, new GetOneListener() {
                                                    @Override
                                                    public void onGetOne(Boolean success,BaseModel item) {
                                                        if(item != null){
                                                            txtUserName.setText(((User)item).Name);
                                                            txtUserName.requestFocus();
                                                        }
                                                    }
                                                });


                                                UserId = userId;
                                            }
                                    });




                                }
                            });

                        }
                    }
                });

            }

        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AnimationHelper.animateButton(v, new Completion() {
                    @Override
                    public void onCompleted() {

                        txtPhoneNo.clearFocus();
                        txtActivationCode.clearFocus();
                        txtUserName.clearFocus();

                        btnDone.setVisibility(View.GONE);
                        btnGetCode.setVisibility(View.VISIBLE);
                        btnCancel.setVisibility(View.GONE);
                        btnCommitCode.setVisibility(View.GONE);

                        txtActivationCode.setVisibility(View.GONE);
                        txtUserName.setVisibility(View.GONE);
                        txtPhoneNo.setVisibility(View.VISIBLE);
                        txtActivationCode.setText("");

                        lblStatus.setText("Vui lòng nhập số điện thoại của bạn.");

                        txtPhoneNo.selectAll();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService( getApplicationContext().INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(txtPhoneNo, InputMethodManager.SHOW_IMPLICIT);
                    }
                });


            }
        });
        
        
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                AnimationHelper.animateButton( v, new Completion() {
                    @Override
                    public void onCompleted() {

                        txtPhoneNo.clearFocus();
                        txtActivationCode.clearFocus();
                        txtUserName.clearFocus();


                        lblStatus.setText( "Cập nhật thông tin cá nhân....");

                        btnDone.setVisibility(View.GONE);
                        btnGetCode.setVisibility(View.GONE);
                        btnCancel.setVisibility(View.GONE);
                        btnCommitCode.setVisibility(View.GONE);
                        final InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService( getApplicationContext().INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);

                        new UserController().updateUserName(txtUserName.getText().toString(), UserId, new Completion() {
                            @Override
                            public void onCompleted() {

                                SCONNECTING.userManager.initCurrentUser(new GetBoolValueListener() {
                                    @Override
                                    public void onCompleted(Boolean success, Boolean isValidUser) {

                                        if(isValidUser == false) {

                                            Intent intent = new Intent(getApplicationContext(), ActivationScreen.class);
                                            startActivity(intent);

                                        }else{
                                            UserController.ActivateUserAccount(UserId, new Completion() {
                                                @Override
                                                public void onCompleted() {


                                                    SCONNECTING.Start(new Completion() {
                                                        @Override
                                                        public void onCompleted() {

                                                            SCONNECTING.orderManager.tryToOpenLastOpenningOrder(null);

                                                        }
                                                    });

                                                }
                                            });
                                        }
                                    }
                                });


                            }
                        });

                    }
                });
            }
        });
        
    }

}