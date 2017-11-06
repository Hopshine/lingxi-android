package me.cl.lingxi.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.cl.lingxi.R;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2017/11/03
 * desc   :
 * version: 1.0
 */

public class PhotoSelAdapter extends RecyclerView.Adapter<PhotoSelAdapter.PhotoViewHolder> {

    public static final String mPhotoAdd = "file:///android_asset/icon_photo_add.png";
    private List<String> mPhotos;

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onPhotoClick(int position);
        void onDelete(int position);
    }

    public PhotoSelAdapter(List<String> photos) {
        this.mPhotos = photos;
        if (mPhotos.size() < 6 && !mPhotos.contains(mPhotoAdd)) mPhotos.add(mPhotoAdd);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_publish_photo, null);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
        holder.bindItem(mPhotos.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public void setPhotos(List<String> photos) {
        this.mPhotos = photos;
        if (mPhotos.size() < 6 && !mPhotos.contains(mPhotoAdd)) mPhotos.add(mPhotoAdd);
        notifyDataSetChanged();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_photo)
        ImageView mIvPhoto;
        @BindView(R.id.iv_delete)
        ImageView mIvDelete;
        private int mPosition;

        PhotoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(String photoUrl, final int position) {
            mPosition = position;
            if (mPhotos.get(position).equals(mPhotoAdd))
                mIvDelete.setVisibility(View.GONE);
            else
                mIvDelete.setVisibility(View.VISIBLE);

            Glide.with(mIvPhoto.getContext())
                    .load(photoUrl)
                    .centerCrop()
//                    .thumbnail(0.1f)
                    .into(mIvPhoto);
        }

        @OnClick({R.id.iv_photo, R.id.iv_delete})
        public void onClick(View view){
            switch (view.getId()) {
                case R.id.iv_photo:
                    if (mOnItemClickListener != null) mOnItemClickListener.onPhotoClick(mPosition);
                    break;
                case R.id.iv_delete:
                    if (mOnItemClickListener != null) mOnItemClickListener.onDelete(mPosition);
                    break;
            }
        }
    }
}
