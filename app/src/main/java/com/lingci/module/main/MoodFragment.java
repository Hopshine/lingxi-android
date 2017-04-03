package com.lingci.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.lingci.R;
import com.lingci.adapter.MoodAdapter;
import com.lingci.common.config.Api;
import com.lingci.common.util.GsonUtil;
import com.lingci.common.util.SPUtils;
import com.lingci.common.util.Utils;
import com.lingci.entity.Like;
import com.lingci.entity.Mood;
import com.lingci.entity.MoodBean;
import com.lingci.entity.Result;
import com.lingci.module.BaseFragment;
import com.lingci.module.mood.MoodActivity;
import com.lingci.module.mood.PublishActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imkit.RongIM;
import okhttp3.Call;

/**
 * 用户心情列表
 */
public class MoodFragment extends BaseFragment {

    private static final String MOOD_TYPE = "mood_type";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private String mType;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private LinearLayoutManager layoutManager;
    private List<Mood<Like>> mList = new ArrayList<>();
    private MoodAdapter mAdapter;

    private final int MOD_REFRESH = 1;
    private final int MOD_LOADING = 2;
    private int RefreshMODE = 0;
    private int load_length = 0;

//    private CustomProgressDialog loadingProgress = new CustomProgressDialog(getActivity(), R.string.dialog_loading_lc);

    private int lastItem;
    private int lastVisibleItem;
    private boolean isLoadMore;

    public MoodFragment() {

    }

    public static MoodFragment newInstance(String moodType) {
        MoodFragment fragment = new MoodFragment();
        Bundle args = new Bundle();
        args.putString(MOOD_TYPE, moodType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getString(MOOD_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mood, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        setupToolbar(mToolbar, "圈子", R.menu.menu_publish, new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_share:
                        startActivity(new Intent(getActivity(), PublishActivity.class));
                        break;
                }
                return false;
            }
        });

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MoodAdapter(getActivity(), mList);
        mRecyclerView.setAdapter(mAdapter);

        initEvent();

        getMoodList("0", "10");
    }

    //初始化事件
    private void initEvent() {
        //刷新
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshMODE = MOD_REFRESH;
                getMoodList("0", "10");
            }
        });

        //item点击
        mAdapter.setOnItemListener(new MoodAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, Mood<Like> mood) {
                switch (view.getId()) {
                    case R.id.user_img:
                        Utils.toastShow(view.getContext(), "头像");
                        break;
                    case R.id.lc_chat:
                        Utils.toastShow(view.getContext(), "聊天");
                        if (RongIM.getInstance() != null)
                            RongIM.getInstance().startPrivateChat(getActivity(), String.valueOf(mood.getUid()), mood.getUname());
                        break;
                    case R.id.mf_comment:
                        Utils.toastShow(view.getContext(), "评论");
                        gotoMood(mood);
                        break;
                    case R.id.mf_like:
                        Utils.toastShow(view.getContext(), "点赞");
                        String lcid = String.valueOf(mood.getLcid());
                        String uname = SPUtils.getInstance(getActivity()).getString("username");
                        postAddLike(lcid, uname);
                        break;
                    case R.id.mood_card:
                        Utils.toastShow(view.getContext(), "点我点我");
                        break;
                }
            }
        });

        //滑动监听
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //得到当前显示的最后一个item的view
                View lastChildView = recyclerView.getLayoutManager().getChildAt(recyclerView.getLayoutManager().getChildCount()-1);
                //得到lastChildView的bottom坐标值
                int lastChildBottom = lastChildView.getBottom();
                //得到Recyclerview的底部坐标减去底部padding值，也就是显示内容最底部的坐标
                int recyclerBottom =  recyclerView.getBottom()-recyclerView.getPaddingBottom();
                //通过这个lastChildView得到这个view当前的position值
                int lastPosition  = recyclerView.getLayoutManager().getPosition(lastChildView);

                //判断lastChildView的bottom值跟recyclerBottom
                //判断lastPosition是不是最后一个position
                //如果两个条件都满足则说明是真正的滑动到了底部
                if(lastChildBottom == recyclerBottom && lastPosition == recyclerView.getLayoutManager().getItemCount()-1 ){
                }



