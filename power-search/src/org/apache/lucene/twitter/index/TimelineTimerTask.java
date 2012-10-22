package org.apache.lucene.twitter.index;

import java.io.File;
import java.util.Date;
import java.util.TimerTask;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TimelineTimerTask extends TimerTask {

	private TwitterIndexer ti;
	private int requestNum = 0;
	private long lastID = 0;
	private Date latest = new Date(0);
	private Twitter twitter;
	private TwitterObj twitterObj = TwitterObj.getInstance();
	
	public TimelineTimerTask(TwitterIndexer ti, int requestNum) {
		this.requestNum = requestNum;
		this.twitter = twitterObj.getTwitter();
		this.ti = ti;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ti.clearList();
		boolean flag = true;
		Date localLatest = new Date();
		Paging page = new Paging(1, requestNum);
		
		try {	
			for (Status each : twitter.getHomeTimeline(page)) {
				if (each.getCreatedAt().after(latest)) {
					if (flag) {
						localLatest = each.getCreatedAt();
						flag = false;
						//System.out.println(localLatest);
					}

					ti.addToTweetList(each);
				}
				/*else
					System.out.println("no new posts");*/
			}
			//System.out.println(ti.getMainDir());
			File f = new File(ti.getMainDir());
			if (f.list().length == 0)
				ti.mainIndex();
			else
				ti.tempIndex();
			latest = localLatest;
			
			//ti.showList();
		}
		catch(TwitterException te) {
			te.printStackTrace();
		}
	}

}
