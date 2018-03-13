package com.sconnecting.userapp.ui.taxi.order.creation.custom.lateconfig;

/**
 * Created by TrungDao on 8/7/16.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.listener.GetItemsListener;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.base.listener.CompletionWithObject;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.ui.taxi.share.DriverProfile.DriverProfileScreen;
import com.sconnecting.userapp.data.models.TravelOrder;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class LateBiddingTable extends RecyclerView.Adapter<LateBiddingCell> {

    List<LateBiddingObject> dataSource;
    RecyclerView tableView;
    SwipeRefreshLayout refreshControl ;
    SwipeRefreshLayout emptyRefreshControl ;
    LinearLayoutManager mLinearLayoutManager;


    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }


    public LateBiddingTable(RecyclerView table, SwipeRefreshLayout refreshCtrl){

        this.tableView = table;
        tableView.setAdapter(this);

        mLinearLayoutManager = new LinearLayoutManager(tableView.getContext());
        this.tableView.setLayoutManager(mLinearLayoutManager);

        tableView.addItemDecoration(new ItemDecoration(){

            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1)
                    outRect.bottom = 100;
            }
        });

        this.refreshControl = refreshCtrl;

        refreshControl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDriverList(new Completion() {
                    @Override
                    public void onCompleted() {
                        refreshControl.setRefreshing(false);
                    }
                });
            }
        });

        emptyRefreshControl = (SwipeRefreshLayout)((FrameLayout) refreshCtrl.getParent()).findViewById(R.id.emptyRefreshControl);
        emptyRefreshControl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDriverList(new Completion() {
                    @Override
                    public void onCompleted() {
                        emptyRefreshControl.setRefreshing(false);
                    }
                });
            }
        });
    }

    public void reloadData( List<LateBiddingObject> data,final Completion listener) {

        this.dataSource = new ArrayList<>();
        this.dataSource.addAll(data);

        notifyDataSetChanged();

        if(listener !=null)
            listener.onCompleted();

    }
    public void appendData( List<LateBiddingObject> data,final Completion listener) {

        if(dataSource == null)
            this.dataSource = new ArrayList<>();
        int lastCount = this.dataSource.size();

        int iNewCount = 0;
        for (LateBiddingObject newItem : data) {

            Boolean isFound = false;
            for (LateBiddingObject oldItem : dataSource) {
                if(oldItem.driverId.equals(newItem.driverId)){
                    isFound = true;
                    break;
                }
            }
            if(isFound == false) {
                iNewCount++;
                dataSource.add(newItem);
            }

        }

        notifyItemRangeInserted(lastCount-1,iNewCount);

        if(listener !=null)
            listener.onCompleted();


    }

    @Override
    public LateBiddingCell onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.taxi_order_creation_custom_late_bidding_cell, null);

        LateBiddingCell viewHolder = new LateBiddingCell(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LateBiddingCell cell, int i) {

        LateBiddingObject item = dataSource.get(i);
        cell.updateWithModel(item);
        cell.bind(item, new LateBiddingCell.OnItemClickListener() {
            @Override
            public void onItemClick(LateBiddingObject item) {

                CurrentOrder().resetToOpen();

                Intent intent = new Intent(tableView.getContext(), DriverProfileScreen.class);
                intent.putExtra("DriverID",item.driverId);

                Parcelable wrappedDriverBidding = Parcels.wrap(item.driverBidding);
                intent.putExtra("DriverBidding",wrappedDriverBidding);

                Parcelable wrappedCurrentOrder = Parcels.wrap(CurrentOrder());
                intent.putExtra("CurrentOrder",wrappedCurrentOrder);

                ((Activity)tableView.getContext()).startActivity(intent);
                ((Activity)tableView.getContext()).overridePendingTransition(R.anim.pull_in_right,R.anim.push_out_left);



            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataSource ? dataSource.size() : 0);
    }




    public void refreshDriverList(final Completion listener){

        dataSource = new ArrayList<>();

        loadDrivers(new Completion() {
            @Override
            public void onCompleted() {

                if(listener !=null)
                    listener.onCompleted();

            }
        });

    }

    public void loadDrivers(final Completion listener){

        loadBiddingDrivers(listener);

    }

    void finishLoading(final Completion listener){

        refreshControl.setVisibility(dataSource.size() > 0 ? View.VISIBLE: View.GONE);
        emptyRefreshControl.setVisibility(dataSource.size() > 0 ? View.GONE: View.VISIBLE);

        if (listener != null)
            listener.onCompleted();
    }

    public void loadBiddingDrivers(final Completion listener){

        if(CurrentOrder().isNew() == false){

            TravelOrderController.GetOpenBiddingsByOrder(CurrentOrder().id, new GetItemsListener() {
                @Override
                public void onGetItems(Boolean success,List biddings) {

                    if(success) {
                        LateBiddingObject.fromArray(CurrentOrder(), biddings, new CompletionWithObject() {
                            @Override
                            public void onCompleted(Object obj) {
                                if (obj instanceof List) {
                                    List list = (List) obj;
                                    if (list.size() > 0) {
                                        appendData(list, new Completion() {
                                            @Override
                                            public void onCompleted() {

                                                finishLoading(listener);
                                            }
                                        });
                                    }else{

                                        finishLoading(listener);
                                    }
                                }else{

                                    finishLoading(listener);
                                }
                            }
                        });

                    }else{

                        finishLoading(listener);
                    }
                }
            });

        }else{

            finishLoading(listener);
        }
    }


}

