package com.lingci.entity;

import java.util.List;

/**
 * Created by Administrator on 2015/11/27.
 */
public class Likes {


    /**
     * minifeedlist : [{"lc_info":"进城进城","lcid":37,"likelist":[],"likenum":0,"pl_time":"2015-11-27 10:40:33.0","uid":26,"uname":"珞酱","viewnum":0},{"lc_info":"还没有删除的功能","lcid":36,"likelist":[],"likenum":0,"pl_time":"2015-11-26 17:01:40.0","uid":26,"uname":"珞酱","viewnum":0},{"lc_info":"正在上实验课，外面晴了天好蓝~","lcid":35,"likelist":[],"likenum":0,"pl_time":"2015-11-26 15:07:02.0","uid":28,"uname":"可燃","viewnum":0},{"lc_info":"把首页的卡顿修好了。大家，可以试试效果怎么样\u2026","lcid":34,"likelist":[{"uid":24,"uname":"零次"}],"likenum":1,"pl_time":"2015-11-26 14:35:20.0","uid":2,"uname":"十兵卫","viewnum":0},{"lc_info":"今天没课好开心","lcid":33,"likelist":[],"likenum":0,"pl_time":"2015-11-26 12:03:15.0","uid":26,"uname":"珞酱","viewnum":0},{"lc_info":"大家，中午好！十兵卫报道。","lcid":32,"likelist":[],"likenum":0,"pl_time":"2015-11-26 12:00:34.0","uid":26,"uname":"十兵卫","viewnum":0},{"lc_info":"吃香锅好开心","lcid":31,"likelist":[],"likenum":0,"pl_time":"2015-11-25 17:46:42.0","uid":26,"uname":"珞酱","viewnum":0},{"lc_info":"今天下午做什么呢，是点赞评论还是先研究图片上传？","lcid":27,"likelist":[],"likenum":0,"pl_time":"2015-11-25 14:22:51.0","uid":24,"uname":"零次","viewnum":0},{"lc_info":"珞酱才起床吗。。","lcid":26,"likelist":[],"likenum":0,"pl_time":"2015-11-25 12:57:16.0","uid":28,"uname":"可燃","viewnum":0},{"lc_info":"起床上高数","lcid":25,"likelist":[],"likenum":0,"pl_time":"2015-11-25 12:56:35.0","uid":26,"uname":"珞酱","viewnum":0}]
     * totalnum : 10
     */

    private DataEntity data;
    /**
     * data : {"minifeedlist":[{"lc_info":"进城进城","lcid":37,"likelist":[],"likenum":0,"pl_time":"2015-11-27 10:40:33.0","uid":26,"uname":"珞酱","viewnum":0},{"lc_info":"还没有删除的功能","lcid":36,"likelist":[],"likenum":0,"pl_time":"2015-11-26 17:01:40.0","uid":26,"uname":"珞酱","viewnum":0},{"lc_info":"正在上实验课，外面晴了天好蓝~","lcid":35,"likelist":[],"likenum":0,"pl_time":"2015-11-26 15:07:02.0","uid":28,"uname":"可燃","viewnum":0},{"lc_info":"把首页的卡顿修好了。大家，可以试试效果怎么样\u2026","lcid":34,"likelist":[{"uid":24,"uname":"零次"}],"likenum":1,"pl_time":"2015-11-26 14:35:20.0","uid":2,"uname":"十兵卫","viewnum":0},{"lc_info":"今天没课好开心","lcid":33,"likelist":[],"likenum":0,"pl_time":"2015-11-26 12:03:15.0","uid":26,"uname":"珞酱","viewnum":0},{"lc_info":"大家，中午好！十兵卫报道。","lcid":32,"likelist":[],"likenum":0,"pl_time":"2015-11-26 12:00:34.0","uid":26,"uname":"十兵卫","viewnum":0},{"lc_info":"吃香锅好开心","lcid":31,"likelist":[],"likenum":0,"pl_time":"2015-11-25 17:46:42.0","uid":26,"uname":"珞酱","viewnum":0},{"lc_info":"今天下午做什么呢，是点赞评论还是先研究图片上传？","lcid":27,"likelist":[],"likenum":0,"pl_time":"2015-11-25 14:22:51.0","uid":24,"uname":"零次","viewnum":0},{"lc_info":"珞酱才起床吗。。","lcid":26,"likelist":[],"likenum":0,"pl_time":"2015-11-25 12:57:16.0","uid":28,"uname":"可燃","viewnum":0},{"lc_info":"起床上高数","lcid":25,"likelist":[],"likenum":0,"pl_time":"2015-11-25 12:56:35.0","uid":26,"uname":"珞酱","viewnum":0}],"totalnum":10}
     * msg : ok
     * ret : 0
     */

    private String msg;
    private int ret;

    public void setData(DataEntity data) {
        this.data = data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public DataEntity getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public int getRet() {
        return ret;
    }

    public static class DataEntity {
        private int totalnum;
        /**
         * lc_info : 进城进城
         * lcid : 37
         * likelist : []
         * likenum : 0
         * pl_time : 2015-11-27 10:40:33.0
         * uid : 26
         * uname : 珞酱
         * viewnum : 0
         */

        private List<MinifeedlistEntity> minifeedlist;

        public void setTotalnum(int totalnum) {
            this.totalnum = totalnum;
        }

        public void setMinifeedlist(List<MinifeedlistEntity> minifeedlist) {
            this.minifeedlist = minifeedlist;
        }

        public int getTotalnum() {
            return totalnum;
        }

        public List<MinifeedlistEntity> getMinifeedlist() {
            return minifeedlist;
        }

        public static class MinifeedlistEntity {
            private String lc_info;
            private int lcid;
            private int likenum;
            private String pl_time;
            private int uid;
            private String uname;
            private int viewnum;
            private List<?> likelist;

            public void setLc_info(String lc_info) {
                this.lc_info = lc_info;
            }

            public void setLcid(int lcid) {
                this.lcid = lcid;
            }

            public void setLikenum(int likenum) {
                this.likenum = likenum;
            }

            public void setPl_time(String pl_time) {
                this.pl_time = pl_time;
            }

            public void setUid(int uid) {
                this.uid = uid;
            }

            public void setUname(String uname) {
                this.uname = uname;
            }

            public void setViewnum(int viewnum) {
                this.viewnum = viewnum;
            }

            public void setLikelist(List<?> likelist) {
                this.likelist = likelist;
            }

            public String getLc_info() {
                return lc_info;
            }

            public int getLcid() {
                return lcid;
            }

            public int getLikenum() {
                return likenum;
            }

            public String getPl_time() {
                return pl_time;
            }

            public int getUid() {
                return uid;
            }

            public String getUname() {
                return uname;
            }

            public int getViewnum() {
                return viewnum;
            }

            public List<?> getLikelist() {
                return likelist;
            }
        }
    }
}
