package me.cl.lingxi.common.config;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.model.Conversation;
import okhttp3.OkHttpClient;

public class Aplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		initRongCloud();

		initOkGo();

		// 为Glide加入okhttp支持
		Glide.get(this).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(new OkHttpClient()));
	}

	// 注册OkGo
	private void initOkGo() {
		// 构建OkHttpClient.Builder
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		// 配置log
		HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
		// log打印级别，决定了log显示的详细程度
		loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
		// log颜色级别，决定了log在控制台显示的颜色
		loggingInterceptor.setColorLevel(Level.INFO);
		builder.addInterceptor(loggingInterceptor);

		// 全局的读取超时时间
		builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
		// 全局的写入超时时间
		builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
		// 全局的连接超时时间
		builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);

		// 实际的OkGo配置
		HttpHeaders headers = new HttpHeaders();
		headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文，不允许有特殊字符
		HttpParams params = new HttpParams();
		params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
		OkGo.getInstance().init(this)                       //必须调用初始化
				.setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置将使用默认的
				.setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
				.setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
				.setRetryCount(3)                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
				.addCommonHeaders(headers)                      //全局公共头
				.addCommonParams(params);                       //全局公共参数
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
			// 扩展功能自定义
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
