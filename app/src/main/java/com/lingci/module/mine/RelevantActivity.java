package com.lingci.module.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.lingci.R;
import com.lingci.adapter.RelevantAdapter;
import com.lingci.common.Api;
import com.lingci.common.util.GsonUtil;
import com.lingci.common.util.MoeToast;
import com.lingci.common.util.SPUtils;
import com.lingci.common.util.Utils;
import com.lingci.common.view.CustomProgressDialog;
import com.lingci.entity.Like;
import com.lingci.entity.Mood;
import com.lingci.entity.Relevant;
import com.lingci.entity.RelevantBean;
import com.lingci.entity.Result;
import com.lingci.module.BaseActivity;
import com.lingci.module.mood.MoodActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
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
                        break;
                }
            }
        });

        loadingProgress.show();
        getRelevantList(saveName);
    }

    public void getRelevantList(String name) {
        OkHttpUtils.post()
                .url(Api.Url + "/unReadList")
                .addParams("uname", name)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        loadingProgress.dismiss();
                        Utils.toastShow(RelevantActivity.this, "加载失败，下拉重新加载");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse: " + response);
                        loadingProgress.dismiss();
                        Type type = new TypeToken<Result<RelevantBean<Relevant<Mood<Like>>>>>() {
                        }.getType();
                        GsonUtil.fromJson(response, type, new GsonUtil.GsonResult<RelevantBean<Relevant<Mood<Like>>>>() {
                            @Override
                            public void onTrue(Result<RelevantBean<Relevant<Mood<Like>>>> result) {
                                updateData(result.getData().getUnreadlist());
                            }

                            @Override
                            public void onErr(Result<Object> result, Exception e) {

                            }
                        });
                    }
                });
    }

    private void updateData(List<Relevant<Mood<Like>>> relevantList){
        mAdapter.updateData(relevantList);
    }

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
