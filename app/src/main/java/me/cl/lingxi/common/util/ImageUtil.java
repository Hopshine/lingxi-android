package me.cl.lingxi.common.util;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/05/26
 * desc   : 图片工具类，压缩使用https://github.com/zetbaitsu/Compressor
 * version: 1.0
 */
public class ImageUtil {

    /**
     * 路径转图片文件
     */
    public static List<File> pathToImageFile(List<String> filePaths) {
        List<File> files = new ArrayList<>();
        if (filePaths == null || filePaths.size() == 0) return files;

        for (String filePath : filePaths) {
            File file = new File(filePath);
            if (!file.exists()) {
                continue;
            }
            files.add(file);
        }
        return files;
    }

    /**
     * 压缩图片，低分辨率
     */
    public static List<String> compressorImage(Context context, List<String> filePaths) {
        return compressorImage(context, filePaths, false);
    }

    /**
     * 压缩图片，可选高分辨率
     */
    public static List<String> compressorImage(Context context, List<String> filePaths, boolean isXxh) {
        List<String> newPaths = new ArrayList<>();
        if (filePaths == null || filePaths.size() == 0) return newPaths;

        // 缓存目录
        String dirPath = context.getCacheDir().getPath();
        // 缓存图片目录
        String imagePath = dirPath + "/image/";
        // 压缩分辨率阈值
        Integer thresholdXxh = 1080;
        Integer thresholdXh = 720;
        // 压缩设置
        Compressor compressor = new Compressor(context);
        compressor.setDestinationDirectoryPath(imagePath);
        compressor.setQuality(75);
        // 判断分辨率
        if (isXxh) {
            thresholdXh = thresholdXxh;
            compressor.setQuality(100);
        }
        // 设置宽高
        compressor.setMaxWidth(thresholdXh);
        compressor.setMaxHeight(thresholdXh);
        // 压缩文件
        for (String filePath : filePaths) {
            try {
                File file = new File(filePath);
                if (!file.exists()) {
                    continue;
                }

                String fileType = getFileType(filePath);
                if (".gif".equals(fileType)) {
                    newPaths.add(filePath);
                    continue;
                }

                // 压缩后的文件
                File newFile = compressor.compressToFile(file);
                newPaths.add(newFile.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return newPaths;
    }

    /**
     * 获取文件类型
     */
    private static String getFileType(String filePath) {
        return filePath.substring(filePath.lastIndexOf("."), filePath.length());
    }


    // 预留，获取文件宽高
    private void getWH(String filePath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 阈值
        Integer thresholdXxh = 1920;
        Integer thresholdXh = 1080;
        // 关键项
        options.inJustDecodeBounds = true;
        // 此处返回的bitmap为null，但宽高可以从options获取
        BitmapFactory.decodeFile(filePath, options);
        // 宽高
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        // 比例
        float proportion = (float) outWidth / (float) outHeight;
        // 判断宽高
        if (outWidth > outHeight) {
            if (outWidth > thresholdXxh) {
                outWidth = thresholdXxh;
                outHeight = (int) (outWidth / proportion);
            }
            if (outWidth > thresholdXh && outWidth < thresholdXxh) {
                outWidth = thresholdXh;
                outHeight = (int) (outWidth / proportion);
            }
        } else {
            if (outHeight > thresholdXxh) {
                outHeight = thresholdXxh;
                outWidth = (int) (outHeight * proportion);
            }
            if (outHeight > thresholdXh && outHeight < thresholdXxh) {
                outHeight = thresholdXh;
                outWidth = (int) (outHeight * proportion);
            }
        }
        Log.d("xl", "outWidth = " + outWidth + ",outHeight = " + outHeight);
    }
}
