package me.cl.lingxi.module.mood;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.EvaluateAdapter;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.util.SPUtils;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.common.view.LoadingDialog;
import me.cl.lingxi.emojicon.EmojiconEditText;
import me.cl.lingxi.emojicon.EmojiconTextView;
import me.cl.lingxi.entity.Evaluate;
import me.cl.lingxi.entity.EvaluateExtend;
import me.cl.lingxi.entity.Mood;
import me.cl.lingxi.entity.Reply;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.module.BaseActivity;
import me.cl.lingxi.module.member.UserActivity;

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
    @BindView(R.id.edit_mask)
    View mEditMask;
    @BindView(R.id.edit_tu_cao)
    EmojiconEditText mEditTuCao;
    @BindView(R.id.btn_publish)
    Button mBtnPublish;

    private int MSG_MODE;
    private final int MSG_EVALUATE = 0;
    private final int MSG_REPLY = 1;

    private int saveId;
    private String mMid;
    private String mEid;
    private int toUid;
    private String toName;
    private InputMethodManager imm;
    private EvaluateAdapter mAdapter;
    private LoadingDialog loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setupToolbar(mToolbar, R.string.title_activity_minifeed, true, 0, null);

        saveId = SPUtils.getInstance(this).getInt(Constants.USER_ID);

        //输入状态模式默认为评论
        MSG_MODE = MSG_EVALUATE;
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mEditTuCao.addTextChangedListener(new EditTextWatcher());
        // 开始禁用
        mBtnPublish.setClickable(false);
        mBtnPublish.setSelected(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new EvaluateAdapter(this, new ArrayList<Evaluate>());
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemListener(new EvaluateAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, Evaluate evaluate) {
                switch (view.getId()) {
                    case R.id.user_img:
                        gotoUser();
                        break;
                    case R.id.evaluate_body:
                        MSG_MODE = MSG_REPLY;
                        mEid = String.valueOf(evaluate.getCmid());
                        toUid = evaluate.getUid();
                        mEditTuCao.setHint("回复：" + evaluate.getUname());
                        openSofInput(mEditTuCao);
                        mEditMask.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onItemChildClick(View view, String eid, Reply reply) {
                MSG_MODE = MSG_REPLY;
                mEid = eid;
                toUid = reply.getUid();
                mEditTuCao.setHint("回复：" + reply.getUname());
                openSofInput(mEditTuCao);
                mEditMask.setVisibility(View.VISIBLE);
            }
        });

        initView();
    }

    private void gotoUser() {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    private void initView() {
        Bundle bundle = this.getIntent().getExtras();
        Mood mood = (Mood) bundle.getSerializable("mood");
        if (mood == null) return;

        mMid = String.valueOf(mood.getLcid());
        //动态详情
        Glide.with(this)
                .load(Api.baseUrl + mood.getUrl())
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
        mMfLikeIcon.setSelected(mood.islike());
        mMfLike.setClickable(mood.islike());
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

    @OnClick({R.id.user_img, R.id.mf_like, R.id.mf_comment, R.id.edit_mask, R.id.edit_tu_cao, R.id.btn_publish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_img:
                break;
            case R.id.mf_like:
                Utils.toastShow(this, "点赞");
                break;
            case R.id.mf_comment:
                mEditTuCao.setHint("吐槽一下");
                MSG_MODE = MSG_EVALUATE;
                openSofInput(mEditTuCao);
                mEditMask.setVisibility(View.VISIBLE);
                break;
            case R.id.edit_mask:
                mEditMask.setVisibility(View.GONE);
                hideSoftInput(mEditTuCao);
                break;
            case R.id.edit_tu_cao:
                mEditMask.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_publish:
                String msg = mEditTuCao.getText().toString().trim();
                switch (MSG_MODE) {
                    case MSG_EVALUATE:
                        //评论
                        loadingProgress = new LoadingDialog(MoodActivity.this, "评论中...");
                        addEvaluate(mMid, saveId, msg);
                        mEditTuCao.setText(null);
                        hideSoftInput(mEditTuCao);
                        break;
                    case MSG_REPLY:
                        //回复
                        loadingProgress = new LoadingDialog(MoodActivity.this, "回复中...");
                        addReply(mEid, saveId, toUid, msg);
                        mEditTuCao.setText(null);
                        hideSoftInput(mEditTuCao);
                        break;
                }
                break;
        }
    }

    /**
     * 添加评论
     */
    public void addEvaluate(String mid, int uid, String evaluate) {
        OkGo.<Result>post(Api.addComment)
                .params("lcid", mid)
                .params("uid", uid)
                .params("comment", evaluate)
                .execute(new me.cl.lingxi.common.widget.JsonCallback<Result>() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Utils.toastShow(MoodActivity.this, "评论成功");
                        mMfCommentNum.setText(String.valueOf(Integer.valueOf(mMfCommentNum.getText().toString()) + 1));

                        getEvaluateList(mMid);
                    }

                    @Override
                    public void onError(Response<Result> response) {
                        Utils.toastShow(MoodActivity.this, "评论失败");
                    }
                });
    }

    /**
     * 添加回复
     */
    public void addReply(String eid, int uid, int toUid, String reply) {
        OkGo.<Result>post(Api.addReply)
                .params("cmid", eid)
                .params("uid", uid)
                .params("touid", toUid)
                .params("reply", reply)
                .execute(new me.cl.lingxi.common.widget.JsonCallback<Result>() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        Utils.toastShow(MoodActivity.this, "回复成功");
                        getEvaluateList(mMid);
                    }

                    @Override
                    public void onError(Response<Result> response) {
                        Utils.toastShow(MoodActivity.this, "回复失败");
                    }
                });
    }

    /**
     * 获取评论数据
     */
    public void getEvaluateList(String mid) {
        OkGo.<Result<EvaluateExtend>>post(Api.commentList)
                .params("lcid", mid)
                .execute(new me.cl.lingxi.common.widget.JsonCallback<Result<EvaluateExtend>>() {
                    @Override
                    public void onSuccess(Response<Result<EvaluateExtend>> response) {
                        setData(response.body().getData().getCmtlist());
                    }

                    @Override
                    public void onError(Response<Result<EvaluateExtend>> response) {
                        Utils.toastShow(MoodActivity.this, R.string.toast_getmf_error);
                    }
                });
    }

    /**
     * 调用输入法
     */
    public void openSofInput(EditText edit) {
        edit.setText(null);
        edit.requestFocus();
//        edit.setFocusable(true);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏输入法
     */
    public void hideSoftInput(EditText edit) {
//        edit.setFocusable(false);
        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

    public void setData(List<Evaluate> data) {
        mAdapter.setDate(data);
    }

    /**
     * EditText 监听
     */
    private class EditTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean isEdit = mEditTuCao.getText().length() > 0;
            mBtnPublish.setClickable(isEdit);
            mBtnPublish.setSelected(isEdit);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
