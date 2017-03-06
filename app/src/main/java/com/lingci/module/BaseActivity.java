package com.lingci.module;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lingci.R;

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "lc";

    /**
     * setupToolbar
     * @param toolbar Toolbar
     * @param titleId TitleId
     * @param isBack 是否添加返回
     * @param menuId MenuId
     * @param listener Menu监听
     */
    public void setupToolbar(@NonNull Toolbar toolbar, @StringRes int titleId, boolean isBack, int menuId, Toolbar.OnMenuItemClickListener listener) {
        setupToolbar(toolbar, getString(titleId), isBack, menuId, listener);
    }

    /**
     * setupToolbar
     * @param toolbar Toolbar
     * @param title Title
     * @param isBack 是否添加返回
     * @param menuId MenuId
     * @param listener Menu监听
     */
    public void setupToolbar(@NonNull Toolbar toolbar, @NonNull String title, boolean isBack, int menuId, Toolbar.OnMenuItemClickListener listener) {
        toolbar.setTitle(title);
        toolbar.setTitleTextAppearance(this, R.style.AppTextAppearance);
        if (isBack) {
            toolbar.setNavigationIcon(R.mipmap.navigate);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        if (listener != null) {
            toolbar.inflateMenu(menuId);
            toolbar.setOnMenuItemClickListener(listener);
        }
    }
}
