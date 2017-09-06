package me.cl.lingxi.common.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import me.cl.lingxi.R;

/**
 * 加载动画
 */
public class CustomProgressDialog extends ProgressDialog {

	private AnimationDrawable mAnimation;
	private ImageView mImageView;
	private CharSequence mLoadingTip;
	private TextView mLoadingTv;
	private int mResid;


	public CustomProgressDialog(Context context, int tipId) {
		this(context, context.getResources().getString(tipId));
	}

	public  CustomProgressDialog(Context context, CharSequence tip) {
        this(context, R.style.dialog, tip, R.drawable.frame_loadin);
	}

	public CustomProgressDialog(Context context, int theme, CharSequence tip, int id) {
		super(context, theme);
		this.mLoadingTip = tip;
		this.mResid = id;
		setCanceledOnTouchOutside(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
		init();
	}

	private void init() {
        mLoadingTv = (TextView) findViewById(R.id.loadingTv);
        mImageView = (ImageView) findViewById(R.id.loadingIv);
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

	@Override
	public void dismiss() {
		super.dismiss();
	}
}
