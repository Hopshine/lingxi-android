package me.cl.lingxi.adapter;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.cl.library.loadmore.LoadMord;
import me.cl.library.loadmore.LoadMoreViewHolder;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.util.DateUtil;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.entity.Like;
import me.cl.lingxi.entity.User;

/**
 * Feed Adapter
 */
public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Feed> mList;

    private LoadMoreViewHolder mLoadMoreViewHolder;

    private static final int TYPE_FOOTER = -1;

    private OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, Feed feed, int position);
        void onPhotoClick(ArrayList<String> photos, int position);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public FeedAdapter(List<Feed> list) {
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
            View view = View.inflate(parent.getContext(), R.layout.lib_load_more, null);
            return mLoadMoreViewHolder = new LoadMoreViewHolder(view);
        } else {
            View view = View.inflate(parent.getContext(), R.layout.item_feed_detail, null);
            return new MoodViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadMoreViewHolder) {
            LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) holder;
            loadMoreViewHolder.bindItem(LoadMord.LOAD_END);
        } else {
            MoodViewHolder moodViewHolder = (MoodViewHolder) holder;
            moodViewHolder.bindItem(mList.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    public void updateLoadStatus(int status) {
        mLoadMoreViewHolder.bindItem(status);
    }

    // 设置数据
    public void setData(List<Feed> data) {
        mList = data;
        notifyDataSetChanged();
        mLoadMoreViewHolder.bindItem(LoadMord.LOAD_MORE);
    }

    // 添加数据
    public void addData(List<Feed> data) {
        mList.addAll(data);
        notifyDataSetChanged();
        mLoadMoreViewHolder.bindItem(LoadMord.LOAD_PULL_TO);
    }

    // 更新item
    public void updateItem(Feed feed, int position) {
        mList.set(position, feed);
        notifyItemChanged(position);
    }

    class MoodViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_img)
        ImageView mUserImg;
        @BindView(R.id.user_name)
        TextView mUserName;
        @BindView(R.id.mood_time)
        TextView mMoodTime;
        @BindView(R.id.mood_info)
        AppCompatTextView mMoodInfo;
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
        @BindView(R.id.recycler_view)
        RecyclerView mRecyclerView;

        private Feed mFeed;
        private int mPosition;

        public MoodViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(Feed feed, int position) {
            mFeed = feed;
            mPosition = position;
            User user = feed.getUser();
            //动态详情
            Glide.with(mUserImg.getContext())
                    .load(Constants.IMG_URL + user.getAvatar())
                    .placeholder(R.drawable.img_user)
                    .error(R.drawable.img_user)
                    .bitmapTransform(new CropCircleTransformation(mUserImg.getContext()))
                    .into(mUserImg);
            mUserName.setText(user.getUsername());
            mMoodTime.setText(DateUtil.showTime(feed.getCreateTime()));
            mMoodInfo.setText(feed.getFeedInfo());
            // 图片
            final List<String> photos = feed.getPhotoList();
            if (photos != null && photos.size() > 0) {
                mRecyclerView.setVisibility(View.VISIBLE);
                int column = 3;
                if (photos.size() == 1) column = 2;
                mRecyclerView.setLayoutManager(new GridLayoutManager(mRecyclerView.getContext(), column));
                FeedPhotoAdapter adapter = new FeedPhotoAdapter(photos);
                adapter.setOnItemClickListener(new FeedPhotoAdapter.OnItemClickListener() {
                    @Override
                    public void onPhotoClick(int position) {
                        if (mOnItemListener != null) mOnItemListener.onPhotoClick((ArrayList<String>) photos, position);
                    }
                });
                mRecyclerView.setAdapter(adapter);
            } else {
                mRecyclerView.setVisibility(View.GONE);
            }
            // 查看评论点赞数
            mMfSeeNum.setText(String.valueOf(feed.getViewNum()));
            mMfCommentNum.setText(String.valueOf(feed.getCommentNum()));
            // 是否已经点赞
            if (feed.isLike()) {
                mMfLikeIcon.setSelected(true);
            } else {
                mMfLikeIcon.setSelected(false);
            }
            // 点赞列表
            List<Like> likeList = feed.getLikeList();
            int likeNum = likeList == null ? 0 :likeList.size();
            mMfLikeNum.setText(String.valueOf(likeNum));
            String likeStr = Utils.getLikeStr(likeList);
            switch (likeNum) {
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
                    likeStr = likeStr + "等" + likeNum + "人觉得很赞";
                    break;
            }
            mLikePeople.setText(Utils.colorFormat(likeStr));
        }


        @OnClick({R.id.user_img, R.id.mf_comment, R.id.mf_like, R.id.mood_card})
        public void onClick(View view) {
            if (mOnItemListener != null) mOnItemListener.onItemClick(view, mFeed, mPosition);
        }
    }
}
