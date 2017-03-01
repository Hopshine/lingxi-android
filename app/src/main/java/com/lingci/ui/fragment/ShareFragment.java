package com.lingci.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lingci.R;
import com.lingci.common.Api;
import com.lingci.common.util.SPUtils;
import com.lingci.emojicon.EmojiconEditText;
import com.lingci.common.view.CustomProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class ShareFragment extends Fragment {

	private TextView tv_top;
	private EmojiconEditText lc_info;
	private Button share;
	private CustomProgressDialog shareProgress;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_share, container, false);
		init(view);
		return view;
	}

	private void init(View view) {
		tv_top = (TextView) view.findViewById(R.id.tv_top);
		tv_top.setText("普通的分享");
		shareProgress = new CustomProgressDialog(getActivity(), "发布中...",R.anim.frame_loadin);
		lc_info = (EmojiconEditText) view.findViewById(R.id.share_lc_info);
		share = (Button) view.findViewById(R.id.share_lc);
		share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String lcinfo = lc_info.getText().toString();
				Log.i("TAG", "lcinfo：" + lcinfo);
				if(lcinfo.equals(null)||lcinfo.length()==0){
					Toast.makeText(getActivity(), "未填写任何内容", Toast.LENGTH_SHORT).show();
				}else{
					String uname = SPUtils.getInstance(getActivity()).getString("username", null);
					if(uname.equals(null)||uname.length()==0){
						Toast.makeText(getActivity(), "请重新登陆", Toast.LENGTH_SHORT).show();
					}else{
						postAddMF(uname, lcinfo);
					}
				}
			}
		});
	}
	
	public void postAddMF(String uname, String lcinfo) {
		shareProgress.show();
		OkHttpUtils.post()
				.url(Api.Url + "/minifeedAdd")
				.addParams("uname", uname)
				.addParams("lcinfo", lcinfo)
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						shareProgress.dismiss();
						Toast.makeText(getActivity(), "发布失败", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onResponse(String response, int id) {
						shareProgress.dismiss();
						try {
							JSONObject json = new JSONObject(response);
							int tag = json.getInt("ret");
							switch (tag) {
								case 0:
									lc_info.setText(null);
									Toast.makeText(getActivity(), "发布成功", Toast.LENGTH_SHORT).show();
									break;
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}
}
