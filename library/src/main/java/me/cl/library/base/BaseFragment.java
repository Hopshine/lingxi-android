package me.cl.library.base;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import me.cl.library.util.ToastUtil;
import me.cl.library.view.MoeToast;

public class BaseFragment extends Fragment {

    public void showToast(@StringRes int msgId) {
        ToastUtil.showToast(getContext(), msgId);
    }

    public void showToast(String msg) {
        ToastUtil.showToast(getContext(), msg);
    }

    public void showMoeToast(@StringRes int msgId) {
        MoeToast.makeText(getContext(), msgId);
    }

    public void showMoeToast(String msg) {
        MoeToast.makeText(getContext(), msg);
    }

}
