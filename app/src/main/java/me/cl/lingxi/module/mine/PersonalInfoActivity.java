package me.cl.lingxi.module.mine;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.cl.library.base.BaseActivity;
import me.cl.library.view.LoadingDialog;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.common.view.MoeToast;

public class PersonalInfoActivity extends BaseActivity implements OnClickListener {

    private static final int RESULT_REQUEST_CODE = 2;
    private static final int CAMERA = 0X12;
    private static final int PIC = 0x01;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.person_img)
    ImageView mPersonImg;
    @BindView(R.id.person_name)
    TextView mPersonName;

    private Bitmap bitmap;
    private String saveName;
    private String saveId;
    private String imgStr;
    private File fileDir;
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
        mPersonImg.setOnClickListener(this);

        int x = (int) (Math.random() * 5) + 1;
        if (x == 1) {
            MoeToast.makeText(this, "是谁，是谁在那里？");
        }
        saveName = SPUtil.build().getString(Constants.USER_NAME);
        saveId = SPUtil.build().getString(Constants.USER_ID);
        mPersonName.setText(saveName);
        fileDir = new File(Environment.getExternalStorageDirectory() + "/lingci/image/avatar");
        if (!fileDir.exists()) {
            fileDir.mkdirs(); // 如果该目录不存在,则创建一个这样的目录
        }
        Utils.setPersonImg(saveName, mPersonImg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_img:
                final Dialog dialog = new Dialog(this, R.style.AppTheme_Dialog);
                dialog.setContentView(R.layout.dialog_photo_camera);
                LinearLayout llPhotograph = dialog.findViewById(R.id.ll_photograph);
                LinearLayout llGetPicture = dialog.findViewById(R.id.ll_getPicture);
                LinearLayout llCancel = dialog.findViewById(R.id.ll_cancel);
                layoutSetClickListener(llPhotograph, dialog);
                layoutSetClickListener(llGetPicture, dialog);
                layoutSetClickListener(llCancel, dialog);
                dialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PIC: // 获取系统相册
                if (data != null) {
                    startPhotoZoom(data.getData());
                }
                break;
            case CAMERA:
                File temp = new File(Environment.getExternalStorageDirectory() + "/lingci/image/avatar/" + "headportraits.png");
                startPhotoZoom(Uri.fromFile(temp));
                break;
            case RESULT_REQUEST_CODE:
                if (data != null) {
                    getImageToView(data);
                }
                break;
        }
    }

    /**
     * dialog点击事件
     */
    public void layoutSetClickListener(final LinearLayout layout, final Dialog dialog) {
        layout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (layout.getId()) {
                    case R.id.ll_photograph:
                        openCarma();
                        break;
                    case R.id.ll_getPicture:
                        getPicture();
                        break;
                    case R.id.ll_cancel:
                        break;
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * 打开系统照相机
     */
    private void openCarma() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File temp = new File(Environment.getExternalStorageDirectory() + "/lingci/image/avatar/" + "headportraits.png");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(temp));
        this.startActivityForResult(intent, CAMERA);
    }

    /**
     * 打开相册
     */
    private void getPicture() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        } else {
            intent = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
        }
        this.startActivityForResult(intent, PIC);

    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }

    /**
     * 保存裁剪之后的图片数据
     */
    private void getImageToView(Intent data) {

        Bundle extras = data.getExtras();
        if (extras != null) {
            bitmap = extras.getParcelable("data");
            savePic(bitmap);
            mPersonImg.setImageBitmap(bitmap);
            imgStr = Bitmap2StrByBase64(bitmap);
            Intent intent = new Intent();
            intent.setAction(Constants.UPDATE_USER_IMG);
            sendBroadcast(intent);
        }
        upPhoto();
    }

    private void upPhoto() {
        Utils.toastShow(PersonalInfoActivity.this, "暂不支持修改头像");
//        loadingProgress.show();
    }

    /**
     * 临时保存到sd卡中
     *
     * @param b
     * @return
     */
    public String savePic(Bitmap b) {
        FileOutputStream fos = null;
        try {
            File f = new File(Environment.getExternalStorageDirectory() + "/lingci/image/avatar/" + "headportraits.png");
//	        if (f.exists()) {
//	        	f.delete();
//	        }
            fos = new FileOutputStream(f);
            Log.i("ml", "strFileName 1= " + f.getPath());
            b.compress(CompressFormat.PNG, 90, fos);
            fos.flush();
            fos.close();
            Log.i("ml", "save pic OK!" + f.toString());
            return f.getPath();
        } catch (FileNotFoundException e) {
            Log.i("ml", "FileNotFoundException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("ml", "IOException");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过Base32将Bitmap转换成Base64字符串
     */
    public String Bitmap2StrByBase64(Bitmap bit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(CompressFormat.JPEG, 40, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
