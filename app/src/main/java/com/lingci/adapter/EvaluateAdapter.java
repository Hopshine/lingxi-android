package com.lingci.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lingci.R;
import com.lingci.common.config.Api;
import com.lingci.emojicon.EmojiconTextView;
import com.lingci.entity.Evaluate;
import com.lingci.entity.Reply;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Evaluate Adapter
 */
public class EvaluateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Evaluate<Reply>> mList;

    public static final int LOAD_MORE = 0;
    public static final int LOAD_PULL_TO = 1;
    public static final int LOAD_NONE = 2;
    public static final int LOAD_END = 3;

    private static final int TYPE_FOOTER = -1;
    private int mStatus = LOAD_END;

    private OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, Evaluate<Reply> evaluate);
        void onItemChildClick(View view, Reply reply);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public EvaluateAdapter(Context context, List<Evaluate<Reply>> list) {
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
            return new FooterViewHolder(view);
        } else {
            View view = View.inflate(parent.getContext(), R.layout.item_evaluate, null);
            return new EvaluateViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.bindItem();
        } else {
            EvaluateViewHolder evaluateViewHolder = (EvaluateViewHolder) holder;
            evaluateViewHolder.bindItem(mContext, mList.get(position));
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

    public void setDate(List<Evaluate<Reply>> data) {
        mList = data;
        notifyDataSetChanged();
    }

    public void updateData(List<Evaluate<Reply>> data) {
        mList.addAll(data);
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
            LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(params);
            itemView.setVisibility(View.GONE);
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
                    mTvLoadPrompt.setText("没有更多了");
                    break;
                case LOAD_END:
                    itemView.setVisibility(View.GONE);
                default:
                    break;
            }
        }
    }

    class EvaluateViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.evaluate_body)
        RelativeLayout mEvaluateBody;
        @BindView(R.id.user_img)
        ImageView mUserImg;
        @BindView(R.id.user_name)
        TextView mUserName;
        @BindView(R.id.evaluate_time)
        TextView mEvaluateTime;
        @BindView(R.id.evaluate_info)
        EmojiconTextView mEvaluateInfo;
        @BindView(R.id.recycler_view)
        RecyclerView mRecyclerView;
        private Evaluate<Reply> mEvaluate;

        public EvaluateViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
        }

        public void bindItem(Context context, Evaluate<Reply> evaluate) {
            mEvaluate = evaluate;
            Glide.with(context)
                    .load(Api.Url + evaluate.getUrl())
                    .skipMemoryCache(true)
                    .placeholder(R.mipmap.userimg)
                    .error(R.mipmap.userimg)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(mUserImg);
            mUserName.setText(evaluate.getUname());
            mEvaluateTime.setText(evaluate.getCm_time());
            mEvaluateInfo.setText(evaluate.getComment());
            ReplyAdapter adapter = new ReplyAdapter(context, evaluate.getReplylist());
            mRecyclerView.setAdapter(adapter);
            adapter.setOnItemListener(new ReplyAdapter.OnItemListener() {
                @Override
                public void onItemClick(View view, Reply reply) {
                    if (mOnItemListener != null) mOnItemListener.onItemChildClick(view, reply);
                }
            });
        }

        @OnClick({R.id.user_img, R.id.evaluate_body})
        public void onClick(View view) {
            if (mOnItemListener != null) mOnItemListener.onItemClick(view, mEvaluate);
        }
    }
}
