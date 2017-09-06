package me.cl.lingxi.common.config;

/**
 * api
 * Created by bafsj on 17/3/1.
 */
public class Api {

    public static boolean isDebug = false;
    private static String onLineApi = "http://139.224.128.232:10660/lingxi";
    private static String offLineApi = "http://cqytxyz.imwork.net:28513/lingci";
    public static String Url = isDebug ? offLineApi : onLineApi;
}
