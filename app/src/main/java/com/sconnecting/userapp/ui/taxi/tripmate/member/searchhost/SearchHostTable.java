package com.sconnecting.userapp.ui.taxi.tripmate.member.searchhost;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.GsonHelper;
import com.sconnecting.userapp.base.listener.GetStringValueListener;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.ui.taxi.tripmate.member.previewhost.PreviewHostScreen;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;


public class SearchHostTable extends RecyclerView.Adapter<SearchHostCell> {

    List<SearchHostObject> dataSource;
    RecyclerView tableView;

    SwipeRefreshLayout refreshControl ;
    SwipeRefreshLayout emptyRefreshControl ;
    LinearLayoutManager mLinearLayoutManager;



    public SearchHostTable(RecyclerView table, SwipeRefreshLayout refreshCtrl){

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

        List<SearchHostObject> data = new ArrayList<>();

        reloadData(data,listener);

    }

    public void reloadData( List<SearchHostObject> data,final Completion listener) {
        this.dataSource = new ArrayList<>();
        this.dataSource.addAll(data);

        notifyDataSetChanged();

        if(listener !=null)
            listener.onCompleted();
    }
    public void appendData( List<SearchHostObject> data,final Completion listener) {

        if(dataSource == null)
            this.dataSource = new ArrayList<>();

        int lastCount = this.dataSource.size();
        this.dataSource.addAll(data);
        notifyItemRangeInserted(lastCount-1,data.size());


        if(listener !=null)
            listener.onCompleted();
    }


    @Override
    public SearchHostCell onCreateViewHolder(ViewGroup viewGroup, int viewtype) {


        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.taxi_order_tripmate_member_searchhost_cell,viewGroup,false);
        return new SearchHostCell(view);


    }

    @Override
    public void onBindViewHolder(SearchHostCell cell, int i) {

        SearchHostObject item = dataSource.get(i);
        cell.updateWithModel(item);
        cell.bind(item, new SearchHostCell.OnItemClickListener() {
            @Override
            public void onItemClick(final SearchHostObject order) {


                Intent intent = new Intent(tableView.getContext(), PreviewHostScreen.class);
                intent.putExtra("Host", Parcels.wrap(order));

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


        TravelOrderController.GetNearestMateHostOrders(SCONNECTING.orderScreen.CurrentOrder().id, new GetStringValueListener() {
            @Override
            public void onCompleted(Boolean success, String value) {


                List<SearchHostObject> list = GsonHelper.getGson().fromJson(value, new TypeToken<ArrayList<SearchHostObject>>(){}.getType());

                if(list !=null && list.size() > 0){
                    reloadData(list, new Completion() {
                        @Override
                        public void onCompleted() {

                            refreshControl.setVisibility(dataSource.size() > 0 ? View.VISIBLE: View.GONE);
                            emptyRefreshControl.setVisibility(dataSource.size() > 0 ? View.GONE: View.VISIBLE);

                            if(listener !=null)
                                listener.onCompleted();

                        }
                    });

                }else{

                    refreshControl.setVisibility(dataSource.size() > 0 ? View.VISIBLE: View.GONE);
                    emptyRefreshControl.setVisibility(dataSource.size() > 0 ? View.GONE: View.VISIBLE);

                    if(listener !=null)
                        listener.onCompleted();
                }
            }
        });


    }

}