/*
                boolean unBottom = mRecyclerView.canScrollVertically(1);//是否能向上滚动，false表示已经滚动到底部

                if (!unBottom) Log.d(TAG, "到底了: ");

                boolean unTop = mRecyclerView.canScrollVertically(-1);//的是否能向下滚动，false表示已经滚动到顶部

                if (!unTop) Log.d(TAG, "到顶了: ");
*/

                if (mSwipeRefreshLayout.isRefreshing()){
                    mAdapter.updateLoadStatus(MoodAdapter.LOAD_END);
                    return;
                }

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    lastItem = layoutManager.findLastVisibleItemPosition();
                    if (layoutManager.getItemCount() == 1){
                        mAdapter.updateLoadStatus(MoodAdapter.LOAD_NONE);
                        return;
                    }

                    if (lastItem + 1 == layoutManager.getItemCount()){
                        mAdapter.updateLoadStatus(MoodAdapter.LOAD_PULL_TO);
                        isLoadMore = true;
                        mAdapter.updateLoadStatus(MoodAdapter.LOAD_MORE);

                        RefreshMODE = MOD_LOADING;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (isLoadMore){
                                    getMoodList(String.valueOf(load_length), "10");
                                }
                            }
                        },1000);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
    }

    //前往动态详情
    private void gotoMood(Mood<Like> mood) {
        Intent intent = new Intent(getActivity(), MoodActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("mood", mood);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //点赞
    private void postAddLike(String lcid, String uname) {
        OkHttpUtils.post()
                .url(Api.Url + "/addLike")
                .addParams("lcid", lcid)
                .addParams("uname", uname)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "add like: " + response);
                    }
                });
    }

    //获取动态列表
    private void getMoodList(String lcid, String count) {
        if (!mSwipeRefreshLayout.isRefreshing() && RefreshMODE == MOD_REFRESH) mSwipeRefreshLayout.setRefreshing(true);
        OkHttpUtils.post()
                .url(Api.Url + "/minifeedList")
                .addParams("startlcid", lcid)
                .addParams("count", count)
                .addParams("uname", SPUtils.getInstance(getActivity()).getString("username", ""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Utils.toastShow(getActivity(), R.string.toast_getmf_error);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "get mood list: " + response);
                        mSwipeRefreshLayout.setRefreshing(false);
                        Type jsonType = new TypeToken<Result<MoodBean<Mood<Like>>>>() {}.getType();
                        GsonUtil.fromJson(response, jsonType, new GsonUtil.GsonResult<MoodBean<Mood<Like>>>() {
                            @Override
                            public void onTrue(Result<MoodBean<Mood<Like>>> result) {
                                switch (RefreshMODE) {
                                    case MOD_LOADING:
                                        load_length = load_length + result.getData().getTotalnum();
                                        if (result.getData().getTotalnum() == 0 ){
                                            mAdapter.updateLoadStatus(MoodAdapter.LOAD_NONE);
                                            return;
                                        }
                                        updateData(result.getData().getMinifeedlist());
                                        break;
                                    default:
                                        load_length = result.getData().getTotalnum();
                                        mAdapter.setData(result.getData().getMinifeedlist());
                                        break;
                                }
                            }

                            @Override
                            public void onErr(Result<Object> result, Exception e) {
                                Log.d(TAG, "gson mood onErr: " + result.getMsg());
                                mAdapter.updateLoadStatus(MoodAdapter.LOAD_NONE);
                            }
                        });
                    }
                });
    }

    //更新数据
    public void updateData(List<Mood<Like>> data) {
        mAdapter.updateData(data);
    }
}
