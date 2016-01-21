package com.lingci.model;

import java.util.List;

public class Comments {

	public int ret;
	public Data data;

	public static final class Data {
		public int totalnum;
		public List<Comment> cmtlist;

		public static final class Comment {
			public int cmid;
			public int uid;
			public String uname;
			public String url;
			public String comment;
			public String cm_time;
			public List<Reply> replylist;

			public static final class Reply {
				public int rpid;
				public int cmid;
				public int uid;
				public String uname;
				public int touid;
				public String touname;
				public String reply;
				public String rp_time;
			}
		}
	}
}
