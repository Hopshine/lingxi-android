package me.cl.library.loadmore;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * RecyclerView加载更多
 * Created by Bafsj on 2016/12/20.
 */

public abstract class OnLoadMoreListener extends RecyclerView.OnScrollListener {

    private int itemCount, lastPosition;

    public abstract void onLoadMore();

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            itemCount = layoutManager.getItemCount();
            lastPosition = layoutManager.findLastCompletelyVisibleItemPosition();
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        // 判断RecyclerView的状态是空闲时，同时，是最后一个可见的ITEM时才加载
        if (newState == RecyclerView.SCROLL_STATE_IDLE && lastPosition == itemCount - 1) {
            this.onLoadMore();
        }
    }
}
