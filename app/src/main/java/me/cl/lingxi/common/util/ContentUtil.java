package me.cl.lingxi.common.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.cl.lingxi.R;
import me.cl.lingxi.common.config.Constants;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/05/29
 * desc   : 内容管理
 * version: 1.0
 */
public class ContentUtil {

    /**
     * 设置与我相关提示
     */
    public static void setMoreBadge(TextView textView) {
        Context context = textView.getContext();
        Drawable mineItemAt = context.getResources().getDrawable(R.drawable.ic_eit);
        Drawable mineItemRight = context.getResources().getDrawable(R.drawable.ic_more);
        Drawable mineBadgeRight = context.getResources().getDrawable(R.drawable.ic_more_badge);
        mineItemAt.setBounds(0, 0, mineItemAt.getIntrinsicWidth(), mineItemAt.getIntrinsicHeight());
        mineItemRight.setBounds(0, 0, mineItemRight.getIntrinsicWidth(), mineItemRight.getIntrinsicHeight());
        mineBadgeRight.setBounds(0, 0, mineBadgeRight.getIntrinsicWidth(), mineBadgeRight.getIntrinsicHeight());
        if (Constants.isRead) {
            textView.setCompoundDrawables(mineItemAt, null, mineItemRight, null);
        } else {
            textView.setCompoundDrawables(mineItemAt, null, mineBadgeRight, null);
        }
    }

    /**
     * 设置用户头像
     */
    public static void setUserAvatar(ImageView imageView, String url) {
        Context context = imageView.getContext();
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.img_user)
                .error(R.drawable.img_user)
                .bitmapTransform(new CropCircleTransformation(context))
                .into(imageView);
    }

}
