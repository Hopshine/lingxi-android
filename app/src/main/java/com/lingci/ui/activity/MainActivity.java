package com.lingci.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lingci.R;
import com.lingci.constants.GlobalParame;
import com.lingci.ui.fragment.HomeFragment;
import com.lingci.ui.fragment.MessageFragment;
import com.lingci.ui.fragment.MineFragment;
import com.lingci.ui.fragment.ShareFragment;
import com.lingci.utils.MoeToast;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

public class MainActivity extends FragmentActivity implements RongIM.UserInfoProvider {

    private RelativeLayout rl_title;
    private TextView tv_top;
    private long mExitTime = 0;
    private FragmentManager fragmentManager;
    private HomeFragment homefragment;
    private ShareFragment sharefragment;
    private MessageFragment messagefragment;
    private MineFragment minefragment;
    private ImageView[] imagebuttons;
    private TextView[] textviews;
    private int index;
    private int currentTabIndex;
    private TextView unread_msg_number;
    private String exit = "MM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        RongIM.setUserInfoProvider(this,true);
    }

    private void init() {
        // TODO Auto-generated method stub
        int num = this.getIntent().getFlags();
        fragmentManager = getSupportFragmentManager();
        rl_title = (RelativeLayout) this.findViewById(R.id.rl_title);
        tv_top = (TextView) this.findViewById(R.id.tv_top);
        unread_msg_number = (TextView) this.findViewById(R.id.unread_msg_number);
        tv_top.setText(R.string.title_bar_index);
        if (num > 0) {
            GlobalParame.isRead = false;
            unread_msg_number.setText("" + num);
            unread_msg_number.setVisibility(View.VISIBLE);
        }
        imagebuttons = new ImageView[4];
        imagebuttons[0] = (ImageView) findViewById(R.id.ib_home);
        imagebuttons[1] = (ImageView) findViewById(R.id.ib_share);
        imagebuttons[2] = (ImageView) findViewById(R.id.ib_message);
        imagebuttons[3] = (ImageView) findViewById(R.id.ib_mine);
        imagebuttons[0].setSelected(true);
        textviews = new TextView[4];
        textviews[0] = (TextView) findViewById(R.id.tv_home);
        textviews[1] = (TextView) findViewById(R.id.tv_share);
        textviews[2] = (TextView) findViewById(R.id.tv_message);
        textviews[3] = (TextView) findViewById(R.id.tv_mine);
        textviews[0].setSelected(true);
        setTabSelection(0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Fragment f = fragmentManager.findFragmentByTag("minefragment");
        f.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.re_home:
                index = 0;
                rl_title.setVisibility(View.VISIBLE);
                tv_top.setText(R.string.title_bar_index);
                setTabSelection(index);
                break;
            case R.id.re_share:
                index = 1;
                rl_title.setVisibility(View.VISIBLE);
                tv_top.setText(R.string.title_bar_share);
                setTabSelection(index);
                break;
            case R.id.re_message:
                index = 2;
                rl_title.setVisibility(View.GONE);
//                tv_top.setText(R.string.title_bar_mine);
                setTabSelection(index);
                break;
            case R.id.re_mine:
                index = 3;
                rl_title.setVisibility(View.VISIBLE);
                tv_top.setText(R.string.title_bar_mine);
                setTabSelection(index);
                break;
        }
        // 把当前tab设为选中状态
        imagebuttons[currentTabIndex].setSelected(false);
        imagebuttons[index].setSelected(true);
        textviews[currentTabIndex].setSelected(false);
        textviews[index].setSelected(true);
        currentTabIndex = index;
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index 每个tab页对应的下标。0表示首页，1表示分享，2表示用户
     */
    private void setTabSelection(int index) {
        // 开启一个Fragment事务
        // FragmentTransaction transaction = fragmentManager.beginTransaction();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        if (index == 0) {
            if (homefragment == null) {
                // 如果FragmentHome为空，则创建一个并添加到界面上
                homefragment = new HomeFragment();
                transaction.add(R.id.fragment_container, homefragment);
            } else {
                // 如果FragmentHome不为空，则直接将它显示出来
                transaction.show(homefragment);
            }
        } else if (index == 1) {
            if (sharefragment == null) {
                // 如果FragmentShare为空，则创建一个并添加到界面上
                sharefragment = new ShareFragment();
                transaction.add(R.id.fragment_container, sharefragment);
            } else {
                // 如果FragmentShare不为空，则直接将它显示出来
                transaction.show(sharefragment);
            }
        } else if (index == 2) {
            if (messagefragment == null) {
                // 如果FragmentMine为空，则创建一个并添加到界面上
                messagefragment = new MessageFragment();
                transaction.add(R.id.fragment_container, messagefragment, "minefragment");
            } else {
                // 如果FragmentMine不为空，则直接将它显示出来
                transaction.show(messagefragment);
            }
        } else if (index == 3) {
            if (minefragment == null) {
                // 如果FragmentMine为空，则创建一个并添加到界面上
                minefragment = new MineFragment();
                transaction.add(R.id.fragment_container, minefragment, "minefragment");
            } else {
                // 如果FragmentMine不为空，则直接将它显示出来
                transaction.show(minefragment);
            }
        }
        transaction.commit();
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (homefragment != null) {
            transaction.hide(homefragment);
        }
        if (sharefragment != null) {
            transaction.hide(sharefragment);
        }
        if (messagefragment != null) {
            transaction.hide(messagefragment);
        }
        if (minefragment != null) {
            transaction.hide(minefragment);
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
//                ToastUtil.showSingleton(this, "(ಥ _ ಥ)你难道要再按一次离开我么");
                MoeToast.makeText(this, "(ಥ _ ಥ)你难道要再按一次离开我么");
                mExitTime = System.currentTimeMillis();
            } else {
                int x = (int) (Math.random() * 10) + 1;
                if (exit.equals("MM")) {
                    if(x==10){
//                        ToastUtil.showSingleton(this, "恭喜你找到隐藏的偶，Game over!");
                        MoeToast.makeText(this, "恭喜你找到隐藏的偶，Game over!");
                        finish();
                    }else {
//                        ToastUtil.showSingleton(this, "你果然想要离开我(＠￣ー￣＠)");
                        MoeToast.makeText(this, "你果然想要离开我(＠￣ー￣＠)");
                    }
                    mExitTime = System.currentTimeMillis();
                    exit="mm";
                } else if (exit.equals("mm")){
                    exit="MM";
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
//    	ImageLoader.getInstance().clearMemoryCache();  
//    	ImageLoader.getInstance().clearDiscCache();  
    }

    @Override
    public UserInfo getUserInfo(String s) {
        if(s.equals("6")){
            return new UserInfo("6","命运的安排", Uri.parse("http://d.hiphotos.baidu.com/zhidao/wh%3D600%2C800/sign=295f784cb1de9c82a630f1895cb1ac32/faf2b2119313b07e7187cc7d0ed7912397dd8c89.jpg"));
        }else if(s.equals("7")){
            return new UserInfo("7", "历史的齿轮", Uri.parse("http://b.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=973525c1d0160924dc70aa1fe13719cc/f11f3a292df5e0feb8edcb965c6034a85fdf72ab.jpg"));
        }
        return null;
    }
}
