package me.cl.lingxi.module.setting;

import android.os.Bundle;
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
        PublicLicenseAdapter mAdapter = new PublicLicenseAdapter(this, mData);
        mRecyclerView.setAdapter(mAdapter);
    }


    private void getData() {
        mData.add(new PublicLicense("okhttp", "square", "An HTTP+HTTP/2 client for Android and Java applications.", "https://github.com/square/okhttp"));
        mData.add(new PublicLicense("okhttputils", "zhy", "", ""));
        mData.add(new PublicLicense("gson", "google", "", ""));
        mData.add(new PublicLicense("glide", "bumptech", "", ""));
        mData.add(new PublicLicense("glide-transformations", "wasabeef", "", ""));
        mData.add(new PublicLicense("bottom-navigation-bar", "ashokvarma", "", ""));
    }
}
