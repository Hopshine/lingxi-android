package me.cl.lingxi.module.member;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.PublicLicenseAdapter;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.entity.PublicLicense;
import me.cl.lingxi.module.BaseActivity;

public class UserActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.scrollView)
    NestedScrollView mScrollView;
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

    private List<PublicLicense> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setupToolbar(mToolbar, "", true, 0, null);

        getData();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        final PublicLicenseAdapter mAdapter = new PublicLicenseAdapter(this, mData);
        mRecyclerView.setAdapter(mAdapter);

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

        mButtonBar.setAlpha(0);

        mScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.i(TAG, "onScrollChange: scrollY = " + scrollY);
            }
        });
    }

    private void getData() {
        mData.add(new PublicLicense("灵犀", "2017-09-16 23:23:23", "第一", ""));
        mData.add(new PublicLicense("灵犀", "2017-09-16 23:23:23", "第二", ""));
        mData.add(new PublicLicense("灵犀", "2017-09-16 23:23:23", "第三", ""));
        mData.add(new PublicLicense("灵犀", "2017-09-16 23:23:23", "第四", ""));
        mData.add(new PublicLicense("灵犀", "2017-09-16 23:23:23", "第五", ""));
        mData.add(new PublicLicense("灵犀", "2017-09-16 23:23:23", "第六", ""));
        mData.add(new PublicLicense("灵犀", "2017-09-16 23:23:23", "第七", ""));
        mData.add(new PublicLicense("灵犀", "2017-09-16 23:23:23", "第八", ""));
        mData.add(new PublicLicense("灵犀", "2017-09-16 23:23:23", "第九", ""));
        mData.add(new PublicLicense("灵犀", "2017-09-16 23:23:23", "第十", ""));
    }
}
