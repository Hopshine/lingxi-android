package com.lingci.module;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.lingci.R;

public class BaseFragment extends Fragment {

    public static final String TAG = "lcDev";

    /**
     * setupToolbar
     * @param toolbar Toolbar
     * @param titleId TitleId
     * @param menuId MenuId
     * @param listener Menu监听
     */
    public void setupToolbar(@NonNull Toolbar toolbar, @StringRes int titleId, int menuId, Toolbar.OnMenuItemClickListener listener) {
        setupToolbar(toolbar, getActivity().getString(titleId), menuId, listener);
    }

    /**
     * setupToolbar
     * @param toolbar Toolbar
     * @param title Title
     * @param menuId MenuId
     * @param listener Menu监听
     */
    public void setupToolbar(@NonNull Toolbar toolbar, @NonNull String title , int menuId, Toolbar.OnMenuItemClickListener listener) {
        toolbar.setTitle(title);
        toolbar.setTitleTextAppearance(getActivity(), R.style.AppTextAppearance);
        if (listener != null) {
            toolbar.inflateMenu(menuId);
            toolbar.setOnMenuItemClickListener(listener);
        }
    }
}
