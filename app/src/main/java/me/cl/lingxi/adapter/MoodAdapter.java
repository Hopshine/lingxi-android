package me.cl.lingxi.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imlib.model.UserInfo;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.emojicon.EmojiconTextView;
import me.cl.lingxi.entity.Like;
import me.cl.lingxi.entity.Mood;

/**
 * Mood Adapter
 */
public class MoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Mood<Like>> mList;

    private FooterViewHolder mFooterViewHolder;

    public static final int LOAD_MORE = 0;
    public static final int LOAD_PULL_TO = 1;
    public static final int LOAD_NONE = 2;
    public static final int LOAD_END = 3;

    private static final int TYPE_FOOTER = -1;

    private OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, Mood<Like> mood);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public MoodAdapter(Context context, List<Mood<Like>> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return position;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View view = View.inflate(parent.getContext(), R.layout.item_footer, null);
            return mFooterViewHolder = new FooterViewHolder(view);
        } else {
            View view = View.inflate(parent.getContext(), R.layout.item_mood_detail, null);
            return new MoodViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.bindItem(LOAD_END);
        } else {
            MoodViewHolder moodViewHolder = (MoodViewHolder) holder;
            moodViewHolder.bindItem(mContext, mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    public void updateLoadStatus(int status) {
        mFooterViewHolder.bindItem(status);
    }

    // 设置数据
    public void setData(List<Mood<Like>> data) {
        mList = data;
        mFooterViewHolder.bindItem(LOAD_END);
        notifyDataSetChanged();
    }

    // 添加数据
    public void updateData(List<Mood<Like>> data) {
        mList.addAll(data);
        mFooterViewHolder.bindItem(LOAD_PULL_TO);
        notifyDataSetChanged();
    }

    public void addLike(Mood<Like> mood){
        mList.contains(mood);
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progress)
        ProgressBar mProgress;
        @BindView(R.id.tv_load_prompt)
        TextView mTvLoadPrompt;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(params);
            itemView.setVisibility(View.GONE);
        }

        public void bindItem(int status) {
            switch (status) {
                case LOAD_MORE:
                    mProgress.setVisibility(View.VISIBLE);
                    mTvLoadPrompt.setText("正在加载...");
                    itemView.setVisibility(View.VISIBLE);
                    break;
                case LOAD_PULL_TO:
                    mProgress.setVisibility(View.GONE);
                    mTvLoadPrompt.setText("上拉加载更多");
                    itemView.setVisibility(View.VISIBLE);
                    break;
                case LOAD_NONE:
                    mProgress.setVisibility(View.GONE);
                    mTvLoadPrompt.setText("没有更多了");
                    break;
                case LOAD_END:
                    itemView.setVisibility(View.GONE);
                default:
                    break;
            }
        }
    }

    class MoodViewHolder extends RecyclerView.ViewHolder {

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
        @BindView(R.id.mood_card)
        LinearLayout mMoodCard;
        private Mood<Like> mMood;

        public MoodViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(Context context, Mood<Like> mood) {
            mMood = mood;
            //是否能够聊天
            if (mood.isIm_ability()) {
                mLcChat.setVisibility(View.VISIBLE);
                String uid = String.valueOf(mood.getUid());
                String uname = mood.getUname();
                String img_url = Api.Url + mood.getUrl();
                if (!Constants.uidList.contains(uid)) {
                    Constants.uidList.add(uid);
                    Constants.userList.add(new UserInfo(uid, uname, Uri.parse(img_url)));
                }
            } else {
                mLcChat.setVisibility(View.GONE);
            }
            //动态详情
            Glide.with(context)
                    .load(Api.Url + mood.getUrl())
                    .placeholder(R.mipmap.userimg)
                    .error(R.mipmap.userimg)
                    .bitmapTransform(new CropCircleTransformation(context))
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
            String likeStr = Utils.getLikeStr(mood.getLikelist());
            switch (mood.getLikenum()) {
                case 0:
                    mMfLikeNum.setText("赞");
                    mLikeWindow.setVisibility(View.GONE);
                    break;
                case 1:
                case 2:
                case 3:
                    mLikeWindow.setVisibility(View.VISIBLE);
                    likeStr = likeStr + "觉得很赞";
                    break;
                default:
                    mLikeWindow.setVisibility(View.VISIBLE);
                    likeStr = likeStr + "等" + mood.getLikenum() + "人觉得很赞";
                    break;
            }
            mLikePeople.setText(Utils.getCharSequence(likeStr));
        }


        @OnClick({R.id.user_img, R.id.lc_chat, R.id.mf_comment, R.id.mf_like, R.id.mood_card})
        public void onClick(View view) {
            if (mOnItemListener != null) mOnItemListener.onItemClick(view, mMood);
        }
    }
}
