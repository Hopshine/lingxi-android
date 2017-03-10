package com.lingci.module.mood;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lingci.R;
import com.lingci.common.Api;
import com.lingci.common.util.SPUtils;
import com.lingci.common.util.Utils;
import com.lingci.common.util.ViewHolder;
import com.lingci.common.view.CustomProgressDialog;
import com.lingci.common.view.MyListView;
import com.lingci.common.view.MyScrollView;
import com.lingci.emojicon.EmojiconEditText;
import com.lingci.emojicon.EmojiconTextView;
import com.lingci.entity.Comments;
import com.lingci.entity.Comments.Data.Comment;
import com.lingci.entity.Comments.Data.Comment.Reply;
import com.lingci.entity.MiniFeeds.Data.MiniFeed;
import com.lingci.entity.MiniFeeds.Data.MiniFeed.Like;
import com.lingci.module.BaseActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class MinifeedActivity extends BaseActivity {

    private final int MSG_COMMRNT = 0;
    private final int MSG_REPLY = 1;
    private int MSG_MODE;
    private String savename;
    private String lcid;
    private String save_uname;
    private String rp_name;
    private int cmid_index;
    private TextView tv_top;
    private LinearLayout lc_ruturn;
    private MyListView mflistView;
    private MyScrollView scroll;
    private CommentAdapter cmtAdapter;
    private ImageView user_img;
    private TextView tv_uname;
    private TextView pl_time;
    private EmojiconTextView lc_info;
    private LinearLayout mf_comment;
    private LinearLayout mf_like;
    private TextView mf_see_num;
    private TextView mf_comment_num;
    private TextView mf_like_num;
    private ImageView mf_like_icon;
    private RelativeLayout like_window;
    private TextView like_people;
    private TextView like_feel;
    private List<Comment> commentList;
    private RelativeLayout mf_body;
    private EmojiconEditText cmt_edit;
    private InputMethodManager imm;
    private View mf_mask;
    private Button cmt_share;
    private CustomProgressDialog loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minifeed);
        initFindView();
        init();
        initEdit();
    }

    private void initFindView() {
        user_img = (ImageView) findViewById(R.id.user_img);
        tv_uname = (TextView) findViewById(R.id.tv_uname);
        pl_time = (TextView) findViewById(R.id.pl_time);
        lc_info = (EmojiconTextView) findViewById(R.id.lc_info);
        mf_comment = (LinearLayout) findViewById(R.id.mf_comment);
        mf_like = (LinearLayout) findViewById(R.id.mf_like);
        mf_see_num = (TextView) findViewById(R.id.mf_see_num);
        mf_comment_num = (TextView) findViewById(R.id.mf_comment_num);
        mf_like_num = (TextView) findViewById(R.id.mf_like_num);
        mf_like_icon = (ImageView) findViewById(R.id.mf_like_icon);
        like_window = (RelativeLayout) findViewById(R.id.like_window);
        like_people = (TextView) findViewById(R.id.like_people);
        like_feel = (TextView) findViewById(R.id.like_feel);

        Bundle bundle = this.getIntent().getExtras();
        MiniFeed mf = (MiniFeed) bundle.getSerializable("minifeed");
        setUserImg(mf.uname, mf.url, user_img);
        tv_uname.setText(mf.uname);
        pl_time.setText(mf.pl_time);
        lc_info.setText(mf.lc_info);
        mf_see_num.setText(String.valueOf(mf.viewnum));
        mf_comment_num.setText(String.valueOf(mf.cmtnum));
        mf_like_num.setText(String.valueOf(mf.likenum));
        if (mf.islike) {
            mf_like_icon.setImageResource(R.mipmap.list_item_icon_like);
            mf_like.setClickable(false);
            mf_like.setEnabled(false);
        } else {
            mf_like_icon.setImageResource(R.mipmap.list_item_icon_like_nor);
            mf_like.setClickable(true);
            mf_like.setEnabled(true);
        }
        lcid = mf.lcid + "";
        List<Like> likes = mf.likelist;
        String likeStr = getLikeStr(likes);
        String length = getIndent(likeStr.length());
        like_people.setText(likeStr);
        switch (mf.likenum) {
            case 0:
                mf_like_num.setText("赞");
                like_window.setVisibility(View.GONE);
                break;
            default:
                like_window.setVisibility(View.VISIBLE);
                like_feel.setText(length + "觉得很赞");
                break;
        }
    }

    private void init() {
        savename = SPUtils.getInstance(MinifeedActivity.this).getString("username", "");
        loadingProgress = new CustomProgressDialog(this, "加载评论中...", R.drawable.frame_loadin);
        commentList = new ArrayList<Comment>();
        tv_top = (TextView) this.findViewById(R.id.tv_top);
        lc_ruturn = (LinearLayout) this.findViewById(R.id.lc_ruturn);
        scroll = (MyScrollView) this.findViewById(R.id.cmt_scroll);
        mflistView = (MyListView) this.findViewById(R.id.cmt_lv);
        getCommentsnAsyncHttpPost(lcid);
        cmtAdapter = new CommentAdapter();
        mflistView.setAdapter(cmtAdapter);
//        setListViewHeightBasedOnChildren(mflistView);
        scroll.smoothScrollTo(0, 0);
        lc_ruturn.setVisibility(View.VISIBLE);
        tv_top.setText("普通的动态");
        lc_ruturn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initEdit() {
        /** 输入状态模式默认为评论 */
        MSG_MODE = MSG_COMMRNT;
        save_uname = SPUtils.getInstance(MinifeedActivity.this).getString("username", "");
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mf_mask = findViewById(R.id.mf_mask);
        mf_body = (RelativeLayout) this.findViewById(R.id.mf_body);
        cmt_edit = (EmojiconEditText) this.findViewById(R.id.cmt_edit);
        cmt_share = (Button) findViewById(R.id.cmt_share);
        cmt_edit.addTextChangedListener(new EditTextWatcher());

        /** 输入框点击调用遮罩 */
        cmt_edit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mf_mask.setVisibility(View.VISIBLE);
            }
        });

        /** 发表评论或回复 */
        cmt_share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String msg = cmt_edit.getText().toString();
                switch (MSG_MODE) {
                    case MSG_COMMRNT:
                        //评论
                        loadingProgress = new CustomProgressDialog(MinifeedActivity.this, "评论中...", R.drawable.frame_loadin);
                        postAddComment(lcid, save_uname, msg);
                        cmt_edit.setText(null);
                        hideSoftInput(cmt_edit);
                        break;
                    case MSG_REPLY:
                        //回复
                        loadingProgress = new CustomProgressDialog(MinifeedActivity.this, "回复中...", R.drawable.frame_loadin);
                        String cmid = commentList.get(cmid_index).cmid + "";
                        postAddReply(cmid, save_uname, rp_name, msg);
                        cmt_edit.setText(null);
                        hideSoftInput(cmt_edit);
                        break;
                    default:
                        break;
                }
                getCommentsnAsyncHttpPost(lcid);
            }
        });

        /** 点击遮罩隐藏输入法 */
        mf_mask.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mf_mask.setVisibility(View.GONE);
                hideSoftInput(cmt_edit);
            }
        });

        /** 动态本体点击调用输入法 */
        mf_comment.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cmt_edit.setHint("吐槽一下");
                MSG_MODE = MSG_COMMRNT;
                openSofInput(cmt_edit);
                mf_mask.setVisibility(View.VISIBLE);
            }
        });

        /** 评论ListView的item点击事件 */
        mflistView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                MSG_MODE = MSG_REPLY;
                cmid_index = position;
                TextView cm_uname = (TextView) view.findViewById(R.id.cm_uname);
                rp_name = cm_uname.getText().toString();
                cmt_edit.setHint("回复：" + cm_uname.getText());
                openSofInput(cmt_edit);
                mf_mask.setVisibility(View.VISIBLE);
