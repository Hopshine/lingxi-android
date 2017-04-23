package com.lingci.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lingci.R;
import com.lingci.adapter.DliAnimationAdapter;
import com.lingci.common.config.Constants;
import com.lingci.common.util.GsonUtil;
import com.lingci.common.util.SPUtils;
import com.lingci.entity.Animation;
import com.lingci.entity.DliAnimation;
import com.lingci.module.BaseFragment;
import com.lingci.module.WebActivity;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dili, container, false);
        ButterKnife.bind(this, rootView);
        init();
        return rootView;
    }

    private void init() {
        initRecyclerView();
//        SPUtils.getInstance(getContext()).putBoolean(Constants.DILI_CACHE, false);
        if (SPUtils.getInstance(getContext()).getBoolean(Constants.DILI_CACHE)) {
            mAdapter.setData(getData());
        } else {
            analysisDli();
        }
    }

    private void analysisDli() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Elements els = Jsoup.connect("http://www.dilidili.wang/anime/201704/").timeout(5000).get().select(".anime_list");
                    for (Element el : els) {
                        DliAnimation mDliAnimation = new DliAnimation();
                        List<Animation> animationList = new ArrayList<>();
                        String title = el.getElementsByTag("h2").text();
                        mDliAnimation.setCategory(title);
                        Elements emt = el.getElementsByTag("dl");
                        for (Element e : emt) {
                            Animation mAnimation = new Animation();
                            String name = e.getElementsByTag("a").text();
                            String url = "http://m.dilidili.wang" + e.getElementsByTag("a").attr("href").trim();
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
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            saveData(mDliAnimationList);
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
        setupToolbar(mToolbar, "201704番剧", 0, null);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                mSwipeRefresh.setRefreshing(false);
            }
        });
        mAdapter = new DliAnimationAdapter(getActivity(), this.mDliAnimationList);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, 1));
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new DliAnimationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View paramView, Animation animation) {
                gotoWeb(animation);
            }
        });
    }

    private void saveData(List<DliAnimation> data) {
        String json = GsonUtil.toJson(data);
        SPUtils.getInstance(getContext()).putBoolean(Constants.DILI_CACHE, true);
        SPUtils.getInstance(getContext()).putString(Constants.DILI_ANIMATE, json);
    }

    private List<DliAnimation> getData() {
        String json = SPUtils.getInstance(getContext()).getString(Constants.DILI_ANIMATE);
        return GsonUtil.toList(json, DliAnimation[].class);
    }

    private void gotoWeb(Animation animation) {
        Intent intent = new Intent(getActivity(), WebActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("tittle", animation.getName());
        bundle.putString("url", animation.getLink());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void getVideo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Element doc = Jsoup.connect("http://m.dilidili.wang/watch3/56212/").get();
                    Log.d(TAG, "run: " + doc.html().trim());
//                    String video = doc.getElementById("videoPlayer").attr("src").trim();
//                    Log.d(TAG, "run: " + video);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
