package me.cl.lingxi.module.member;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.MoodAdapter;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.util.SPUtils;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.common.widget.ItemAnimator;
import me.cl.lingxi.common.widget.JsonCallback;
import me.cl.lingxi.common.widget.OnLoadMoreListener;
import me.cl.lingxi.entity.Mood;
import me.cl.lingxi.entity.MoodExtend;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.module.BaseActivity;
import me.cl.lingxi.module.mood.MoodActivity;
import me.iwf.photopicker.PhotoPreview;

public class UserActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.scrollView)
    NestedScrollView mScrollView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;
    @BindView(R.id.button_bar)
    ButtonBarLayout mButtonBar;
    @BindView(R.id.parallax)
    ImageView mParallax;
    @BindView(R.id.title_img)
    ImageView mTitleImg;
    @BindView(R.id.title_name)
    TextView mTitleName;
    @BindView(R.id.user_img)
    ImageView mUserImg;
    @BindView(R.id.user_name)
    TextView mUserName;
    @BindView(R.id.contact)
    TextView mContact;

    private int saveUid;
    private int mUid;
    private String mUName;
    private List<Mood> mList = new ArrayList<>();
    private MoodAdapter mAdapter;

    private int mPage = 0;
    private int mCount = 10;
    private final int MOD_REFRESH = 1;
    private final int MOD_LOADING = 2;
    private int RefreshMODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setupToolbar(mToolbar, "", true, 0, null);
        saveUid = SPUtils.getInstance(this).getInt(Constants.USER_ID);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new ItemAnimator());
        mAdapter = new MoodAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);

        initView();
        initEvent();
        listFeed(mPage, mCount);
    }

    private void initView() {
        Bundle bundle = this.getIntent().getExtras();
        Mood mood = (Mood) bundle.getSerializable("mood");
        if (mood == null) return;

        mUid = mood.getUid();
        mUName = mood.getUname();
        String mUrl = Api.baseUrl + mood.getUrl();
        mTitleName.setText(mUName);
        mUserName.setText(mUName);
        Glide.with(this)
                .load(mUrl)
                .placeholder(R.mipmap.userimg)
                .error(R.mipmap.userimg)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(mTitleImg);
        Glide.with(this)
                .load(mUrl)
                .placeholder(R.mipmap.userimg)
                .error(R.mipmap.userimg)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(mUserImg);
        if (!mood.isIm_ability())
            mContact.setVisibility(View.GONE);
    }

    //初始化事件
    private void initEvent() {
        // 下拉刷新
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshMODE = MOD_REFRESH;
                mPage = 0;
                listFeed(mPage, mCount);
            }
        });

        // item点击
        mAdapter.setOnItemListener(new MoodAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, Mood mood, int position) {
                switch (view.getId()) {
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

            @Override
            public void onPhotoClick(ArrayList<String> photos, int position) {
                PhotoPreview.builder()
                        .setPhotos(photos)
                        .setCurrentItem(position)
                        .setShowDeleteButton(false)
                        .start(UserActivity.this);
            }
        });

        // 头像切换
        mButtonBar.setAlpha(0);
        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            private int size = Utils.dp2px(56);

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int h = appBarLayout.getTotalScrollRange();
                int offset = Math.abs(verticalOffset);
                Log.i(TAG, "onOffsetChanged: verticalOffset = " + offset + ":" + h);

                int bbr = offset - 50 < 0 ? 0 : offset;
                mButtonBar.setAlpha(1f * bbr / h);
                int ui = offset * 2 > h ? h : offset;
                float f = 1f - (1f * ui / h);
                int after = (int) (size * f);
//                mUserImg.setAlpha(1f - (1f * ui / h));

                // 头像大小缩放
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mUserImg.getLayoutParams();
                params.width = after;
                params.height = after;
                mUserImg.setLayoutParams(params);

            }
        });

        // 加载更多
        mRecyclerView.addOnScrollListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                if (mAdapter.getItemCount() < 4) return;

                RefreshMODE = MOD_LOADING;
                mAdapter.updateLoadStatus(MoodAdapter.LOAD_MORE);

                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listFeed(mPage, mCount);
                    }
                },1000);
            }
        });
    }

    @OnClick({R.id.contact})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.contact:
                if (RongIM.getInstance() != null)
                    RongIM.getInstance().startPrivateChat(this, String.valueOf(mUid), mUName);
                break;
        }
    }

    private void postAddLike(Mood mood, int position) {
    }

    // 获取动态列表
    private void listFeed(int page, int count) {
        if (!mSwipeRefreshLayout.isRefreshing() && RefreshMODE == MOD_REFRESH)
            mSwipeRefreshLayout.setRefreshing(true);
        OkGo.<Result<MoodExtend>>get(Api.listFeed)
                .params("page", page)
                .params("count", count)
                .params("sUid", mUid)
                .params("uid", saveUid)
                .execute(new JsonCallback<Result<MoodExtend>>() {
                    @Override
                    public void onSuccess(Response<Result<MoodExtend>> response) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mPage++;
                        MoodExtend moodBean = response.body().getData();
                        switch (RefreshMODE) {
                            case MOD_LOADING:
                                if (moodBean.getTotalnum() == 0) {
                                    mAdapter.updateLoadStatus(MoodAdapter.LOAD_NONE);
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
                    public void onError(Response<Result<MoodExtend>> response) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mAdapter.updateLoadStatus(MoodAdapter.LOAD_NONE);
                        Utils.toastShow(UserActivity.this, R.string.toast_getmf_error);
                    }
                });
    }

    //更新数据
    public void updateData(List<Mood> data) {
        mAdapter.addData(data);
    }

    // 刷新数据
    private void onRefresh(){
        RefreshMODE = MOD_REFRESH;
        mPage = 0;
        listFeed(mPage, mCount);
    }

    // 前往动态详情
    private void gotoMood(Mood mood) {
        Intent intent = new Intent(this, MoodActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("mood", mood);
        intent.putExtras(bundle);
//        startActivityForResult(intent, Constants.ACTIVITY_MOOD);
        startActivity(intent);
    }
}
