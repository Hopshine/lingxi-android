package com.lingci.common;

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

    public static List<UserInfo> userList = new ArrayList<>();
    public static List<String> uidList = new ArrayList<>();
}
