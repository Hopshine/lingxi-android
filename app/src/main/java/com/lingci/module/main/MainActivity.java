package com.lingci.module.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.lingci.R;
import com.lingci.common.Constants;
import com.lingci.common.util.MoeToast;
import com.lingci.common.util.SPUtils;
import com.lingci.module.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class MainActivity extends BaseActivity implements RongIM.UserInfoProvider {

    @BindView(R.id.bottom_navigation)
    BottomNavigationBar mBottomNavigation;

    private long mExitTime = 0;
    private FragmentManager fragmentManager;
    private HomeFragment homefragment;
    private MoodFragment mMoodFragment;
    private MessageFragment messagefragment;
    private MineFragment minefragment;

    private String exit = "MM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        initBottomNavigation();
        RongIM.setUserInfoProvider(this, true);
        RongIM.getInstance().setMessageAttachedUserInfo(true);
    }

    private void initBottomNavigation() {
        BottomNavigationItem home = new BottomNavigationItem(R.mipmap.icon_home, "主页").setInactiveIconResource(R.mipmap.icon_home_nor);
        BottomNavigationItem camera = new BottomNavigationItem(R.mipmap.icon_camera, "圈子").setInactiveIconResource(R.mipmap.icon_camera_nor);
        BottomNavigationItem message = new BottomNavigationItem(R.mipmap.icon_message, "消息").setInactiveIconResource(R.mipmap.icon_message_nor);
        BottomNavigationItem mine = new BottomNavigationItem(R.mipmap.icon_mine, "我的").setInactiveIconResource(R.mipmap.icon_mine_nor);
        mBottomNavigation.addItem(home).addItem(camera).addItem(message).addItem(mine)
                .setMode(BottomNavigationBar.MODE_FIXED)//切换模式
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)//背景风格
                .initialise();
        mBottomNavigation.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                setTabSelection(position);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

    private void init() {
        int num = this.getIntent().getFlags();
        fragmentManager = getSupportFragmentManager();
        setTabSelection(0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment f = fragmentManager.findFragmentByTag("minefragment");
        f.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     * @param index 每个tab页对应的下标。0表示首页，1表示分享，2表示用户
     */
    private void setTabSelection(int index) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        if (index == 0) {
            if (homefragment == null) {
                homefragment = new HomeFragment();
                transaction.add(R.id.fragment_container, homefragment);
            } else {
                transaction.show(homefragment);
            }
        } else if (index == 1) {
            if (mMoodFragment == null){
                mMoodFragment = MoodFragment.newInstance("", "");
                transaction.add(R.id.fragment_container, mMoodFragment);
            } else {
                transaction.show(mMoodFragment);
            }
        } else if (index == 2) {
            if (messagefragment == null) {
                messagefragment = new MessageFragment();
                transaction.add(R.id.fragment_container, messagefragment, "messagefragment");
            } else {
                transaction.show(messagefragment);
            }
        } else if (index == 3) {
            if (minefragment == null) {
                minefragment = new MineFragment();
                transaction.add(R.id.fragment_container, minefragment, "minefragment");
            } else {
                transaction.show(minefragment);
            }
        }
        transaction.commit();
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (homefragment != null) {
            transaction.hide(homefragment);
        }
        if (messagefragment != null) {
            transaction.hide(messagefragment);
        }
        if (minefragment != null) {
            transaction.hide(minefragment);
        }
        if (mMoodFragment != null) {
            transaction.hide(mMoodFragment);
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                MoeToast.makeText(this, "(ಥ _ ಥ)你难道要再按一次离开我么");
                mExitTime = System.currentTimeMillis();
            } else {
                int x = (int) (Math.random() * 10) + 1;
                if (exit.equals("MM")) {
                    if (x == 10) {
                        MoeToast.makeText(this, "恭喜你找到隐藏的偶，Game over!");
                        finish();
                    } else {
                        MoeToast.makeText(this, "你果然想要离开我(＠￣ー￣＠)");
                    }
                    mExitTime = System.currentTimeMillis();
                    exit = "mm";
                } else if (exit.equals("mm")) {
                    exit = "MM";
                    finish();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        String save_date = SPUtils.getInstance(MainActivity.this).getString("save_date",null);
//        if(save_date == null || save_date.length() == 0){
//            saveTiem();
//            ToastUtil.showSingleton(this,DateComparUtil.getDuration(save_date));
//        }else if(DateComparUtil.getDuration(save_date).equals("7")){
//            ImageLoader.getInstance().clearMemoryCache();
//            ImageLoader.getInstance().clearDiscCache();
//            saveTiem();
//        }
    }

    private void saveTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(date);
        SPUtils.getInstance(MainActivity.this).putString("save_date", dateStr);
    }

    @Override
    public UserInfo getUserInfo(String s) {
        for (UserInfo userInfo : Constants.userList) {
            if (userInfo.getUserId().equals(s)) {
                return userInfo;
            }
        }
        if (s.equals("6")) {
            return new UserInfo("6", "命运的安排", Uri.parse("http://d.hiphotos.baidu.com/zhidao/wh%3D600%2C800/sign=295f784cb1de9c82a630f1895cb1ac32/faf2b2119313b07e7187cc7d0ed7912397dd8c89.jpg"));
        } else if (s.equals("7")) {
            return new UserInfo("7", "历史的齿轮", Uri.parse("http://b.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=973525c1d0160924dc70aa1fe13719cc/f11f3a292df5e0feb8edcb965c6034a85fdf72ab.jpg"));
        }
        return null;
    }
}
