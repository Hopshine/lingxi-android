package me.cl.lingxi.module.dd;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.lingxi.R;
import me.cl.lingxi.entity.dd.Hive;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2017/09/07
 * desc   :
 * version: 1.0
 */
public class ArcHiveAdapter extends RecyclerView.Adapter<ArcHiveAdapter.ViewHolder> {

    private List<Hive> mList;
    private boolean[] mSelect;
    private int mSavePosition;

    private OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, Hive hive, int position);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public ArcHiveAdapter(List<Hive> list) {
        this.mList = list;
        int size = list.size();
        this.mSelect = new boolean[size];
        if (size != 0) {
            this.mSavePosition = size - 1;
            this.mSelect[mSavePosition] = true;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.arc_hive_recycle_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindItem(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void setData(List<Hive> list) {
        this.mList = list;
        int size = list.size();
        this.mSelect = new boolean[size];
        if (size != 0) {
            this.mSavePosition = size - 1;
            this.mSelect[mSavePosition] = true;
        }
        notifyDataSetChanged();
    }

    public void updateData(List<Hive> list) {
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.arc_hive_name)
        TextView mArcTypeName;

        private String regex = "^[-\\+]?[\\d]*$";
        private Hive mHive;
        private int mPosition;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemListener != null) {
                        mSelect[mSavePosition] = false;
                        mSavePosition = mPosition;
                        mSelect[mPosition] = true;
                        notifyDataSetChanged();
                        mOnItemListener.onItemClick(v, mHive, mPosition);
                    }
                }
            });
        }

        public void bindItem(Hive hive, int position) {
            mPosition = position;
            mHive = hive;
            Pattern pattern = Pattern.compile(regex);
            String writer = hive.getWriter();
            if (pattern.matcher(writer).matches()) {
                writer = String.format("第 %s 话", writer);
            }
            mArcTypeName.setText(writer);
            mArcTypeName.setSelected(mSelect[mPosition]);
        }
    }
}
