package com.lingci.globals;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.model.Conversation;

public class BaseApplication extends Application {
	
	@Override
	public void onCreate() {
		initImageLoader();
		initRongCloud();
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

	private void initImageLoader() {
		//创建默认的ImageLoader配置参数
		ImageLoaderConfiguration configuration = ImageLoaderConfiguration
				.createDefault(this);
		//Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(getConfiguration());
	}

	private ImageLoaderConfiguration getConfiguration(){

	    File cacheDir =StorageUtils.getOwnCacheDirectory(this, "lingci/image/Cache");  
	    ImageLoaderConfiguration fig = new ImageLoaderConfiguration   
	              .Builder(this)   
	              .memoryCacheExtraOptions(480, 800) // maxwidth, max height，即保存的每个缓存文件的最大长宽   
	              .threadPoolSize(3)//线程池内加载的数量   
	              .threadPriority(Thread.NORM_PRIORITY -2)   
	              .denyCacheImageMultipleSizesInMemory()   
	               .memoryCache(new UsingFreqLimitedMemoryCache(2* 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现   
	               .memoryCacheSize(2 * 1024 * 1024)     
	              .discCacheSize(50 * 1024 * 1024)     
	              .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密   
	               .tasksProcessingOrder(QueueProcessingType.LIFO)   
	               .discCacheFileCount(100) //缓存的文件数量   
	               .discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径   
	               .defaultDisplayImageOptions(DisplayImageOptions.createSimple())   
	               .imageDownloader(new BaseImageDownloader(this,5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间   
	               .writeDebugLogs() // Remove for releaseapp   
	              .build();//开始构建   
		return fig;

	}

	/**
	 * 获得当前进程的名字
	 *
	 * @param context
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
