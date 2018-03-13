package com.sconnecting.userapp.ui.taxi.share.DriverProfile;

import android.graphics.Rect;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sconnecting.userapp.R;
import com.sconnecting.userapp.base.listener.GetItemsListener;
import com.sconnecting.userapp.base.listener.Completion;
import com.sconnecting.userapp.data.controllers.TravelOrderController;
import com.sconnecting.userapp.data.models.TravelOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TrungDao on 8/10/16.
 */

public class CommentTable extends RecyclerView.Adapter<CommentCell> {

    List<TravelOrder> dataSource;
    RecyclerView tableView;
    
    LinearLayoutManager mLinearLayoutManager;
    Integer currentPages = 1;

    String DriverId;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 0; 


    public CommentTable(RecyclerView table, String driverId){

        this.tableView = table;
        tableView.setAdapter(this);

        this.DriverId = driverId;

        mLinearLayoutManager = new LinearLayoutManager(tableView.getContext());
        this.tableView.setLayoutManager(mLinearLayoutManager);

        tableView.addItemDecoration(new RecyclerView.ItemDecoration(){

            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1)
                    outRect.bottom = 100;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tableView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    int visibleItemCount = tableView.getChildCount();
                    int totalItemCount = mLinearLayoutManager.getItemCount();
                    int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold)) {

                        loadMoreObjects(new Completion() {
                            @Override
                            public void onCompleted() {

                            }
                        });

                        loading = true;
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


    }

    public void reloadData( List<TravelOrder> data) {

        this.dataSource = data;
        notifyDataSetChanged();

    }
    public void appendData( List<TravelOrder> data) {

        int lastCount = this.dataSource.size();
        this.dataSource.addAll(data);
        notifyItemRangeInserted(lastCount-1,data.size());


    }

    @Override
    public CommentCell onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.taxi_share_driverprofile_comment_cell, null);

        CommentCell viewHolder = new CommentCell(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CommentCell cell, int i) {

        TravelOrder item = dataSource.get(i);
        cell.updateWithModel(item);
    }

    @Override
    public int getItemCount() {
        return (null != dataSource ? dataSource.size() : 0);
    }


    public void refreshObjectList(final Completion listener){

        currentPages = 1;

        dataSource = new ArrayList<>();

        notifyDataSetChanged();

        loadObjects(listener);

    }

    public void loadObjects(final Completion listener){

        TravelOrderController.GetOrderComments(DriverId,currentPages, 10, new GetItemsListener() {
            @Override
            public void onGetItems(Boolean success,List list) {

                if(list != null && list.size() > 0){
                    appendData(list);

                }else{
                    currentPages = -1;
                }

                if(listener !=null)
                    listener.onCompleted();
            }
        });


    }

    public void loadMoreObjects(final Completion listener){

        if(currentPages < 0){
            if(listener !=null)
                listener.onCompleted();
            return;
        }

        currentPages += 1;
        loadObjects(listener);
    }
}