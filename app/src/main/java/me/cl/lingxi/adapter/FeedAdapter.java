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
import me.cl.lingxi.common.util.FeedTextUtil;
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
        this.mList = list == null ? new ArrayList<Feed>() : list;
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
            View loadView = View.inflate(parent.getContext(), R.layout.lib_load_more, null);
            return mLoadMoreViewHolder = new LoadMoreViewHolder(loadView);
        } else {
            View feedView = View.inflate(parent.getContext(), R.layout.item_feed_detail, null);
            return new MoodViewHolder(feedView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadMoreViewHolder) {
            LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) holder;
            if (getItemCount() > 5) {
                loadMoreViewHolder.bindItem(LoadMord.LOAD_PULL_TO);
            } else {
                loadMoreViewHolder.bindItem(LoadMord.LOAD_END);
            }
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
        mLoadMoreViewHolder.bindItem(LoadMord.LOAD_PULL_TO);
        notifyDataSetChanged();
    }

    // 添加数据
    public void addData(List<Feed> data) {
        mList.addAll(data);
        mLoadMoreViewHolder.bindItem(LoadMord.LOAD_PULL_TO);
        notifyDataSetChanged();
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
        @BindView(R.id.feed_time)
        TextView mFeedTime;
        @BindView(R.id.feed_info)
        AppCompatTextView mFeedInfo;
        @BindView(R.id.feed_body)
        LinearLayout mFeedBody;
        @BindView(R.id.feed_view_num)
        TextView mFeedSeeNum;
        @BindView(R.id.feed_comment_num)
        TextView mFeedCommentNum;
        @BindView(R.id.feed_comment_layout)
        LinearLayout mFeedCommentLayout;
        @BindView(R.id.feed_like_icon)
        ImageView mFeedLikeIcon;
        @BindView(R.id.feed_like_num)
        TextView mFeedLikeNum;
        @BindView(R.id.feed_like_layout)
        LinearLayout mFeedLikeLayout;
        @BindView(R.id.feed_action_layout)
        LinearLayout mFeedActionLayout;
        @BindView(R.id.like_people)
        TextView mLikePeople;
        @BindView(R.id.like_window)
        LinearLayout mLikeWindow;
        @BindView(R.id.feed_card)
        LinearLayout mFeedCard;
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
            user = user == null ? new User() : user;
            //动态详情
            Glide.with(mUserImg.getContext())
                    .load(Constants.IMG_URL + user.getAvatar())
                    .placeholder(R.drawable.img_user)
                    .error(R.drawable.img_user)
                    .bitmapTransform(new CropCircleTransformation(mUserImg.getContext()))
                    .into(mUserImg);
            mUserName.setText(user.getUsername());
            mFeedTime.setText(DateUtil.showTime(feed.getCreateTime()));
            mFeedInfo.setText(FeedTextUtil.getFeedText(feed.getFeedInfo(), mFeedInfo));
            // 图片
            final List<String> photos = feed.getPhotoList();
            if (photos != null && photos.size() > 0) {
                mRecyclerView.setVisibility(View.VISIBLE);
                int size = photos.size();
                // 如果只有一张图，设置两列，否则三列
                int column = size == 1 ? 2 : 3;
                mRecyclerView.setLayoutManager(new GridLayoutManager(mRecyclerView.getContext(), column));
                FeedPhotoAdapter adapter = new FeedPhotoAdapter(photos);
                adapter.setOnItemClickListener(new FeedPhotoAdapter.OnItemClickListener() {
                    @Override
                    public void onPhotoClick(int position) {
                        // 新对象接防止拼接后影响原来的url
                        List<String> urls = new ArrayList<>(photos);
                        int size = urls.size();
                        // 拼接url
                        for (int i = 0; i < size; i++) {
                            String photo = urls.get(i);
                            photo = Constants.IMG_URL + photo;
                            urls.set(i, photo);
                        }
                        if (mOnItemListener != null) mOnItemListener.onPhotoClick((ArrayList<String>) urls, position);
                    }
                });
                mRecyclerView.setAdapter(adapter);
            } else {
                mRecyclerView.setVisibility(View.GONE);
            }
            // 查看评论点赞数
            mFeedSeeNum.setText(String.valueOf(feed.getViewNum()));
            mFeedCommentNum.setText(String.valueOf(feed.getCommentNum()));
            // 是否已经点赞
            if (feed.isLike()) {
                mFeedLikeIcon.setSelected(true);
            } else {
                mFeedLikeIcon.setSelected(false);
            }
            // 点赞列表
            List<Like> likeList = feed.getLikeList();
            int likeNum = likeList == null ? 0 :likeList.size();
            mFeedLikeNum.setText(String.valueOf(likeNum));
            String likeStr = Utils.getLikeStr(likeList);
            switch (likeNum) {
                case 0:
                    mFeedLikeNum.setText("赞");
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


        @OnClick({R.id.user_img, R.id.feed_comment_layout, R.id.feed_like_layout, R.id.feed_card})
        public void onClick(View view) {
            if (mOnItemListener != null) mOnItemListener.onItemClick(view, mFeed, mPosition);
        }
    }
}
