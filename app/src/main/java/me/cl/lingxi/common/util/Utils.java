package me.cl.lingxi.common.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.cl.library.utils.ColorPhrase;
import me.cl.lingxi.entity.Like;

/**
 * 工具类
 * Created by bafsj on 17/3/1.
 */
public class Utils {

    private static Toast toast;
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    public static void showToast(final Context context, final String info) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                toastShow(context, info, Toast.LENGTH_SHORT);
            }
        });
    }

    public static void showToast(final Context context, final int infoId) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                toastShow(context, context.getResources().getString(infoId), Toast.LENGTH_SHORT);
            }
        });
    }

    @SuppressLint("ShowToast")
    private static void toastShow(Context context, String info, int duration) {
        if (toast == null) {
            toast = Toast.makeText(context, info, duration);
        } else {
            toast.setText(info);
        }
        toast.show();
    }

    public static int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 手机验证
     */
    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern.compile("1[34578]\\d{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 隐藏虚拟键盘
     */
    public static void HideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

        }
    }

    /**
     * 获取点赞人字符串
     */
    public static String getLikeStr(List<Like> likes) {
        String likeStr = "";
        for (int i = 0, size = likes.size(); i < size; i++) {
            if (i == 3) break;
            likeStr = likeStr + "{" + likes.get(i).getUsername() + "、}" + "";
        }
        if (likes.size() > 0) likeStr = likeStr.substring(0, likeStr.length() - 2) + "}";
        return likeStr;
    }

    /**
     * 获取所有点赞人字符串
     */
    public static String getLongLikeStr(List<Like> likes) {
        String likeStr = "";
        for (int i = 0; i < likes.size(); i++) {
            if (i == likes.size() - 1) {
                likeStr = likeStr + "{" + likes.get(i).getUsername() + "}";
            } else {
                likeStr = likeStr + "{" + likes.get(i).getUsername() + "、}";
            }
        }
        return likeStr;
    }

    /**
     * 突出颜色
     */
    public static CharSequence colorFormat(String str) {
        return ColorPhrase.from(str).withSeparator("{}").innerColor(0xFF4FC1E9).outerColor(0xFF666666).format();
    }

    /**
     * 获取Manifest中meta值
     */
    public static String getMetaValue(Context context, String metaKey) {
        if (context == null || TextUtils.isEmpty(metaKey)) {
            return null;
        }
        String metaValue = null;
        try {
            ApplicationInfo app = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle metaData = null;
            if (null != app) {
                metaData = app.metaData;
            }
            if (null != metaData) {
                metaValue = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return metaValue;
    }

    // 获取版本名称
    public static String getAppVersionName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi == null ? null : pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 获取版本号
    public static int getAppVersionCode(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi == null ? -1 : pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean wpaQQ(Context context, String key) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=" + key));
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    /**
     * 发起添加群流程。群号：大龄儿童二次元同好群(468620613) 的 key 为： U6BT7JHlX9bzMdCNWjkIjwu5g3Yt_Wi9
     * 调用 joinQQGroup(U6BT7JHlX9bzMdCNWjkIjwu5g3Yt_Wi9) 即可发起手Q客户端申请加群 大龄儿童二次元同好群(468620613)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     */
    public static boolean joinQQGroup(Context context, String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }
}
