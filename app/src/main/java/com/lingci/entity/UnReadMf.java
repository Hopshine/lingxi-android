package com.lingci.entity;

import java.io.Serializable;
import java.util.List;

import com.lingci.entity.MiniFeeds.Data.MiniFeed;

public class UnReadMf implements Serializable{

	private static final long serialVersionUID = 6070948746656319327L;
	
	public int ret;
	public Data data;

	public static final class Data  implements Serializable{

		private static final long serialVersionUID = -5711128274281697136L;

		public List<Unread> unreadlist ;
		public int unreadnum;

		public static final class Unread implements Serializable{
			
			private static final long serialVersionUID = 1586523302101162782L;
			
			public int uid;
			public String uname;
			public String url;
			public int cmid;
			public String cm_time;
			public String comment;
			public MiniFeed minifeed;
		}
	}
}
