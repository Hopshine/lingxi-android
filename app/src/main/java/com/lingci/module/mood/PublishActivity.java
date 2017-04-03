package com.lingci.module.mood;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ImageView;

import com.lingci.R;
import com.lingci.common.config.Api;
import com.lingci.common.util.SPUtils;
import com.lingci.common.util.Utils;
import com.lingci.common.view.CustomProgressDialog;
import com.lingci.emojicon.EmojiconEditText;
import com.lingci.module.BaseActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

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
        OkHttpUtils.post()
                .url(Api.Url + "/minifeedAdd")
                .addParams("uname", uName)
                .addParams("lcinfo", moodInfo)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        shareProgress.dismiss();
                        Utils.toastShow(PublishActivity.this, "发布失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        shareProgress.dismiss();
                        try {
                            JSONObject json = new JSONObject(response);
                            int tag = json.getInt("ret");
                            switch (tag) {
                                case 0:
                                    mMoodInfo.setText(null);
                                    Utils.toastShow(PublishActivity.this, "发布成功");
                                    onBackPressed();
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


}
