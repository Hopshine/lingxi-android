package me.cl.lingxi.module.mood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.cl.lingxi.R;
import me.cl.lingxi.adapter.PhotoSelAdapter;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.util.OSSUploadUtils;
import me.cl.lingxi.common.util.SPUtils;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.common.widget.JsonCallback;
import me.cl.lingxi.emojicon.EmojiconEditText;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.entity.STSToken;
import me.cl.lingxi.module.BaseActivity;
import me.cl.lingxi.module.main.MainActivity;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

public class PublishActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.mood_info)
    EmojiconEditText mMoodInfo;
    @BindView(R.id.iv_submit)
    ImageView mIvSubmit;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private PhotoSelAdapter mPhotoSelAdapter;
    private List<String> mPhotos = new ArrayList<>();

    private int mUid;
    private String mInfo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mUid = SPUtils.getInstance(this).getInt("uid", 0);
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
                    if (mPhotos.contains(PhotoSelAdapter.mPhotoAdd)) mPhotos.remove(PhotoSelAdapter.mPhotoAdd);
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
        if (TextUtils.isEmpty(mInfo)){
            Utils.toastShow(this, "好歹写点什么吧！");
        }else {
            if (mPhotos.size() <= 1)
                postSubmitMood(mUid, mInfo, mPhotos);
            else
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

    private void getOssToken(){
        showLoading();
        OkGo.<Result<STSToken>>get(Api.ossToken)
                .execute(new JsonCallback<Result<STSToken>>() {
                    @Override
                    public void onSuccess(Response<Result<STSToken>> response) {
                        if (response.body().getRet() == 0) {
                            if (mPhotos.contains(PhotoSelAdapter.mPhotoAdd)) mPhotos.remove(PhotoSelAdapter.mPhotoAdd);
                            OSSUploadUtils.build(PublishActivity.this, response.body().getData())
                                    .uploadFiles("lingxi/mood/pt_", mPhotos, new OSSUploadUtils.UploadCallback() {

                                @Override
                                public void onProgress(int position, long currentSize, long totalSize) {
                                    Log.d("PutObject", "position:" + position +  " currentSize: " + currentSize + " totalSize: " + totalSize);
                                }

                                @Override
                                public void onSuccess(List<String> uploadImg) {
                                    for (String img: uploadImg) {
                                        Log.d("PutObject", img);
                                    }
                                    postSubmitMood(mUid, mInfo, uploadImg);
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
                    }
                });
    }

    // 发布动态
    private void postSubmitMood(int uid, String info, List<String> photos) {
        if (photos.contains(PhotoSelAdapter.mPhotoAdd)) photos.remove(PhotoSelAdapter.mPhotoAdd);
        OkGo.<Result>post(Api.addFeed)
                .params("uid", uid)
                .params("info", info)
                .addUrlParams("photos", photos)
                .execute(new JsonCallback<Result>() {
                    @Override
                    public void onSuccess(Response<Result> response) {
                        dismissLoading();
                        int tag = response.body().getRet();
                        switch (tag) {
                            case 0:
                                mMoodInfo.setText(null);
                                Utils.toastShow(PublishActivity.this, "发布成功");
                                onBackPressed();
                                break;
                        }
                    }

                    @Override
                    public void onError(Response<Result> response) {
                        dismissLoading();
                        Utils.toastShow(PublishActivity.this, "发布失败");
                    }
                });
    }

    @Override
    public void onBackPressed() {
        // 此处监听回退，通知首页刷新
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.GO_INDEX, Constants.INDEX_MOOD);
        intent.putExtras(bundle);
        setResult(Constants.ACTIVITY_PUBLISH, intent);
        finish();
    }
}
