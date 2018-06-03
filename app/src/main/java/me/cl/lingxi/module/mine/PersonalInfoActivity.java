package me.cl.lingxi.module.mine;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
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
import me.cl.lingxi.common.util.DialogUtil;
import me.cl.lingxi.common.util.ImageUtil;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.entity.UserInfo;
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
    @BindView(R.id.user_signature)
    TextView mUserSignature;

    private String mUserId;
    private String saveName;
    private String mImagePath;

    private LoadingDialog loadingProgress;

    // 用户更新的参数
    private String username;
    private String avatar;
    private Integer sex;
    private String qq;
    private String signature;

    // 是否为更新头像
    private boolean isUpdateAvatar = false;

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
        saveName = SPUtil.build().getString(Constants.USER_NAME);
        mPersonName.setText(saveName);
        postUserInfo();
    }

    @OnClick({R.id.person_img, R.id.person_name})
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
            case R.id.person_name:
                DialogUtil.editText(PersonalInfoActivity.this, "修改用户名", InputType.TYPE_CLASS_TEXT, saveName, 24, new DialogUtil.onPositiveListener() {
                    @Override
                    public void onPositive(DialogInterface dialog, String value) {
                        if (!TextUtils.isEmpty(value) && value.length() > 4 && !saveName.equals(value)) {
                            Utils.showToast(PersonalInfoActivity.this, "暂不支持修改用户名");
                            username = null;
                        }
                    }
                });
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

        // 图片选择
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

        // 图片裁剪
        if (requestCode == PHOTO_REQUEST_CUT) {
            ContentUtil.loadAvatar(mPersonImg, mImagePath);
            postUserImage();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取用户信息
     */
    private void postUserInfo() {
        OkUtil.post()
                .url(Api.userInfo)
                .addParam("id", mUserId)
                .execute(new ResultCallback<Result<UserInfo>>() {

                    @Override
                    public void onSuccess(Result<UserInfo> response) {
                        if ("00000".equals(response.getCode())) {
                            setUserInfo(response.getData());
                        } else {
                            onBackPressed();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        onBackPressed();
                    }

                    @Override
                    public void onFinish() {
                        onBackPressed();
                    }
                });
    }

    /**
     * 上传用户头像
     */
    private void postUserImage() {
        File file = new File(mImagePath);
        if (!file.exists()) {
            showUserImageUpdateError();
            return;
        }
        OkUtil.post()
                .url(Api.uploadUserImage)
                .addFile("file", file)
                .execute(new ResultCallback<Result<List<String>>>() {
                    @Override
                    public void onSuccess(Result<List<String>> response) {
                        String code = response.getCode();
                        List<String> photos = response.getData();
                        if (!"00000".equals(code) || photos == null || photos.size() == 0) {
                            showUserImageUpdateError();
                            return;
                        }
                        avatar = photos.get(0);
                        isUpdateAvatar = true;
                        postUpdateUserInfo();
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        showUserImageUpdateError();
                    }

                    @Override
                    public void onFinish() {
                        showUserImageUpdateError();
                    }
                });
    }

    /**
     * 更新用户信息
     */
    private void postUpdateUserInfo() {
        OkUtil.post()
                .url(Api.updateUser)
                .addParam("id", mUserId)
                .addParam("username", username)
                .addParam("avatar", avatar)
                .execute(new ResultCallback<Result<UserInfo>>() {

                    @Override
                    public void onSuccess(Result<UserInfo> response) {
                        if ("00000".equals(response.getCode())) {
                            showUserUpdateSuccess();
                            setUserInfo(response.getData());
                        } else {
                            showUserUpdateError();
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        showUserUpdateError();
                    }

                    @Override
                    public void onFinish() {
                        showUserUpdateError();
                    }
                });
    }

    /**
     * 设置用户信息
     */
    private void setUserInfo(UserInfo userInfo) {
        if (isUpdateAvatar) {
            isUpdateAvatar = false;
            notifyUpdateUserImage();
            File file = new File(mImagePath);
            if (file.exists()) {
                boolean delete = file.delete();
                Log.d(TAG, "setUserInfo: delete file " + delete);
            }
        }

        ContentUtil.loadUserAvatar(mPersonImg, userInfo.getAvatar());

        if (!TextUtils.isEmpty(userInfo.getUsername())) {
            mPersonName.setText(userInfo.getUsername());
        }

        cleanData();
    }

    /**
     * 清除数据
     */
    private void cleanData() {
        avatar = null;
        username = null;
        sex = null;
        qq = null;
        signature = null;
    }

    /**
     * 通知更新用户头像
     */
    private void notifyUpdateUserImage() {
        Intent intent = new Intent();
        intent.setAction(Constants.UPDATE_USER_IMG);
        sendBroadcast(intent);
    }

    /**
     * 提示头像修改失败
     */
    private void showUserImageUpdateError() {
        Utils.showToast(this, "更新头像失败");
    }

    /**
     * 提示用户信息更新失败
     */
    private void showUserUpdateSuccess() {
        Utils.showToast(this, "更新用户信息成功");
    }

    /**
     * 提示用户信息更新失败
     */
    private void showUserUpdateError() {
        Utils.showToast(this, "更新用户信息失败");
    }
}
