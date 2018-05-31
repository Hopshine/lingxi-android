package me.cl.lingxi.module.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.library.base.BaseActivity;
import me.cl.library.view.LoadingDialog;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.RelevantAdapter;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.view.MoeToast;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.entity.PageInfo;
import me.cl.lingxi.entity.Relevant;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.module.feed.FeedActivity;
import okhttp3.Call;

public class RelevantActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private RelevantAdapter mAdapter;
    private LoadingDialog loadingProgress;
    private String saveId;
    private List<Relevant> mRelevantList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relevant);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setupToolbar(mToolbar, "与我相关", true, 0, null);
        saveId = SPUtil.build().getString(Constants.USER_ID);
        int x = (int) (Math.random() * 4) + 1;
        if (x == 1) {
            MoeToast.makeText(this, "你能找到这个秘密吗？");
        }
        loadingProgress = new LoadingDialog(this, "正在加载...");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RelevantAdapter(this, mRelevantList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemListener(new RelevantAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, Relevant relevant) {
                switch (view.getId()) {
                    case R.id.user_img:
                        break;
                    case R.id.feed_body:
                        gotoFeed(relevant.getFeed());
                        break;
                }
            }
        });

        loadingProgress.show();
        getRelevantList();
        updateUnread();
    }

    /**
     * 更新未读条数
     */
    public void updateUnread() {
        String userId = SPUtil.build().getString(Constants.USER_ID);
        OkUtil.post()
                .url(Api.updateUnread)
                .addParam("userId", userId)
                .execute(new ResultCallback<Result<Integer>>() {
                    @Override
                    public void onSuccess(Result<Integer> response) {
                        Constants.isRead = true;
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onFinish() {

                    }
                });
    }

    // 请求与我相关
    public void getRelevantList() {
        Integer pageNum = 1;
        Integer pageSize = 20;
        OkUtil.post()
                .url(Api.relevant)
                .addParam("userId", saveId)
                .addParam("pageNum", pageNum)
                .addParam("pageSize", pageSize)
                .execute(new ResultCallback<Result<PageInfo<Relevant>>>() {
                    @Override
                    public void onSuccess(Result<PageInfo<Relevant>> response) {
                        loadingProgress.dismiss();
                        String code = response.getCode();
                        if (!"00000".equals(code)) {
                            Utils.toastShow(RelevantActivity.this, "加载失败，下拉重新加载");
                            return;
                        }
                        updateData(response.getData().getList());
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        loadingProgress.dismiss();
                        Utils.toastShow(RelevantActivity.this, "加载失败，下拉重新加载");
                    }

                    @Override
                    public void onFinish() {
                        loadingProgress.dismiss();
                        Utils.toastShow(RelevantActivity.this, "加载失败，下拉重新加载");
                    }
                });
    }

    private void updateData(List<Relevant> relevantList) {
        mAdapter.updateData(relevantList);
    }

    //前往详情页
    private void gotoFeed(Feed feed) {
        Intent intent = new Intent(RelevantActivity.this, FeedActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("feed", feed);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
