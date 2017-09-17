package me.cl.lingxi.common.config;

/**
 * api
 * Created by bafsj on 17/3/1.
 */
public class Api {

    // 版本设置，isDebug为true为开发版本，为false为线上版本
    private static boolean isDebug = false;
    private static String onLineApi = "http://139.224.128.232:10660/lingxi";
//    private static String offLineApi = "http://cqytxyz.imwork.net:28513/lingci";
    private static String offLineApi = "http://139.224.128.232:10660/lingxi-dev";
    public static String baseUrl = isDebug ? offLineApi : onLineApi;


    // 具体接口地址设置
    public static String login = baseUrl + "/Login";
    public static String register = baseUrl + "/register";
    public static String updatePwd = baseUrl + "/updatePwd";

    public static String moodList = baseUrl + "/minifeedList";
    public static String publishMood = Api.baseUrl + "/minifeedAdd";
    public static String commentList = baseUrl + "/commentList";
    public static String addComment = baseUrl + "/addComment";
    public static String addReply = baseUrl + "/addReply";
    public static String addLike = baseUrl + "/addLike";

    public static String unReadList = baseUrl + "/unReadList";
    public static String imUser = baseUrl + "/getImUser";
    public static String unSeeNum = baseUrl + "/unSeeNum";
    public static String uploadPrimg = baseUrl + "/uploadPrimg";
    public static String imgBase = baseUrl + "/getImgbase";

}
