package me.cl.lingxi.common.config;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.UserInfo;

/**
 * 常量
 * Created by bafsj on 17/3/1.
 */
public class Constants {

    public static Activity main;

    public static boolean isRead = true;

    public static final String UPDATE_USERIMG = "com.lingci.updateimg";
    public static final String DILI_ANIMATE = "dilianimate";
    public static final String DILI_CACHE = "dilicache";

    public static List<UserInfo> userList = new ArrayList<>();
    public static List<String> uidList = new ArrayList<>();

    // 本地缓存key
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";

    // 页面标识
    public static final int ACTIVITY_MAIN = 10001;
    public static final int ACTIVITY_PUBLISH = 10002;
    public static final int ACTIVITY_MOOD = 10003;
    public static final int ACTIVITY_PERSONAL = 10004;



    // 回退标识
    public static final String GO_INDEX = "go_index";
    public static final int INDEX_MOOD = 1; // 圈子
    public static final int INDEX_MINE = 3; // 我的
}
