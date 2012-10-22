package org.apache.lucene.twitter.index;

import java.io.Serializable;

public class TweetResult implements Serializable {
	
	private String text;
	private String screenName;
	private long id;
	private String realName;
	
	private TweetResult() {
		
	}
	
	public TweetResult(String text, String screenName, long id, String realName) {
		this.text = text;
		this.screenName = screenName;
		this.id = id;
		this.realName = realName;
	}
	
	
	public String getText() {
		return this.text;
	}
	
	public String getScreenName() {
		return this.screenName;
	}
	
	public long getId() {
		return this.id;
	}
	
	public String getRealName() {
		return this.realName;
	}

}
