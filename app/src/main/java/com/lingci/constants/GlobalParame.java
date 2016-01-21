package com.lingci.constants;

import java.io.File;

import com.lingci.R;
import com.lingci.utils.MD5Util;
import com.lingci.views.RoundImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class GlobalParame {

	public static String URl = "http://103.44.145.243:34922/lingci";
	public static Activity main;
	
	public static boolean isRead = true;
	
	public static final String UPDATE_USERIMG = "com.lingci.updateimg";
	
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
				ImageLoader.getInstance().displayImage(url,imgView, getOptionsUnDisc());
			}
		}
	}
	
}
