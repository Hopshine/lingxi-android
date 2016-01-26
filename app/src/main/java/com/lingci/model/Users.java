package com.lingci.model;

import java.util.List;

/**
 * Created by bafsj on 16/1/25.
 */
public class Users {

    public int ret;
    public Data data;

    public static final class Data{
        public int usernum;
        public List<User> userlist;

        public static final class User{

            public int uid;
            public String uname;
            public String url;
        }
    }
}
