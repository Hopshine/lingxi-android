package me.cl.lingxi.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.library.recycle.ItemDecoration;
import me.cl.lingxi.R;
import me.cl.lingxi.entity.inc.Episode;
import me.cl.lingxi.entity.inc.VideoSource;

/**
 * @author : happyc
 * e-mail : bafs.jy@live.com
 * time   : 2019/04/21
 * desc   : 多播放资源
 * version: 1.0
 */
public class IncSourceAdapter extends RecyclerView.Adapter<IncSourceAdapter.ViewHolder> {

    private List<VideoSource> mList;

    private OnItemListener mOnItemListener;

    public interface OnItemListener {
        void onItemChildClick(View view, Episode episode);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mOnItemListener = onItemListener;
    }
    public IncSourceAdapter(List<VideoSource> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.inc_source_recycle_item, null);
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

    public void setDate(List<VideoSource> data) {
        mList = data;
        notifyDataSetChanged();
    }

    public void updateData(List<VideoSource> data) {
        mList.addAll(data);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.source_flag)
        TextView mSourceFlag;
        @BindView(R.id.episode_recycler_view)
        RecyclerView mEpisodeRecyclerView;
        private IncEpisodeAdapter mAdapter;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            mEpisodeRecyclerView.setLayoutManager(layoutManager);
            ItemDecoration itemDecoration = new ItemDecoration(ItemDecoration.HORIZONTAL, 10, Color.parseColor("#ffffff"));
            itemDecoration.setGoneLast(true);
            mEpisodeRecyclerView.addItemDecoration(itemDecoration);
        }

        public void bindItem(VideoSource videoSource) {
            String flag = videoSource.getFlag();
            flag = mSourceFlag.getContext().getString(R.string.hint_playback_source) + flag;
            List<Episode> episodeList = videoSource.getEpisode();
            mSourceFlag.setText(flag);
            mAdapter = new IncEpisodeAdapter(episodeList);
            mEpisodeRecyclerView.setAdapter(mAdapter);
            mEpisodeRecyclerView.scrollToPosition(episodeList.size() - 1);

            mAdapter.setOnItemListener(new IncEpisodeAdapter.OnItemListener() {
                @Override
                public void onItemClick(View view, Episode episode, int position) {
                    if (mOnItemListener != null) mOnItemListener.onItemChildClick(view, episode);
                }
            });
        }
    }
}
