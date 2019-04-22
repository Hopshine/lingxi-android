package me.cl.lingxi.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.library.base.BaseFragment;
import me.cl.library.loadmore.OnLoadMoreListener;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.IncVideoAdapter;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.util.GsonUtil;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.common.widget.GridItemDecoration;
import me.cl.lingxi.entity.inc.IncResult;
import me.cl.lingxi.entity.inc.IncVideo;
import me.cl.lingxi.module.search.SearchActivity;
import me.cl.lingxi.module.video.IncVideoPlayActivity;
import okhttp3.Call;

public class HomeFragment extends BaseFragment {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private static final String TYPE = "type";

    private String mType;
    private IncVideoAdapter mAdapter;
    private int mPage = 0;

    public HomeFragment() {

    }

    public static HomeFragment newInstance(String newsType) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(TYPE, newsType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getString(TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        ToolbarUtil.init(mToolbar, getActivity())
                .setTitle(R.string.title_bar_home)
                .setTitleCenter()
                .setMenu(R.menu.search_menu, new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_search:
                                gotoSearch();
                                break;
                        }
                        return false;
                    }
                })
                .build();

        initRecyclerView();
        initData();
    }


    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        List<IncVideo> incVideos = new ArrayList<>();
        String videoJson = SPUtil.build().getString(IncVideoPlayActivity.INC_VIDEO);
        if (!TextUtils.isEmpty(videoJson)) {
            incVideos = GsonUtil.toList(videoJson, IncVideo[].class);
        }
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        GridItemDecoration gridItemDecoration = new GridItemDecoration();
//        gridItemDecoration.setDrawable(Color.parseColor("#f2f2f2"));
        gridItemDecoration.setDecoration(Utils.dp2px(8));
        mRecyclerView.addItemDecoration(gridItemDecoration);

        mAdapter = new IncVideoAdapter(incVideos);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new IncVideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View paramView, IncVideo incVideo) {
                gotoVideoPlay(incVideo);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 0;
                getData();
            }
        });
        mRecyclerView.addOnScrollListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mPage++;
                getData();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mPage = 0;
        mSwipeRefreshLayout.setRefreshing(true);
        getData();
    }

    private void getData() {
        OkUtil.get()
                .url(Api.incApi)
                .addUrlParams("h", "24")
                .addUrlParams("t", "4")
                .addUrlParams("pg", String.valueOf(mPage))
                .execute(new ResultCallback<IncResult>() {
                    @Override
                    public void onSuccess(IncResult response) {
                        List<IncVideo> video = response.getVideo();
                        if (video != null) {
                            if (mPage == 0) {
                                setData(video);
                            } else {
                                updateData(video);
                            }
                        } else {
                            setError();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        setError();
                    }
                });
    }

    /**
     * 设置Adapter数据
     */
    private void setData(List<IncVideo> list) {
        setRefreshFalse();
        mAdapter.setData(list);
        SPUtil.build().putString(IncVideoPlayActivity.INC_VIDEO, GsonUtil.toJson(list));
    }

    /**
     * 更新Adapter数据
     */
    private void updateData(List<IncVideo> list) {
        setRefreshFalse();
        mAdapter.updateData(list);
    }

    /**
     * 设置异常提示
     */
    private void setError() {
        setRefreshFalse();
        showToast("没有更多了╮(╯▽╰)╭");
    }

    /**
     * 结束刷新
     */
    private void setRefreshFalse() {
        boolean refreshing = mSwipeRefreshLayout.isRefreshing();
        if (refreshing) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void gotoSearch() {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivity(intent);
    }

    private void gotoVideoPlay(IncVideo incVideo) {
        Intent intent = new Intent(getActivity(), IncVideoPlayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(IncVideoPlayActivity.INC_VIDEO, incVideo);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
