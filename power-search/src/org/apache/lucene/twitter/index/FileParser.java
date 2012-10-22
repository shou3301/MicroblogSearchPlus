/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.lucene.twitter.index;

import org.apache.lucene.analysis.AnalyzerUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.util.Version;

import java.io.*;
import java.util.*;
/**
 *
 * @author cshou
 */
public class FileParser {
    private String dataDir = "";
    private Document doc;
    private ArrayList docs;
    
    /**
     * @param xmlFile
     * @param docTag - Tag that indicates document
     */
    public FileParser(String dataDir) throws IOException
    {
        this.dataDir = dataDir;
        docs = new ArrayList();
        //parse(xmlFile);
        /*File[] files = new File(dataDir).listFiles();
        for (File f : files) {
            parse(f);
        }*/
    }
    
    public void parse(File file) throws IOException
    {
        BufferedReader reader = new BufferedReader( new FileReader( file ) );
        StringBuffer strb = new StringBuffer(100);
        String docline = "";	
        boolean intext = false;
		boolean indocline = false;

        while ( reader.ready() )
        {
            String line = reader.readLine();
            if ( line.indexOf( "</DOCNO>" ) != -1 ) {
				docline += line;
				doc = createDoc( docline );
				docs.add( doc );
				docline = "";
				indocline = false;
	    	} else if ( line.indexOf( "<DOCNO>" ) != -1 ) {
            	docline += line;
            	indocline = true;
	    	} else if (indocline) {
				docline += line.replaceAll("[!]", "");
	    	} else if ( line.toUpperCase().indexOf("<TEXT>") != -1 ) {
				intext = true;
	    	} else if (line.toUpperCase().indexOf("</TEXT>") != -1 ) {
				intext = false;
	    	}
            // Add contents to a doc
        	else if ( doc != null && intext) {
            	// Strip out xml tags
            	String txt = stripTagsOut( line );
		
				strb.append( " " + txt + " " );
        	}

        	// If we hit the end of doc then add text to it
        	if ( line.indexOf( "</DOC>" ) != -1 )
        	{
                    //System.out.println(strb.toString());
        		AnalyzerUtils.displayTokensWithFullDetails(new StandardAnalyzer(Version.LUCENE_40), strb.toString());
                    doc.add(new Field("content", strb.toString(), Field.Store.YES, 
				Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
                    strb = new StringBuffer(100);
        	}
        }
        reader.close();
    }
    
    public String stripTagsOut( String str)
    {
        boolean ignore = false;
        StringBuffer strb = new StringBuffer();
        for ( int i = 0; i < str.length(); i++ )
        {
            char ch = str.charAt( i );
            if ( ch == '<' )
            {
                ignore = true;
            }
            else if ( ch == '>' )
            {
                ignore = false;
            }
            else if ( !ignore )
            {
                strb.append( ch );
            }
        }
        // Strip out some html tags - &blank; &hyph; &amp; &sect;        
        String strP = strb.toString();
        strP = strP.replaceAll( "&blank;|&hyph;|&amp;|&sect;|&bull;", " " );        
        
        return strP;
    }
    
    /**
     * Creates empty doc from a str of form:
     * <DOCNO> FBIS3-1 </DOCNO>
     */
    public Document createDoc( String str )
    {
        // DocNo is a second token
        StringTokenizer tknzr = new StringTokenizer( str, " \t\n\r\f<>" );
        tknzr.nextToken();
        String docNo = tknzr.nextToken().trim();
        Document doc = new Document();
        //noc.add( Field.Text( "DOCNO", docNo ) );
        //System.out.println(docNo);
        doc.add(new Field("docno", docNo, Field.Store.YES, Field.Index.NOT_ANALYZED));
        return doc;
    }
    
    
    public ArrayList getDocuments()
    {
        return docs;
    }
    
    public void cleanList() {
        this.docs.clear();
    }
}
