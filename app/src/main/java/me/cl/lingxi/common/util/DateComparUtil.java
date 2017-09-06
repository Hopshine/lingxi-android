package me.cl.lingxi.common.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间换算类
 *
 */
public class DateComparUtil {
	
	private static String[] units = { "", "十", "百", "千", "万", "十万", "百万", "千万", "亿",
		"十亿", "百亿", "千亿", "万亿" };
	private static char[] numArray = { '零', '一', '二', '三', '四', '五', '六', '七', '八', '九' };

	/**
	 * 获得某个时间与当前时间的时间差
	 * @param time
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getInterval(String time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfday = new SimpleDateFormat("yyyy-MM-dd");
		Date date= new Date();
		String curTime = sdf.format(date);
		Date before = null;
		Date daytime = null;
		Date beforeday = null;
		try {
			before = (Date) sdf.parse(time);
			daytime = (Date) sdfday.parse(sdfday.format(date));
			beforeday = (Date) sdfday.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (date.before(before)) {
			return time.substring(0, time.length() - 2);
		}
		long between = (date.getTime() - before.getTime()) / 1000;
		long daybetween = (daytime.getTime() - beforeday.getTime()) / 1000;
		int curYear = Integer.parseInt(curTime.substring(0, 4));
		int year = Integer.parseInt(time.substring(0, 4));
		int day = (int) (daybetween / (24 * 3600));
		int hour = (int) (between % (24 * 3600) / 3600);
		int minute = (int) (between % 3600 / 60);
		int second = (int) (between % 60 / 60);
		int length = time.length();
		System.out.println(day);
		switch (day) {
		case 0:
			switch (hour) {
			case 0:
				switch (minute) {
				case 0:
					return second + "秒前";
				default:
					return minute + "分钟前";
				}
			case 1:
			case 2:
			case 3:
				return hour+"小时前";
			default:
				return "今天"+time.substring(11, length - 5);
			}
		case 1:
			return "昨天"+time.substring(11, length - 5);
		case 2:
			return "前天"+time.substring(11, length - 5);
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
			return day + "天前"+time.substring(11, length - 5);
		default:
			if (curYear == year) {
				return time.substring(5, length - 5);
			} else {
				return time.substring(0, length - 5);
			}
		}
	}
	
	/**
	 * 将阿拉伯数字转为汉字数
	 * 当前时差可能存在负值，当前方法不可用
	 * @param num
	 * @return
	 */
	private static String foematInteger(int num) {
		char[] val = String.valueOf(num).toCharArray();
		int len = val.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			String m = val[i] + "";
			int n = Integer.valueOf(m);
			boolean isZero = n == 0;
			String unit = units[(len - 1) - i];
			if (isZero) {
				if ('0' == val[i - 1]) {
					// not need process if the last digital bits is 0
					continue;
				} else {
					// no unit for 0
					sb.append(numArray[n]);
				}
			} else {
				sb.append(numArray[n]);
				sb.append(unit);
			}
		}
		return sb.toString();
	}

	public static String getDuration(String time){
		String dayStr = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date= new Date();
		String curTime = sdf.format(date);
		try {
			Date before = (Date) sdf.parse(time);
			Date current = (Date) sdf.parse(curTime);
			long daybetween = (current.getTime() - before.getTime()) / 1000;
			int day= (int) (daybetween / (24 * 3600));
			dayStr =String.valueOf(day);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dayStr;
	}
}
