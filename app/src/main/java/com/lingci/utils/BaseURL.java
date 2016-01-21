package com.lingci.utils;

/**
 * 所有的网络访问路径的基类
 * @author wangqinghua
 *
 */
public class BaseURL {
	
	/**
	 * 网络调试模式开关
	 * false 关闭 （正式环境）
	 * true  打开（测试环境）
	 */
	private static boolean isDebug = true;
	
	/**
	 * 线上的地址
	 * 
	 */
	private static String onLineApi = "";
	
	/**
	 * 线下的地址
	 */
	private static String offLineApi = "";
	
	/**
	 * 网络访问地址
	 */
	public static String ROOT = isDebug ? offLineApi : onLineApi;
}

