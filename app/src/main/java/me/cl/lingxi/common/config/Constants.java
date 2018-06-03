package me.cl.lingxi.common.config;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.UserInfo;
import me.cl.lingxi.BuildConfig;

/**
 * 常量
 */
public class Constants {

    public static boolean isRead = true;

    public static final String UPDATE_USER_IMG = "me.cl.update.img";

    public static final String UNREAD_NUM = "unread_num";
    public static final String USER_INFO = "user_info";

    public static List<UserInfo> userList = new ArrayList<>();
    public static List<String> uidList = new ArrayList<>();

    // 本地缓存key
    public static final String ANIMATE_JSON = "animate_json";
    public static final String ANIMATE_CACHE = "animate_cache";
    public static final String ANIMATE_SELECT = "animate_select";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String BEEN_LOGIN = "been_login";
    public static final String RC_USER = "rc_user";
    public static final String RC_TOKEN = "rc_token";
    public static final String UPDATE_FLAG = "update_flag";

    // 页面标识
    public static final int ACTIVITY_MAIN = 10001;
    public static final int ACTIVITY_PUBLISH = 10002;
    public static final int ACTIVITY_MOOD = 10003;
    public static final int ACTIVITY_PERSONAL = 10004;

    // 回退标识
    public static final String GO_INDEX = "go_index";

    // 服务器rss图片
    public static final String IMG_URL = BuildConfig.RssUrl;

}
