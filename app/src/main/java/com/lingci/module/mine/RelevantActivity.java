package com.lingci.module.mine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lingci.R;
import com.lingci.common.Api;
import com.lingci.common.util.DateComparUtil;
import com.lingci.common.util.MoeToast;
import com.lingci.common.util.SPUtils;
import com.lingci.common.util.Utils;
import com.lingci.common.util.ViewHolder;
import com.lingci.common.view.CustomProgressDialog;
import com.lingci.emojicon.EmojiconTextView;
import com.lingci.entity.MiniFeeds.Data.MiniFeed;
import com.lingci.entity.UnReadMf;
import com.lingci.entity.UnReadMf.Data.Unread;
import com.lingci.module.BaseActivity;
import com.lingci.module.mood.MinifeedActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;

public class RelevantActivity extends BaseActivity {
	
	private TextView tv_top;
	private LinearLayout lc_ruturn;
	private PullToRefreshListView pull_relevant_list;
	private RelevantAdapter relevantAdapter;
	private CustomProgressDialog loadingProgress;
	private String savename;
	private List<Unread> unreadlist;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_relevant);
		init();
	}

	private void init() {
		savename = SPUtils.getInstance(RelevantActivity.this).getString("username", "");
		tv_top = (TextView) this.findViewById(R.id.tv_top);
		lc_ruturn = (LinearLayout) this.findViewById(R.id.lc_ruturn);
		pull_relevant_list = (PullToRefreshListView) findViewById(R.id.pull_relevant_list);
		lc_ruturn.setVisibility(View.VISIBLE);
		tv_top.setText("与我相关");
		int x = (int) (Math.random() * 4) + 1;
		if(x==1){
//			ToastUtil.showSingleton(this, "你能找到这个秘密吗？");
			MoeToast.makeText(this, "你能找到这个秘密吗？");
		}
		loadingProgress = new CustomProgressDialog(this,"正在加载...", R.drawable.frame_loadin);
		lc_ruturn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		relevantAdapter = new RelevantAdapter();
		loadingProgress.show();
		getRelevantsnAsyncHttpPost(savename);
		
		pull_relevant_list.setMode(Mode.BOTH);
		initPullToRefresh();
		pull_relevant_list.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				getRelevantsnAsyncHttpPost(savename);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				
			}
		});
	}
	
	private void initPullToRefresh() {
		ILoadingLayout startLabels = pull_relevant_list.getLoadingLayoutProxy(true,false);
		startLabels.setPullLabel("下拉刷新...");
		startLabels.setRefreshingLabel("正在刷新...");
		startLabels.setReleaseLabel("放开刷新...");

		ILoadingLayout endLabels = pull_relevant_list.getLoadingLayoutProxy(false,true);
		endLabels.setPullLabel("上拉加载...");
		endLabels.setRefreshingLabel("正在加载...");
		endLabels.setReleaseLabel("放开加载...");
	}
	
	public void getRelevantsnAsyncHttpPost(String name) {
		OkHttpUtils.post()
				.url(Api.Url + "/unReadList")
				.addParams("uname", name)
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						loadingProgress.dismiss();
						Toast.makeText(RelevantActivity.this, "加载失败，下拉重新加载",Toast.LENGTH_LONG).show();
					}

					@Override
					public void onResponse(String response, int id) {
						Log.d(TAG, "onResponse: " + response);
						Gson gson = new Gson();
						UnReadMf unReadMf = gson.fromJson(response, UnReadMf.class);
						int tag = unReadMf.ret;
						List<Unread> unReads = unReadMf.data.unreadlist;
						loadingProgress.dismiss();
						switch (tag) {
							case 0:
								unreadlist = unReads;
								pull_relevant_list.setAdapter(relevantAdapter);
								break;
							default:
								break;
						}
						pull_relevant_list.onRefreshComplete();
					}
				});
	}
	
	/** 适配器 */
	private class RelevantAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			return unreadlist == null ? 0 : unreadlist.size();
		}

		@Override
		public Object getItem(int position) {
			return unreadlist == null ? null : unreadlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(RelevantActivity.this);
				convertView = inflater.inflate(R.layout.list_item_relevant, null);
			}
			ImageView rluser_img = ViewHolder.get(convertView, R.id.rluser_img);
			TextView rl_uname = ViewHolder.get(convertView, R.id.rl_uname);
			TextView rl_time = ViewHolder.get(convertView, R.id.rl_time);
			EmojiconTextView rl_comment = ViewHolder.get(convertView, R.id.rl_comment);
			LinearLayout rl_lc = ViewHolder.get(convertView, R.id.rl_lc);
			TextView rl_lcuname = ViewHolder.get(convertView, R.id.rl_lcuname);
			EmojiconTextView rl_minifeed = ViewHolder.get(convertView, R.id.rl_minifeed);
			Unread unread = null;
			if (unreadlist != null) {
				unread = unreadlist.get(position);
				rl_uname.setText(unread.uname);
				rl_time.setText(DateComparUtil.getInterval(unread.cm_time));
				rl_comment.setText(unread.comment);
				rl_lcuname.setText(unread.minifeed.uname);
				rl_minifeed.setText(unread.minifeed.lc_info);
				if (unread.uname.equals(savename)) {
					Utils.setPersonImg(savename,rluser_img);
				}else{
					if (unread.url!=null) {
						Glide.with(RelevantActivity.this)
								.load(Api.Url + unread.url)
								.skipMemoryCache(true)
								.into(rluser_img);
					}else{
						rluser_img.setImageResource(R.mipmap.userimg);
					}
				}
			}
			final MiniFeed mfd = unread.minifeed;
			rl_lc.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(RelevantActivity.this,MinifeedActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("minifeed", mfd);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});
			return convertView;
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

}
