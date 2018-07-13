package me.cl.lingxi.module;

import android.app.Application;

import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.util.SPUtil;

public class LxApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// 不一定要注册
		OkUtil.newInstance().init(this);
		// 一定要注册
		SPUtil.newInstance().init(this);
	}

}
