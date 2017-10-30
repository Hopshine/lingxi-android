package me.cl.lingxi.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间换算类
 */
public class DateUtil {

    /**
     * 显示时间，如果与当前时间差别小于一天，则自动用**秒(分，小时)前，
     * 如果大于一天则用format规定的格式显示
     *
     * @param date 时间
     * @return 处理得到的时间字符串
     */
    public static String showTime(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

        long nowYearLong = 0;
        try {
            nowYearLong = sdf.parse(sdf.format(new Date())).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date cTime = null;
        try {
            cTime = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String r = "未知";
        if (cTime == null) return r;

        long nowTimeLong = System.currentTimeMillis();
        long cTimeLong = cTime.getTime();
        long result = Math.abs(nowTimeLong - cTimeLong);

        if (result < 60000) {
            long seconds = result / 1000;
            if (seconds == 0) {
                r = "刚刚";
            } else {
                r = seconds + "秒前";
            }
        } else if (result >= 60000 && result < 3600000) {
            long seconds = result / 60000;
            r = seconds + "分前";
        } else if (result >= 3600000 && result < 86400000) {
            long seconds = result / 3600000;
            r = seconds + "时前";
        } else if (result >= 86400000 && result < 1702967296) {
            long seconds = result / 86400000;
            r = seconds + "天前";
        } else if (nowYearLong < cTimeLong){
            sdf = new SimpleDateFormat("MM-dd hh:mm");
            r = sdf.format(cTime).toString();
        } else {
            r = sdf.format(cTime).toString();
        }
        return r;
    }
}
