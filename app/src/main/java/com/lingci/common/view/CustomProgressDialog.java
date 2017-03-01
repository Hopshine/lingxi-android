package com.lingci.common.view;

import com.lingci.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @Description:自定义加载动画
 */
public class CustomProgressDialog extends ProgressDialog {

	private AnimationDrawable mAnimation;
	// private Context mContext;
	private ImageView mImageView;
	private CharSequence mLoadingTip;
	private TextView mLoadingTv;
	private int mResid;

	
	public CustomProgressDialog(Context context, int tipid, int id) {
		super(context);
		// this.mContext = context;
		this.mLoadingTip = context.getResources().getText(tipid);
		this.mResid = id;
		setCanceledOnTouchOutside(true);
	}
	
	public  CustomProgressDialog(Context context, CharSequence tip, int id) {
		super(context);
		// this.mContext = context;
		this.mLoadingTip = tip;
		this.mResid = id;
		setCanceledOnTouchOutside(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}

	private void initData() {

		mImageView.setBackgroundResource(mResid);
		// 通过ImageView对象拿到背景显示的AnimationDrawable
		mAnimation = (AnimationDrawable) mImageView.getBackground();
		// 为了防止在onCreate方法中只显示第一帧的解决方案之一
		mImageView.post(new Runnable() {
			@Override
			public void run() {
				mAnimation.start();
			}
		});
		mLoadingTv.setText(mLoadingTip);

	}
	
	public void setContent(CharSequence str) {
		mLoadingTv.setText(str);
	}

	private void initView() {
		setContentView(R.layout.progress_dialog);
		mLoadingTv = (TextView) findViewById(R.id.loadingTv);
		mImageView = (ImageView) findViewById(R.id.loadingIv);
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
	}
}
