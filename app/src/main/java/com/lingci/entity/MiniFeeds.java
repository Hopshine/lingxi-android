package com.lingci.entity;

import java.io.Serializable;
import java.util.List;

public class MiniFeeds  implements Serializable{

	public int ret;
	public Data data;

	public static final class Data  implements Serializable{

		public List<MiniFeed> minifeedlist;
		public int totalnum;

		public static final class MiniFeed implements Serializable{

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
			

			public void setLikenum(int likenum) {
				this.likenum = likenum;
			}

			public void setIslike(boolean islike) {
				this.islike = islike;
			}

			public void setLikelist(List<Like> likelist) {
				this.likelist = likelist;
			}

			public static class Like implements Serializable{

				public int uid;
				public String uname;
				
				public Like(int uid, String uname) {
					super();
					this.uid = uid;
					this.uname = uname;
				}
				
			}
		}
	}
}
