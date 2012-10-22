package org.apache.lucene.twitter.index;

import java.io.File;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.twitter.TwitterAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class TempIndexer {

	 private static String indexDir = "main_index";  
	 private static String dataDir = "MyTestData";
	 private static FileParser filePar;
	 private static ArrayList<Document> docList = new ArrayList<Document>();
	
	 public static void main(String[] args) throws Exception {
		 filePar = new FileParser(dataDir);
		 docList = filePar.getDocuments();
		 
		 Directory maindir = FSDirectory.open(new File(indexDir));
		 IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LUCENE_40, new StandardAnalyzer(Version.LUCENE_40));
		 IndexWriter mainWriter = new IndexWriter (maindir, iwConfig);
		 
		 File[] files = new File(dataDir).listFiles();
		 for (File f : files) {
			 filePar.parse(f);
		     ArrayList<Document> doclist = filePar.getDocuments();
		     for (Document d : doclist) {
		    	 mainWriter.addDocument(d);
		     }
		     filePar.cleanList();
		 }
		    
		 mainWriter.close();
	 }
}
