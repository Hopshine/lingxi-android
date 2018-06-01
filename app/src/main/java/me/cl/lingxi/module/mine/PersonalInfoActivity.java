package me.cl.lingxi.module.mine;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.cl.library.base.BaseActivity;
import me.cl.library.view.LoadingDialog;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.util.ContentUtil;
import me.cl.lingxi.common.util.ImageUtil;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.view.MoeToast;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.Call;

public class PersonalInfoActivity extends BaseActivity {

    private static final int PHOTO_REQUEST_CUT = 456;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.person_img)
    ImageView mPersonImg;
    @BindView(R.id.person_name)
    TextView mPersonName;

    private String mUserId;
    private String mImagePath;

    private LoadingDialog loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setupToolbar(mToolbar, "个人信息", true, 0, null);
        loadingProgress = new LoadingDialog(this, "修改头像中...");

        int x = (int) (Math.random() * 5) + 1;
        if (x == 1) {
            MoeToast.makeText(this, "是谁，是谁在那里？");
        }

        mUserId = SPUtil.build().getString(Constants.USER_ID);
        String saveName = SPUtil.build().getString(Constants.USER_NAME);
        mPersonName.setText(saveName);
    }

    @OnClick({R.id.person_img})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_img:
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setShowGif(false)
                        .setPreviewEnabled(false)
                        .start(PersonalInfoActivity.this, PhotoPicker.REQUEST_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (requestCode == PHOTO_REQUEST_CUT) {
            ContentUtil.loadAvatar(mPersonImg, mImagePath);
//            postUserImage();
        }

        if (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            if (photos != null) {
                String photo = photos.get(0);
                Uri uri = Uri.fromFile(new File(photo));
                String imagePath = ImageUtil.getImagePath();
                mImagePath = imagePath;
                int size = 240;
                Intent intent = ImageUtil.callSystemCrop(uri, imagePath, size);
                startActivityForResult(intent, PHOTO_REQUEST_CUT);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 上传用户头像
     */
    private void postUserImage(){
        OkUtil.post()
                .url(Api.uploadUserImage)
                .addFile("file", new File(mImagePath))
                .execute(new ResultCallback<Result<List<String>>>() {
                    @Override
                    public void onSuccess(Result<List<String>> response) {
                        String code = response.getCode();
                        List<String> photos = response.getData();
                        if (!"00000".equals(code) || photos == null || photos.size() == 0) {
                            Utils.toastShow(PersonalInfoActivity.this, "更新头像失败");
                            return;
                        }
                        String photo = photos.get(0);
                        postUpdateUserInfo(photo);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        Utils.toastShow(PersonalInfoActivity.this, "更新头像失败");
                    }

                    @Override
                    public void onFinish() {
                        Utils.toastShow(PersonalInfoActivity.this, "更新头像失败");
                    }
                });
    }

    /**
     * 更新用户信息
     */
    private void postUpdateUserInfo(String photo) {


    }

    /**
     * 通知更新用户头像
     */
    private void notifyUpdateUserImage(){
        Intent intent = new Intent();
        intent.setAction(Constants.UPDATE_USER_IMG);
        sendBroadcast(intent);
    }
}
