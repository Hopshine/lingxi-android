package me.cl.lingxi.common.config;

/**
 * author : Bafs
 * e-mail : bafs.jy@live.com
 * time   : 2018/09/04
 * desc   : 嘀哩嘀哩appApi
 * version: 1.0
 */
public class DDApi {

    /* 周番，page: 0，pagesize: 12 */
    public static String weekArcType = "http://usr.005.tv/Appapi/api_getbyweekarctype";

    /* 榜单，type: news */
    public static String sortArcType = "http://usr.005.tv/Appapi/api_sortarctype";

    /* 番剧详情，typeid: 3357 */
    public static String arcTypeInfo = "http://usr.005.tv/Appapi/api_getarctypeinfo";

    /* 番剧剧集，typeid: 3357 */
    public static String arcHive = "http://usr.005.tv/Appapi/api_getarchive";

    /* 剧集详情，id: 67134 */
    public static String arcHiveInfo = "http://usr.005.tv/Appapi/api_getarchives";

    /* 最近更新，zhuangtai: 连载中 */
    public static String newsArcType = "http://usr.005.tv/Appapi/api_newsarctype";

    /* 播放配置，id: 1 */
    public static String playConfig = "http://usr.005.tv/Appapi/api_getconfig";

    /* 类型分类，tempindex: classify_tongyong|list_riqitongyong */
    public static String typeName = "http://usr.005.tv/Appapi/api_gettypename";

    /* 分类番剧，typeid: 3917，istype: riqi|classify */
    public static String arcByRaId = "http://usr.005.tv/Appapi/api_getbyraid";

    /* 搜索接口，keywords: 轻羽飞扬 */
    public static String searchArc = "http://usr.005.tv/Appapi/api_sosuoarctype";

}
