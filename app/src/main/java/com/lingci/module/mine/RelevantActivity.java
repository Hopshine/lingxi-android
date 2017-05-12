package com.lingci.module.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lingci.R;
import com.lingci.adapter.RelevantAdapter;
import com.lingci.common.config.Api;
import com.lingci.common.config.JsonCallback;
import com.lingci.common.util.SPUtils;
import com.lingci.common.util.Utils;
import com.lingci.common.view.CustomProgressDialog;
import com.lingci.common.view.MoeToast;
import com.lingci.entity.Like;
import com.lingci.entity.Mood;
import com.lingci.entity.Relevant;
import com.lingci.entity.RelevantBean;
import com.lingci.entity.Result;
import com.lingci.module.BaseActivity;
import com.lingci.module.mood.MoodActivity;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class RelevantActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private RelevantAdapter mAdapter;
    private CustomProgressDialog loadingProgress;
    private String saveName;
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
        saveName = SPUtils.getInstance(RelevantActivity.this).getString("username", "");
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
        getRelevantList(saveName);
    }

    //请求与我相关
    public void getRelevantList(String name) {
        OkHttpUtils.post()
                .url(Api.Url + "/unReadList")
                .addParams("uname", name)
                .build()
                .execute(new JsonCallback<Result<RelevantBean<Relevant<Mood<Like>>>>>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        loadingProgress.dismiss();
                        Utils.toastShow(RelevantActivity.this, "加载失败，下拉重新加载");
                    }

                    @Override
                    public void onResponse(Result<RelevantBean<Relevant<Mood<Like>>>> response, int id) {
                        loadingProgress.dismiss();
                        updateData(response.getData().getUnreadlist());
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
