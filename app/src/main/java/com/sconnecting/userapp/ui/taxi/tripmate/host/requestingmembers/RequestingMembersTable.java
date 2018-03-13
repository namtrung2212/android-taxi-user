package com.sconnecting.userapp.ui.taxi.tripmate.host.requestingmembers;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.GetItemsListener;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.ui.taxi.tripmate.host.previewmember.PreviewMemberScreen;
import com.sconnecting.userapp.data.models.TravelOrder;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;


public class RequestingMembersTable extends RecyclerView.Adapter<RequestingMemberCell> {

    List<TravelOrder> dataSource;
    RecyclerView tableView;

    SwipeRefreshLayout reqRefreshControl ;
    SwipeRefreshLayout reqEmptyRefreshControl ;
    LinearLayoutManager mLinearLayoutManager;



    public RequestingMembersTable(RecyclerView table, SwipeRefreshLayout refreshCtrl){

        tableView = table;
        tableView.setAdapter(this);

        mLinearLayoutManager = new LinearLayoutManager(tableView.getContext());
        this.tableView.setLayoutManager(mLinearLayoutManager);

        tableView.addItemDecoration(new RecyclerView.ItemDecoration(){

            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1)
                    outRect.bottom = 100;
            }
        });



        this.reqRefreshControl = refreshCtrl;

        reqRefreshControl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDataList(new Completion() {
                    @Override
                    public void onCompleted() {
                        reqRefreshControl.setRefreshing(false);
                    }
                });
            }
        });

        reqEmptyRefreshControl = (SwipeRefreshLayout)((ConstraintLayout) refreshCtrl.getParent()).findViewById(R.id.reqEmptyRefreshControl);
        reqEmptyRefreshControl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDataList(new Completion() {
                    @Override
                    public void onCompleted() {
                        reqEmptyRefreshControl.setRefreshing(false);
                    }
                });
            }
        });
    }


    public void reloadData(final Completion listener ) {

        List<TravelOrder> data = new ArrayList<>();

        reloadData(data,listener);

    }

    public void reloadData( List<TravelOrder> data,final Completion listener) {
        this.dataSource = new ArrayList<>();
        this.dataSource.addAll(data);

        notifyDataSetChanged();

        if(listener !=null)
            listener.onCompleted();
    }
    public void appendData( List<TravelOrder> data,final Completion listener) {

        if(dataSource == null)
            this.dataSource = new ArrayList<>();

        int lastCount = this.dataSource.size();
        this.dataSource.addAll(data);
        notifyItemRangeInserted(lastCount-1,data.size());


        if(listener !=null)
            listener.onCompleted();
    }


    @Override
    public RequestingMemberCell onCreateViewHolder(ViewGroup viewGroup, int viewtype) {


        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.taxi_order_tripmate_host_requestingmembers_cell,viewGroup,false);
        return new RequestingMemberCell(view);


    }

    @Override
    public void onBindViewHolder(RequestingMemberCell cell, int i) {

        TravelOrder item = dataSource.get(i);
        cell.updateWithModel(item);
        cell.bind(item, new RequestingMemberCell.OnItemClickListener() {
            @Override
            public void onItemClick(final TravelOrder order) {


                Intent intent = new Intent(tableView.getContext(), PreviewMemberScreen.class);

                Parcelable wrappedMemberOrder = Parcels.wrap(order);
                intent.putExtra("Member",wrappedMemberOrder);

                Parcelable wrappedCurrentOrder = Parcels.wrap(SCONNECTING.orderManager.currentOrder);
                intent.putExtra("Host",wrappedCurrentOrder);

                ((Activity) tableView.getContext()).startActivity(intent);
                ((Activity) tableView.getContext()).overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);

            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataSource ? dataSource.size() : 0);
    }



    public void refreshDataList(final Completion listener){

        dataSource = new ArrayList<>();

        notifyDataSetChanged();

        loadData(new Completion() {

            @Override
            public void onCompleted() {

                if(listener !=null)
                    listener.onCompleted();

            }
        });

    }

    public void loadData(final Completion listener){

        new TravelOrderController().GetRequestingMateOrder(SCONNECTING.orderManager.currentOrder.id, new GetItemsListener() {
            @Override
            public void onGetItems(Boolean success, List list) {

                if(list !=null && list.size() > 0){
                    reloadData(list, new Completion() {
                        @Override
                        public void onCompleted() {

                            reqRefreshControl.setVisibility(dataSource.size() > 0 ? View.VISIBLE: View.GONE);
                            reqEmptyRefreshControl.setVisibility(dataSource.size() > 0 ? View.GONE: View.VISIBLE);

                            if(listener !=null)
                                listener.onCompleted();

                        }
                    });

                }else{

                    reqRefreshControl.setVisibility(dataSource.size() > 0 ? View.VISIBLE: View.GONE);
                    reqEmptyRefreshControl.setVisibility(dataSource.size() > 0 ? View.GONE: View.VISIBLE);

                    if(listener !=null)
                        listener.onCompleted();
                }
            }
        });



    }

}

