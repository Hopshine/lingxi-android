package me.cl.lingxi.adapter;

import android.content.Context;
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
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.util.ColorPhrase;
import me.cl.lingxi.emojicon.EmojiconTextView;
import me.cl.lingxi.entity.Mood;
import me.cl.lingxi.entity.Relevant;

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
        EmojiconTextView mRelevantInfo;
        @BindView(R.id.mood_info)
        EmojiconTextView mMoodInfo;
        @BindView(R.id.mood_body)
        LinearLayout mMoodBody;

        private Relevant mRelevant;

        public RelevantViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(Context context, Relevant relevant) {
            mRelevant = relevant;
            Glide.with(context)
                    .load(Api.baseUrl + relevant.getUrl())
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
