package com.lingci.module.mood;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.reflect.TypeToken;
import com.lingci.R;
import com.lingci.adapter.EvaluateAdapter;
import com.lingci.common.config.Api;
import com.lingci.common.util.GsonUtil;
import com.lingci.common.util.Utils;
import com.lingci.emojicon.EmojiconTextView;
import com.lingci.entity.Evaluate;
import com.lingci.entity.EvaluateBean;
import com.lingci.entity.Like;
import com.lingci.entity.Mood;
import com.lingci.entity.Reply;
import com.lingci.entity.Result;
import com.lingci.module.BaseActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;

public class MoodActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.user_img)
    ImageView mUserImg;
    @BindView(R.id.user_name)
    TextView mUserName;
    @BindView(R.id.mood_time)
    TextView mMoodTime;
    @BindView(R.id.lc_chat)
    ImageView mLcChat;
    @BindView(R.id.mood_info)
    EmojiconTextView mMoodInfo;
    @BindView(R.id.mood_body)
    LinearLayout mMoodBody;
    @BindView(R.id.mf_see_num)
    TextView mMfSeeNum;
    @BindView(R.id.mf_comment_num)
    TextView mMfCommentNum;
    @BindView(R.id.mf_comment)
    LinearLayout mMfComment;
    @BindView(R.id.mf_like_icon)
    ImageView mMfLikeIcon;
    @BindView(R.id.mf_like_num)
    TextView mMfLikeNum;
    @BindView(R.id.mf_like)
    LinearLayout mMfLike;
    @BindView(R.id.mood_action)
    LinearLayout mMoodAction;
    @BindView(R.id.like_people)
    TextView mLikePeople;
    @BindView(R.id.like_window)
    LinearLayout mLikeWindow;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private int MSG_MODE;
    private final int MSG_EVALUATE = 0;
    private final int MSG_REPLY = 1;

    private EvaluateAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setupToolbar(mToolbar, R.string.title_activity_minifeed, true, 0, null);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new EvaluateAdapter(this, new ArrayList());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemListener(new EvaluateAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, Evaluate<Reply> evaluate) {
                switch (view.getId()) {
                    case R.id.user_img:
                        Utils.toastShow(view.getContext(), "头像");
                        break;
                    case R.id.evaluate_body:
                        Utils.toastShow(view.getContext(), "评论");
                        break;
                }
            }

            @Override
            public void onItemChildClick(View view, Reply reply) {
                Utils.toastShow(view.getContext(), reply.getUname());
            }
        });

        initView();
    }

    private void initView() {
        Bundle bundle = this.getIntent().getExtras();
        Mood<Like> mood = (Mood<Like>) bundle.getSerializable("mood");
        if (mood == null) return;
        //动态详情
        Glide.with(this)
                .load(Api.Url + mood.getUrl())
                .skipMemoryCache(true)
                .placeholder(R.mipmap.userimg)
                .error(R.mipmap.userimg)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(mUserImg);
        mUserName.setText(mood.getUname());
        mMoodTime.setText(mood.getPl_time());
        mMoodInfo.setText(mood.getLc_info());
        //查看评论点赞数
        mMfSeeNum.setText(String.valueOf(mood.getViewnum()));
        mMfCommentNum.setText(String.valueOf(mood.getCmtnum()));
        mMfLikeNum.setText(String.valueOf(mood.getLikenum()));
        //是否已经点赞
        if (mood.islike()) {
            mMfLikeIcon.setSelected(true);
            mMfLike.setClickable(false);
        } else {
            mMfLikeIcon.setSelected(false);
            mMfLike.setClickable(true);
        }
        //点赞列表
        String likeStr = Utils.getLongLikeStr(mood.getLikelist());
        switch (mood.getLikenum()) {
            case 0:
                mMfLikeNum.setText("赞");
                mLikeWindow.setVisibility(View.GONE);
                break;
            default:
                mLikeWindow.setVisibility(View.VISIBLE);
                likeStr = likeStr + "觉得很赞";
                break;
        }
        mLikePeople.setText(Utils.getCharSequence(likeStr));

        getEvaluateList(String.valueOf(mood.getLcid()));
    }

    @OnClick({R.id.user_img, R.id.mf_like})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_img:
                break;
            case R.id.mf_like:
                Utils.toastShow(this, "点赞");
                break;
        }
    }

    /**
     * 获取评论数据
     */
    public void getEvaluateList(String lcid) {
        Utils.setTime();
        OkHttpUtils.post()
                .url(Api.Url + "/commentList")
                .addParams("lcid", lcid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Utils.toastShow(MoodActivity.this, R.string.toast_getmf_error);
                    }

                    @Override
                    public void onResponse(final String response, int id) {
                        Log.d(TAG, "onResponse: " + response);
                        Type type = new TypeToken<Result<EvaluateBean<Evaluate<Reply>>>>() {
                        }.getType();
                        GsonUtil.fromJson(response, type, new GsonUtil.GsonResult<EvaluateBean<Evaluate<Reply>>>() {
                            @Override
                            public void onTrue(Result<EvaluateBean<Evaluate<Reply>>> result) {
                                setData(result.getData().getCmtlist());
                            }

                            @Override
                            public void onErr(Result<Object> result, Exception e) {
                                Log.d(TAG, "onErr: " + result.getMsg());
                            }
                        });
                    }
                });
    }

    public void setData(List<Evaluate<Reply>> data) {
        mAdapter.updateData(data);
    }
}
