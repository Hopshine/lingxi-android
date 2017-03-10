package com.lingci.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lingci.R;
import com.lingci.emojicon.EmojiconTextView;
import com.lingci.entity.Mood;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Mood Adapter
 * Created by bafsj on 17/3/2.
 */
public class MoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List mList;
    private String mTag;

    public static final int LOAD_MORE = 0;
    public static final int LOAD_PULL_TO = 1;
    public static final int LOAD_NONE = 2;
    public static final int LOAD_END = 3;

    private static final int TYPE_FOOTER = -1;
    private int mStatus = 1;

    public MoodAdapter(Context context, List list, String tag) {
        this.mContext = context;
        this.mList = list;
        this.mTag = tag;
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
            return new FooterViewHolder(view);
        } else {
            View view = View.inflate(parent.getContext(), R.layout.item_mood_detail, null);
            return new MoodViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.bindItem();
        } else {
            MoodViewHolder moodViewHolder = (MoodViewHolder) holder;
            moodViewHolder.bindItem(mContext, (Mood)mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    public void updateLoadStatus(int status) {
        this.mStatus = status;
        notifyDataSetChanged();
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.progress)
        ProgressBar mProgress;
        @BindView(R.id.tv_load_prompt)
        TextView mTvLoadPrompt;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem() {
            switch (mStatus) {
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
                    mTvLoadPrompt.setText("已无更多加载");
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
        @BindView(R.id.like_feel)
        TextView mLikeFeel;
        @BindView(R.id.like_window)
        LinearLayout mLikeWindow;
        @BindView(R.id.mood_card)
        LinearLayout mMoodCard;

        public MoodViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(Context context, Mood mood) {

        }
    }
}
