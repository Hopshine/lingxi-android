package me.cl.lingxi.module.dd;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.library.base.BaseFragment;
import me.cl.library.loadmore.OnLoadMoreListener;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.DDApi;
import me.cl.lingxi.common.okhttp.ArcCallback;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.util.GsonUtil;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.common.widget.GridItemDecoration;
import me.cl.lingxi.entity.dd.ArcType;
import me.cl.lingxi.entity.dd.SplitArc;

public class ArcListFragment extends BaseFragment {

    private static final String ARC_TYPE = "arc_type";
    public static final String ARC_LIST_CACHE = "arc_list_cache";
    public static final String DEFAULT = "default";

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.tip_action)
    TextView mTipAction;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private String mArcTypeStr;
    private ArcListAdapter mAdapter;
    private ArcType mArcType;
    private int mPage = 0;

    public ArcListFragment() {

    }

    public static ArcListFragment newInstance(String arcType) {
        ArcListFragment fragment = new ArcListFragment();
        Bundle args = new Bundle();
        args.putString(ARC_TYPE, arcType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mArcTypeStr = getArguments().getString(ARC_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.arc_list_fragment, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        initRecyclerView();
        initData();
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        List<SplitArc> arcList = new ArrayList<>();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        GridItemDecoration gridItemDecoration = new GridItemDecoration();
//        gridItemDecoration.setDrawable(Color.parseColor("#f2f2f2"));
        gridItemDecoration.setDecoration(Utils.dp2px(8));
        mRecyclerView.addItemDecoration(gridItemDecoration);

        mAdapter = new ArcListAdapter(arcList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new ArcListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View paramView, SplitArc splitArc) {
                goToArcPlay(splitArc);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 0;
                getData();
            }
        });
        mRecyclerView.addOnScrollListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mPage++;
                getData();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mArcTypeStr = SPUtil.build().getString(ARC_TYPE, DEFAULT);
        switch (mArcTypeStr) {
            case DEFAULT:
                getDefaultData();
                break;
            default:
                initArcData(mArcTypeStr);
                break;
        }
    }

    /**
     * 初始化分类番剧
     */
    private void initArcData(String acrTypeStr) {
        ArcType arcType = GsonUtil.toObject(acrTypeStr, ArcType.class);
        mArcType = arcType;
        mPage = 0;
        setTipAction(arcType);
        getArcData(arcType);
    }

    /**
     * 设置提示
     */
    private void setTipAction(ArcType arcType) {
        String name = arcType.getTypename();
        mTipAction.setText(String.format("当前选择=%s=", name));
    }

    /**
     * 番剧分类选择
     */
    public void onArcTypeChange(ArcType arcType) {
        mArcTypeStr = GsonUtil.toJson(arcType);
        SPUtil.build().putString(ARC_TYPE, mArcTypeStr);

        mArcType = arcType;
        mPage = 0;
        setTipAction(arcType);
        getArcData(arcType);
    }

    /**
     * 获取数据
     */
    private void getData() {
        if (DEFAULT.equals(mArcTypeStr)) {
            getDefaultData();
        } else {
            getArcData(mArcType);
        }
    }

    /**
     * 默认获取新番榜单
     */
    private void getDefaultData() {
        OkUtil.arc()
                .url(DDApi.sortArcType)
                .addParam("type", "news")
                .addParam("page", mPage)
                .execute(new ArcCallback<List<SplitArc>>() {
                    @Override
                    public void onSuccess(List<SplitArc> response) {
                        if (response != null) {
                            if (mPage == 0) {
                                setData(response);
                            } else {
                                updateData(response);
                            }
                        } else {
                            setError();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        setError();
                    }
                });
    }

    /**
     * 获取分类番剧
     */
    private void getArcData(ArcType arcType) {
        String typeId = arcType.getTypeid();
        String typeName = arcType.getTypename();
        String isType = arcType.getIstype();
        OkUtil.arc()
                .url(DDApi.arcByRaId)
                .addParam("typeid", typeId)
                .addParam("typename", typeName)
                .addParam("istype", isType)
                .addParam("page", mPage)
                .execute(new ArcCallback<List<SplitArc>>() {
                    @Override
                    public void onSuccess(List<SplitArc> response) {
                        if (response != null) {
                            if (mPage == 0) {
                                setData(response);
                            } else {
                                updateData(response);
                            }
                        } else {
                            setError();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        setError();
                    }
                });
    }

    /**
     * 设置Adapter数据
     */
    private void setData(List<SplitArc> list) {
        setRefreshFalse();
        mAdapter.setData(list);
    }
    /**
     * 更新Adapter数据
     */
    private void updateData(List<SplitArc> list) {
        setRefreshFalse();
        mAdapter.updateData(list);
    }

    /**
     * 设置异常提示
     */
    private void setError() {
        setRefreshFalse();
        showToast("没有更多了╮(╯▽╰)╭");
    }

    /**
     * 结束刷新
     */
    private void setRefreshFalse() {
        boolean refreshing = mSwipeRefreshLayout.isRefreshing();
        if (refreshing) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * 前往播放
     */
    private void goToArcPlay(SplitArc splitArc) {
        String id = splitArc.getId();
        if (TextUtils.isEmpty(id)) {
            id = splitArc.getTypeid();
        }
        Intent intent = new Intent(getActivity(), ArcPlayActivity.class);
        intent.putExtra("arc_id", id);
        startActivity(intent);
    }
}
