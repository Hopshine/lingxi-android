package com.lingci.ui.activity;

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
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.annotate.InjectView;
import com.google.annotate.Injector;
import com.lingci.R;
import com.lingci.constants.GlobalParame;
import com.lingci.constants.PreferencesManager;
import com.lingci.utils.DaHttpRequest;
import com.lingci.utils.MD5Util;
import com.lingci.utils.MoeToast;
import com.lingci.views.CustomProgressDialog;
import com.lingci.views.RoundImageView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PersonalInfoActivity extends Activity implements OnClickListener{


	private static final int RESULT_REQUEST_CODE = 2;
	private static final int CAMRMA = 0X12;
	private static final int PIC = 0x01;
	@InjectView(R.id.lc_ruturn)
	private LinearLayout lc_ruturn;
	@InjectView(R.id.tv_top)
	private TextView tv_top;
	@InjectView(R.id.person_img)
	private RoundImageView person_img;
	@InjectView(R.id.person_name)
	private TextView person_name;
	private Bitmap bitmap;
	private String savename;
	private String imgStr;
	private File fileDir;
	private CustomProgressDialog loadingProgress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_info);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
//		person_img = (RoundImageView) findViewById(R.id.person_img);
		loadingProgress = new CustomProgressDialog(this,"修改头像中...", R.anim.frame_loadin);
		Injector.get(this).inject();
		lc_ruturn.setVisibility(View.VISIBLE);
		lc_ruturn.setOnClickListener(this);
		person_img.setOnClickListener(this);
		tv_top.setText("个人信息");

		int x = (int) (Math.random() * 5) + 1;
		if(x==1){
//			ToastUtil.showSingleton(this, "是谁，是谁在哪里？");
			MoeToast.makeText(this, "是谁，是谁在那里？");
		}
		savename = PreferencesManager.getInstance().getString("username", "");
		person_name.setText(savename);
		fileDir = new File(Environment.getExternalStorageDirectory() + "/lingci/image/avatar");
		if (!fileDir.exists()) {
			fileDir.mkdirs(); // 如果该目录不存在,则创建一个这样的目录
		}
		GlobalParame.setPersonImg(savename,person_img);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.lc_ruturn:
			onBackPressed();
			break;
		case R.id.person_img:
			final Dialog dialog = new Dialog(PersonalInfoActivity.this,R.style.dialog);
			dialog.setContentView(R.layout.photo_camera_dialog);
			LinearLayout ll_photograph = (LinearLayout) dialog.findViewById(R.id.ll_photograph);
			LinearLayout ll_getPicture = (LinearLayout) dialog.findViewById(R.id.ll_getPicture);
			LinearLayout ll_cancel = (LinearLayout) dialog.findViewById(R.id.ll_cancel);
			layouSetClickListener(ll_photograph, dialog);
			layouSetClickListener(ll_getPicture, dialog);
			layouSetClickListener(ll_cancel, dialog);
			dialog.show();
			break;	
		default:
			break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.personal_info, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
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
		case CAMRMA:
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
	
	/**dialog点击事件
	 * 
	 * @param layout
	 * @param dialog
	 */
	public void layouSetClickListener(final LinearLayout layout,final Dialog dialog) {
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
		this.startActivityForResult(intent, CAMRMA);
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
			intent = new Intent(Intent.ACTION_PICK , Media.EXTERNAL_CONTENT_URI);
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
	 *
	 */
	private void getImageToView(Intent data) {
	
		Bundle extras = data.getExtras();
		if (extras != null) {
			bitmap = extras.getParcelable("data");
			savePic(bitmap);
			person_img.setImageBitmap(bitmap);
			imgStr = Bitmap2StrByBase64(bitmap);
			Intent intent = new Intent();
			intent.setAction(GlobalParame.UPDATE_USERIMG);
			sendBroadcast(intent);
		}
		upPhoto();
	}
	
	private void upPhoto() {
		// TODO Auto-generated method stub
		String path = GlobalParame.URl + "/uploadPrimg";
		// MD5name uname imgStr
		DaHttpRequest dr = new DaHttpRequest(this);
		RequestParams params = new RequestParams();
		params.put("MD5name", MD5Util.MD5(savename));
		params.put("uname", savename);
		params.put("imgStr", imgStr);
		dr.post(path, params, new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				loadingProgress.show();
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				Log.i("hello", "请求失败：");
				loadingProgress.dismiss();
			}
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				String str = new String(arg2);
				Log.i("hello", "请求成功：" + str);
				loadingProgress.dismiss();
			}
		});
	}

	/**
	 * 临时保存到sd卡中
	 * @param b
	 * @return
	 */
    public  String  savePic(Bitmap b) {
        FileOutputStream fos = null;
        try {
	        File f = new File(Environment.getExternalStorageDirectory() + "/lingci/image/avatar/" + "headportraits.png");
//	        if (f.exists()) {
//	        	f.delete();
//	        }
	        fos = new FileOutputStream(f);
	        Log.i("ml","strFileName 1= " + f.getPath());
	        if (null != fos) {
	        	b.compress(CompressFormat.PNG, 90, fos);
	            fos.flush();
	            fos.close();
	            Log.i("ml","save pic OK!"+   f.toString());
	            return f.getPath();
	        }
        } catch (FileNotFoundException e) {
        Log.i("ml","FileNotFoundException");
            e.printStackTrace();
        } catch (IOException e) {
        Log.i("ml","IOException");
            e.printStackTrace();
        }
		return null;
    }
    
	/** 
	 * 通过Base32将Bitmap转换成Base64字符串 
	 * @param bit 
	 * @return 
	 */  
	public String Bitmap2StrByBase64(Bitmap bit){  
	   ByteArrayOutputStream bos=new ByteArrayOutputStream();  
	   bit.compress(CompressFormat.JPEG, 40, bos);//参数100表示不压缩  
	   byte[] bytes=bos.toByteArray();  
	   return Base64.encodeToString(bytes, Base64.DEFAULT);  
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
