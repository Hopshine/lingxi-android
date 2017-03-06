package com.lingci.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bafsj on 17/3/3.
 */

public class Mood {

    public int lcid;
    public int uid;
    public String uname;
    public String url;
    public boolean im_ability;
    public String lc_info;
    public String pl_time;
    public int viewnum;
    public int likenum;
    public int cmtnum;
    public boolean islike;
    public List<Like> likelist;

    public static class Like implements Serializable {

        public int uid;
        public String uname;

        public Like(int uid, String uname) {
            super();
            this.uid = uid;
            this.uname = uname;
        }

    }

}
