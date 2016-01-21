package com.lingci.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lingci.R;
import com.lingci.ui.adapter.ConversationListAdapterEx;

import io.rong.imkit.RongContext;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

public class MessageFragment extends Fragment implements ViewPager.OnPageChangeListener {

	private ViewPager message_viewpager;
	MyFragmentPagerAdapter myFragmentPagerAdapter;
	private Fragment mConversationFragment = null;
	private Fragment shareFragment = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_message, container, false);
		init(view);
		return view;
	}

	private void init(View view) {
		// TODO Auto-generated method stub
		message_viewpager = (ViewPager) view.findViewById(R.id.message_viewpager);
		myFragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager());
		message_viewpager.setAdapter(myFragmentPagerAdapter);
	}

	@Override
	public void onPageScrolled(int i, float v, int i1) {

	}

	@Override
	public void onPageSelected(int i) {

	}

	@Override
	public void onPageScrollStateChanged(int i) {

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
						fragment = listFragment;
					} else {
						fragment = mConversationFragment;
					}
					break;
				case 1:
					if(shareFragment == null){
						fragment = new ShareFragment();
					}else{
						fragment = shareFragment;
					}
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
