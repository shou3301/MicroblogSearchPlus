package org.apache.lucene.analysis.wikipedia;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.twitter.search.Cluster;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.Version;

public class WikiSearcher {
	
	private Directory dir;
	private IndexReader ir;
	private IndexSearcher is;
	private static int numOfWords = 5;
	
	public WikiSearcher () throws Exception {

		dir = FSDirectory.open(new File("WikiIndex"));
		setIr(IndexReader.open(dir));
		is = new IndexSearcher(getIr());
	}
	
	public TopDocs search (String q) throws Exception {
	    
		//TODO remember to change back
	    Term term = new Term("precisetitle",q);
	    Query query1 = new TermQuery(term);
		
		//Query query1 = new QueryParser(Version.LUCENE_40, "precisetitle", new StandardAnalyzer(Version.LUCENE_40)).parse(q);

	    TopDocs hits = is.search(query1, 50);
	    
	    if (hits.totalHits == 0) {
	    	String[] fs = new String[] {"ambigoustitle", "content"};
	    	Query query2 = new MultiFieldQueryParser(Version.LUCENE_40, fs, new StandardAnalyzer(Version.LUCENE_40)).parse(q);
	    	hits = is.search(query2, 50);
	    }
	    
	    return hits;
	}
	
	
	public ArrayList<String> getWords (Cluster first, String q) throws Exception {
		
		TopDocs topDocs = search(q);
		
		int flag = 0;
		int N = getIr().numDocs();
		
		if (topDocs.totalHits > 1) {
		
			double sumCos=0;

			TreeMap<String, Double> tgm = first.getMean();
			
			for(ScoreDoc scoreDoc : topDocs.scoreDocs) {
				
				TreeMap<String, Double> tv = new TreeMap<String, Double>();
				Terms terms = getIr().getTermVector(scoreDoc.doc, "content");
				
				TermsEnum te = terms.iterator(null);
				double tempCos = 0, mti = 0;
				
				while (te.next() != null)  {
					
					double preCos;
					String current = te.term().utf8ToString();
					
					//System.out.println("Term: " + current);
					
					int df = getIr().docFreq(new Term("content", current));
					//System.out.println("Df = " + df);
					
					DocsEnum docs = te.docs(new Bits.MatchAllBits(N), null, true);
					int tf = docs.freq();
					//System.out.println("Got the term freq = " + tf);
	
					
					Double tfidf = tf * Math.log10(N / df);
					mti = mti + tfidf*tfidf;
					tv.put(current, tfidf);
					//System.out.println("tfidf: " + tfidf);
					
					//System.out.println();
					
					//TODO: contain Key in the Mean Cluster
					if (tgm.containsKey(current)) {
						//TODO: compute the cosine similarity
						preCos = tfidf*tgm.get(current);
					}
					else {
						preCos = 0;
					}
					
					tempCos = tempCos + preCos;
				}
				
				tempCos = tempCos / Math.sqrt(mti);
				
				
				if (tempCos > sumCos) {
					sumCos = tempCos;
					flag = scoreDoc.doc;
				}
				
			}	
		}
		else {
			for(ScoreDoc scoreDoc : topDocs.scoreDocs) {
				flag = scoreDoc.doc;
				break;
			}
		}
			
		//System.out.println(ir.document(flag).get("precisetitle"));
		return getTopWords(flag, N, q);
	}
	
	public ArrayList<String> getTopWords (int id, int N, String q) throws Exception {
		
		List<TermResult> tops = new ArrayList<TermResult>();
		
		Terms terms = getIr().getTermVector(id, "content");
		TermsEnum te = terms.iterator(null);
		while (te.next() != null)  {
			
			DocsEnum docs = te.docs(new Bits.MatchAllBits(N), null, true);
			int tf = docs.freq();
			
			tops.add(new TermResult(te.term().utf8ToString(), tf));
		}
		
		Collections.sort(tops);
	
		ArrayList<String> finals = new ArrayList<String>();
		
		int count = 0;
		for (int i = 0; i < tops.size(); i++) {
			if (!q.toLowerCase().contains(tops.get(i).getTerm())) {
				//System.out.println(tops.get(i).getTerm() + ": " + tops.get(i).getFreq());
				if (!tops.get(i).getTerm().matches("[0-9]")) {
					finals.add(tops.get(i).getTerm());
					count++;
				}
				
				if (count == numOfWords) {
					break;
				}
			}
		}
		
		return finals;
	}

	public IndexReader getIr() {
		return ir;
	}

	public void setIr(IndexReader ir) {
		this.ir = ir;
	}

	public static int getNumOfWords() {
		return numOfWords;
	}

	public static void setNumOfWords(int numOfWords) {
		WikiSearcher.numOfWords = numOfWords;
	}
	
}
