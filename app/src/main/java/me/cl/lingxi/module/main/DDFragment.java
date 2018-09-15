package me.cl.lingxi.module.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.library.base.BaseFragment;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.ViewPagerAdapter;
import me.cl.lingxi.entity.dd.ArcType;
import me.cl.lingxi.module.dd.ArcListFragment;
import me.cl.lingxi.module.dd.ArcTypeFragment;
import me.cl.lingxi.module.dd.OnArcTypeChangeListener;

/**
 * 嘀哩嘀哩动画appApi版本
 */
public class DDFragment extends BaseFragment implements OnArcTypeChangeListener {

    private static final String ARC_TYPE = "type";
    private static final int ARC_LIST_INDEX = 1;

    private String mType;

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private ViewPagerAdapter mPagerAdapter;
    private String[] tabNamArray = {"季度列表", "番剧列表", "类型列表"};

    public DDFragment() {

    }

    public static DDFragment newInstance(String type) {
        DDFragment fragment = new DDFragment();
        Bundle args = new Bundle();
        args.putString(ARC_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getString(ARC_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dd_fragment, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        mPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        addFragment(mPagerAdapter);
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setAdapter(mPagerAdapter);
                mTabLayout.setupWithViewPager(mViewPager);
                mViewPager.setCurrentItem(ARC_LIST_INDEX);
            }
        });
    }

    /**
     * 番剧类型选择
     */
    @Override
    public void onArcTypeChange(ArcType arcType) {
        mViewPager.setCurrentItem(ARC_LIST_INDEX);
        ArcListFragment fragment = (ArcListFragment) mPagerAdapter.getItem(ARC_LIST_INDEX);
        fragment.onArcTypeChange(arcType);
    }

    /**
     * 添加Fragment
     */
    private void addFragment(ViewPagerAdapter mPagerAdapter) {
        ArcTypeFragment riQiFragment = ArcTypeFragment.newInstance(ArcTypeFragment.RI_QI);
        ArcTypeFragment ifyFragment = ArcTypeFragment.newInstance(ArcTypeFragment.IFY);
        ArcListFragment newFragment = ArcListFragment.newInstance(ArcListFragment.DEFAULT);
        mPagerAdapter.addFragment(riQiFragment, tabNamArray[0]);
        mPagerAdapter.addFragment(newFragment, tabNamArray[1]);
        mPagerAdapter.addFragment(ifyFragment, tabNamArray[2]);

        // 绑定番剧类型改变事件
        riQiFragment.setOnArcTypeChangeListener(this);
        ifyFragment.setOnArcTypeChangeListener(this);
    }
}
