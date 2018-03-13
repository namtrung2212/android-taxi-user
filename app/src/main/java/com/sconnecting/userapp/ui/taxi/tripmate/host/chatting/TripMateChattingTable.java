package com.sconnecting.userapp.ui.taxi.tripmate.host.chatting;

import android.graphics.Rect;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sconnecting.userapp.R;
import com.sconnecting.userapp.SCONNECTING;
import com.sconnecting.userapp.base.AnimationHelper;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.base.listener.GetItemsListener;
import com.sconnecting.userapp.base.listener.GetOneListener;
import com.sconnecting.userapp.data.entity.BaseController;
import com.sconnecting.userapp.data.entity.BaseModel;
import com.sconnecting.userapp.data.models.TravelOrder;
import com.sconnecting.userapp.data.models.TripMateChatting;
import com.sconnecting.userapp.data.storages.client.ClientStorage;
import com.sconnecting.userapp.data.storages.server.ServerStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by TrungDao on 8/26/16.
 */

public class TripMateChattingTable extends RecyclerView.Adapter<TripMateChattingCell> {

    public List<TripMateChattingObject> dataSource = new ArrayList<>();
    public RecyclerView tableView;
    SwipeRefreshLayout refreshControl ;
    SwipeRefreshLayout emptyRefreshControl ;

    LinearLayoutManager mLinearLayoutManager;
    Integer currentPages = 1;

    private boolean loading = true;

    public TravelOrder CurrentOrder() {

        if( SCONNECTING.orderManager == null)
            return null;

        return SCONNECTING.orderManager.currentOrder;

    }

