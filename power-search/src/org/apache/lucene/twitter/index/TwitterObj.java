package org.apache.lucene.twitter.index;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterObj {
	
	private final static String CONSUMER_KEY = "yWgus2dCCpxDNJAnMGRw4Q";
	private final static String CONSUMER_KEY_SECRET = "3ttzCbjLlYuinMsVxdf4qWkBCSD0ZMbkbAfp4ag6M";
	private static String accessToken = "490677075-ecSKnmrZCgsGg3KBtR8WY9Zr5b7bl8hPAfv8CP8g";
	private static String accessTokenSecret = "FAbkWeYRVsnYEojtjLg1M5pZC0QGgltbn2cGFRzSsI";
	//private static AccessToken oathAccessToken = new AccessToken(accessToken, accessTokenSecret);
	private static Twitter twitter = new TwitterFactory().getInstance();
	private static TwitterObj uniqueTwitterObj;
	
	private TwitterObj() {
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);	
	}
	
	public static TwitterObj getInstance() {
		if (uniqueTwitterObj == null)
			uniqueTwitterObj = new TwitterObj();

		return uniqueTwitterObj;
	}
	
	public Twitter getTwitter(String token, String secretToken) {
		AccessToken oathAccessToken = new AccessToken(token, secretToken);	
		twitter.setOAuthAccessToken(oathAccessToken);
		
		return twitter;
	}
	
	public Twitter getTwitter() {
		return getTwitter(accessToken, accessTokenSecret);
	}

}
