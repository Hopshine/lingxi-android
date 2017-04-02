package com.lingci.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lingci.R;
import com.lingci.common.Api;
import com.lingci.common.util.ColorPhrase;
import com.lingci.emojicon.EmojiconTextView;
import com.lingci.entity.Like;
import com.lingci.entity.Mood;
import com.lingci.entity.Relevant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Reply Adapter
 */
public class RelevantAdapter extends RecyclerView.Adapter<RelevantAdapter.RelevantViewHolder> {

    private Context mContext;
    private List<Relevant<Mood<Like>>> mList;

    private OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, Relevant<Mood<Like>> relevant);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public RelevantAdapter(Context context, List<Relevant<Mood<Like>>> list) {
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

    public void updateData(List<Relevant<Mood<Like>>> data) {
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
        EmojiconTextView mRelevantInfo;
        @BindView(R.id.mood_info)
        EmojiconTextView mMoodInfo;
        @BindView(R.id.mood_body)
        LinearLayout mMoodBody;

        private Relevant<Mood<Like>> mRelevant;

        public RelevantViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(Context context, Relevant<Mood<Like>> relevant) {
            mRelevant = relevant;
            Glide.with(context)
                    .load(Api.Url + relevant.getUrl())
                    .skipMemoryCache(true)
                    .placeholder(R.mipmap.userimg)
                    .error(R.mipmap.userimg)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(mUserImg);
            mUserName.setText(relevant.getUname());
            mRelevantTime.setText(relevant.getCm_time());
            mRelevantInfo.setText(relevant.getComment());
            Mood mood = relevant.getMinifeed();
            String replyStr = "{" + mood.getUname() + "}：" + mood.getLc_info();
            CharSequence chars = ColorPhrase.from(replyStr).withSeparator("{}").innerColor(0xFF4FC1E9).outerColor(0xFF666666).format();
            mMoodInfo.setText(chars);
        }

        @OnClick({R.id.user_img, R.id.mood_body})
        public void onClick(View view) {
            if (mOnItemListener != null) mOnItemListener.onItemClick(view, mRelevant);
        }
    }
}
