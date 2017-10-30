package me.cl.lingxi.module.setting;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.PublicLicenseAdapter;
import me.cl.lingxi.entity.PublicLicense;
import me.cl.lingxi.module.BaseActivity;

public class PublicLicenseActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private List<PublicLicense> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_license);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setupToolbar(mToolbar, "开源相关", true, 0, null);

        getData();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        PublicLicenseAdapter mAdapter = new PublicLicenseAdapter(this, mData);
        mRecyclerView.setAdapter(mAdapter);
    }


    private void getData() {
        mData.add(new PublicLicense("okhttp", "square", "An HTTP+HTTP/2 client for Android and Java applications.", "https://github.com/square/okhttp"));
        mData.add(new PublicLicense("okgo", "jeasonlzy", "OkGo - 3.0 震撼来袭，该库是基于 Http 协议，封装了 OkHttp 的网络请求框架，比 Retrofit 更简单易用，支持 RxJava，RxJava2，支持自定义缓存，支持批量断点下载管理和批量上传管理功能", "https://github.com/jeasonlzy/okhttp-OkGo"));
        mData.add(new PublicLicense("gson", "google", "A Java serialization/deserialization library to convert Java Objects into JSON and back", "https://github.com/google/gson"));
        mData.add(new PublicLicense("glide", "bumptech", "An image loading and caching library for Android focused on smooth scrolling", "https://github.com/bumptech/glide"));
        mData.add(new PublicLicense("glide-transformations", "wasabeef", "An Android transformation library providing a variety of image transformations for Glide.", "https://github.com/wasabeef/glide-transformations"));
        mData.add(new PublicLicense("bottom-navigation-bar", "ashokvarma", "This Library helps users to use Bottom Navigation Bar (A new pattern from google) with ease and allows ton of customizations", "https://github.com/Ashok-Varma/BottomNavigation"));
    }
}
