package me.cl.lingxi.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imkit.RongIM;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.MoodAdapter;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.JsonCallback;
import me.cl.lingxi.common.util.SPUtils;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.common.view.OnLoadMoreListener;
import me.cl.lingxi.entity.Like;
import me.cl.lingxi.entity.Mood;
import me.cl.lingxi.entity.MoodBean;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.module.BaseFragment;
import me.cl.lingxi.module.mood.MoodActivity;
import me.cl.lingxi.module.mood.PublishActivity;
import okhttp3.Call;

/**
 * 圈子动态
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
                        startActivity(new Intent(getActivity(), PublishActivity.class));
                        break;
                }
                return false;
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
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
                        int uid = SPUtils.getInstance(getActivity()).getInt("uid");
                        postAddLike(lcid, uid);
                        break;
                    case R.id.mood_card:
                        Utils.toastShow(view.getContext(), "点我点我");
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

    //前往动态详情
    private void gotoMood(Mood<Like> mood) {
        Intent intent = new Intent(getActivity(), MoodActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("mood", mood);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //点赞
    private void postAddLike(String lcid, int uid) {
        OkHttpUtils.post()
                .url(Api.Url + "/addLike")
                .addParams("lcid", lcid)
                .addParams("uid", String.valueOf(uid))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "add like: " + response);
                        // TODO 刷新UI
                    }
                });
    }

    //获取动态列表
    private void getMoodList(String lcid, String count) {
        if (!mSwipeRefreshLayout.isRefreshing() && RefreshMODE == MOD_REFRESH) mSwipeRefreshLayout.setRefreshing(true);
        int uid = SPUtils.getInstance(getActivity()).getInt("uid", -1);
        OkHttpUtils.post()
                .url(Api.Url + "/minifeedList")
                .addParams("startlcid", lcid)
                .addParams("count", count)
                .addParams("uid", String.valueOf(uid))
                .build()
                .execute(new JsonCallback<Result<MoodBean<Mood<Like>>>>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mAdapter.updateLoadStatus(MoodAdapter.LOAD_NONE);
                        Utils.toastShow(getActivity(), R.string.toast_getmf_error);
                    }

                    @Override
                    public void onResponse(Result<MoodBean<Mood<Like>>> response, int id) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        switch (RefreshMODE) {
                            case MOD_LOADING:
                                load_length = load_length + response.getData().getTotalnum();
                                if (response.getData().getTotalnum() == 0 ){
                                    mAdapter.updateLoadStatus(MoodAdapter.LOAD_NONE);
                                    return;
                                }
                                updateData(response.getData().getMinifeedlist());
                                break;
                            default:
                                load_length = response.getData().getTotalnum();
                                mAdapter.setData(response.getData().getMinifeedlist());
                                break;
                        }
                    }
                });
    }

    //更新数据
    public void updateData(List<Mood<Like>> data) {
        mAdapter.updateData(data);
    }

    private void goToMember(){

    }
}
