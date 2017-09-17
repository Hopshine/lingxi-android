package me.cl.lingxi.common.widget;

import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.lingxi.R;

import static android.view.View.VISIBLE;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2017/09/07
 * desc   :
 * version: 1.0
 */

public class LoadViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.progress)
    ProgressBar mProgress;
    @BindView(R.id.tv_load_prompt)
    TextView mTvLoadPrompt;

    public static final int LOAD_MORE = 0;
    public static final int LOAD_PULL_TO = 1;
    public static final int LOAD_NONE = 2;
    public static final int LOAD_END = 3;

    private LinearLayoutCompat.LayoutParams mParams;

    public LoadViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mParams = (LinearLayoutCompat.LayoutParams)itemView.getLayoutParams();
    }

    public void bindItem(int status) {
        setVisible();
        switch (status) {
            case LOAD_MORE:
                mProgress.setVisibility(VISIBLE);
                mTvLoadPrompt.setText("正在加载...");
                itemView.setVisibility(VISIBLE);
                break;
            case LOAD_PULL_TO:
                mProgress.setVisibility(View.GONE);
                mTvLoadPrompt.setText("上拉加载更多");
                itemView.setVisibility(VISIBLE);
                break;
            case LOAD_NONE:
                mProgress.setVisibility(View.GONE);
                mTvLoadPrompt.setText("没有更多了");
                break;
            case LOAD_END:
            default:
                setGone();
                itemView.setVisibility(View.GONE);
                break;
        }
    }

    private void setVisible() {
        mParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        itemView.setLayoutParams(mParams);
    }

    private void setGone() {
        mParams.width = 0;
        mParams.height = 0;
        itemView.setLayoutParams(mParams);
    }
}
