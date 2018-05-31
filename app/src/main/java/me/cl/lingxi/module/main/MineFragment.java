package me.cl.lingxi.module.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.cl.library.base.BaseFragment;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Api;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.common.okhttp.OkUtil;
import me.cl.lingxi.common.okhttp.ResultCallback;
import me.cl.lingxi.common.util.ContentUtil;
import me.cl.lingxi.common.util.DialogUtil;
import me.cl.lingxi.common.util.SPUtil;
import me.cl.lingxi.common.util.Utils;
import me.cl.lingxi.entity.Result;
import me.cl.lingxi.entity.UserInfo;
import me.cl.lingxi.module.mine.PersonalInfoActivity;
import me.cl.lingxi.module.mine.RelevantActivity;
import me.cl.lingxi.module.setting.AboutActivity;
import okhttp3.Call;

public class MineFragment extends BaseFragment {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.user_img)
    ImageView mUserImg;
    @BindView(R.id.user_name)
    TextView mUserName;
    @BindView(R.id.user_description)
    TextView mUserDescription;
    @BindView(R.id.user_body)
    LinearLayout mUserBody;
    @BindView(R.id.mine_top)
    RelativeLayout mMineTop;
    @BindView(R.id.mine_reply)
    TextView mMineReply;
    @BindView(R.id.mine_relevant)
    TextView mMineRelevant;
    @BindView(R.id.mine_setting)
    TextView mMineSetting;
    @BindView(R.id.mine_about)
    TextView mMineAbout;
    @BindView(R.id.mine_sign_out)
    TextView mMineSignOut;

    private String mUserId;
    private OperateBroadcastReceiver receiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.bind(this, view);
        init(view);
        initReceiver();
        return view;
    }

    private void init(View view) {
        setupToolbar(mToolbar, R.string.nav_mine, 0, null);
        mUserId = SPUtil.build().getString(Constants.USER_ID);

        postUserInfo(mUserId);
    }


    private final class OperateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case Constants.UPDATE_USER_IMG:
                        postUserInfo(mUserId);
                        break;
                }
            }
        }
    }

    private void initReceiver() {
        receiver = new OperateBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.UPDATE_USER_IMG);
        getActivity().registerReceiver(receiver, filter);
    }

    private void postUserInfo(String id) {
        OkUtil.post()
                .url(Api.userInfo)
                .addParam("id", id)
                .execute(new ResultCallback<Result<UserInfo>>() {

                    @Override
                    public void onSuccess(Result<UserInfo> response) {
                        initUser(response.getData());
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        initUser(null);
                    }

                    @Override
                    public void onFinish() {
                        initUser(null);
                    }
                });
    }

    private void initUser(UserInfo userInfo) {
        String username = getString(R.string.app_name);
        String avatar = "";
        if (userInfo != null) {
            username = userInfo.getUsername();
            avatar = userInfo.getAvatar();
        }
        mUserName.setText(username);
        ContentUtil.loadUserAvatar(mUserImg, avatar);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        ContentUtil.setMoreBadge(mMineRelevant);
        if (Constants.isRead) ((MainActivity)getActivity()).goneBadge();
    }

    @OnClick({R.id.user_body, R.id.mine_reply, R.id.mine_relevant, R.id.mine_setting, R.id.mine_about, R.id.mine_sign_out})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_body:
                gotoPersonal();
                break;
            case R.id.mine_reply:
                break;
            case R.id.mine_relevant:
                gotoRelevant();
                break;
            case R.id.mine_setting:
                boolean isJoin = joinQQGroup("U6BT7JHlX9bzMdCNWjkIjwu5g3Yt_Wi9");
                if (!isJoin) {
                    Utils.toastShow(getActivity(), "未安装手Q或安装的版本不支持");
                }
                break;
            case R.id.mine_about:
                gotoAbout();
                break;
            case R.id.mine_sign_out:
                DialogUtil.signOut(getActivity());
                break;
        }
    }

    //前往信息修改
    private void gotoPersonal() {
        Intent goPerson = new Intent(getActivity(), PersonalInfoActivity.class);
        startActivity(goPerson);
    }

    //前往关于
    private void gotoAbout() {
        Intent goAbout = new Intent(getActivity(), AboutActivity.class);
        startActivity(goAbout);
    }

    //前往与我相关
    private void gotoRelevant() {
        Intent goRelevant = new Intent(getActivity(), RelevantActivity.class);
        startActivity(goRelevant);
    }


    /**
     * 发起添加群流程。群号：大龄儿童二次元同好群(468620613) 的 key 为： U6BT7JHlX9bzMdCNWjkIjwu5g3Yt_Wi9
     * 调用 joinQQGroup(U6BT7JHlX9bzMdCNWjkIjwu5g3Yt_Wi9) 即可发起手Q客户端申请加群 大龄儿童二次元同好群(468620613)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     */
    private boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }
}
