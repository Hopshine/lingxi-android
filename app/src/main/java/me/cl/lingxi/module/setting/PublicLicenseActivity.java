package me.cl.lingxi.module.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.library.base.BaseActivity;
import me.cl.library.util.ToolbarUtil;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.PublicLicenseAdapter;
import me.cl.lingxi.entity.PublicLicense;
import me.cl.lingxi.module.webview.WebActivity;

public class PublicLicenseActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private List<PublicLicense> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_license_activity);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        ToolbarUtil.init(mToolbar, this)
                .setTitle(R.string.title_bar_public_license)
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();

        getData();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        PublicLicenseAdapter mAdapter = new PublicLicenseAdapter(this, mData);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemListener(new PublicLicenseAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, PublicLicense license) {
                gotoWeb(license.getName(), license.getUrl());
            }
        });
    }

    /**
     * 初始化数据
     */
    private void getData() {
        mData.add(new PublicLicense("okhttp", "square",
                "An HTTP+HTTP/2 client for Android and Java applications.",
                "https://github.com/square/okhttp"));
        mData.add(new PublicLicense("gson", "google",
                "A Java serialization/deserialization library to convert Java Objects into JSON and back",
                "https://github.com/google/gson"));
        mData.add(new PublicLicense("butterknife", "JakeWharton",
                "Bind Android views and callbacks to fields and methods",
                "https://github.com/JakeWharton/butterknife"));
        mData.add(new PublicLicense("glide", "bumptech",
                "An image loading and caching library for Android focused on smooth scrolling",
                "https://github.com/bumptech/glide"));
        mData.add(new PublicLicense("glide-transformations", "wasabeef",
                "An Android transformation library providing a variety of image transformations for Glide.",
                "https://github.com/wasabeef/glide-transformations"));
        mData.add(new PublicLicense("Compressor", "zetbaitsu",
                "An android image compression library.",
                "https://github.com/zetbaitsu/Compressor"));
        mData.add(new PublicLicense("PhotoPicker", "donglua",
                "Image Picker like Wechat",
                "https://github.com/donglua/PhotoPicker"));
        mData.add(new PublicLicense("jsoup", "jhy",
                "jsoup: Java HTML Parser, with best of DOM, CSS, and jquery",
                "https://github.com/jhy/jsoup/"));
    }

    // 前往web页
    private void gotoWeb(String tittle, String url) {
        Intent intent = new Intent(this, WebActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("tittle",tittle);
        bundle.putString("url", url);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
