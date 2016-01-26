package com.lingci.constants;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;

import com.lingci.R;
import com.lingci.utils.MD5Util;
import com.lingci.views.RoundImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.UserInfo;

public class GlobalParame {

	public static String URl = "http://103.44.145.243:34922/lingci";
	public static Activity main;
	
	public static boolean isRead = true;
	
	public static final String UPDATE_USERIMG = "com.lingci.updateimg";

	public static List<UserInfo> userList = new ArrayList<UserInfo>();
	public static List<String> uidList = new ArrayList<String>();
	
	public static DisplayImageOptions getOptions(){
		DisplayImageOptions options = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.userimg)
        .showImageForEmptyUri(R.drawable.userimg)
        .showImageOnFail(R.drawable.userimg)
        .cacheInMemory(true)//内存
        .cacheOnDisc(true)//磁盘
        .build();
		return options;
	}
	
	public static DisplayImageOptions getOptionsUnDisc(){
		DisplayImageOptions options = new DisplayImageOptions.Builder()  
        .showImageOnLoading(R.drawable.userimg)
        .showImageForEmptyUri(R.drawable.userimg)
        .showImageOnFail(R.drawable.userimg)
        .cacheInMemory(true)//内存
        //.cacheOnDisc(true)//磁盘
        .build();
		return options;
	}

	/** 设置头像 */
	public static void setPersonImg(String uname, RoundImageView imgView) {
		// TODO Auto-generated method stub
		String pathName = Environment.getExternalStorageDirectory() + "/lingci/image/avatar/" + "headportraits.png";
		if (new File(pathName).exists()) {
			Bitmap btm = BitmapFactory.decodeFile(pathName);
			imgView.setImageBitmap(btm);
		}else{
			String url = URl+"/image/avatar/at_"+MD5Util.MD5(uname)+".jpg";
			if (url!=null) {
				ImageLoader.getInstance().displayImage(url,imgView, getOptions());
			}
		}
	}

	/** 获取Manifest中meta值 */
	public static String getMetaValue(Context context, String metaKey) {
		Bundle metaData = null;
		String metaValue = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				metaValue = metaData.getString(metaKey);
			}
		} catch (PackageManager.NameNotFoundException e) {
		}
		return metaValue;
	}
	
}
