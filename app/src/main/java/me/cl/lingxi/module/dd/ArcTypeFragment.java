package me.cl.lingxi.module.dd;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.library.base.BaseFragment;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.DDApi;
import me.cl.lingxi.common.okhttp.ArcCallback;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.util.GsonUtil;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.common.widget.GridItemDecoration;
import me.cl.lingxi.entity.dd.ArcType;
import me.cl.lingxi.entity.dd.TypeResult;

public class ArcTypeFragment extends BaseFragment {

    private static final String ARC_TYPE = "apr_type";
    public static final String IFY = "classify_tongyong";
    public static final String RI_QI = "list_riqitongyong";

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private String mArcType;
    private ArcTypeAdapter mArcTypeAdapter;
    private OnArcTypeChangeListener mOnArcTypeChangeListener;

    public ArcTypeFragment() {

    }

    public void setOnArcTypeChangeListener(OnArcTypeChangeListener onArcTypeChangeListener) {
        mOnArcTypeChangeListener = onArcTypeChangeListener;
    }

    public static ArcTypeFragment newInstance(String arcType) {
        ArcTypeFragment fragment = new ArcTypeFragment();
        Bundle args = new Bundle();
        args.putString(ARC_TYPE, arcType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mArcType = getArguments().getString(ARC_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.arc_type_fragment, container, false);
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
        List<ArcType> typeList = new ArrayList<>();
        mRecyclerView.setLayoutManager(new GridLayoutManager(mRecyclerView.getContext(), 3));
        mRecyclerView.addItemDecoration(new GridItemDecoration());
        mArcTypeAdapter = new ArcTypeAdapter(typeList);
        mRecyclerView.setAdapter(mArcTypeAdapter);
        mArcTypeAdapter.setOnItemListener(new ArcTypeAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, ArcType arcType) {
                onArcTypeChange(arcType);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
    }

    /**
     * 初始化数据，先从缓存拿，没有则网络请求
     */
    private void initData() {
        String arcTypeJson = SPUtil.build().getString(mArcType);
        if (TextUtils.isEmpty(arcTypeJson)) {
            getData();
        } else {
            List<ArcType> list = GsonUtil.toList(arcTypeJson, ArcType[].class);
            setData(list);
        }
    }

    /**
     * 获取网络数据
     */
    private void getData() {
        OkUtil.arc()
                .url(DDApi.typeName)
                .addParam("tempindex", mArcType)
                .execute(new ArcCallback<TypeResult>() {
                    @Override
                    public void onSuccess(TypeResult response) {
                        List<ArcType> result = response.getResult();
                        if (result != null) {
                            cacheData(result);
                            setData(result);
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
    private void setData(List<ArcType> list) {
        setRefreshFalse();
        mArcTypeAdapter.setData(list);
    }

    /**
     * 更新Adapter数据
     */
    private void updateData(List<ArcType> list) {
        setRefreshFalse();
        mArcTypeAdapter.updateData(list);
    }

    /**
     * 缓存数据
     */
    private void cacheData(List<ArcType> list) {
        String arcTypeJson = GsonUtil.toJson(list);
        SPUtil.build().putString(mArcType, arcTypeJson);
    }

    /**
     * 设置异常提示
     */
    private void setError() {
        setRefreshFalse();
        showToast("获取失败");
    }

    /**
     * 番剧类型改变
     *
     * @param arcType 番剧类型
     */
    private void onArcTypeChange(ArcType arcType) {
        if (mOnArcTypeChangeListener != null) {
            mOnArcTypeChangeListener.onArcTypeChange(arcType);
        }
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
}
