package me.cl.lingxi.common.config;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.UserInfo;

/**
 * 常量
 * Created by bafsj on 17/3/1.
 */
public class Constants {

    public static boolean isRead = true;

    public static final String UPDATE_USER_IMG = "me.cl.update.img";

    public static final String ANIMATE_JSON = "dili.animate";
    public static final String ANIMATE_CACHE = "dili.cache";
    public static final String ANIMATE_SELECT = "dili.index";

    public static List<UserInfo> userList = new ArrayList<>();
    public static List<String> uidList = new ArrayList<>();

    // 本地缓存key
    public static final String USER_ID = "uid";
    public static final String USER_NAME = "username";

    // 页面标识
    public static final int ACTIVITY_MAIN = 10001;
    public static final int ACTIVITY_PUBLISH = 10002;
    public static final int ACTIVITY_MOOD = 10003;
    public static final int ACTIVITY_PERSONAL = 10004;



    // 回退标识
    public static final String GO_INDEX = "go_index";
    public static final int INDEX_MOOD = 1; // 圈子
    public static final int INDEX_MINE = 3; // 我的

    // oss图片处理
    public static final String IMG_RESIZE_HW250 = "?x-oss-process=image/resize,m_fill,h_250,w_250";
    public static final String IMG_RESIZE_HW720 = "?x-oss-process=image/resize,m_mfit,h_720,w_720";
}
