package com.lingci.common.config;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;

import java.io.InputStream;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.model.Conversation;
import okhttp3.OkHttpClient;

public class BaseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		initRongCloud();
		//为Glide加入okhttp支持
		Glide.get(this).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(new OkHttpClient()));
	}

	private void initRongCloud() {
		/**
		 * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
		 * io.rong.push 为融云 push 进程名称，不可修改。
		 */
		if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
				"io.rong.push".equals(getCurProcessName(getApplicationContext()))) {
			/**
			 * IMKit SDK调用第一步 初始化
			 */
			RongIM.init(this);
			//扩展功能自定义
			InputProvider.ExtendProvider[] provider = {
					new ImageInputProvider(RongContext.getInstance()),//图片
					new CameraInputProvider(RongContext.getInstance()),//相机
//                new LocationInputProvider(RongContext.getInstance()),//地理位置
			};
			RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, provider);
			RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.DISCUSSION, provider);
			RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.GROUP, provider);
			RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.CUSTOMER_SERVICE, provider);
			RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.CHATROOM, provider);
		}

	}

	/**
	 * 获得当前进程的名字
	 *
	 * @param context Context
	 * @return 进程号
	 */
	public static String getCurProcessName(Context context) {

		int pid = android.os.Process.myPid();

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
				.getRunningAppProcesses()) {

			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}
}
