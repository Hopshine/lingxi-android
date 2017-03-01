package com.lingci.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.lingci.R;
import com.lingci.ui.adapter.ConversationListAdapterEx;

import io.rong.imkit.RongContext;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

public class MessageFragment extends Fragment implements ViewPager.OnPageChangeListener ,View.OnClickListener{

	private ViewPager message_viewpager;
	private MyFragmentPagerAdapter myFragmentPagerAdapter;
	private Fragment mConversationFragment = null;
	private Fragment mContactsFragment = null;
	private ImageView msg_switch_img;
	private int indicatorWidth;
	private TextView msg_tv_conversation, msg_tv_friend;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_message, container, false);
		init(view);
		return view;
	}


	private void init(View view) {
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm); // 获取屏幕信息
		indicatorWidth = dm.widthPixels / 4;// 指示器宽度为屏幕宽度的4/1
		msg_switch_img = (ImageView) view.findViewById(R.id.msg_switch_img);
		msg_tv_conversation = (TextView) view.findViewById(R.id.msg_tv_conversation);
		msg_tv_friend = (TextView) view.findViewById(R.id.msg_tv_friend);
		message_viewpager = (ViewPager) view.findViewById(R.id.message_viewpager);
		myFragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager());
		message_viewpager.setAdapter(myFragmentPagerAdapter);
		message_viewpager.addOnPageChangeListener(this);

		ViewGroup.LayoutParams cursor_Params = msg_switch_img.getLayoutParams();
		cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
		msg_switch_img.setLayoutParams(cursor_Params);

		msg_tv_conversation.setOnClickListener(this);
		msg_tv_friend.setOnClickListener(this);
	}

	@Override
	public void onPageScrolled(int i, float v, int i1) {

	}

	@Override
	public void onPageSelected(int i) {
		switch (i) {
			case 0:
				msg_tv_conversation.setSelected(true);
				msg_tv_friend.setSelected(false);
				TranslateAnimation animation = new TranslateAnimation(indicatorWidth, 0, 0f, 0f);
				animation.setInterpolator(new LinearInterpolator());
				animation.setDuration(150);
				animation.setFillAfter(true);
				msg_switch_img.startAnimation(animation);
				break;
			case 1:
				msg_tv_friend.setSelected(true);
				msg_tv_conversation.setSelected(false);
				TranslateAnimation animation1 = new TranslateAnimation(0, indicatorWidth, 0f, 0f);
				animation1.setInterpolator(new LinearInterpolator());
				animation1.setDuration(150);
				animation1.setFillAfter(true);
				msg_switch_img.startAnimation(animation1);
				break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int i) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.msg_tv_conversation:
				message_viewpager.setCurrentItem(0);
				break;
			case R.id.msg_tv_friend:
				message_viewpager.setCurrentItem(1);
				break;
		}
	}

	private class MyFragmentPagerAdapter extends FragmentPagerAdapter{

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = null;
			switch (i) {
				case 0:
					if (mConversationFragment == null) {
						ConversationListFragment listFragment = ConversationListFragment.getInstance();
						listFragment.setAdapter(new ConversationListAdapterEx(RongContext.getInstance()));
						Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
								.appendPath("conversationlist")
								.appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
								.appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//群组
								.appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//讨论组
								.appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
								.appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
								.appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//系统
								.build();
						listFragment.setUri(uri);
						mConversationFragment = listFragment;
					}
					fragment = mConversationFragment;
					break;
				case 1:
					if(mContactsFragment == null){
						mContactsFragment = new ContactsFragment();
					}
					fragment = mContactsFragment;
					break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 2;
		}
	}
}
