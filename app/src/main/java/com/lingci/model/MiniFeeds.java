package com.lingci.model;

import java.io.Serializable;
import java.util.List;

public class MiniFeeds  implements Serializable{

	private static final long serialVersionUID = -5580870724749014510L;
	public int ret;
	public Data data;

	public static final class Data  implements Serializable{

		private static final long serialVersionUID = 5058873416530108567L;
		public List<MiniFeed> minifeedlist;
		public int totalnum;

		public static final class MiniFeed implements Serializable{
			
			private static final long serialVersionUID = -6088596146626068418L;
			public int lcid;
			public int uid;
			public String uname;
			public String url;
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
				
				private static final long serialVersionUID = -7478750404079736541L;
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
