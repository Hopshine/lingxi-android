package me.cl.lingxi.common.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import me.cl.lingxi.entity.STSToken;

/**
 * author : bafsj
 * e-mail : bafs.jy@live.com
 * time   : 2017/04/15
 * desc   : OSS文件上传
 * version: 1.0
 */
public class OSSUploadUtils {

    private static final String endpoint = "http://oss-cn-shanghai.aliyuncs.com";
    private static final String bucketName = "lingci";
    private static final String imgUrl = "http://lingci.oss-cn-shanghai.aliyuncs.com/";

    private OSS oss;

    // 上传路径
    private String uploadPath;
    // 成功上传的图片path集合
    private List<String> uploadImg = new ArrayList<>();
    // 临时记录的图片
    private String imgTemp;

    private UploadCallback mCallback;

    // 上传回调
    public interface UploadCallback {
        void onProgress(int position, long currentSize, long totalSize);

        void onSuccess(List<String> uploadImg);

        void onFailure(ClientException clientException, ServiceException serviceException);
    }

    private static OSSUploadUtils ossUtil;

    public static OSSUploadUtils build(Context context, STSToken token){
        if (ossUtil == null){
            ossUtil = new OSSUploadUtils(context, token);
        }
        return ossUtil;
    }

    private OSSUploadUtils(Context context, STSToken token) {
        // 从业务服务器获取的STS token设置secret
        OSSCredentialProvider stsCredentialProvider = new OSSStsTokenCredentialProvider(token.getAccessKeyId(), token.getAccessKeySecret(), token.getSecurityToken());
        Log.d("xl", "OSSUploadUtils: " + token.getSecurityToken());
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次

        oss = new OSSClient(context, endpoint, stsCredentialProvider, conf);
    }

    /**
     * 上传多文件
     */
    public void uploadFiles(@NonNull String path, List<String> urls, UploadCallback callback) {
        if (null == urls || urls.size() == 0) {
            return;
        }
        // 设置上传路径
        uploadPath = path;
        // 设置回调
        mCallback = callback;
        //上传文件
        ossUpload(urls);
    }

    private void ossUpload(final List<String> urls) {
        if (urls.size() <= 0) {
            // 文件全部上传完毕
            mCallback.onSuccess(uploadImg);
            return;
        }
        final String url = urls.get(0);
        if (TextUtils.isEmpty(url)) {
            urls.remove(0);
            // url为空就跳过它继续上传
            ossUpload(urls);
            return;
        }
        File file = new File(url);
        if (!file.exists()) {
            urls.remove(0);
            // 文件为空或不存在跳过它继续上传
            ossUpload(urls);
            return;
        }
        // 文件前缀
        String filePrefix = "";
        // 文件后缀
        String fileSuffix = "";
        if (file.isFile()) {
            // 获取文件后缀名
            fileSuffix = file.getName().substring(file.getName().lastIndexOf("."));
            // 设置文件前缀
            filePrefix = uploadPath + MD5(file.getName()) + "_";
            Log.d("xl", "ossUpload: " + filePrefix);
        }
        // 文件标识符objectKey
        final String objectKey = filePrefix + System.currentTimeMillis() + fileSuffix;
        imgTemp = imgUrl + objectKey;
        // 下面3个参数依次为bucket名，ObjectKey名，上传文件路径
        PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, url);

        // 设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                // 设置每个文件的上传进度
                mCallback.onProgress(uploadImg.size(), currentSize, totalSize);
            }
        });
        // 异步上传
        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                // 上传成功
                urls.remove(0);
                // 将上传成功的图篇路径加入集合
                uploadImg.add(imgTemp);
                // 递归同步效果
                ossUpload(urls);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) { // 上传失败
                // 请求异常
                mCallback.onFailure(clientExcepion, serviceException);
            }
        });
        // task.cancel(); // 可以取消任务
        // task.waitUntilFinished(); // 可以等待直到任务完成
    }

    // MD5
    private String MD5(String val) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(val.getBytes());
            byte[] hash = md5.digest();
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                int number = b & 0xFF;
                if (number < 0x10)
                    hex.append("0");
                hex.append(Integer.toHexString(number));
            }
            return hex.toString();
        }catch (Exception e){
            return null;
        }
    }
}
