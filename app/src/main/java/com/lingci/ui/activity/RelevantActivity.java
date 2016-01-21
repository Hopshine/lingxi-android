package com.lingci.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lingci.R;
import com.lingci.constants.GlobalParame;
import com.lingci.constants.PreferencesManager;
import com.lingci.model.MiniFeeds.Data.MiniFeed;
import com.lingci.model.UnReadMf;
import com.lingci.model.UnReadMf.Data.Unread;
import com.lingci.utils.DaHttpRequest;
import com.lingci.utils.DateComparUtil;
import com.lingci.utils.MoeToast;
import com.lingci.utils.RepeatUtils;
import com.lingci.utils.ViewHolder;
import com.lingci.views.CustomProgressDialog;
import com.lingci.views.RoundImageView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rockerhieu.emojicon.EmojiconTextView;

import org.apache.http.Header;

import java.util.List;

public class RelevantActivity extends Activity {
	
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
		// TODO Auto-generated method stub
		savename = PreferencesManager.getInstance().getString("username", "");
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
		loadingProgress = new CustomProgressDialog(this,"正在加载...", R.anim.frame_loadin);
		lc_ruturn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub
				getRelevantsnAsyncHttpPost(savename);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void initPullToRefresh() {
		// TODO Auto-generated method stub
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
		String path = GlobalParame.URl + "/unReadList";
		DaHttpRequest dr = new DaHttpRequest(this);
		RequestParams params = new RequestParams();
		params.put("uname", name);
		dr.post(path, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				loadingProgress.dismiss();
				Toast.makeText(RelevantActivity.this, "加载失败，下拉重新加载",Toast.LENGTH_LONG).show();
				// Log.i("TAG", "请求失败：" + new String(arg2));
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				String minifeedStr = new String(arg2);
				Log.i("hello", "请求成功：" + minifeedStr);
				Gson gson = new Gson();
				UnReadMf unReadMf = gson.fromJson(minifeedStr, UnReadMf.class);
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
			// TODO Auto-generated method stub
			return unreadlist == null ? 0 : unreadlist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return unreadlist == null ? null : unreadlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(RelevantActivity.this);
				convertView = inflater.inflate(R.layout.list_item_relevant, null);
			}
			RoundImageView rluser_img = ViewHolder.get(convertView, R.id.rluser_img);
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
					GlobalParame.setPersonImg(savename,rluser_img);
				}else{
					if (unread.url!=null) {
						ImageLoader.getInstance().displayImage(GlobalParame.URl+unread.url,rluser_img, GlobalParame.getOptionsUnDisc());
					}else{
						rluser_img.setImageBitmap(RepeatUtils.readBitMap(RelevantActivity.this, R.drawable.userimg));
					}
				}
			}
			final MiniFeed mfd = unread.minifeed;
			rl_lc.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.relevant, menu);
		return true;
	}

}
