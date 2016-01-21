package com.lingci.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.lingci.model.MiniFeeds;
import com.lingci.model.MiniFeeds.Data.MiniFeed;
import com.lingci.model.MiniFeeds.Data.MiniFeed.Like;
import com.lingci.ui.activity.MinifeedActivity;
import com.lingci.utils.DaHttpRequest;
import com.lingci.utils.DateComparUtil;
import com.lingci.utils.RepeatUtils;
import com.lingci.utils.ViewHolder;
import com.lingci.views.CustomProgressDialog;
import com.lingci.views.RoundImageView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rockerhieu.emojicon.EmojiconTextView;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

	private PullToRefreshListView pullToRefresh;
	private MiniFeeds minifeed;
	private List<MiniFeed> minifeeds;
	private List<String> lcids;
	private MiniFeddAdapter mfAdapter;
	private CustomProgressDialog loadingProgress;
	private final int MOD_REFRESH = 1;
	private final int MOD_LOADING = 2;
	private int PullToRefreshMODE;
	private int Loading_num = 0;
	private String savename;
	private Handler handler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		init(view);
		return view;
	}
	
	private void init(View view) {
		// TODO Auto-generated method stub
		savename = PreferencesManager.getInstance().getString("username", "");
		minifeeds = new ArrayList<MiniFeed>();
		lcids = new ArrayList<String>();
		loadingProgress = new CustomProgressDialog(getActivity(),
				R.string.dialog_loading_lc, R.anim.frame_loadin);
		pullToRefresh = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
		mfAdapter = new MiniFeddAdapter();
		pullToRefresh.setMode(Mode.BOTH);
		initPullToRefresh();
		String minifeedStr = PreferencesManager.getInstance().getString("minifeedStr", "");
		if (minifeedStr == "" && minifeedStr.length() == 0) {
			getMiniFeedsnAsyncHttpPost("0", "10");
			loadingProgress.show();
		} else {
			minifeed = getMiniFeeds(minifeedStr);
			minifeeds = minifeed.data.minifeedlist;
			lcids.clear();
			for (MiniFeed miniFeed : minifeeds) {
				lcids.add(miniFeed.lcid + "");
			}
			pullToRefresh.setAdapter(mfAdapter);
		}

		pullToRefresh.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				PullToRefreshMODE = MOD_REFRESH;
				getMiniFeedsnAsyncHttpPost("0", "10");
				mfAdapter.notifyDataSetChanged();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				PullToRefreshMODE = MOD_LOADING;
				Loading_num = minifeeds.size();
				getMiniFeedsnAsyncHttpPost(Loading_num + "", "10");
				mfAdapter.notifyDataSetChanged();
			}
		});

		pullToRefresh.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),MinifeedActivity.class);
				MiniFeed mfd = minifeeds.get(position-1);
				Bundle bundle = new Bundle();
				bundle.putSerializable("minifeed", mfd);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void initPullToRefresh() {
		// TODO Auto-generated method stub
		ILoadingLayout startLabels = pullToRefresh.getLoadingLayoutProxy(true,false);
		startLabels.setPullLabel("下拉刷新...");
		startLabels.setRefreshingLabel("正在刷新...");
		startLabels.setReleaseLabel("放开刷新...");

		ILoadingLayout endLabels = pullToRefresh.getLoadingLayoutProxy(false,true);
		endLabels.setPullLabel("上拉加载...");
		endLabels.setRefreshingLabel("正在加载...");
		endLabels.setReleaseLabel("放开加载...");
	}

	public void getMiniFeedsnAsyncHttpPost(String startlcid, String count) {
		String path = GlobalParame.URl + "/minifeedList";
		DaHttpRequest dr = new DaHttpRequest(getActivity());
		RequestParams params = new RequestParams();
		params.put("startlcid", startlcid);
		params.put("count", count);
		params.put("uname",PreferencesManager.getInstance().getString("username", ""));
		dr.post(path, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				loadingProgress.dismiss();
				pullToRefresh.onRefreshComplete();
				Toast.makeText(getActivity(), R.string.toast_getmf_error,Toast.LENGTH_SHORT).show();
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
				minifeed = getMiniFeeds(minifeedStr);
				int tag = minifeed.ret;
				List<MiniFeed> mfs = minifeed.data.minifeedlist;
				loadingProgress.dismiss();
				switch (tag) {
				case 0:
					switch (PullToRefreshMODE) {
					case MOD_REFRESH:
						PreferencesManager.getInstance().putString("minifeedStr", minifeedStr);
						for (int i = 0; i < mfs.size(); i++) {
							if (lcids.contains(mfs.get(i).lcid + "")) {
								minifeeds = mfs;
							} else {
								minifeeds.add(i, mfs.get(i));
							}
						}
						mfAdapter.notifyDataSetChanged();
						break;
					case MOD_LOADING:
						for (MiniFeed miniFeed : mfs) {
							minifeeds.add(miniFeed);
						}
						mfAdapter.notifyDataSetChanged();
						break;
					default:
						if (minifeeds.size() == 0) {
							PreferencesManager.getInstance().putString("minifeedStr", minifeedStr);
							minifeeds = mfs;
							pullToRefresh.setAdapter(mfAdapter);
						} else {
							minifeeds = mfs;
							mfAdapter.notifyDataSetChanged();
						}
						break;
					}
					lcids.clear();
					for (MiniFeed miniFeed : minifeeds) {
						lcids.add(miniFeed.lcid + "");
					}
					pullToRefresh.onRefreshComplete();
					break;
				}
			}
		});
	}

	public void addLikesnAsyncHttpPost(String lcid, String uname) {
		String path = GlobalParame.URl + "/addLike";
		// lcid uid uname
		DaHttpRequest dr = new DaHttpRequest(getActivity());
		RequestParams params = new RequestParams();
		params.put("lcid", lcid);
		params.put("uname", uname);
		dr.post(path, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				Log.i("hello", "请求失败：");
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				String str = new String(arg2);
				Log.i("hello", "请求成功：" + str);
//				pullToRefresh.onRefreshComplete();
//				getMiniFeedsnAsyncHttpPost(likeLcid+"","1");
			}
		});
	}

	/** 解析数据 */
	public MiniFeeds getMiniFeeds(String json) {
		Gson gson = new Gson();
		MiniFeeds minifeeds = gson.fromJson(json, MiniFeeds.class);
		return minifeeds;
	}

	/** 适配器 */
	private class MiniFeddAdapter extends BaseAdapter {
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return minifeeds == null ? 0 : minifeeds.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return minifeeds == null ? null : minifeeds.get(position);
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
				LayoutInflater inflater = LayoutInflater.from(getActivity());
				convertView = inflater.inflate(R.layout.list_item_home, null);
			}
			RoundImageView user_img = ViewHolder.get(convertView, R.id.user_img);
			TextView tv_uname = ViewHolder.get(convertView, R.id.tv_uname);
			TextView pl_time = ViewHolder.get(convertView, R.id.pl_time);
			EmojiconTextView lc_info = ViewHolder.get(convertView, R.id.lc_info);
			LinearLayout mf_comment = ViewHolder.get(convertView,R.id.mf_comment);
			LinearLayout mf_like = ViewHolder.get(convertView, R.id.mf_like);
			TextView mf_see_num = ViewHolder.get(convertView, R.id.mf_see_num);
			TextView mf_comment_num = ViewHolder.get(convertView,R.id.mf_comment_num);
			final TextView mf_like_num = ViewHolder.get(convertView,R.id.mf_like_num);
			final ImageView mf_like_icon = ViewHolder.get(convertView,R.id.mf_like_icon);
			RelativeLayout like_window = ViewHolder.get(convertView,R.id.like_window);
			TextView like_people = ViewHolder.get(convertView, R.id.like_people);
			TextView like_feel = ViewHolder.get(convertView,R.id.like_feel);

			if (minifeeds != null) {
				final MiniFeed mf = minifeeds.get(position);
				boolean isLike = mf.islike;
				tv_uname.setText(mf.uname);
				pl_time.setText(DateComparUtil.getInterval(mf.pl_time));
				lc_info.setText(mf.lc_info);
				mf_see_num.setText(String.valueOf(mf.viewnum));
				mf_comment_num.setText(String.valueOf(mf.cmtnum));
				mf_like_num.setText(String.valueOf(mf.likenum));
				if (isLike) {
					mf_like_icon.setImageResource(R.drawable.list_item_icon_like);
					mf_like.setClickable(false);
					mf_like.setEnabled(false);
				} else {
					mf_like_icon.setImageResource(R.drawable.list_item_icon_like_nor);
					mf_like.setClickable(true);
					mf_like.setEnabled(true);
				}
				List<Like> likes =  mf.likelist;
				String likeStr = getLikeStr(likes);
				String length = getIndent(likeStr.length());
				like_people.setText(likeStr);
				switch (mf.likenum) {
				case 0:
					mf_like_num.setText("赞");
					like_window.setVisibility(View.GONE);
					break;
				case 1:
				case 2:
				case 3:
					like_window.setVisibility(View.VISIBLE);
					like_feel.setText(length+"觉得很赞");
					break;
				default:
					like_window.setVisibility(View.VISIBLE);
					like_feel.setText(length+"等"+mf.likenum+"人觉得很赞");
					break;
				}
				if (mf.uname.equals(savename)) {
					GlobalParame.setPersonImg(savename,user_img);
				}else{
					if (mf.url!=null) {
						ImageLoader.getInstance().displayImage(GlobalParame.URl+mf.url,user_img, GlobalParame.getOptionsUnDisc());
					}else{
//						user_img.setImageResource(R.drawable.userimg);
						user_img.setImageBitmap(RepeatUtils.readBitMap(getActivity(), R.drawable.userimg));
					}
				}
				final int index = position;
				mf_comment.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(),MinifeedActivity.class);
						MiniFeed mfd = minifeeds.get(index);
						Bundle bundle = new Bundle();
						bundle.putSerializable("minifeed", mfd);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				});
				mf_like.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub=
						String lcid = String.valueOf(mf.lcid);
						String uname = PreferencesManager.getInstance().getString("username", "");
						Log.i("hello", "lcid=" + lcid + "uname=" + uname);
						Like like = new Like(0, uname);
						mf.setIslike(true);
						mf.setLikenum(mf.likenum + 1);
						mf.likelist.add(like);
						minifeeds.set(index, mf);
						addLikesnAsyncHttpPost(lcid, uname);
						mfAdapter.notifyDataSetChanged();
//						mf_like_num.setText(String.valueOf(mf.likenum + 1));
//						mf_like_icon.setImageResource(R.drawable.list_item_icon_like);
//						v.setClickable(false);
//						v.setEnabled(false);
					}
				});
			}
			return convertView;
		}
	}
	
	/** 获取点赞人字符串 */
	public String getLikeStr(List<Like> likes){
		String likeStr = "";
		switch (likes.size()) {
		case 0:
			break;
		case 1:
			likeStr = likes.get(0).uname;
			break;
		case 2:
			likeStr = likes.get(0).uname+"、"+likes.get(1).uname;
			break;
		default:
			likeStr = likes.get(0).uname+"、"+likes.get(1).uname+"、"+likes.get(2).uname;
			break;
		}
		return likeStr;
	}
	
	/** 获取缩进 */
	public String getIndent(int lenght){
		String indent = "";
		for (int i = 0; i < lenght; i++) {
			indent = indent + "\u3000";
		}
		return indent;
	}
}
