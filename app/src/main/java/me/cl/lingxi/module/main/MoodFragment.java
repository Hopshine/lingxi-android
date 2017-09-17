package me.cl.lingxi.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imkit.RongIM;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.MoodAdapter;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.util.SPUtils;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.common.widget.ItemAnimator;
import me.cl.lingxi.common.widget.OnLoadMoreListener;
import me.cl.lingxi.entity.Like;
import me.cl.lingxi.entity.Mood;
import me.cl.lingxi.entity.MoodBean;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.module.BaseFragment;
import me.cl.lingxi.module.member.UserActivity;
import me.cl.lingxi.module.mood.MoodActivity;
import me.cl.lingxi.module.mood.PublishActivity;

/**
 * 圈子动态
 */
public class MoodFragment extends BaseFragment {

    private static final String MOOD_TYPE = "mood_type";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private String mType;
    private int saveUid;
    private String saveUName;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Mood<Like>> mList = new ArrayList<>();
    private MoodAdapter mAdapter;

    private final int MOD_REFRESH = 1;
    private final int MOD_LOADING = 2;
    private int RefreshMODE = 0;
    private int load_length = 0;

//    private CustomProgressDialog loadingProgress = new CustomProgressDialog(getActivity(), R.string.dialog_loading_lc);

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
                        gotoPublish();
                        break;
                }
                return false;
            }
        });

        saveUid = SPUtils.getInstance(getActivity()).getInt("uid", 0);
        saveUName = SPUtils.getInstance(getActivity()).getString("username", "");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new ItemAnimator());
        mAdapter = new MoodAdapter(mList);
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
            public void onItemClick(View view, Mood<Like> mood, int position) {
                switch (view.getId()) {
                    case R.id.user_img:
                        goToUser();
                        break;
                    case R.id.lc_chat:
                        if (RongIM.getInstance() != null)
                            RongIM.getInstance().startPrivateChat(getActivity(), String.valueOf(mood.getUid()), mood.getUname());
                        break;
                    case R.id.mood_card:
                    case R.id.mf_comment:
                        gotoMood(mood);
                        break;
                    case R.id.mf_like:
                        if (mood.islike()) return;
                        // 未点赞点赞
                        postAddLike(mood, position);
                        break;
                }
            }
        });

        //滑动监听
        mRecyclerView.addOnScrollListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                if (mAdapter.getItemCount() < 4) return;

                RefreshMODE = MOD_LOADING;
                mAdapter.updateLoadStatus(MoodAdapter.LOAD_MORE);

                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getMoodList(String.valueOf(load_length), "10");
                    }
                },1000);
            }
        });
    }

    // 前往动态发布
    private void gotoPublish() {
        Intent intent = new Intent(getActivity(), PublishActivity.class);
        startActivityForResult(intent, Constants.ACTIVITY_PUBLISH);
    }

    // 前往动态详情
    private void gotoMood(Mood<Like> mood) {
        Intent intent = new Intent(getActivity(), MoodActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("mood", mood);
        intent.putExtras(bundle);
        startActivityForResult(intent, Constants.ACTIVITY_MOOD);
    }

    // 点赞
    private void postAddLike(final Mood<Like> mood, final int position) {
        String lcId = String.valueOf(mood.getLcid());
        OkGo.<String>post(Api.addLike)
                .params("lcid", lcId)
                .params("uid", saveUid)
                .execute(new me.cl.lingxi.common.widget.JsonCallback<String>() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        // 刷新UI，本地模拟数据，等待下一次刷新获取
                        mood.setLikenum(mood.getLikenum() + 1);
                        mood.setIslike(true);
                        List<Like> likes = new ArrayList<>(mood.getLikelist());
                        Like like = new Like(saveUid, saveUName);
                        likes.add(like);
                        mood.setLikelist(likes);
                        mAdapter.updateItem(mood, position);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        Utils.toastShow(getActivity(), "点赞失败");
                    }
                });
    }

    // 获取动态列表
    private void getMoodList(String lcid, String count) {
        if (!mSwipeRefreshLayout.isRefreshing() && RefreshMODE == MOD_REFRESH) mSwipeRefreshLayout.setRefreshing(true);
        int uid = SPUtils.getInstance(getActivity()).getInt("uid", -1);
        OkGo.<Result<MoodBean<Mood<Like>>>>post(Api.moodList)
                .params("startlcid", lcid)
                .params("count", count)
                .params("uid", uid)
                .execute(new me.cl.lingxi.common.widget.JsonCallback<Result<MoodBean<Mood<Like>>>>() {
                    @Override
                    public void onSuccess(Response<Result<MoodBean<Mood<Like>>>> response) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        MoodBean<Mood<Like>> moodBean = response.body().getData();
                        switch (RefreshMODE) {
                            case MOD_LOADING:
                                load_length = load_length + moodBean.getTotalnum();
                                if (moodBean.getTotalnum() == 0 ){
                                    mAdapter.updateLoadStatus(MoodAdapter.LOAD_NONE);
                                    return;
                                }
                                updateData(moodBean.getMinifeedlist());
                                break;
                            default:
                                load_length = moodBean.getTotalnum();
                                mAdapter.setData(moodBean.getMinifeedlist());
                                break;
                        }
                    }

                    @Override
                    public void onError(Response<Result<MoodBean<Mood<Like>>>> response) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mAdapter.updateLoadStatus(MoodAdapter.LOAD_NONE);
                        Utils.toastShow(getActivity(), R.string.toast_getmf_error);
                    }
                });
    }

    //更新数据
    public void updateData(List<Mood<Like>> data) {
        mAdapter.addData(data);
    }

    // 刷新数据
    private void onRefresh(){
        RefreshMODE = MOD_REFRESH;
        getMoodList("0", "10");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 发布动态回退则掉调起刷新
        if (resultCode == Constants.ACTIVITY_PUBLISH)
            onRefresh();
    }

    // 前往用户页面
    private void goToUser(){
        Intent intent = new Intent(getActivity(), UserActivity.class);
        startActivity(intent);
    }
}
