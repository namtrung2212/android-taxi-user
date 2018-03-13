package com.sconnecting.userapp.ui.taxi.history;

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
import com.sconnecting.userapp.ui.taxi.order.OrderScreen;
import com.sconnecting.userapp.data.models.TravelOrder;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TrungDao on 8/18/16.
 */

public class NotYetPickupTable extends RecyclerView.Adapter<TravelHistoryCell> {

    List<TravelOrder> dataSource;
    RecyclerView tableView;

    SwipeRefreshLayout refreshControl ;
    SwipeRefreshLayout emptyRefreshControl ;
    LinearLayoutManager mLinearLayoutManager;

    public NotYetPickupTable(RecyclerView table, SwipeRefreshLayout refreshCtrl){

        this.tableView = table;
        tableView.setAdapter(this);

        mLinearLayoutManager = new LinearLayoutManager(tableView.getContext());
        this.tableView.setLayoutManager(mLinearLayoutManager);

        tableView.addItemDecoration(new RecyclerView.ItemDecoration(){

            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1)
                    outRect.bottom = 100;
            }
        });

        this.refreshControl = refreshCtrl;

        refreshControl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDataList(new Completion() {
                    @Override
                    public void onCompleted() {
                        refreshControl.setRefreshing(false);
                    }
                });
            }
        });

        emptyRefreshControl = (SwipeRefreshLayout)((ConstraintLayout) refreshCtrl.getParent()).findViewById(R.id.emptyRefreshControl);
        emptyRefreshControl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDataList(new Completion() {
                    @Override
                    public void onCompleted() {
                        emptyRefreshControl.setRefreshing(false);
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
    public TravelHistoryCell onCreateViewHolder(ViewGroup viewGroup, int viewtype) {


        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.taxi_history_cell,viewGroup,false);
        return new TravelHistoryCell(view);


    }

    @Override
    public void onBindViewHolder(TravelHistoryCell cell, int i) {

        TravelOrder item = dataSource.get(i);
        cell.updateWithModel(item);
        cell.bind(item, new TravelHistoryCell.OnItemClickListener() {
            @Override
            public void onItemClick(final TravelOrder order) {

                SCONNECTING.orderManager.reset(order,true,null);

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

        loadData(listener);

    }


    void finishLoading(final Completion listener){

        refreshControl.setVisibility(dataSource.size() > 0 ? View.VISIBLE: View.GONE);
        emptyRefreshControl.setVisibility(dataSource.size() > 0 ? View.GONE: View.VISIBLE);

        if (listener != null)
            listener.onCompleted();
    }


    public void loadData(final Completion listener){

        TravelOrderController.GetNotYetPickupOrderByUser(SCONNECTING.userManager.CurrentUser.id, new GetItemsListener() {
            @Override
            public void onGetItems(Boolean success,List list) {

                if(list != null && list.size() > 0){
                    reloadData(list, new Completion() {
                        @Override
                        public void onCompleted() {

                            finishLoading(listener);
                        }
                    });

                }else{

                    finishLoading(listener);
                }
            }
        });


    }
}