//				Toast.makeText(MinifeedActivity.this, cm_uname.getText(),Toast.LENGTH_SHORT).show();
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
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏输入法
     */
    public void hideSoftInput(EditText edit) {
        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

    /**
     * 获取缩进
     */
    public String getIndent(int lenght) {
        String indent = "";
        for (int i = 0; i < lenght; i++) {
            indent = indent + "\u3000";
        }
        return indent;
    }

    /**
     * 获取点赞人字符串
     */
    public String getLikeStr(List<Like> likes) {
        String likeStr = "";
        for (int i = 0; i < likes.size(); i++) {
            if (i == likes.size() - 1) {
                likeStr = likeStr + likes.get(i).uname;
            } else {
                likeStr = likeStr + likes.get(i).uname + "、";
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
            boolean isEdit = cmt_edit.getText().length() > 0;
            if (isEdit) {
                cmt_share.setEnabled(true);
                cmt_share.setTextColor(getResources().getColor(R.color.white));
                cmt_share.setBackgroundResource(R.color.aqua_bule);
            } else {
                cmt_share.setEnabled(false);
                cmt_share.setTextColor(getResources().getColor(R.color.dark_gray));
                cmt_share.setBackgroundResource(R.color.white);
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
                        Toast.makeText(MinifeedActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MinifeedActivity.this, "回复成功", Toast.LENGTH_SHORT).show();
                        mf_comment_num.setText(String.valueOf(Integer.valueOf(mf_comment_num.getText().toString()) + 1));
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
                        Toast.makeText(MinifeedActivity.this, R.string.toast_getmf_error, Toast.LENGTH_SHORT).show();
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
                                hideSoftInput(cmt_edit);
                            }
                        });

                        /**
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
                                            hideSoftInput(cmt_edit);
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
            Comment comment = commentList.get(position);
            setUserImg(comment.uname, comment.url, mfuser_img);
            cm_uname.setText(comment.uname);
            cm_time.setText(comment.cm_time);
            cm_comment.setText(comment.comment);
            ArrayList<Reply> replys = (ArrayList<Reply>) comment.replylist;
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
                    TextView rp_uname = (TextView) view.findViewById(R.id.rp_uname);
                    String uname = rp_uname.getText().toString();
//					if(uname.equals(save_uname)){
                    /** 不支持自己搞自己 */
//					}else{
                    rp_name = uname;
                    cmt_edit.setHint("回复：" + rp_uname.getText());
                    openSofInput(cmt_edit);
                    mf_mask.setVisibility(View.VISIBLE);
                    cmid_index = (Integer) parent.getTag();
//						cmt_edit.setText("cmid的索引"+cmid_index);
//					}
                }
            });
            return convertView;
        }
    }

    /**
     * 评论回复的适配器
     */
    private class ReplyAdapter extends BaseAdapter {

        private ArrayList<Reply> replyList = new ArrayList<Reply>();

        @SuppressWarnings("unchecked")
        public ReplyAdapter(ArrayList<Reply> replyList) {
            this.replyList = (ArrayList<Reply>) replyList.clone();
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
            TextView rp_uname = ViewHolder.get(convertView, R.id.rp_uname);
            TextView rp_touname = ViewHolder.get(convertView, R.id.rp_touname);
            Reply reply = replyList.get(position);
            rp_uname.setText(reply.uname);
            rp_touname.setText(reply.touname);
            int length = reply.uname.length() + reply.touname.length() + 3;
            String indent = getIndent(length);
            rp_reply.setText(indent + reply.reply);
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
}
