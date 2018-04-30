package me.cl.lingxi.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.entity.Comment;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.entity.Relevant;
import me.cl.lingxi.entity.Reply;
import me.cl.lingxi.entity.User;

/**
 * Reply Adapter
 */
public class RelevantAdapter extends RecyclerView.Adapter<RelevantAdapter.RelevantViewHolder> {

    private Context mContext;
    private List<Relevant> mList;

    private OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, Relevant relevant);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public RelevantAdapter(Context context, List<Relevant> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public RelevantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_relevant, null);
        return new RelevantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RelevantViewHolder holder, int position) {
        holder.bindItem(mContext, mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(List<Relevant> data) {
        mList.addAll(data);
        notifyDataSetChanged();
    }

    class RelevantViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_img)
        ImageView mUserImg;
        @BindView(R.id.user_name)
        TextView mUserName;
        @BindView(R.id.relevant_time)
        TextView mRelevantTime;
        @BindView(R.id.relevant_info)
        AppCompatTextView mRelevantInfo;
        @BindView(R.id.mood_info)
        AppCompatTextView mMoodInfo;
        @BindView(R.id.mood_body)
        LinearLayout mMoodBody;

        private Relevant mRelevant;

        public RelevantViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(Context context, Relevant relevant) {
            mRelevant = relevant;
            Comment comment = relevant.getComment();
            User user = comment.getUser();
            Integer replyNum = relevant.getReplyNum();
            StringBuilder relevantInfo = new StringBuilder();
            String timeStr = "";
            if (replyNum > 0) {
                List<Reply> replyList = relevant.getReplyList();
                for (int i = 0, size = replyList.size(); i < size; i++) {
                    Reply reply = replyList.get(i);
                    if (i == 0) {
                        user = reply.getUser();
                        timeStr = reply.getCreateTime();
                        relevantInfo.append(reply.getCommentInfo());
                    } else {
                        relevantInfo.append("//{@").append(reply.getUser().getUsername()).append("}:")
                                .append(reply.getCommentInfo());
                    }

                }
                relevantInfo.append("//{@").append(comment.getUser().getUsername()).append("}:")
                        .append(comment.getCommentInfo());
            } else {
                timeStr = comment.getCreateTime();
                relevantInfo.append(comment.getCommentInfo());
            }

            Glide.with(context)
                    .load(Constants.IMG_URL + user.getAvatar())
                    .placeholder(R.drawable.img_user)
                    .error(R.drawable.img_user)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(mUserImg);
            mUserName.setText(user.getUsername());
            mRelevantTime.setText(timeStr);

            mRelevantInfo.setText(Utils.colorFormat(relevantInfo.toString()));

            Feed feed = relevant.getFeed();
            String feedInfo = "{" + feed.getUser().getUsername() + "}：" + feed.getFeedInfo();
            mMoodInfo.setText(Utils.colorFormat(feedInfo));
        }

        @OnClick({R.id.user_img, R.id.mood_body})
        public void onClick(View view) {
            if (mOnItemListener != null) mOnItemListener.onItemClick(view, mRelevant);
        }
    }
}
