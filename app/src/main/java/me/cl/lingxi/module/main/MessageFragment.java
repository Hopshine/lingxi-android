package me.cl.lingxi.module.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.ViewPagerAdapter;

public class MessageFragment extends Fragment{

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private String[] tabNamArray = {"飞鸽传书", "留言"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }


    private void init() {
        final ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        addFragment(mPagerAdapter);
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setAdapter(mPagerAdapter);
                mTabLayout.setupWithViewPager(mViewPager);
            }
        });
    }

    private void addFragment(ViewPagerAdapter mPagerAdapter) {
        HomeFragment newsFragment = HomeFragment.newInstance("飞鸽传书功能准备当中\n" +
                "有好的建议可点击下方按钮↓");
        HomeFragment friendsFragment = HomeFragment.newInstance("留言功能也准备当中");
        mPagerAdapter.addFragment(newsFragment, tabNamArray[0]);
        mPagerAdapter.addFragment(friendsFragment, tabNamArray[1]);
    }

}
