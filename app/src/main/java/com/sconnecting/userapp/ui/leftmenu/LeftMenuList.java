package com.sconnecting.userapp.ui.leftmenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sconnecting.userapp.base.BaseActivity;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.manager.OrderManager;
import com.sconnecting.userapp.ui.taxi.history.NotYetPaidScreen;
import com.sconnecting.userapp.ui.taxi.history.NotYetPickupScreen;
import com.sconnecting.userapp.ui.taxi.history.OnTheWayScreen;
import com.sconnecting.userapp.ui.taxi.history.TravelHistoryScreen;
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;


public class LeftMenuList extends RecyclerView.Adapter<LeftMenuCell> {

    List<LeftMenuObject> dataSource;
    RecyclerView tableView;

    LinearLayoutManager mLinearLayoutManager;



    public LeftMenuList(RecyclerView table){

        this.tableView = table;
        mLinearLayoutManager = new LinearLayoutManager(tableView.getContext());
        this.tableView.setLayoutManager(mLinearLayoutManager);


    }

    public void reloadData( ) {

        List<LeftMenuObject> data = new ArrayList<>();

        data.add( new LeftMenuObject(true,0,5,"Gọi Taxi","",null,null));
        data.add( new LeftMenuObject(false,0,5,"Tạo hành trình","NewTravel",null,0));
        data.add( new LeftMenuObject(false,0,5,"Chưa khởi hành","NotYetPickup",null,1));
        data.add( new LeftMenuObject(false,0,5,"Trong hành trình","OnTheWay",null,2));
        data.add( new LeftMenuObject(false,0,5,"Chưa thanh toán","NotYetPaid",null,3));
        data.add( new LeftMenuObject(false,0,5,"Lịch sử","History",null,4));


        data.add( new LeftMenuObject(true,1,6,"Đi chung","",null,null));
        data.add( new LeftMenuObject(false,1,6,"Tạo yêu cầu",null,null,0));
        data.add( new LeftMenuObject(false,1,6,"Cộng đồng",null,null,1));
        data.add( new LeftMenuObject(false,1,6,"Chưa có nhóm",null,null,2));
        data.add( new LeftMenuObject(false,1,6,"Đã có nhóm",null,null,3));
        data.add( new LeftMenuObject(false,1,6,"Tin nhắn",null,null,4));
        data.add( new LeftMenuObject(false,1,6,"Thông báo",null,null,5));

        data.add( new LeftMenuObject(true,2,5,"Thẻ thanh toán","",null,null));
        data.add( new LeftMenuObject(false,2,5,"Tạo thẻ mới",null,null,0));
        data.add( new LeftMenuObject(false,2,5,"Danh sách thẻ",null,null,1));
        data.add( new LeftMenuObject(false,2,5,"Tài khoản",null,null,2));
        data.add( new LeftMenuObject(false,2,5,"Cấp hạn mức",null,null,3));
        data.add( new LeftMenuObject(false,2,5,"Lịch sử dùng thẻ",null,null,4));


        reloadData(data);

    }

    public void reloadData( List<LeftMenuObject> data) {

        this.dataSource = data;
        tableView.setAdapter(this);

    }
    public void appendData( List<LeftMenuObject> data) {

        this.dataSource.addAll(data);
        tableView.setAdapter(this);

    }

    @Override
    public int getItemViewType(int position) {

        LeftMenuObject obj = this.dataSource.get(position);

        if(obj.isGroup == false){

            return 0;

        }else{

            return 1;
        }

    }



    @Override
    public LeftMenuCell onCreateViewHolder(ViewGroup viewGroup, int viewtype) {

        if(viewtype == 0){

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.base_leftmenu_cellitem, viewGroup,false);
            return new LeftMenuCellItem(view);

        }else  if(viewtype == 1){

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.base_leftmenu_cellgroup, viewGroup,false);
            return new LeftMenuCellGroup(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(LeftMenuCell cell, int i) {

        LeftMenuObject item = dataSource.get(i);
        cell.updateWithModel(item);
        cell.bind(item, new LeftMenuCell.OnItemClickListener() {
            @Override
            public void onItemClick(LeftMenuObject item) {


                if(item.section == 0){

                    if(item.index == 0){ //NewTravel


                        if(tableView.getContext() instanceof OrderScreen && SCONNECTING.orderScreen != null){

                            SCONNECTING.orderManager.reset(true,null);


                        }else {

                            Intent intent = new Intent(tableView.getContext(), OrderScreen.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                            Parcelable wrappedCurrentOrder = Parcels.wrap(OrderManager.initNewOrder());
                            intent.putExtra("CurrentOrder", wrappedCurrentOrder);

                            ((Activity) tableView.getContext()).startActivity(intent);
                            ((Activity) tableView.getContext()).overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                        }

                        ((BaseActivity)tableView.getContext()).showLeftMenu(false);


                    }else if(item.index == 1){ //NotYetPickup

                        Intent intent = new Intent(tableView.getContext(), NotYetPickupScreen.class);
                        intent.putExtra("caller", tableView.getContext().getClass().getSimpleName());
                        ((Activity)tableView.getContext()).startActivity(intent);
                        ((Activity)tableView.getContext()).overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);
                        ((BaseActivity)tableView.getContext()).showLeftMenu(false);

                    }else if(item.index == 2){ //OnTheWay

                        Intent intent = new Intent(tableView.getContext(), OnTheWayScreen.class);
                        intent.putExtra("caller", tableView.getContext().getClass().getSimpleName());
                        ((Activity)tableView.getContext()).startActivity(intent);
                        ((Activity)tableView.getContext()).overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);
                        ((BaseActivity)tableView.getContext()).showLeftMenu(false);

                    }else if(item.index == 3){ //NotYetPaid

                        Intent intent = new Intent(tableView.getContext(), NotYetPaidScreen.class);
                        intent.putExtra("caller", tableView.getContext().getClass().getSimpleName());
                        ((Activity)tableView.getContext()).startActivity(intent);
                        ((Activity)tableView.getContext()).overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);
                        ((BaseActivity)tableView.getContext()).showLeftMenu(false);

                    }else if(item.index == 4){ //History

                        Intent intent = new Intent(tableView.getContext(), TravelHistoryScreen.class);
                        intent.putExtra("caller", tableView.getContext().getClass().getSimpleName());
                        ((Activity)tableView.getContext()).startActivity(intent);
                        ((Activity)tableView.getContext()).overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);
                        ((BaseActivity)tableView.getContext()).showLeftMenu(false);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataSource ? dataSource.size() : 0);
    }


}
