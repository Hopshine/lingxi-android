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
import me.cl.library.base.BaseFragment;
import me.cl.library.loadmore.LoadMord;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.FeedAdapter;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.util.SPUtils;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.common.widget.ItemAnimator;
import me.cl.library.loadmore.OnLoadMoreListener;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.entity.FeedExtend;
import me.cl.lingxi.entity.Like;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.module.feed.FeedActivity;
import me.cl.lingxi.module.feed.PublishActivity;
import me.cl.lingxi.module.member.UserActivity;
import me.iwf.photopicker.PhotoPreview;

/**
 * 圈子动态
 */
public class FeedFragment extends BaseFragment {

    private static final String MOOD_TYPE = "mood_type";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private int saveUid;
    private String saveUName;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private List<Feed> mList = new ArrayList<>();
    private FeedAdapter mAdapter;

    private int mPage = 0;
    private int mCount = 10;
    private final int MOD_REFRESH = 1;
    private final int MOD_LOADING = 2;
    private int RefreshMODE = 0;

    public FeedFragment() {

    }

    public static FeedFragment newInstance(String moodType) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(MOOD_TYPE, moodType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mType = getArguments().getString(MOOD_TYPE);
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

        saveUid = SPUtils.getInstance(getActivity()).getInt(Constants.USER_ID, 0);
        saveUName = SPUtils.getInstance(getActivity()).getString(Constants.USER_NAME, "");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new ItemAnimator());
        mAdapter = new FeedAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);

        initEvent();

        getMoodList(mPage, mCount);
    }

    //初始化事件
    private void initEvent() {
        //刷新
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshMODE = MOD_REFRESH;
                mPage = 0;
                getMoodList(mPage, mCount);
            }
        });

        //item点击
        mAdapter.setOnItemListener(new FeedAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, Feed feed, int position) {
                switch (view.getId()) {
                    case R.id.user_img:
                        goToUser(feed);
                        break;
                    case R.id.lc_chat:
                        if (RongIM.getInstance() != null)
                            RongIM.getInstance().startPrivateChat(getActivity(), String.valueOf(feed.getUid()), feed.getUname());
                        break;
                    case R.id.mood_card:
                    case R.id.mf_comment:
                        gotoMood(feed);
                        break;
                    case R.id.mf_like:
                        if (feed.islike()) return;
                        // 未点赞点赞
                        postAddLike(feed, position);
                        break;
                }
            }

            @Override
            public void onPhotoClick(ArrayList<String> photos, int position) {

                PhotoPreview.builder()
                        .setPhotos(photos)
                        .setCurrentItem(position)
                        .setShowDeleteButton(false)
                        .start(getActivity());
            }
        });

        //滑动监听
        mRecyclerView.addOnScrollListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                if (mAdapter.getItemCount() < 4) return;

                RefreshMODE = MOD_LOADING;
                mAdapter.updateLoadStatus(LoadMord.LOAD_MORE);

                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getMoodList(mPage, mCount);
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
    private void gotoMood(Feed feed) {
        Intent intent = new Intent(getActivity(), FeedActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("mood", feed);
        intent.putExtras(bundle);
        startActivityForResult(intent, Constants.ACTIVITY_MOOD);
    }

    // 点赞
    private void postAddLike(final Feed feed, final int position) {
        String lcId = String.valueOf(feed.getLcid());
        OkGo.<String>post(Api.addLike)
                .params("lcid", lcId)
                .params("uid", saveUid)
                .execute(new me.cl.lingxi.common.widget.JsonCallback<String>() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        // 刷新UI，本地模拟数据，等待下一次刷新获取
                        feed.setLikenum(feed.getLikenum() + 1);
                        feed.setIslike(true);
                        List<Like> likes = new ArrayList<>(feed.getLikelist());
                        Like like = new Like(saveUid, saveUName);
                        likes.add(like);
                        feed.setLikelist(likes);
                        mAdapter.updateItem(feed, position);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        Utils.toastShow(getActivity(), "点赞失败");
                    }
                });
    }

    // 获取动态列表
    private void getMoodList(int page, int count) {
        if (!mSwipeRefreshLayout.isRefreshing() && RefreshMODE == MOD_REFRESH) mSwipeRefreshLayout.setRefreshing(true);
        int uid = SPUtils.getInstance(getActivity()).getInt("uid", -1);
        OkGo.<Result<FeedExtend>>get(Api.listFeed)
                .params("page", page)
                .params("count", count)
                .params("uid", uid)
                .execute(new me.cl.lingxi.common.widget.JsonCallback<Result<FeedExtend>>() {
                    @Override
                    public void onSuccess(Response<Result<FeedExtend>> response) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mPage++;
                        FeedExtend moodBean = response.body().getData();
                        switch (RefreshMODE) {
                            case MOD_LOADING:
                                if (moodBean.getTotalnum() == 0 ){
                                    mAdapter.updateLoadStatus(LoadMord.LOAD_NONE);
                                    return;
                                }
                                updateData(moodBean.getMinifeedlist());
                                break;
                            default:
                                mAdapter.setData(moodBean.getMinifeedlist());
                                break;
                        }
                    }

                    @Override
                    public void onError(Response<Result<FeedExtend>> response) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mAdapter.updateLoadStatus(LoadMord.LOAD_NONE);
                        Utils.toastShow(getActivity(), R.string.toast_getmf_error);
                    }
                });
    }

    //更新数据
    public void updateData(List<Feed> data) {
        mAdapter.addData(data);
    }

    // 刷新数据
    private void onRefresh(){
        RefreshMODE = MOD_REFRESH;
        mPage = 0;
        getMoodList(mPage, mCount);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 发布动态回退则掉调起刷新
        if (resultCode == Constants.ACTIVITY_PUBLISH)
            onRefresh();
    }

    // 前往用户页面
    private void goToUser(Feed feed){
        Intent intent = new Intent(getActivity(), UserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("mood", feed);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
