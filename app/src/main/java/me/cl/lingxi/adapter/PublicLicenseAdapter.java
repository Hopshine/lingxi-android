package me.cl.lingxi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.lingxi.R;
import me.cl.lingxi.entity.PublicLicense;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2017/09/07
 * desc   :
 * version: 1.0
 */

public class PublicLicenseAdapter extends RecyclerView.Adapter<PublicLicenseAdapter.ViewHolder> {

    private Context mContext;
    private List<PublicLicense> mList;

    private OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemClick(View view, PublicLicense license);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }

    public PublicLicenseAdapter(Context context, List<PublicLicense> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.public_license_recycle_item, null);
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

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name)
        TextView mName;
        @BindView(R.id.author)
        TextView mAuthor;
        @BindView(R.id.desc)
        TextView mDesc;

        private PublicLicense mLicense;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemListener != null) mOnItemListener.onItemClick(v, mLicense);
                }
            });
        }

        public void bindItem(PublicLicense license) {
            mLicense = license;
            mName.setText(license.getName());
            mAuthor.setText(license.getAuthor());
            mDesc.setText(license.getDesc());
        }
    }
}
