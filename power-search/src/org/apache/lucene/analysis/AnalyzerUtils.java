/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.lucene.analysis;


import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.StringReader;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.RamUsageEstimator;
/**
 *
 * @author cshou
 */
public class AnalyzerUtils {
    
    public static void main(String[] args) throws IOException {
        //AcronymEngine engine = new AcronymEngine();
        //Analyzer analyzer = new AcronymAnalyzer(Version.LUCENE_30, engine);
        //displayTokens(analyzer, "U.S.A. Intertional Business Machine what the fuck");
    	//StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
    	//StopAnalyzer analyzer = new StopAnalyzer(Version.LUCENE_35);
    	//WhitespaceAnalyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_35);
    	
    	//TwitterObj twitterObj = TwitterObj.getInstance();
    	//TweetAnalyzer analyzer = new TweetAnalyzer(twitterObj.getTwitter());
    	
    	//displayTokensWithFullDetails(analyzer, "@MissKellyO");
    	//SimpleAnalyzer analyzer = new SimpleAnalyzer(Version.LUCENE_35);
    	//displayTokensWithFullDetails(analyzer, "___");
    	//"Spotted: Sophia Grace and Rosie! RT @MissKellyO: Look who I just ran into on #eredcarpet #grammys http://instagr.am/p/G7Ejp7Ab1P/"
    }

    
    public static void displayTokens(Analyzer analyzer,
                                   String text) throws IOException {
        displayTokens(analyzer.tokenStream("contents", new StringReader(text)));  //A
    }

    public static void displayTokens(TokenStream stream)
        throws IOException {

        CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
        while(stream.incrementToken()) {
            System.out.print("[" + new String(term.buffer(), 0, term.length()) + "] ");    //B
        }
    }
    
    public static int getPositionIncrement(AttributeSource source) {
        PositionIncrementAttribute attr = source.addAttribute(PositionIncrementAttribute.class);
        return attr.getPositionIncrement();
    }

    public static String getTerm(AttributeSource source) {
        CharTermAttribute attr = source.addAttribute(CharTermAttribute.class);
        return new String(attr.buffer(), 0, attr.length());
    }

    public static String getType(AttributeSource source) {
        TypeAttribute attr = source.addAttribute(TypeAttribute.class);
        return attr.type();
    }

    public static void setPositionIncrement(AttributeSource source, int posIncr) {
        PositionIncrementAttribute attr = source.addAttribute(PositionIncrementAttribute.class);
        attr.setPositionIncrement(posIncr);
    }

    public static void setTerm(AttributeSource source, String term) {
        CharTermAttribute attr = source.addAttribute(CharTermAttribute.class);
        //Maybe have a problem here
        int length = term.length();
        //char[] termBuffer = new char[ArrayUtil.oversize(length, RamUsageEstimator.NUM_BYTES_CHAR)];
        char[] termBuffer = new char[length];
        term.getChars(0, length, termBuffer, 0);
        //System.out.println("length"+length + "buffersize"+attr.length());
        if (length < attr.length())
            attr = attr.setLength(length);
        //System.out.println(attr.length());
        attr.copyBuffer(termBuffer, 0, termBuffer.length);
        //System.out.println(new String(source.getAttribute(CharTermAttribute.class).buffer()));
        //System.out.println("set term: " + new String(attr.buffer()) + " " + attr.length());
        
    }

    public static void setType(AttributeSource source, String type) {
        TypeAttribute attr = source.addAttribute(TypeAttribute.class);
        attr.setType(type);
    }
    
    
    public static void displayTokensWithPositions
    (Analyzer analyzer, String text) throws IOException {

    TokenStream stream = analyzer.tokenStream("contents",
                                              new StringReader(text));
    CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
    PositionIncrementAttribute posIncr = stream.addAttribute(PositionIncrementAttribute.class);

    int position = 0;
    while(stream.incrementToken()) {
      int increment = posIncr.getPositionIncrement();
      if (increment > 0) {
        position = position + increment;
        System.out.println();
        System.out.print(position + ": ");
      }

      System.out.print("[" + new String(term.buffer(), 0, term.length()) + "] ");
    }
    System.out.println();
  }
    
      public static void displayTokensWithFullDetails(Analyzer analyzer,
                                                  String text) throws IOException {

    TokenStream stream = analyzer.tokenStream("contents",                        // #A
                                              new StringReader(text));

    CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);        // #B
    PositionIncrementAttribute posIncr =                                  // #B 
    	stream.addAttribute(PositionIncrementAttribute.class);              // #B
    OffsetAttribute offset = stream.addAttribute(OffsetAttribute.class);  // #B
    TypeAttribute type = stream.addAttribute(TypeAttribute.class);        // #B

    int position = 0;
    while(stream.incrementToken()) {                                  // #C

      int increment = posIncr.getPositionIncrement();                 // #D
      if (increment > 0) {                                            // #D
        position = position + increment;                              // #D
        System.out.println();                                         // #D
        System.out.print(position + ": ");                            // #D
      }

      System.out.print("[" +                                 // #E
                       new String(term.buffer(), 0, term.length()) + ":" +                   // #E
                       offset.startOffset() + "->" +         // #E
                       offset.endOffset() + ":" +            // #E
                       type.type() + "] ");                  // #E
    }
    System.out.println();
  }

}
