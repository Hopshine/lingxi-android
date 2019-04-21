package me.cl.lingxi.module.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.library.base.BaseActivity;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.IncVideoAdapter;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.common.widget.GridItemDecoration;
import me.cl.lingxi.entity.inc.IncResult;
import me.cl.lingxi.entity.inc.IncVideo;
import me.cl.lingxi.module.video.IncVideoPlayActivity;
import okhttp3.Call;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    SearchView mSearchView;

    private IncVideoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        ToolbarUtil.init(mToolbar, this)
                .setMenu(R.menu.search_view_menu, null)
                .setBack()
                .build();
        Menu menu = mToolbar.getMenu();
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        initSearchView();
        initRecyclerView();
    }

    private void initSearchView() {
        // 当展开无输入内容的时候，没有关闭的图标
        mSearchView.onActionViewExpanded();
        // 显示隐藏提交按钮
        mSearchView.setSubmitButtonEnabled(true);
        // 事件
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        List<IncVideo> incVideos = new ArrayList<>();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        GridItemDecoration gridItemDecoration = new GridItemDecoration();
//        gridItemDecoration.setDrawable(Color.parseColor("#f2f2f2"));
        gridItemDecoration.setDecoration(Utils.dp2px(8));
        mRecyclerView.addItemDecoration(gridItemDecoration);

        mAdapter = new IncVideoAdapter(incVideos);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new IncVideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View paramView, IncVideo incVideo) {
                gotoVideoPlay(incVideo);
            }
        });
    }

    private void getData(String query) {
        OkUtil.get()
                .url(Api.incApi)
                .addUrlParams("wd", query)
                .execute(new ResultCallback<IncResult>() {
                    @Override
                    public void onSuccess(IncResult response) {
                        List<IncVideo> video = response.getVideo();
                        if (video != null) {
                            setData(video);
                        } else {
                            setError();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        setError();
                    }
                });
    }

    /**
     * 设置Adapter数据
     */
    private void setData(List<IncVideo> list) {
        mAdapter.setData(list);
    }

    /**
     * 设置异常提示
     */
    private void setError() {
        showToast("没有找到任何内容╮(╯▽╰)╭");
    }

    private void gotoVideoPlay(IncVideo incVideo) {
        Intent intent = new Intent(this, IncVideoPlayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(IncVideoPlayActivity.INC_VIDEO, incVideo);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
