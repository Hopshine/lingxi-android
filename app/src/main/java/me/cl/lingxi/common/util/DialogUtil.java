package me.cl.lingxi.common.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Constants;
import me.cl.lingxi.module.member.LoginActivity;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/05/31
 * desc   : DialogUtil
 * version: 1.0
 */
public class DialogUtil {

    /**
     * 退出登录
     */
    public static void signOut(final Activity activity) {
        String content = "", certain = "", cancel = "";
        final Dialog dialog = new Dialog(activity, R.style.AppTheme_Dialog);
        dialog.setContentView(R.layout.dialog_prompt);
        TextView promptInfo = dialog.findViewById(R.id.prompt_info);
        Button promptOk = dialog.findViewById(R.id.prompt_ok);
        Button promptCancel = dialog.findViewById(R.id.prompt_cancel);
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
        promptInfo.setText(content);
        promptOk.setText(certain);
        promptCancel.setText(cancel);
        promptOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SPUtil.build().putBoolean(Constants.BEEN_LOGIN, false);
                Intent intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });
        promptCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