    public TripMateChattingTable(RecyclerView table, SwipeRefreshLayout refreshCtrl){

        this.tableView = table;
        tableView.setAdapter(this);
        tableView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AnimationHelper.hideKeyBoard(v);
                return false;
            }
        });

        mLinearLayoutManager = new LinearLayoutManager(tableView.getContext());
        mLinearLayoutManager.setStackFromEnd(true);
        this.tableView.setLayoutManager(mLinearLayoutManager);

        tableView.addItemDecoration(new RecyclerView.ItemDecoration(){

            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) == 0)
                    outRect.top = 100;
                if (parent.getChildAdapterPosition(view) == mLinearLayoutManager.getItemCount() -1 )
                    outRect.bottom = 50;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tableView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    onTableScrolled();

                }
            });
        }else {
            tableView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    onTableScrolled();
                }
            });
        }



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

        emptyRefreshControl = (SwipeRefreshLayout)((FrameLayout) refreshCtrl.getParent()).findViewById(R.id.emptyRefreshControl);
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

    @Override
    public TripMateChattingCell onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.taxi_order_tripmate_host_chatcell, null);

        TripMateChattingCell viewHolder = new TripMateChattingCell(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TripMateChattingCell cell, int i) {

        TripMateChattingObject item = dataSource.get(i);
        cell.updateWithModel(item);
    }


    private void onTableScrolled() {

        int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (!loading && (firstVisibleItem == 0)) {

            loadOldData(new Completion() {
                @Override
                public void onCompleted() {
                }
            });
        }
    }


    void finishLoading(final Completion listener){

        refreshControl.setVisibility(dataSource.size() > 0 ? View.VISIBLE: View.GONE);
        emptyRefreshControl.setVisibility(dataSource.size() > 0 ? View.GONE: View.VISIBLE);

        loading = false;
        if (listener != null)
            listener.onCompleted();
    }

    public void reloadData( List<TripMateChattingObject> data) {

        this.dataSource = data;
        displayData();
        notifyDataSetChanged();

    }
    public void appendData(List<TripMateChattingObject> newList, final Completion listener) {

        if(newList.size() <= 0)
            return;

        int oldVisibleItemPos = mLinearLayoutManager.findFirstVisibleItemPosition();

        TripMateChattingObject oldItem = oldVisibleItemPos >=0 ? this.dataSource.get(oldVisibleItemPos) : null;

        int lastCount = this.dataSource.size();

        int iNewCount = 0;
        for (TripMateChattingObject newObject : newList) {

            boolean isFound = false;
            for (TripMateChattingObject oldObject : dataSource) {

                if(oldObject.cellObject.id.equals(newObject.cellObject.id)){
                    isFound = true;
                    break;
                }
            }

            if(!isFound){
                dataSource.add(newObject);
                iNewCount++;
            }

        }


        displayData();
        //notifyItemRangeInserted(lastCount,iNewCount);

        notifyDataSetChanged();

        if(oldItem != null)
             scrollToObjectId(oldItem.cellObject.id);
        else
                mLinearLayoutManager.scrollToPositionWithOffset(dataSource.size() -1,0);

        if(listener !=null)
            listener.onCompleted();
    }
    public void displayData(){

        Collections.sort(this.dataSource);

    }


    @Override
    public int getItemCount() {
        return (null != dataSource ? dataSource.size() : 0);
    }


    public void refreshDataList(final Completion listener){

        currentPages = 1;

        dataSource = new ArrayList<>();

        notifyDataSetChanged();

        loadData(listener);

    }

    public void loadData(final Completion listener){

        loading = true;

        if(currentPages <=0){
            currentPages = -1;

            finishLoading(listener);

            return;
        }

        String query = "";

        if(CurrentOrder().IsMateHost == 1)
            query = "HostOrder=" + CurrentOrder().id.toString();
        else
            query = "HostOrder=" + CurrentOrder().MateHostOrder;


        query = query + "&sortField=createdAt&pagesize=10&page=" + currentPages +"&sort=-1";

        new ServerStorage(TripMateChatting.class).query("selectall", query, new GetItemsListener() {
            @Override
            public void onGetItems(Boolean success,List chattings) {

                TripMateChattingObject.fromArray(CurrentOrder(), (List<TripMateChatting>) chattings, new TripMateChattingObject.GetChatObjectsListener() {
                    @Override
                    public void onGetObjects(Boolean success, List<TripMateChattingObject> list) {

                        if(list != null && list.size() > 0){
                            appendData(list,new Completion() {
                                @Override
                                public void onCompleted() {

                                    finishLoading(listener);
                                }
                            });

                        }else{
                            currentPages = -1;
                            finishLoading(listener);
                        }

                    }
                });
            }
        });


    }

    public void loadNewData(final Completion listener){
       
        currentPages = 1;
        loadData(listener);
        
    }


    public void loadOldData(final Completion listener){

        if(currentPages < 0){
            if(listener !=null)
                listener.onCompleted();
            return;
        }

        currentPages += 1;
        loadData(listener);
    }


    public void createNewItem(final String content,final Completion listener){

        SCONNECTING.orderManager.chattingHandler.UserChatToGroup(content, new GetOneListener() {
            @Override
            public void onGetOne(Boolean success, BaseModel item) {

                if(success){

                    addItem((TripMateChatting)item,listener);

                }else{

                    if(listener != null)
                        listener.onCompleted();
                }
            }
        });
    }


    public void addItemFromOtherUser(String contentId, String content,final Completion listener) {

        if (this.CurrentOrder() == null || this.CurrentOrder().id == null) {

            if (listener != null)
                listener.onCompleted();

            return;
        }


        TripMateChatting obj = new TripMateChatting();

        obj.id = contentId;
        obj.HostOrder = this.CurrentOrder().MateHostOrder;
        obj.UserOrder = this.CurrentOrder().id;
        obj.User = this.CurrentOrder().User;
        obj.UserName = this.CurrentOrder().UserName;
        obj.IsViewedList = null;
        obj.Content = content;
        obj.createdAt = new Date();
        obj.updatedAt = new Date();


        addItem(obj,listener);
    }

    public void addItem(final TripMateChatting item ,final Completion listener){

        if(this.CurrentOrder() == null || this.CurrentOrder().id == null){

            if(listener != null)
                listener.onCompleted();

            return;
        }


        TripMateChattingObject newObject = new TripMateChattingObject(this.CurrentOrder(),item);
        List<TripMateChattingObject> arr = new ArrayList<>();
        arr.add(0,newObject);
        appendData(arr, new Completion() {
            @Override
            public void onCompleted() {

                scrollToObjectId(item.id);

                if(listener != null)
                    listener.onCompleted();

            }
        });

    }



    public void scrollToObjectId(String objectId){

        for (int i = 0; i < dataSource.size(); i++) {
            if(dataSource.get(i).cellObject.id.equals(objectId)){

                mLinearLayoutManager.scrollToPositionWithOffset(i,0);
                return;

            }
        }


    }
}