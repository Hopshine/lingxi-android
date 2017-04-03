package com.lingci.common.config;

/**
 * api
 * Created by bafsj on 17/3/1.
 */
public class Api {

    public static boolean isDebug = false;
    private static String onLineApi = "http://139.224.128.232:10660/lingci";
    private static String offLineApi = "http://cqyt.zicp.net:34922/lingci";
    public static String Url = isDebug ? offLineApi : onLineApi;
}
