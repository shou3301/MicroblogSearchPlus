package org.apache.lucene.twitter.index;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;

import org.apache.lucene.analysis.AnalyzerUtils;
import org.apache.lucene.analysis.twitter.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterIndexer {

	private static int REQUEST_INTERVAL = 60000;
	private static int REQUEST_TWEET_NUM = 20;
	private static final String mainIndexDir = "MainIndex";
	private static final String tempIndexDir = "temp_index";
	/*private final static String CONSUMER_KEY = "yWgus2dCCpxDNJAnMGRw4Q";
	private final static String CONSUMER_KEY_SECRET = "3ttzCbjLlYuinMsVxdf4qWkBCSD0ZMbkbAfp4ag6M";
	private static String accessToken = "490677075-ecSKnmrZCgsGg3KBtR8WY9Zr5b7bl8hPAfv8CP8g";
	private static String accessTokenSecret = "FAbkWeYRVsnYEojtjLg1M5pZC0QGgltbn2cGFRzSsI";
	private static AccessToken oathAccessToken = new AccessToken(accessToken, accessTokenSecret);*/
	private TwitterObj twitterObj = TwitterObj.getInstance();
	private Twitter twitter = twitterObj.getTwitter();
	private volatile ArrayList<Status> tweetList = new ArrayList<Status>();
	
	//For test
	private TwitterAnalyzer analyzer = new TwitterAnalyzer(twitter, Version.LUCENE_40);
	
	public static void main(String[] args) {
		TwitterIndexer ti = new TwitterIndexer();
		ti.start();
	}
	
	public TwitterIndexer() {
		//twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
		//twitter.setOAuthAccessToken(oathAccessToken);
	}
	
	public TwitterIndexer(int interval, int num) {
		this();
		this.REQUEST_INTERVAL = interval;
		this.REQUEST_TWEET_NUM = num;
	}
	
	public void start() {
		Timer requestTimer = new Timer();
		// Num changed, interval changed
		requestTimer.schedule(new TimelineTimerTask(this, REQUEST_TWEET_NUM), 
				0, REQUEST_INTERVAL);
	}
	
	public synchronized void mainIndex() {
		try {
			Directory maindir = FSDirectory.open(new File(mainIndexDir));
			IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LUCENE_40, new TwitterAnalyzer(twitter, Version.LUCENE_40));
			IndexWriter mainWriter = new IndexWriter (maindir, iwConfig);
			
			/*if (flag) {				
				Directory tempdir = FSDirectory.open(new File(tempIndexDir));
				Directory[] dirs = {maindir, tempdir};
				
				mainWriter.addIndexes(dirs);
			}
			else {*/
				//System.out.println("New");
				index(mainWriter);
			//}
			
			System.out.println("The main_index now has " + mainWriter.numDocs() + " documents.");
			mainWriter.close();
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void tempIndex() {
		System.out.println("Merge");
		try {
			Directory tempdir = FSDirectory.open(new File(mainIndexDir));
			/*IndexWriter tempWriter = new IndexWriter (tempdir,
					new StandardAnalyzer(Version.LUCENE_30), true,
					IndexWriter.MaxFieldLength.UNLIMITED);*/
			
			IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LUCENE_40, new TwitterAnalyzer(twitter, Version.LUCENE_40));
			IndexWriter tempWriter = new IndexWriter (tempdir, iwConfig);
			
			index(tempWriter);
			System.out.println("The temp_index now indexed " + tempWriter.numDocs() + " documents.");
			tempWriter.close();
			//mainIndex(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void index(IndexWriter writer) throws Exception {
		for (Status s : tweetList) {
			writer.addDocument(createDoc(s));
		}
	}
	
	private Document createDoc(Status status) throws Exception {
		Document doc = new Document();
		Long id = new Long(status.getId());

		doc.add(new Field("id", id.toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("timestamp", status.getCreatedAt().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("owner", status.getUser().getScreenName(),Field.Store.YES, Field.Index.NOT_ANALYZED));
		
		if (status.isRetweet()) {
			/*System.out.println(id.toString() + "  " +
					status.getUser().getScreenName() + "  " +
					status.getUser().getName() + "  " +
					status.getRetweetedStatus().getText() + "  " +
					status.getCreatedAt().toString());*/
			String str = status.getRetweetedStatus().getText();
			str = str.replaceAll("@(https?|ftp)://(-\\.)?([^\\s/?\\.#-]+\\.?)+(/[^\\s]*)?$@iS", "");
			AnalyzerUtils.displayTokensWithFullDetails(analyzer, str);
			doc.add(new Field("text", str, Field.Store.NO, 
					Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
		}
		else {
			/*System.out.println(id.toString() + "  " +
					status.getUser().getScreenName() + "  " +
					status.getUser().getName() + "  " +
					status.getText() + "  " +
					status.getCreatedAt().toString());*/
			String str = status.getText();
			str = str.replaceAll("@(https?|ftp)://(-\\.)?([^\\s/?\\.#-]+\\.?)+(/[^\\s]*)?$@iS", "");
			AnalyzerUtils.displayTokensWithFullDetails(analyzer, str);
			doc.add(new Field("text", str, Field.Store.NO, 
					Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
		}
		
		return doc;
	}
	
	
	//************Getters & Setters*****************
	/*public Twitter getTwitter() {
		//twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_KEY_SECRET);
		//twitter.setOAuthAccessToken(oathAccessToken);
		return twitter;
	}*/
	
	public void addToTweetList(Status status) {
		tweetList.add(status);
	}
	
	public void clearList() {
		this.tweetList.clear();
	}
	
	/*public void showList() {
		for (Status s : tweetList) {
			System.out.println(s.getText());
		}
	}*/
	
	public String getMainDir() {
		return this.mainIndexDir;
	}
}
