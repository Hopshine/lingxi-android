package me.cl.lingxi.module.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import me.cl.lingxi.common.util.NetworkUtils;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.entity.AppVersion;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2017/10/26
 * desc   : 更新广播
 * version: 1.0
 */

public class UpdateReceiver extends BroadcastReceiver {

    public static final String UPDATE_ACTION = "LING_XI_UPDATE";
    private String title = "发现新版本";
    private String none = "已是最新版";

    private AppVersion mAppVersion;

    public UpdateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mAppVersion = (AppVersion) intent.getSerializableExtra("app_version");
        int versionCode = Utils.getAppVersionCode(context);
        if (versionCode >= mAppVersion.getVersionCode()) {
            Utils.toastShow(context, none);
        } else {
            // 判断网络状态
            if (NetworkUtils.isWifiConnected(context)) {
                showUpdate(context);
            } else {
                showHint(context);
            }
        }
    }

    // 网络提示
    private void showHint(final Context context) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(context);
        mDialog.setMessage("当前网络非WIFI状态，是否继续下载");
        mDialog.setNegativeButton("取消", null)
                .setPositiveButton("继续", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showUpdate(context);
            }
        }).setCancelable(false).create().show();
    }

    // 展示更新弹窗
    private void showUpdate(final Context context) {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(context);
        mDialog.setTitle(title);
        mDialog.setMessage(mAppVersion.getUpdateInfo());
        if (mAppVersion.getUpdateFlag() != 2) {
            mDialog.setNegativeButton("取消", null);
        }
        mDialog.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateApp(context);
            }
        }).setCancelable(false).create().show();
    }

    // 启动更新服务
    private void updateApp(Context context) {
        Intent mIntent = new Intent(context, UpdateService.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.putExtra("app_version", mAppVersion);
        context.startService(mIntent);
    }

}
