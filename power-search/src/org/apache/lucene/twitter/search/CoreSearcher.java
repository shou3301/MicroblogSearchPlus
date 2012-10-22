package org.apache.lucene.twitter.search;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.TreeMap;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.wikipedia.WikiSearcher;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.SingleTermsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.twitter.index.TwitterObj;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.Version;

import twitter4j.Status;
import twitter4j.Twitter;

public class CoreSearcher {

	private static final String mainIndexDir = "MainIndex";
	private static TwitterObj twitterObj = TwitterObj.getInstance();
	private static Twitter twitter = twitterObj.getTwitter();
	private static int K = 3;
	private Directory dir;
	private IndexReader ir;
	private IndexSearcher is;
	
	public CoreSearcher() throws Exception {
		dir = FSDirectory.open(new File(mainIndexDir));
		setIr(IndexReader.open(dir));
		is = new IndexSearcher(getIr());
	}
	
	public void search (String query) throws Exception {
		System.out.println("Got query...");
		postSearch(richQuery(query));
	}
	
	public ArrayList<Integer> preSearch (String q) {
		
		TopDocs hits = null;
		ArrayList<Integer> docIDs = new ArrayList<Integer>();
		
		try {
			//Directory dir = FSDirectory.open(new File(mainIndexDir));
			//IndexReader ir =IndexReader.open(dir);
			//IndexSearcher is = new IndexSearcher(ir);
		    
		    //Term term = new Term("text",q);
		    //Query query = new TermQuery(term);
		    Query query = new QueryParser(Version.LUCENE_40, "text", new StandardAnalyzer(Version.LUCENE_40)).parse(q);

		    hits = is.search(query, 50);
		    
			for(ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = is.doc(scoreDoc.doc);   
			    //System.out.println(doc.get("owner"));
				//long id = Long.parseLong(doc.get("id"));
				
				//docIDs.add(id);
				docIDs.add(scoreDoc.doc);
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return docIDs;
		
	}
	
	public Cluster clustering(String q) throws Exception {
		
		ArrayList<Integer> docIDs = preSearch(q);
		
		Hashtable<String, TreeMap<String, Double>> resultVectors = new Hashtable<String, TreeMap<String, Double>>();
		
		for (Integer id : docIDs) {
			
			//TODO call and get tree map 
			
			TreeMap<String, Double> d = getVectorByDocID(id);
			resultVectors.put(id.toString(), d);
		}
		
		CoreKMeans kmeans = new CoreKMeans(K, resultVectors);
		ClusterRanker ranker = new ClusterRanker(getIr());
		kmeans.start();
		ranker.setClusters(kmeans.getClusters());
		ranker.setColl(docIDs.size());
		
		
		return ranker.rankCluster(q);
		
	}
	
	public TreeMap<String, Double> getVectorByDocID(Integer id) throws Exception {
		
		int N = getIr().numDocs();
		
		// Test
		//System.out.println("Num of Docs: " + N);
		//System.out.println("DocID = " + id);
		
		TreeMap<String, Double> vector = new TreeMap<String, Double>();
		Terms terms = getIr().getTermVector(id, "text");
		//System.out.println("SumDocFreq= " + terms.getSumDocFreq());
		//System.out.println("TotalTermFreq= " + terms.getDocCount());
		TermsEnum te = terms.iterator(null);
		
		while (te.next() != null) {
			
			String current = te.term().utf8ToString();
			
			//System.out.println("Term: " + current);
			
			int df = getIr().docFreq(new Term("text", current));
			//System.out.println("Df = " + df);
			
			DocsEnum docs = te.docs(new Bits.MatchAllBits(N), null, true);
			int tf = docs.freq();
			//System.out.println("Got the term freq = " + tf);

			
			Double tfidf = tf * Math.log10(N / df);
			vector.put(current, tfidf);
			//System.out.println("tfidf: " + tfidf);
			
			//System.out.println();
		}
		
		return vector;
	}
	
	public String richQuery(String q) throws Exception {
		
		Cluster first = clustering(q);
		
		// TODO invoke wiki module
		WikiSearcher ws = new WikiSearcher();
		
		ArrayList<String> words = ws.getWords(first, q);	// Return results
		
		q = q + "^3";
		int i = 0;
		for (String s : words) {
			if (i <= 2)
				q += " " + s + "^2";
			else
				q += " " + s;
			
			i++;
		}
		
		System.out.println(q);
		
		return q;
	}
	
	
	public void postSearch(String q) {
		TopDocs hits = null;
		
		try {
			Directory dir = FSDirectory.open(new File(mainIndexDir));
			IndexReader ir =IndexReader.open(dir);
			IndexSearcher is = new IndexSearcher(ir);
		    
			Query query = new QueryParser(Version.LUCENE_40, "text", new StandardAnalyzer(Version.LUCENE_40)).parse(q);
		    
		    hits = is.search(query, 20);
		    
		    System.out.println("postsearch");
		    
			for(ScoreDoc scoreDoc : hits.scoreDocs) {
				
				System.out.println("result");
				
				Document doc = is.doc(scoreDoc.doc);     
				
				long id = Long.parseLong(doc.get("id"));
				
				String text = null;
				Status s = twitter.showStatus(id);
				if (s.isRetweet())
					text = s.getRetweetedStatus().getText();
				else 
					text = s.getText();
				String screenName = s.getUser().getScreenName();
				String realName = s.getUser().getName();
				
				System.out.println("User: " + realName + 
						"\n Tweet: " + text + "\n");
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getK() {
		return K;
	}

	public static void setK(int k) {
		K = k;
	}

	public IndexReader getIr() {
		return ir;
	}

	public void setIr(IndexReader ir) {
		this.ir = ir;
	}
}
