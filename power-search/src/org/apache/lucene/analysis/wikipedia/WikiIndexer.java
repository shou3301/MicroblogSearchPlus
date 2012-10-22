package org.apache.lucene.analysis.wikipedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

import org.apache.lucene.analysis.AnalyzerUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class WikiIndexer {
	//private static int docID = 000000001;
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Directory wikidir = FSDirectory.open(new File("WikiIndex"));
		IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LUCENE_40, new WikiAnalyzer(Version.LUCENE_40));
		IndexWriter wikiWriter = new IndexWriter (wikidir, iwConfig);
		
		indexer("DumpOutput", wikiWriter);
		
		wikiWriter.close();
	}
	
	private static void indexer(String path, IndexWriter writer) throws Exception {
		File input = new File(path);
		boolean flag = true;
		while(flag){
			File[] files = input.listFiles(new FileFilter());
			if(files != null)    {
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    System.out.println("parent directory:"+f.getParent()+",file name:"+f.getName());
                    writer.addDocument(createDoc(f));
                }                
            }
			
			File[] dirFolders = input.listFiles(new DirectortyFilter());
			if(dirFolders != null)    {
                for (int i = 0; i < dirFolders.length; i++) {
                    File dir = dirFolders[i];
    
                    String pathName =  dir.getAbsolutePath();
               
                    indexer(pathName, writer);
                }
            }
            flag = false;
		}
	}
	
	private static void index (IndexWriter writer) throws Exception {
		File input = new File("DumpOutput");
		
		File[] files = input.listFiles();
		
		for (File f : files) {
			System.out.println(f.getName());
			writer.addDocument(createDoc(f));
		}
	}
	
	private static Document createDoc (File f) throws Exception {
		Document doc = new Document();
		String title = "";
		String content = "";
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		
		for (int i = 0; i < 5; i++) {
			
			String line = br.readLine();
			
			if (i == 2) {
				title = line;
			}
			else if (i == 4) {
				content = line;
			}
		}
		//String di = Integer.toString(docID);
		AnalyzerUtils.displayTokensWithFullDetails(new WikiAnalyzer(Version.LUCENE_40), content);
		//doc.add(new Field("docID", di, Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("precisetitle", title, Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("ambigoustitle", title, Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("content", content, Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
		
		//docID = docID + 1;
		return doc;
	}

}
