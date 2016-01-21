package com.lingci.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 
 * @author wangqinghua 常用方法
 */
public class RepeatUtils {

	// 隐藏虚拟键盘
	public static void HideKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

		}
	}

	/**
	 * 以最省内存的方式读取本地资源的图片
	 * @param context
	 * @param resId
	 * @return
	 * 
	 */
	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * 手机验证
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNum(String mobiles) {

		Pattern p = Pattern.compile("1[34578]\\d{9}$");
		Matcher m = p.matcher(mobiles);
		System.out.println(m.matches() + "---");
		return m.matches();
	}

	/**
	 * 返回按钮
	 */
	public static void myFinish(LinearLayout ll_ruturn, final Activity activity) {
		ll_ruturn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.finish();
			}
		});
	}

	/**
	 * 解决频繁点击
	 * 
	 * @param context
	 * @param lastClick
	 */
	public static boolean isRepetition(Context context, long lastClick) {
		if (System.currentTimeMillis() - lastClick <= 1000) {
			Toast.makeText(context, "请不要频繁点击", 0).show();
			;
			return false;
		}
		lastClick = System.currentTimeMillis();
		return true;
	}

	/**
	 * 切换碎片
	 * 
	 * @param activity 上下文
	 * @param fragmentConttanier 装碎片的容器
	 * @param fragment 替换的容器
	 * @param toBackStack 是否加入回退栈
	 */
	public static void changfragment(Activity activity, int fragmentConttanier,
			Fragment fragment, Boolean toBackStack) {
		FragmentTransaction transaction = activity.getFragmentManager()
				.beginTransaction();
		transaction.replace(fragmentConttanier, fragment);
		if (toBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	/**
	 * 添加一个碎片
	 * 
	 * @param activity 上下文
	 * @param fragmentConttanier 装碎片的容器
	 * @param fragment 替换的容器
	 */
	public static void addFragment(Activity activity, int fragmentConttanier,
			Fragment fragment) {
		FragmentTransaction transaction = activity.getFragmentManager()
				.beginTransaction();
		transaction.add(fragmentConttanier, fragment);
		transaction.commit();
	}

	/**
	 * 文件路径转String
	 * 
	 * @param filePath
	 * @return
	 */
	public static String filebyString(String filePath) {
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}

	/**
	 * 根据值拿到对应的key
	 * 
	 * @param data map数据
	 * @param vlaue vlaue的值
	 * @return 成功 返回 key 失败 返回 -1
	 */
	public static int vlaueToGetKey(Map<Integer, String> data, String vlaue) {
		Set<Entry<Integer, String>> entries = data.entrySet();
		for (Entry<Integer, String> entry : entries) {
			if (entry.getValue().equals(vlaue)) {
				return entry.getKey();
			}
		}
		return -1;
	}

	/**
	 * 判断一个路径是否为图片
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isImage(String path) {
		boolean flag = false;
		Bitmap drawable = BitmapFactory.decodeFile(path);
		if (drawable == null) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 给key值是int,value的map按小到大排序
	 * 
	 * @param map 需要排序的map
	 * @param values 容器
	 * @return 排序后的ArrayList value集合
	 */
	public static ArrayList<String> getSortMap(Map<Integer, String> map,
			ArrayList<String> values) {
		Set<Entry<Integer, String>> set = map.entrySet();
		int[] keys = new int[map.size()];
		int z = 0;
		for (Entry<Integer, String> entry : set) {
			keys[z++] = entry.getKey();
		}
		for (int i = 0; i < keys.length; i++) {
			for (int j = i + 1; j < keys.length; j++) {
				if (keys[i] > keys[j]) {
					int b = keys[i];
					keys[i] = keys[j];
					keys[j] = b;
				}
			}
		}
		for (int i = 0; i < keys.length; i++) {
			values.add(map.get(keys[i]));
		}
		return values;
	}

}
