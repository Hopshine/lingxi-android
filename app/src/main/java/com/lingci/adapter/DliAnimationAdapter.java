package com.lingci.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.utils.Utils;
import com.bumptech.glide.Glide;
import com.lingci.R;
import com.lingci.entity.Animation;
import com.lingci.entity.DliAnimation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DliAnimationAdapter extends RecyclerView.Adapter<DliAnimationAdapter.ViewHolder> {

    private List<Animation> mAnimationList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View paramView, Animation animation);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public DliAnimationAdapter(Context context, List<DliAnimation> animationList) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
        for (int i = 0; i < animationList.size(); i++) {
            this.mAnimationList.addAll(animationList.get(i).getAnimationList());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(this.mInflater.inflate(R.layout.item_dili, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.bindView(mContext, mAnimationList.get(position));
        if (this.mAnimationList != null) {
            Animation animation = mAnimationList.get(position);
            holder.mAnimateName.setText(animation.getName());
            int i = (Utils.getScreenWidth(this.mContext) - Utils.dp2px(mContext, 24f)) / 3;
            int j = i * 13 / 9;
            ViewGroup.LayoutParams localLayoutParams = holder.mAnimateImg.getLayoutParams();
            localLayoutParams.width = i;
            localLayoutParams.height = j;
            holder.mAnimateImg.setLayoutParams(localLayoutParams);
            Glide.with(mContext).load(animation.getImgUrl()).into(holder.mAnimateImg);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, mAnimationList.get(position));
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.mAnimationList == null ? 0 : mAnimationList.size();
    }

    public void setData(List<DliAnimation> animationList){
        mAnimationList.clear();
        for (DliAnimation animation: animationList){
            mAnimationList.addAll(animation.getAnimationList());
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.animate_img)
        ImageView mAnimateImg;
        @BindView(R.id.animate_name)
        TextView mAnimateName;
        private Animation mAnimation;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(Context context, Animation animation) {
            mAnimation = animation;
            mAnimateName.setText(animation.getName());
            int i = (Utils.getScreenWidth(context) - Utils.dp2px(mContext, 16f)) / 3;
            int j = i * 13 / 9;
            ViewGroup.LayoutParams localLayoutParams = mAnimateImg.getLayoutParams();
            localLayoutParams.width = i;
            localLayoutParams.height = j;
            mAnimateImg.setLayoutParams(localLayoutParams);
            Glide.with(mContext).load(animation.getImgUrl()).into(mAnimateImg);
            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, mAnimation);
                    }
                }
            });
        }
    }

}
