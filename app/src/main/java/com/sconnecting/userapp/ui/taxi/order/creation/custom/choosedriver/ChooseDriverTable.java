package com.sconnecting.userapp.ui.taxi.order.creation.custom.choosedriver;

/**
 * Created by TrungDao on 8/7/16.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.gson.reflect.TypeToken;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.GsonHelper;
import com.sconnecting.userapp.base.listener.GetStringValueListener;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.ui.taxi.share.DriverProfile.DriverProfileScreen;
import com.sconnecting.userapp.data.models.TravelOrder;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ChooseDriverTable extends RecyclerView.Adapter<ChooseDriverCell> {

    List<ChooseDriverObject> dataSource;
    RecyclerView tableView;
    SwipeRefreshLayout refreshControl ;
    SwipeRefreshLayout emptyRefreshControl ;
    LinearLayoutManager mLinearLayoutManager;
    Integer currentPages = 1;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 0; // The minimum amount of items to have below your current scroll position before loading more.




    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }


    public ChooseDriverTable(RecyclerView table, SwipeRefreshLayout refreshCtrl){

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tableView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    int totalItemCount = mLinearLayoutManager.getItemCount();
                    int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();


                    if (!loading && (totalItemCount <= (lastVisibleItem + visibleThreshold))) {

                        loadMoreDrivers(new Completion() {
                            @Override
                            public void onCompleted() {

                            }
                        });
                    }

                }
            });
        }else {
            tableView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                }
            });
        }



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

    public void reloadData( List<ChooseDriverObject> data,final Completion listener) {

        this.dataSource = new ArrayList<>();
        this.dataSource.addAll(data);

        notifyDataSetChanged();

        if(listener !=null)
            listener.onCompleted();

    }
    public void appendData( List<ChooseDriverObject> data,final Completion listener) {

        if(dataSource == null)
            this.dataSource = new ArrayList<>();
        int lastCount = this.dataSource.size();

        int iNewCount = 0;
        for (ChooseDriverObject newItem : data) {

            Boolean isFound = false;
            for (ChooseDriverObject oldItem : dataSource) {
                if(oldItem.DriverId.equals(newItem.DriverId)){
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
    public ChooseDriverCell onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.taxi_order_creation_custom_choosedriver_cell, null);

        ChooseDriverCell viewHolder = new ChooseDriverCell(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChooseDriverCell cell, int i) {

        ChooseDriverObject item = dataSource.get(i);
        cell.updateWithModel(item);
        cell.bind(item, new ChooseDriverCell.OnItemClickListener() {
            @Override
            public void onItemClick(ChooseDriverObject item) {

                CurrentOrder().resetToOpen();

                Intent intent = new Intent(tableView.getContext(), DriverProfileScreen.class);
                intent.putExtra("DriverID",item.DriverId);

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

        currentPages = 1;

        dataSource = new ArrayList<>();

        notifyDataSetChanged();

        loadDrivers(new Completion() {
            @Override
            public void onCompleted() {

                if(listener !=null)
                    listener.onCompleted();

            }
        });

    }



    void finishLoading(final Completion listener){

        refreshControl.setVisibility(dataSource.size() > 0 ? View.VISIBLE: View.GONE);
        emptyRefreshControl.setVisibility(dataSource.size() > 0 ? View.GONE: View.VISIBLE);

        loading = false;
        if (listener != null)
            listener.onCompleted();
    }

    public void loadDrivers(final Completion listener){

        loading = true;

        if(currentPages <=0) {

            currentPages = -1;

            finishLoading(listener);
            return;
        }

            if(CurrentOrder().OrderPickupLoc == null || CurrentOrder().OrderPickupLoc.getLatLng() == null){

                finishLoading(listener);
                return;
            }


        TravelOrderController.GetNearestDriversForOrder(SCONNECTING.orderScreen.CurrentOrder().id,currentPages, new GetStringValueListener() {
            @Override
            public void onCompleted(Boolean success, String value) {

                if(success) {

                    List<ChooseDriverObject> list = GsonHelper.getGson().fromJson(value, new TypeToken<ArrayList<ChooseDriverObject>>() {
                    }.getType());


                    if(list !=null && list.size() > 0){
                        appendData(list, new Completion() {
                            @Override
                            public void onCompleted() {

                                finishLoading(listener);
                            }
                        });

                    }else{
                        currentPages = -1;
                        finishLoading(listener);
                    }

                }else{

                    currentPages = -1;
                    finishLoading(listener);
                }
            }
        });


    }


    public void loadMoreDrivers(final Completion listener){

        if(CurrentOrder().IsPickupNow()){

            currentPages += 1;
            loadDrivers(listener);

        }else{

            if(listener !=null)
                listener.onCompleted();
        }
    }
}

