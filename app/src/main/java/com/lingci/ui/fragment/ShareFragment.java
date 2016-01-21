package com.lingci.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.lingci.R;
import com.lingci.constants.GlobalParame;
import com.lingci.constants.PreferencesManager;
import com.lingci.utils.DaHttpRequest;
import com.lingci.views.CustomProgressDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rockerhieu.emojicon.EmojiconEditText;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class ShareFragment extends Fragment {

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
		// TODO Auto-generated method stub
		shareProgress = new CustomProgressDialog(getActivity(), "发布中...",R.anim.frame_loadin);
		lc_info = (EmojiconEditText) view.findViewById(R.id.share_lc_info);
		share = (Button) view.findViewById(R.id.share_lc);
		share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String lcinfo = lc_info.getText().toString();
				Log.i("TAG", "lcinfo：" + lcinfo);
				if(lcinfo.equals(null)||lcinfo.length()==0){
					Toast.makeText(getActivity(), "未填写任何内容", Toast.LENGTH_SHORT).show();
				}else{
					String uname = PreferencesManager.getInstance().getString("username", null);
					if(uname.equals(null)||uname.length()==0){
						Toast.makeText(getActivity(), "请重新登陆", Toast.LENGTH_SHORT).show();
					}else{
						AddMFAsyncHttpPost(uname, lcinfo);
					}
				}
			}
		});
	}
	
	public void AddMFAsyncHttpPost(String uname,
			String lcinfo) {
		String path = GlobalParame.URl + "/minifeedAdd";
		DaHttpRequest dr = new DaHttpRequest(getActivity());
		RequestParams params = new RequestParams();
//		params.put("uid", uid);
		params.put("uname", uname);
		params.put("lcinfo", lcinfo);
		dr.post(path, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "发布失败", Toast.LENGTH_SHORT).show();
//				Log.i("TAG", "请求失败：" + new String(arg2));
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				shareProgress.show();
			}
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				String str = new String(arg2);
				Log.i("TAG", "请求成功：" + str);
				try {
					JSONObject json = new JSONObject(str);
					int tag = json.getInt("ret");
					shareProgress.dismiss();
					switch (tag) {
					case 0:
						lc_info.setText(null);
						Toast.makeText(getActivity(), "发布成功", Toast.LENGTH_SHORT).show();
						break;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
