package me.cl.lingxi.module;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;

import java.io.InputStream;

import io.rong.imkit.RongIM;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.util.SPUtil;
import okhttp3.OkHttpClient;

public class LxApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		initRongCloud();

		// 不一定要注册
		OkUtil.newInstance().init(this);
		// 一定要注册
		SPUtil.newInstance().init(this);

	}

	private void initRongCloud() {
		// OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
		// io.rong.push 为融云 push 进程名称，不可修改。
		if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
				"io.rong.push".equals(getCurProcessName(getApplicationContext()))) {
			// IMKit SDK调用第一步 初始化
			RongIM.init(this);
		}

	}

	/**
	 * 获得当前进程的名字
	 */
	public static String getCurProcessName(Context context) {

		int pid = android.os.Process.myPid();

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		if (activityManager != null) {
			for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                    .getRunningAppProcesses()) {

                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
		}
		return null;
	}
}
