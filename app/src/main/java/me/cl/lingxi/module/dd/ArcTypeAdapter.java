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
import me.cl.lingxi.entity.dd.ArcType;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2017/09/07
 * desc   :
 * version: 1.0
 */
public class ArcTypeAdapter extends RecyclerView.Adapter<ArcTypeAdapter.ViewHolder> {

    private List<ArcType> mList;

    private OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, ArcType sortArc);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    ArcTypeAdapter(List<ArcType> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.arc_type_recycle_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindItem(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void setData(List<ArcType> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void updateData(List<ArcType> list) {
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.arc_type_image)
        ImageView mArcTypeImage;
        @BindView(R.id.arc_type_name)
        TextView mArcTypeName;

        private ArcType mArcType;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // 设置图片宽高
            int screenWidth = Utils.getScreenWidth();
            int px = Utils.dp2px(10);
            int itemSize = (screenWidth - (px * 12)) / 3;
            ViewGroup.LayoutParams layoutParams = mArcTypeImage.getLayoutParams();
            layoutParams.width = itemSize;
            layoutParams.height = itemSize;
            mArcTypeImage.setLayoutParams(layoutParams);

            // 绑定事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemListener != null) mOnItemListener.onItemClick(v, mArcType);
                }
            });
        }

        /**
         * 数据绑定
         */
        void bindItem(ArcType arcType) {
            mArcType = arcType;
            mArcTypeName.setText(arcType.getTypename());

            ContentUtil.loadCircleCropImage(mArcTypeImage, arcType.getSuoluetudizhi());
        }
    }
}
