package com.lingci.module.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lingci.R;
import com.lingci.adapter.DliAnimationAdapter;
import com.lingci.entity.Animation;
import com.lingci.entity.DliAnimation;
import com.lingci.module.BaseFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DliFragment extends BaseFragment {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;

    private DliAnimationAdapter mAdapter;
    private List<DliAnimation> mDliAnimationList = new ArrayList();

    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dili, container, false);
        ButterKnife.bind(this, rootView);
        initRecyclerView();
        analysisDli();
        return rootView;
    }

    private void analysisDli() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Elements els = Jsoup.connect("http://www.dilidili.wang/anime/201701/").timeout(5000).get().select(".anime_list");
                    for (Element el : els) {
                        DliAnimation mDliAnimation = new DliAnimation();
                        List<Animation> animationList = new ArrayList<>();
                        String title = el.getElementsByTag("h2").text();
                        mDliAnimation.setCategory(title);
                        Elements emt = el.getElementsByTag("dl");
                        for (Element e : emt) {
                            Animation mAnimation = new Animation();
                            String name = e.getElementsByTag("a").text();
                            String url = "http://www.dilidili.wang" + e.getElementsByTag("a").attr("href").trim();
                            String img = e.getElementsByTag("img").attr("src").trim();
                            mAnimation.setName(name);
                            mAnimation.setLink(url);
                            mAnimation.setImgUrl(img);
                            Elements infoList = e.getElementsByClass("d_label");
                            for (Element ee : infoList) {
                                String aa = ee.getElementsByTag("b").text();
                                switch (aa) {
                                    case "地区：":
                                        mAnimation.setArea(ee.text());
                                        break;
                                    case "年代：":
                                        mAnimation.setYear(ee.text());
                                        break;
                                    case "标签：":
                                        mAnimation.setType(ee.text());
                                        break;
                                    case "播放：":
                                        mAnimation.setHook(ee.text());
                                        break;
                                }
                            }
                            Elements infos = e.getElementsByTag("p");
                            for (Element ee : infos) {
                                String bb = ee.getElementsByTag("b").text();
                                switch (bb) {
                                    case "看点：":
                                        mAnimation.setBroadcast(ee.text());
                                        break;
                                    case "简介：":
                                        mAnimation.setIntroduction(ee.text());
                                        break;
                                    case "状态: ":
                                        mAnimation.setState(ee.text());
                                        break;
                                }
                            }
                            animationList.add(mAnimation);
                        }
                        mDliAnimation.setAnimationList(animationList);
                        mDliAnimationList.add(mDliAnimation);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.setData(mDliAnimationList);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initRecyclerView() {
        setupToolbar(mToolbar, "201701番剧", 0, null);
        handler = new Handler();
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                mSwipeRefresh.setRefreshing(false);
            }
        });
        mAdapter = new DliAnimationAdapter(getActivity(), this.mDliAnimationList);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, 1));
        mRecyclerView.setAdapter(mAdapter);
    }
}
