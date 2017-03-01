package com.lingci.common.util;

import android.content.Context;
import android.widget.Toast;

/**
 * 吐司辅助输出类
 *
 * @author wangqinghua
 */
public class ToastUtil {

    private static Toast toast;

    private ToastUtil() {
    }

    /**
     * 弹出单例吐司
     * @param content
     * @param info
     */
    public static void showSingleton(Context content, String info) {
        if (toast == null) {
            toast = Toast.makeText(content, info, Toast.LENGTH_SHORT);
        } else {
            toast.setText(info);
        }
        toast.show();
    }

    /**
     * 自定义内容的吐司
     *
     * @param content
     * @param info
     */
    public static void ToastTest(Context content, String info) {
        Toast.makeText(content, info, Toast.LENGTH_SHORT).show();
    }

    /**
     * 固定内容的吐司 测试时使用
     *
     * @param content
     */
    public static void ToastTest(Context content) {
        Toast.makeText(content, "drawn吐司测试", Toast.LENGTH_SHORT).show();
    }

}
