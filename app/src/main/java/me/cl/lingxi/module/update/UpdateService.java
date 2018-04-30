package me.cl.lingxi.module.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.entity.AppVersion;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2017/10/26
 * desc   : 更新服务
 * version: 1.0
 */

public class UpdateService extends Service {

    private static final String rootPath = Environment.getExternalStorageDirectory() + File.separator;
    private static final String downloadPath = "lingci/download/";
    private String title = "下载灵悉APP";
    private String loading = "正在下载...";
    private int fileSize;
    private int fileCache;

    private AppVersion mAppVersion;
    private String fileName;
    private String filePath;

    // 通知栏
    private NotificationManager mNotificationManager;
    private Notification.Builder mBuilder;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mAppVersion = (AppVersion) intent.getSerializableExtra("app_version");
        fileName = mAppVersion.getApkName();
        filePath = rootPath + downloadPath;

        downloadApk(mAppVersion.getApkUrl());
        return super.onStartCommand(intent, flags, startId);
    }

    // 下载
    private void downloadApk(String url) {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new Notification.Builder(getApplicationContext());
        mBuilder.setContentTitle(title)
                .setContentText(loading)
                .setProgress(100, 0 , false)
                .setSmallIcon(android.R.drawable.stat_sys_download);

        // 如果文件存在，则不再下载
        String path = filePath + fileName;
        File file = new File(path);
        if (file.exists())
            installApk(path);
        else
            new UpdateTask().execute(url);
    }

    // 更新任务
    class UpdateTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            Utils.toastShow(getApplicationContext(), "开始下载");
            mBuilder.setTicker(title)
                    .setProgress(100, 0 , false);
            mNotificationManager.notify(0, mBuilder.build());
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progress = ((values[0] + 1) * 100 / fileSize);
            mBuilder.setProgress(100, progress, false).setContentText("已下载" + progress + "%");
            mNotificationManager.notify(0, mBuilder.build());
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                URLConnection conn = url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                //根据响应获取文件大小
                fileSize = conn.getContentLength();
                if (fileSize <= 0) {
                    throw new RuntimeException("无法获知文件大小");
                }
                if (is == null) throw new RuntimeException("stream is null");

                File file = new File(filePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                filePath = filePath + fileName;
                FileOutputStream fos = new FileOutputStream(filePath);
                byte buf[] = new byte[1024];
                fileCache = 0;
                do {
                    int num = is.read(buf);
                    if (num == -1) {
                        break;
                    }
                    fos.write(buf, 0, num);
                    fileCache += num;
                    this.publishProgress(fileCache);
                } while (true);

                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "下载完成";
        }

        @Override
        protected void onPostExecute(String s) {
            Utils.toastShow(getApplicationContext(), s);

            mBuilder.setContentText(s).setProgress(100, 0, false);
            mNotificationManager.cancel(0);

            installApk(filePath);

            // 关闭服务
            UpdateService.this.stopSelf();
            super.onPostExecute(s);
        }
    }

    //安装程序
    private void installApk(String filePath) {
        Uri uri = Uri.fromFile(new File(filePath));
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(installIntent);
    }
}
