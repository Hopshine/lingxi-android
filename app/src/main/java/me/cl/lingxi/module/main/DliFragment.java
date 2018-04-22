package me.cl.lingxi.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.cl.library.base.BaseFragment;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.DliAnimationAdapter;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.util.GsonUtil;
import me.cl.lingxi.common.util.SPUtils;
import me.cl.lingxi.entity.Animation;
import me.cl.lingxi.entity.DliAnimation;
import me.cl.lingxi.module.WebActivity;

/**
 * 番剧信息
 */
public class DliFragment extends BaseFragment {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.animate_select)
    TextView mAnimateSelect;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefresh;

    private String baseAnimateUrl = "http://www.dilidili.wang/anime/";
    private String[] selectAnimate = {"201804", "201801", "201710", "201707", "201704", "201701"};
    private String animateUrl = baseAnimateUrl + selectAnimate[0];
    private int mSelect = 0;
    private ListPopupWindow mPopupWindow;

    private DliAnimationAdapter mAdapter;
    private List<DliAnimation> mDliAnimationList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dili, container, false);
        ButterKnife.bind(this, rootView);
        init();
        return rootView;
    }

    private void init() {
        setupToolbar(mToolbar, "番剧", 0, null);
        initAnimateSelect();
        initRecyclerView();
        if (SPUtils.getInstance(getContext()).getBoolean(Constants.ANIMATE_CACHE)) {
            mAdapter.setData(getData());
        } else {
            analysisDli();
        }
    }

    @OnClick({R.id.animate_select})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.animate_select:
                mPopupWindow.show();
                break;
        }
    }

    private void initAnimateSelect() {
        mSelect = SPUtils.getInstance(getContext()).getInt(Constants.ANIMATE_SELECT, 0);
        mAnimateSelect.setText(selectAnimate[mSelect]);
        mPopupWindow = new ListPopupWindow(getActivity());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, selectAnimate);
        mPopupWindow.setAdapter(adapter);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setModal(true);
        mPopupWindow.setDropDownGravity(Gravity.END);
        mPopupWindow.setAnchorView(mAnimateSelect);
        mPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAnimateSelect.setText(selectAnimate[position]);
                animateUrl = baseAnimateUrl + selectAnimate[position];
                SPUtils.getInstance(getContext()).putInt(Constants.ANIMATE_SELECT, position);
                cleanData();
                analysisDli();
                mPopupWindow.dismiss();
            }
        });
    }

    private void initRecyclerView() {
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                cleanData();
                analysisDli();
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

    // 解析网页获取番剧信息
    private void analysisDli() {
        if (!mSwipeRefresh.isRefreshing())
            mSwipeRefresh.setRefreshing(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Elements els = Jsoup.connect(animateUrl).timeout(5000).get().select(".anime_list");
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
                            if (mSwipeRefresh.isRefreshing())
                            mSwipeRefresh.setRefreshing(false);
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

    // 保存番剧信息
    private void saveData(List<DliAnimation> data) {
        String json = GsonUtil.toJson(data);
        SPUtils.getInstance(getContext()).putBoolean(Constants.ANIMATE_CACHE, true);
        SPUtils.getInstance(getContext()).putString(Constants.ANIMATE_JSON, json);
    }

    // 获取番剧信息
    private List<DliAnimation> getData() {
        String json = SPUtils.getInstance(getContext()).getString(Constants.ANIMATE_JSON);
        return GsonUtil.toList(json, DliAnimation[].class);
    }

    // 清除保存信息
    private void cleanData() {
        mDliAnimationList.clear();
        SPUtils.getInstance(getContext()).putString(Constants.ANIMATE_JSON, "{}");
        SPUtils.getInstance(getContext()).putBoolean(Constants.ANIMATE_CACHE, false);
    }

    // 前往web页
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
