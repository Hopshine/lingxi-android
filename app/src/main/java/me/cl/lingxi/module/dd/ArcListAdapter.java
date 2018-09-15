package me.cl.lingxi.module.dd;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.lingxi.R;
import me.cl.lingxi.common.util.ContentUtil;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.entity.dd.SplitArc;

/**
 * 嘀哩嘀哩动画番剧
 */
public class ArcListAdapter extends RecyclerView.Adapter<ArcListAdapter.ViewHolder> {

    private List<SplitArc> mArcList;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View paramView, SplitArc splitArc);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public ArcListAdapter(List<SplitArc> list) {
        this.mArcList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.arc_list_recycle_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.bindItem(mArcList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return this.mArcList == null ? 0 : mArcList.size();
    }

    public void setData(List<SplitArc> list) {
        mArcList = list;
        notifyDataSetChanged();
    }

    public void updateData(List<SplitArc> list) {
        mArcList.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.arc_pic)
        ImageView mArcPic;
        @BindView(R.id.arc_name)
        TextView mArcName;
        private SplitArc mSplitArc;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            int width = (Utils.getScreenWidth() - Utils.dp2px(32)) / 3;
            int height = width * 13 / 9;
            ViewGroup.LayoutParams localLayoutParams = mArcPic.getLayoutParams();
            localLayoutParams.width = width;
            localLayoutParams.height = height;
            mArcPic.setLayoutParams(localLayoutParams);

            ViewGroup.LayoutParams layoutParams = mArcName.getLayoutParams();
            layoutParams.width = width;
            mArcName.setLayoutParams(layoutParams);

            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, mSplitArc);
                    }
                }
            });

        }

        public void bindItem(SplitArc splitArc, int position) {
            mSplitArc = splitArc;
            mArcName.setText(splitArc.getTypename());
            ContentUtil.loadImage(mArcPic, splitArc.getSuoluetudizhi());
        }
    }

}
