package me.cl.lingxi.common.config;

import me.cl.lingxi.BuildConfig;

/**
 * api manage
 * Created by bafsj on 17/3/1.
 */
public class Api {

    // 版本设置由gradle接管，通过flavor控制多版本
    public static String baseUrl = BuildConfig.BaseUrl;

    // v1 api
    public static String login = baseUrl + "/Login";
    public static String register = baseUrl + "/register";
    public static String updatePwd = baseUrl + "/updatePwd";

    @Deprecated
    public static String moodList = baseUrl + "/minifeedList";
    @Deprecated
    public static String publishMood = baseUrl + "/minifeedAdd";
    public static String commentList = baseUrl + "/commentList";
    public static String addComment = baseUrl + "/addComment";
    public static String addReply = baseUrl + "/addReply";
    public static String addLike = baseUrl + "/addLike";

    public static String unReadList = baseUrl + "/unReadList";
    public static String imUser = baseUrl + "/getImUser";
    public static String unSeeNum = baseUrl + "/unSeeNum";
    public static String uploadPrimg = baseUrl + "/uploadPrimg";
    public static String imgBase = baseUrl + "/getImgbase";


    // v2 api
    public static String addFeed = baseUrl + "/feed/addFeed";
    public static String listFeed = baseUrl + "/feed/listFeed";

    public static String appUpdate = baseUrl + "/config/appUpdate";

    private static String ossBase = "http://139.224.128.232:10660/lingxi";
    public static String ossToken = ossBase + "/oss/distributeToken";

}
