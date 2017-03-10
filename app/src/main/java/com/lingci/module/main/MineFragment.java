package com.lingci.module.main;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lingci.R;
import com.lingci.common.Api;
import com.lingci.common.Constants;
import com.lingci.common.util.SPUtils;
import com.lingci.common.util.Utils;
import com.lingci.module.member.LoginActivity;
import com.lingci.module.mine.PersonalInfoActivity;
import com.lingci.module.mine.RelevantActivity;
import com.lingci.module.setting.AboutActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class MineFragment extends Fragment implements OnClickListener {

    private TextView tv_top;
    private TextView tv_uname, mine_aet, mine_exit, mine_appinfo;
    private RelativeLayout mine_top;
    private ImageView user_img;
    private String uname;
    private OperateBroadcastReceiver receiver;

    private String content,certain,cancel;//退出dialog的内容，确定和取消按钮的提示


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        init(view);
        initReceiver();
        return view;
    }


    private final class OperateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.UPDATE_USERIMG.equals(action)) {
                Utils.setPersonImg(uname, user_img);
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
        tv_top = (TextView) view.findViewById(R.id.tv_top);
        tv_top.setText("个人中心");
        uname = SPUtils.getInstance(getActivity()).getString("username", "");
        tv_uname = (TextView) view.findViewById(R.id.mine_tv_uname);
        mine_top = (RelativeLayout) view.findViewById(R.id.mine_top);
        user_img = (ImageView) view.findViewById(R.id.mine_user_img);
        mine_aet = (TextView) view.findViewById(R.id.mine_aet);
        mine_appinfo = (TextView) view.findViewById(R.id.mine_appinfo);
        mine_exit = (TextView) view.findViewById(R.id.mine_exit);
        mine_top.setOnClickListener(this);
        mine_aet.setOnClickListener(this);
        mine_appinfo.setOnClickListener(this);
        mine_exit.setOnClickListener(this);
        //设置TextView左右图片
        Drawable mine_item_aet = getResources().getDrawable(R.mipmap.mine_item_aet);
        Drawable mine_item_right = getResources().getDrawable(R.mipmap.mine_item_right);
        Drawable mine_unread_right = getResources().getDrawable(R.mipmap.mine_unread_right);
        mine_item_aet.setBounds(0, 0, mine_item_aet.getIntrinsicWidth(), mine_item_aet.getIntrinsicHeight());
        mine_item_right.setBounds(0, 0, mine_item_right.getIntrinsicWidth(), mine_item_right.getIntrinsicHeight());
        mine_unread_right.setBounds(0, 0, mine_unread_right.getIntrinsicWidth(), mine_unread_right.getIntrinsicHeight());
        if (Constants.isRead) {
            mine_aet.setCompoundDrawables(mine_item_aet, null, mine_item_right, null);
        } else {
            mine_aet.setCompoundDrawables(mine_item_aet, null, mine_unread_right, null);
        }

        tv_uname.setText(uname);
        Utils.setPersonImg(uname, user_img);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_top:
                Intent goPerson = new Intent(getActivity(), PersonalInfoActivity.class);
                startActivity(goPerson);
                break;
            case R.id.mine_aet:
                Intent goRelevant = new Intent(getActivity(), RelevantActivity.class);
                startActivity(goRelevant);
                break;
            case R.id.mine_set:
                break;
            case R.id.mine_appinfo:
                Intent goAbout = new Intent(getActivity(), AboutActivity.class);
                startActivity(goAbout);
                break;
            case R.id.mine_exit:
                final Dialog dialog = new Dialog(getActivity(), R.style.dialog);
                dialog.setContentView(R.layout.prompt_dialog);
                TextView prompt_info = (TextView) dialog.findViewById(R.id.prompt_info);
                Button prompt_ok = (Button) dialog.findViewById(R.id.prompt_ok);
                Button prompt_cancel = (Button) dialog.findViewById(R.id.prompt_cancel);
                int x = (int) (Math.random() * 100) + 1;
                if(x==100){
                    content="偶是隐藏内容哦！100次退出才有一次能够看见我呢！";
                    certain ="就算你是隐藏人物我也要离开";
                    cancel="lucky,我还要去找到跟多的彩蛋";
                }else if(x<20){
                    content="o(>﹏<)o不要走！";
                    certain ="忍痛离开！";
                    cancel="好啦，好啦，我不走了。";
                }else if(x<40){
                    content="你走了就不要再回来，哼！(｀へ´)";
                    certain ="走就走！（(￣_,￣ )）";
                    cancel="额！（(⊙﹏⊙)，你停下了脚步）";
                }else if(x<60){
                    content="你真的要走么 ╥﹏╥...";
                    certain ="(ノへ￣、) 默默离开";
                    cancel="(⊙3⊙) 留下";
                }else if(x<80){
                    content="落花有意流水无情！";
                    certain ="便做春江都是泪，流不尽，许多愁!(⊙﹏⊙)";
                    cancel="花随水走,水载花流~~o(>_<)o ~~";
                }else if(x<100){
                    content="慢慢阳关路，劝君更进一杯酒！";
                    certain ="举杯邀明月，对影成三人。";
                    cancel="不醉不归！";
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
                break;
            default:
                break;
        }
    }

    public void loadUserImage() {
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
                        Bitmap userImage = stringtoBitmap(imgBase64);
                        user_img.setImageBitmap(userImage);
                    }
                });
    }

    /**
     * Base64字符串转换成Bitmap
     *
     * @param string imgBase64
     * @return Bitmap
     */
    public Bitmap stringtoBitmap(String string) {
        // 将字符串转换成Bitmap类型
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


}
