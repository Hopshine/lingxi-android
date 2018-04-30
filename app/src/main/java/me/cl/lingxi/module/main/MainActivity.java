package me.cl.lingxi.module.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import me.cl.library.base.BaseActivity;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.view.MoeToast;
import me.cl.library.utils.BottomNavigationViewHelper;

public class MainActivity extends BaseActivity implements RongIM.UserInfoProvider {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView mBottomNavigation;

    private FragmentManager mFragmentManager;
    private HomeFragment mHomeFragment;
    private DliFragment mDliFragment;
    private FeedFragment mFeedFragment;
    private MessageFragment mMessageFragment;
    private MineFragment mMineFragment;

    private String mExit = "MM";
    private long mExitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        RongIM.setUserInfoProvider(this, true);
        RongIM.getInstance().setMessageAttachedUserInfo(true);

        initFragment();
        initBottomNavigation();

        int num = this.getIntent().getFlags();
    }

    private void initFragment() {
        mFragmentManager = getSupportFragmentManager();
        mHomeFragment = new HomeFragment();
        mDliFragment = new DliFragment();
        mFeedFragment = FeedFragment.newInstance("home");
        mMessageFragment = new MessageFragment();
        mMineFragment = new MineFragment();
        switchFragment(mDliFragment);
    }

    //底部导航
    private void initBottomNavigation() {
        BottomNavigationViewHelper.disableShiftMode(mBottomNavigation);
        mBottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        switchFragment(mDliFragment);
                        return true;
                    case R.id.navigation_camera:
                        switchFragment(mFeedFragment);
                        return true;
                    case R.id.navigation_interactive:
                        switchFragment(mMessageFragment);
                        return true;
                    case R.id.navigation_mine:
                        switchFragment(mMineFragment);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Constants.ACTIVITY_PUBLISH:
                int id = data.getIntExtra(Constants.GO_INDEX, R.id.navigation_home);
                // 非导航本身事件，手动切换
                mBottomNavigation.setSelectedItemId(id);
                break;
            case Constants.ACTIVITY_PERSONAL:
                mMineFragment.onActivityResult(requestCode, resultCode, data);
                break;
            default:
                break;
        }
    }

    private Fragment currentFragment;

    /**
     * 切换Fragment
     */
    private void switchFragment(Fragment targetFragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (!targetFragment.isAdded()) {
            //首次currentFragment为null
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.fragment_container, targetFragment, targetFragment.getClass().getName());
        } else {
            transaction.hide(currentFragment).show(targetFragment);
        }
        currentFragment = targetFragment;
        transaction.commitAllowingStateLoss();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                MoeToast.makeText(this, "(ಥ _ ಥ)你难道要再按一次离开我么");
                mExitTime = System.currentTimeMillis();
            } else {
                int x = (int) (Math.random() * 10) + 1;
                if ("MM".equals(mExit)) {
                    if (x == 10) {
                        MoeToast.makeText(this, "恭喜你找到隐藏的偶，Game over!");
                        finish();
                    } else {
                        MoeToast.makeText(this, "你果然想要离开我(＠￣ー￣＠)");
                    }
                    mExitTime = System.currentTimeMillis();
                    mExit = "mm";
                } else if ("mm".equals(mExit)) {
                    mExit = "MM";
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
