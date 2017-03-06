package com.lingci.module.mood;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.lingci.R;
import com.lingci.module.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PublishActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setupToolbar(mToolbar, "发布新动态", true, 0, null);
    }
}
