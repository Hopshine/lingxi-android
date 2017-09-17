package me.cl.lingxi.module.mood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ImageView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.util.SPUtils;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.common.view.CustomProgressDialog;
import me.cl.lingxi.common.widget.JsonCallback;
import me.cl.lingxi.emojicon.EmojiconEditText;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.module.BaseActivity;
import me.cl.lingxi.module.main.MainActivity;

public class PublishActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.mood_info)
    EmojiconEditText mMoodInfo;
    @BindView(R.id.iv_submit)
    ImageView mIvSubmit;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private CustomProgressDialog shareProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setupToolbar(mToolbar, "发布新动态", true, 0, null);
        shareProgress = new CustomProgressDialog(this, "发布中...");
    }

    @OnClick(R.id.iv_submit)
    public void onClick() {
        String moodInfo = mMoodInfo.getText().toString().trim();
        if (TextUtils.isEmpty(moodInfo)){
            Utils.toastShow(this, "好歹写点什么吧！");
        }else {
            String uName = SPUtils.getInstance(this).getString("username", null);
            if(TextUtils.isEmpty(uName)){
                Utils.toastShow(this, "请重新登陆");
            }else{
                postSubmitMood(uName, moodInfo);
            }
        }
    }

    private void postSubmitMood(String uName, String moodInfo) {
        shareProgress.show();
        OkGo.<Result>post(Api.publishMood)
                .params("uname", uName)
                .params("lcinfo", moodInfo)
                .execute(new JsonCallback<Result>() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        int tag = response.body().getRet();
                        switch (tag) {
                            case 0:
                                mMoodInfo.setText(null);
                                Utils.toastShow(PublishActivity.this, "发布成功");
                                onBackPressed();
                                break;
                        }
                    }

                    @Override
                    public void onError(Response<Result> response) {
                        shareProgress.dismiss();
                        Utils.toastShow(PublishActivity.this, "发布失败");
                    }
                });
    }

    @Override
    public void onBackPressed() {
        // 此处监听回退，通知首页刷新
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.GO_INDEX, Constants.INDEX_MOOD);
        intent.putExtras(bundle);
        setResult(Constants.ACTIVITY_PUBLISH, intent);
        finish();
//        super.onBackPressed();
    }
}
