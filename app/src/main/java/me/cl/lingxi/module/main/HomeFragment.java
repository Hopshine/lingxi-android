package me.cl.lingxi.module.main;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.cl.lingxi.R;
import me.cl.lingxi.module.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends BaseFragment {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        setupToolbar(mToolbar, "普通的首页", 0, null);

    }

}
