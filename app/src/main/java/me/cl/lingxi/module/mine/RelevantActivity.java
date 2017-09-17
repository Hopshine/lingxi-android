package me.cl.lingxi.module.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.RelevantAdapter;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.util.SPUtils;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.common.view.CustomProgressDialog;
import me.cl.lingxi.common.view.MoeToast;
import me.cl.lingxi.entity.Like;
import me.cl.lingxi.entity.Mood;
import me.cl.lingxi.entity.Relevant;
import me.cl.lingxi.entity.RelevantBean;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.module.BaseActivity;
import me.cl.lingxi.module.mood.MoodActivity;

public class RelevantActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private RelevantAdapter mAdapter;
    private CustomProgressDialog loadingProgress;
    private int saveId;
    private List<Relevant<Mood<Like>>> mRelevantList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relevant);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setupToolbar(mToolbar, "与我相关", true, 0, null);
        saveId = SPUtils.getInstance(RelevantActivity.this).getInt("uid", -1);
        int x = (int) (Math.random() * 4) + 1;
        if (x == 1) {
            MoeToast.makeText(this, "你能找到这个秘密吗？");
        }
        loadingProgress = new CustomProgressDialog(this, "正在加载...");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RelevantAdapter(this, mRelevantList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemListener(new RelevantAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, Relevant<Mood<Like>> relevant) {
                switch (view.getId()) {
                    case R.id.user_img:
                        break;
                    case R.id.mood_body:
                        gotoMood(relevant.getMinifeed());
                        break;
                }
            }
        });

        loadingProgress.show();
        getRelevantList();
    }

    //请求与我相关
    public void getRelevantList() {
        OkGo.<Result<RelevantBean<Relevant<Mood<Like>>>>>post(Api.unReadList)
                .params("uid", saveId)
                .execute(new me.cl.lingxi.common.widget.JsonCallback<Result<RelevantBean<Relevant<Mood<Like>>>>>() {
                    @Override
                    public void onSuccess(Response<Result<RelevantBean<Relevant<Mood<Like>>>>> response) {
                        loadingProgress.dismiss();
                        updateData(response.body().getData().getUnreadlist());
                    }

                    @Override
                    public void onError(Response<Result<RelevantBean<Relevant<Mood<Like>>>>> response) {
                        loadingProgress.dismiss();
                        Utils.toastShow(RelevantActivity.this, "加载失败，下拉重新加载");
                    }
                });
    }

    private void updateData(List<Relevant<Mood<Like>>> relevantList){
        mAdapter.updateData(relevantList);
    }

    //前往详情页
    private void gotoMood(Mood<Like> mood){
        Intent intent = new Intent(RelevantActivity.this, MoodActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("mood", mood);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
