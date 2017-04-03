package com.lingci.module.mood;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lingci.R;
import com.lingci.common.config.Api;
import com.lingci.common.util.ColorPhrase;
import com.lingci.common.util.SPUtils;
import com.lingci.common.util.Utils;
import com.lingci.common.util.ViewHolder;
import com.lingci.common.view.CustomProgressDialog;
import com.lingci.common.view.JellyScrollView;
import com.lingci.common.view.MyListView;
import com.lingci.emojicon.EmojiconEditText;
import com.lingci.emojicon.EmojiconTextView;
import com.lingci.module.BaseActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

import static com.lingci.R.id.lc_info;
import static com.lingci.R.id.like_window;
import static com.lingci.R.id.mf_comment;
import static com.lingci.R.id.mf_comment_num;
import static com.lingci.R.id.mf_like;
import static com.lingci.R.id.mf_like_icon;
import static com.lingci.R.id.mf_like_num;
import static com.lingci.R.id.mf_mask;
import static com.lingci.R.id.pl_time;
import static com.lingci.R.id.tv_uname;

public class MinifeedActivity extends BaseActivity {

    private int MSG_MODE;
    private final int MSG_EVALUATE = 0;
    private final int MSG_REPLY = 1;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.user_img)
    ImageView mUserImg;
    @BindView(tv_uname)
    TextView mTvUname;
    @BindView(pl_time)
    TextView mPlTime;
    @BindView(R.id.lc_chat)
    ImageView mLcChat;
    @BindView(lc_info)
    EmojiconTextView mLcInfo;
    @BindView(R.id.minifeed)
    LinearLayout mMinifeed;
    @BindView(R.id.mf_see_num)
    TextView mMfSeeNum;
    @BindView(mf_comment_num)
    TextView mMfCommentNum;
    @BindView(mf_comment)
    LinearLayout mMfComment;
    @BindView(mf_like_icon)
    ImageView mMfLikeIcon;
    @BindView(mf_like_num)
    TextView mMfLikeNum;
    @BindView(mf_like)
    LinearLayout mMfLike;
    @BindView(R.id.btn_list)
    LinearLayout mBtnList;
    @BindView(R.id.like_people)
    TextView mLikePeople;
    @BindView(like_window)
    RelativeLayout mLikeWindow;
    @BindView(R.id.mf_body)
    RelativeLayout mMfBody;
    @BindView(R.id.cmt_lv)
    MyListView mCmtLv;
    @BindView(R.id.cmt_scroll)
    JellyScrollView mCmtScroll;
    @BindView(R.id.cmt_edit)
    EmojiconEditText mCmtEdit;
    @BindView(R.id.cmt_share)
    Button mCmtShare;
    @BindView(mf_mask)
    View mMfMask;


    private String savename;
    private String lcid;
    private String save_uname;
    private String rp_name;
    private int cmid_index;
    private CommentAdapter cmtAdapter;
    private List<Comments.Data.Comment> commentList;
    private InputMethodManager imm;
    private CustomProgressDialog loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minifeed);
        ButterKnife.bind(this);
        initFindView();
        init();
        initEdit();
    }

    private void initFindView() {
        Bundle bundle = this.getIntent().getExtras();
        MiniFeeds.Data.MiniFeed mf = (MiniFeeds.Data.MiniFeed) bundle.getSerializable("minifeed");
        setUserImg(mf.uname, mf.url, mUserImg);
        mTvUname.setText(mf.uname);
        mPlTime.setText(mf.pl_time);
        mLcInfo.setText(mf.lc_info);
        mMfSeeNum.setText(String.valueOf(mf.viewnum));
        mMfCommentNum.setText(String.valueOf(mf.cmtnum));
        mMfLikeNum.setText(String.valueOf(mf.likenum));
        if (mf.islike) {
            mMfLikeIcon.setImageResource(R.mipmap.icon_like);
            mMfLike.setClickable(false);
            mMfLike.setEnabled(false);
        } else {
            mMfLikeIcon.setImageResource(R.mipmap.icon_like_nor);
            mMfLike.setClickable(true);
            mMfLike.setEnabled(true);
        }
        lcid = mf.lcid + "";
        List<MiniFeeds.Data.MiniFeed.Like> likes = mf.likelist;
        String likeStr = getLikeStr(likes);
        switch (mf.likenum) {
            case 0:
                mMfLikeNum.setText("赞");
                mLikeWindow.setVisibility(View.GONE);
                break;
            default:
                mLikeWindow.setVisibility(View.VISIBLE);
                likeStr = likeStr + "觉得很赞";
                break;
        }
        //突出颜色
        CharSequence chars = ColorPhrase.from(likeStr).withSeparator("{}").innerColor(0xFF4FC1E9).outerColor(0xFF666666).format();
        mLikePeople.setText(chars);
    }

    private void init() {
        savename = SPUtils.getInstance(MinifeedActivity.this).getString("username");
        loadingProgress = new CustomProgressDialog(this, "加载评论中...");
        setupToolbar(mToolbar, "普通的动态", true, 0, null);
        commentList = new ArrayList<>();
        getCommentsnAsyncHttpPost(lcid);
        cmtAdapter = new CommentAdapter();
        mCmtLv.setAdapter(cmtAdapter);
//        setListViewHeightBasedOnChildren(mflistView);
        mCmtScroll.smoothScrollTo(0, 0);


    }

    private void initEdit() {
        /* 输入状态模式默认为评论 */
        MSG_MODE = MSG_EVALUATE;
        save_uname = SPUtils.getInstance(MinifeedActivity.this).getString("username");
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mCmtEdit.addTextChangedListener(new EditTextWatcher());

        /* 输入框点击调用遮罩 */
        mCmtEdit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mMfMask.setVisibility(View.VISIBLE);
            }
        });

        /* 发表评论或回复 */
        mCmtShare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String msg = mCmtEdit.getText().toString();
                switch (MSG_MODE) {
                    case MSG_EVALUATE:
                        //评论
                        loadingProgress = new CustomProgressDialog(MinifeedActivity.this, "评论中...");
                        postAddComment(lcid, save_uname, msg);
                        mCmtEdit.setText(null);
                        hideSoftInput(mCmtEdit);
                        break;
                    case MSG_REPLY:
                        //回复
                        loadingProgress = new CustomProgressDialog(MinifeedActivity.this, "回复中...");
                        String cmid = commentList.get(cmid_index).cmid + "";
                        postAddReply(cmid, save_uname, rp_name, msg);
                        mCmtEdit.setText(null);
                        hideSoftInput(mCmtEdit);
                        break;
                    default:
                        break;
                }
                getCommentsnAsyncHttpPost(lcid);
            }
        });

        /* 点击遮罩隐藏输入法 */
        mMfMask.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mMfMask.setVisibility(View.GONE);
                hideSoftInput(mCmtEdit);
            }
        });

        /* 动态本体点击调用输入法 */
        mMfComment.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mCmtEdit.setHint("吐槽一下");
                MSG_MODE = MSG_EVALUATE;
                openSofInput(mCmtEdit);
                mMfMask.setVisibility(View.VISIBLE);
            }
        });

        /* 评论ListView的item点击事件 */
        mCmtLv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                MSG_MODE = MSG_REPLY;
                cmid_index = position;
                TextView cm_uname = (TextView) view.findViewById(R.id.cm_uname);
                rp_name = cm_uname.getText().toString();
                mCmtEdit.setHint("回复：" + cm_uname.getText());
                openSofInput(mCmtEdit);
                mMfMask.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setUserImg(String name, String url, ImageView imgView) {
        if (name.equals(savename)) {
            Utils.setPersonImg(savename, imgView);
        } else {
            if (url != null) {
                Glide.with(MinifeedActivity.this)
                        .load(Api.Url + url)
                        .skipMemoryCache(true)
                        .into(imgView);
            } else {
                imgView.setImageResource(R.mipmap.userimg);
            }
        }
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

    /**
     * 获取点赞人字符串
     */
    public String getLikeStr(List<MiniFeeds.Data.MiniFeed.Like> likes) {
        String likeStr = "";
        for (int i = 0; i < likes.size(); i++) {
            if (i == likes.size() - 1) {
                likeStr = likeStr + "{" + likes.get(i).uname + "}";
            } else {
                likeStr = likeStr + "{" + likes.get(i).uname + "、}";
            }
        }
        return likeStr;
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
            boolean isEdit = mCmtEdit.getText().length() > 0;
            if (isEdit) {
                mCmtShare.setEnabled(true);
                mCmtShare.setTextColor(getResources().getColor(R.color.white));
                mCmtShare.setBackgroundResource(R.color.aqua_bule);
            } else {
                mCmtShare.setEnabled(false);
                mCmtShare.setTextColor(getResources().getColor(R.color.dark_gray));
                mCmtShare.setBackgroundResource(R.color.white);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    /**
     * 解析json字符串获取Comments
     */
    public Comments getComments(String json) {
        return new Gson().fromJson(json, Comments.class);
    }

    /**
     * 添加评论
     */
    public void postAddComment(String lcid, String uname, String comment) {
        loadingProgress.show();
        OkHttpUtils.post()
                .url(Api.Url + "/addComment")
                .addParams("lcid", lcid)
                .addParams("uname", uname)
                .addParams("comment", comment)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        loadingProgress.dismiss();
                        Log.d(TAG, "onError: " + id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        loadingProgress.dismiss();
                        Log.d(TAG, "onResponse: " + response);
                        Utils.toastShow(MinifeedActivity.this, "评论成功");
                    }
                });
    }

    /**
     * 添加回复
     */
    public void postAddReply(String cmid, String uname, String touname, String reply) {
        loadingProgress.show();
        OkHttpUtils.post()
                .url(Api.Url + "/addReply")
                .addParams("cmid", cmid)
                .addParams("uname", uname)
                .addParams("touname", touname)
                .addParams("reply", reply)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        loadingProgress.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        loadingProgress.dismiss();
                        Utils.toastShow(MinifeedActivity.this, "回复成功");
                        mMfCommentNum.setText(String.valueOf(Integer.valueOf(mMfCommentNum.getText().toString()) + 1));
                    }
                });
    }

    /**
     * 获取评论数据
     */
    public void getCommentsnAsyncHttpPost(String lcid) {
        loadingProgress.show();
        Utils.setTime();
        OkHttpUtils.post()
                .url(Api.Url + "/commentList")
                .addParams("lcid", lcid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        loadingProgress.dismiss();
                        Utils.toastShow(MinifeedActivity.this, R.string.toast_getmf_error);
                    }

                    @Override
                    public void onResponse(final String response, int id) {
                        Utils.loadingTime(new Handler(), new Utils.OnLoading() {
                            @Override
                            public void onLoading() {
                                loadingProgress.dismiss();
                                Log.d(TAG, "onResponse: " + response);
                                Comments comment = getComments(response);
                                int tag = comment.ret;
                                switch (tag) {
                                    case 0:
                                        commentList = comment.data.cmtlist;
                                        cmtAdapter.notifyDataSetChanged();
                                        break;
                                    default:
                                        break;
                                }
                                hideSoftInput(mCmtEdit);
                            }
                        });

                        /*
                        final long time = Utils.getTime();
                        Log.d(TAG, "time: " + time);
                        if (time < 1500)
                            handler = new Handler();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep((1500 - time));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 在这里进行UI操作
                                        handler = null;
                                        loadingProgress.dismiss();
                                        Log.d(TAG, "onResponse: " + response);
                                        Comments comment = getComments(response);
                                        int tag = comment.ret;
                                        switch (tag) {
                                            case 0:
                                                commentList = comment.data.cmtlist;
                                                cmtAdapter.notifyDataSetChanged();
                                                break;
                                            default:
                                                break;
                                        }
                                        hideSoftInput(mCmtEdit);
                                    }
                                });
                            }
                        }).start();
                        */
                    }
                });
    }

    /**
     * 动态评论的适配器
     */
    private class CommentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return commentList == null ? 0 : commentList.size();
        }

        @Override
        public Object getItem(int position) {
            return commentList == null ? null : commentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(MinifeedActivity.this);
                convertView = inflater.inflate(R.layout.mf_comment_item, null);
            }
            ImageView mfuser_img = ViewHolder.get(convertView, R.id.cmuser_img);
            TextView cm_uname = ViewHolder.get(convertView, R.id.cm_uname);
            TextView cm_time = ViewHolder.get(convertView, R.id.cm_time);
            EmojiconTextView cm_comment = ViewHolder.get(convertView, R.id.comment);
            Comments.Data.Comment comment = commentList.get(position);
            setUserImg(comment.uname, comment.url, mfuser_img);
            cm_uname.setText(comment.uname);
            cm_time.setText(comment.cm_time);
            cm_comment.setText(comment.comment);
            ArrayList<Comments.Data.Comment.Reply> replys = (ArrayList<Comments.Data.Comment.Reply>) comment.replylist;
            MyListView replyList = ViewHolder.get(convertView, R.id.cm_reply_list);
            ReplyAdapter replyAdaptre = new ReplyAdapter(replys);
            replyList.setAdapter(replyAdaptre);
            replyList.setTag(position);
            /** 回复ListView的item点击事件 */
            replyList.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    MSG_MODE = MSG_REPLY;
                    TextView rp_reply = (TextView) view.findViewById(R.id.reply);
                    String replyStr = rp_reply.getText().toString();
                    String uName = replyStr.substring(0, replyStr.indexOf("回复"));
                    rp_name = uName;
                    mCmtEdit.setHint("回复：" + uName);
                    openSofInput(mCmtEdit);
                    mMfMask.setVisibility(View.VISIBLE);
                    cmid_index = (Integer) parent.getTag();
                }
            });
            return convertView;
        }
    }

    /**
     * 评论回复的适配器
     */
    private class ReplyAdapter extends BaseAdapter {

        private ArrayList<Comments.Data.Comment.Reply> replyList = new ArrayList<>();

        @SuppressWarnings("unchecked")
        public ReplyAdapter(ArrayList<Comments.Data.Comment.Reply> replyList) {
            this.replyList = (ArrayList<Comments.Data.Comment.Reply>) replyList.clone();
        }



        @Override
        public int getCount() {
            return replyList == null ? 0 : replyList.size();
        }

        @Override
        public Object getItem(int position) {
            return replyList == null ? null : replyList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(MinifeedActivity.this);
                convertView = inflater.inflate(R.layout.cmt_reply_itme, null);
            }
            EmojiconTextView rp_reply = ViewHolder.get(convertView, R.id.reply);
            Comments.Data.Comment.Reply reply = replyList.get(position);
            String replyStr = "{" + reply.uname + "}回复{" + reply.touname + "}：" + reply.reply;
            CharSequence chars = ColorPhrase.from(replyStr).withSeparator("{}").innerColor(0xFF4FC1E9).outerColor(0xFF666666).format();
            rp_reply.setText(chars);
            return convertView;
        }

    }

    /**
     * 设置listView的高度
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public static class MiniFeeds  implements Serializable{

        public int ret;
        public Data data;

        public static final class Data  implements Serializable{

            public List<MiniFeed> minifeedlist;
            public int totalnum;

            public static final class MiniFeed implements Serializable{

                public int lcid;
                public int uid;
                public String uname;
                public String url;
                public boolean im_ability;
                public String lc_info;
                public String pl_time;
                public int viewnum;
                public int likenum;
                public int cmtnum;
                public boolean islike;
                public List<Like> likelist;


                public void setLikenum(int likenum) {
                    this.likenum = likenum;
                }

                public void setIslike(boolean islike) {
                    this.islike = islike;
                }

                public void setLikelist(List<Like> likelist) {
                    this.likelist = likelist;
                }

                public static class Like implements Serializable {

                    public int uid;
                    public String uname;

                    public Like(int uid, String uname) {
                        super();
                        this.uid = uid;
                        this.uname = uname;
                    }

                }
            }
        }
    }


    public static class Comments {

        public int ret;
        public Data data;

        public static final class Data {
            public int totalnum;
            public List<Comment> cmtlist;

            public static final class Comment {
                public int cmid;
                public int uid;
                public String uname;
                public String url;
                public String comment;
                public String cm_time;
                public List<Reply> replylist;

                public static final class Reply {
                    public int rpid;
                    public int cmid;
                    public int uid;
                    public String uname;
                    public int touid;
                    public String touname;
                    public String reply;
                    public String rp_time;
                }
            }
        }
    }
}
