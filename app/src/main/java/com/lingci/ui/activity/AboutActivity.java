package com.lingci.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lingci.R;
import com.lingci.utils.MoeToast;

public class AboutActivity extends Activity {
	
	private LinearLayout lc_ruturn;
	private TextView tv_top;
	private WebView about_info;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		lc_ruturn = (LinearLayout) findViewById(R.id.lc_ruturn);
		tv_top = (TextView) findViewById(R.id.tv_top);
		about_info = (WebView) findViewById(R.id.about_info);
		lc_ruturn.setVisibility(View.VISIBLE);
		tv_top.setText("关于普通的APP");
		int x = (int) (Math.random() * 3) + 1;
		if(x==1){
//			ToastUtil.showSingleton(this,"据说这APP隐藏着一个不为人知的秘密！");
			MoeToast.makeText(this,"据说这APP隐藏着一个不为人知的秘密！");
		}
		about_info.loadUrl(" file:///android_asset/about.html ");
		lc_ruturn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

}
