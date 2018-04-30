package me.cl.lingxi.module.feed;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.cl.library.base.BaseActivity;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.PhotoSelAdapter;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.util.OSSUploadUtils;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.entity.Feed;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.entity.STSToken;
import me.cl.lingxi.module.main.MainActivity;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.Call;

public class PublishActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.mood_info)
    AppCompatEditText mMoodInfo;
    @BindView(R.id.iv_submit)
    ImageView mIvSubmit;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private PhotoSelAdapter mPhotoSelAdapter;
    private List<String> mPhotos = new ArrayList<>();

    private String mUid;
    private String mInfo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mUid = SPUtil.build().getString(Constants.USER_ID);
        setupToolbar(mToolbar, "发布新动态", true, 0, null);
        setLoading("发布中...");
        initRecycleView();
    }

    private void initRecycleView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(PublishActivity.this, 3));
        mPhotoSelAdapter = new PhotoSelAdapter(mPhotos);
        mRecyclerView.setAdapter(mPhotoSelAdapter);
        mPhotoSelAdapter.setOnItemClickListener(new PhotoSelAdapter.OnItemClickListener() {
            @Override
            public void onPhotoClick(int position) {
                if (mPhotos.get(position).equals(PhotoSelAdapter.mPhotoAdd)) {
                    mPhotos.remove(position);
                    PhotoPicker.builder()
                            .setPhotoCount(6)
                            .setShowCamera(true)
                            .setShowGif(false)
                            .setSelected((ArrayList<String>) mPhotos)
                            .setPreviewEnabled(false)
                            .start(PublishActivity.this, PhotoPicker.REQUEST_CODE);
                } else {
                    if (mPhotos.contains(PhotoSelAdapter.mPhotoAdd))
                        mPhotos.remove(PhotoSelAdapter.mPhotoAdd);
                    PhotoPreview.builder()
                            .setPhotos((ArrayList<String>) mPhotos)
                            .setCurrentItem(position)
                            .setShowDeleteButton(true)
                            .start(PublishActivity.this);
                }
            }

            @Override
            public void onDelete(int position) {
                mPhotos.remove(position);
                mPhotoSelAdapter.setPhotos(mPhotos);
            }
        });
    }

    @OnClick(R.id.iv_submit)
    public void onClick() {
        mInfo = mMoodInfo.getText().toString().trim();
        if (TextUtils.isEmpty(mInfo)) {
            Utils.toastShow(this, "好歹写点什么吧！");
            return;
        }
        if (mPhotos.size() <= 1) {
            postSaveFeed(mPhotos);
        } else {
            getOssToken();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                switch (requestCode) {
                    case PhotoPicker.REQUEST_CODE:
                    case PhotoPreview.REQUEST_CODE:
                        mPhotos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        break;
                }
            }
        }
        mPhotoSelAdapter.setPhotos(mPhotos);
    }

    private void getOssToken() {
        showLoading();
        OkUtil.post()
                .url(Api.ossToken)
                .execute(new ResultCallback<Result<STSToken>>() {
                    @Override
                    public void onSuccess(Result<STSToken> response) {
                        String code = response.getCode();
                        if (!"00000".equals(code)) {
                            Utils.toastShow(PublishActivity.this, "发布失败");
                            return;
                        }
                        uploadImage(response.getData());
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        Utils.toastShow(PublishActivity.this, "发布失败");
                    }

                    @Override
                    public void onFinish() {
                        Utils.toastShow(PublishActivity.this, "发布失败");
                    }
                });
    }

    private void uploadImage(STSToken stsToken) {
        removePhotoAdd(mPhotos);
        OSSUploadUtils.build(PublishActivity.this, stsToken)
                .uploadFiles("lingxi/feed/pt_", mPhotos, new OSSUploadUtils.UploadCallback() {

                    @Override
                    public void onProgress(int position, long currentSize, long totalSize) {
                        Log.d("PutObject", "position:" + position + " currentSize: " + currentSize + " totalSize: " + totalSize);
                    }

                    @Override
                    public void onSuccess(List<String> uploadImg) {
                        for (String img : uploadImg) {
                            Log.d("PutObject", img);
                        }
                        postSaveFeed(uploadImg);
                    }

                    @Override
                    public void onFailure(ClientException clientException, ServiceException serviceException) {
                        dismissLoading();
                        Utils.toastShow(PublishActivity.this, "发布失败");
                        if (clientException != null) {
                            // 本地异常如网络异常等
                            clientException.printStackTrace();
                        }
                        if (serviceException != null) {
                            // 服务异常
                            Log.e("ErrorCode", serviceException.getErrorCode());
                            Log.e("RequestId", serviceException.getRequestId());
                            Log.e("HostId", serviceException.getHostId());
                            Log.e("RawMessage", serviceException.getRawMessage());
                        }
                    }
                });
    }

    // 发布动态
    private void postSaveFeed(List<String> uploadImg) {
        removePhotoAdd(uploadImg);
        OkUtil.post()
                .url(Api.saveFeed)
                .addParam("userId", mUid)
                .addParam("feedInfo", mInfo)
                .addUrlParams("photoList", uploadImg)
                .execute(new ResultCallback<Result<Feed>>() {
                    @Override
                    public void onSuccess(Result<Feed> response) {
                        dismissLoading();
                        String code = response.getCode();
                        if (!"00000".equals(code)) {
                            Utils.toastShow(PublishActivity.this, "发布失败");
                            return;
                        }
                        mMoodInfo.setText(null);
                        Utils.toastShow(PublishActivity.this, "发布成功");
                        onBackPressed();
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        dismissLoading();
                        Utils.toastShow(PublishActivity.this, "发布失败");
                    }

                    @Override
                    public void onFinish() {
                        dismissLoading();
                        Utils.toastShow(PublishActivity.this, "发布失败");
                    }
                });
    }

    private void removePhotoAdd(List<String> photList) {
        if (photList.contains(PhotoSelAdapter.mPhotoAdd)) {
            photList.remove(PhotoSelAdapter.mPhotoAdd);
        }
    }

    @Override
    public void onBackPressed() {
        // 此处监听回退，通知首页刷新
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.GO_INDEX, R.id.navigation_camera);
        intent.putExtras(bundle);
        setResult(Constants.ACTIVITY_PUBLISH, intent);
        finish();
    }
}
