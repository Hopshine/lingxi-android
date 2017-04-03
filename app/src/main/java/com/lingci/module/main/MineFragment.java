package com.lingci.module.main;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lingci.R;
import com.lingci.common.config.Api;
import com.lingci.common.config.Constants;
import com.lingci.common.util.SPUtils;
import com.lingci.common.util.Utils;
import com.lingci.module.BaseFragment;
import com.lingci.module.member.LoginActivity;
import com.lingci.module.mine.PersonalInfoActivity;
import com.lingci.module.mine.RelevantActivity;
import com.lingci.module.setting.AboutActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    private String uName;
    private OperateBroadcastReceiver receiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.bind(this, view);
        init(view);
        initReceiver();
        return view;
    }


    private final class OperateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.UPDATE_USERIMG.equals(action)) {
                Utils.setPersonImg(uName, mUserImg);
            }
        }
    }

    private void initReceiver() {
        receiver = new OperateBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.UPDATE_USERIMG);
        getActivity().registerReceiver(receiver, filter);
    }

    private void init(View view) {
        setupToolbar(mToolbar, "我的", 0, null);
        uName = SPUtils.getInstance(getActivity()).getString("username");
        //设置TextView左右图片
        Drawable mine_item_aet = getResources().getDrawable(R.mipmap.mine_item_aet);
        Drawable mine_item_right = getResources().getDrawable(R.mipmap.mine_item_right);
        Drawable mine_unread_right = getResources().getDrawable(R.mipmap.mine_unread_right);
        mine_item_aet.setBounds(0, 0, mine_item_aet.getIntrinsicWidth(), mine_item_aet.getIntrinsicHeight());
        mine_item_right.setBounds(0, 0, mine_item_right.getIntrinsicWidth(), mine_item_right.getIntrinsicHeight());
        mine_unread_right.setBounds(0, 0, mine_unread_right.getIntrinsicWidth(), mine_unread_right.getIntrinsicHeight());
        if (Constants.isRead) {
            mMineRelevant.setCompoundDrawables(mine_item_aet, null, mine_item_right, null);
        } else {
            mMineRelevant.setCompoundDrawables(mine_item_aet, null, mine_unread_right, null);
        }

        mUserName.setText(uName);
        Utils.setPersonImg(uName, mUserImg);
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
                if (Api.isDebug) {
                    boolean isJoin = joinQQGroup("U6BT7JHlX9bzMdCNWjkIjwu5g3Yt_Wi9");
                    if (!isJoin) {
                        Utils.toastShow(getActivity(), "未安装手Q或安装的版本不支持");
                    }
                }
                break;
            case R.id.mine_about:
                gotoAbout();
                break;
            case R.id.mine_sign_out:
                signOut();
                break;
        }
    }

    private void loadUserImage() {
        OkHttpUtils.post()
                .url(Api.Url + "/getImgbase")
                .addParams("imgid", "1")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String imgBase64 = null;
                        try {
                            JSONObject json = new JSONObject(response);
                            imgBase64 = json.getString("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Bitmap userImage = strToBitmap(imgBase64);
                        mUserImg.setImageBitmap(userImage);
                    }
                });
    }

    //Base64字符串转换成Bitmap
    private Bitmap strToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
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

    //退出登录
    private void signOut() {
        String content = "", certain = "", cancel = "";
        final Dialog dialog = new Dialog(getActivity(), R.style.dialog);
        dialog.setContentView(R.layout.prompt_dialog);
        TextView prompt_info = (TextView) dialog.findViewById(R.id.prompt_info);
        Button prompt_ok = (Button) dialog.findViewById(R.id.prompt_ok);
        Button prompt_cancel = (Button) dialog.findViewById(R.id.prompt_cancel);
        int x = (int) (Math.random() * 100) + 1;
        if (x == 100) {
            content = "偶是隐藏内容哦！100次退出才有一次能够看见我呢！";
            certain = "就算你是隐藏人物我也要离开";
            cancel = "lucky,我还要去找到跟多的彩蛋";
        } else if (x < 20) {
            content = "o(>﹏<)o不要走！";
            certain = "忍痛离开！";
            cancel = "好啦，好啦，我不走了。";
        } else if (x < 40) {
            content = "你走了就不要再回来，哼！(｀へ´)";
            certain = "走就走！（(￣_,￣ )）";
            cancel = "额！（(⊙﹏⊙)，你停下了脚步）";
        } else if (x < 60) {
            content = "你真的要走么 ╥﹏╥...";
            certain = "(ノへ￣、) 默默离开";
            cancel = "(⊙3⊙) 留下";
        } else if (x < 80) {
            content = "落花有意流水无情！";
            certain = "便做春江都是泪，流不尽，许多愁!(⊙﹏⊙)";
            cancel = "花随水走,水载花流~~o(>_<)o ~~";
        } else if (x < 100) {
            content = "慢慢阳关路，劝君更进一杯酒！";
            certain = "举杯邀明月，对影成三人。";
            cancel = "不醉不归！";
        }
        prompt_info.setText(content);
        prompt_ok.setText(certain);
        prompt_cancel.setText(cancel);
        prompt_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SPUtils.getInstance(getActivity()).putBoolean("islogin", false);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        prompt_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
