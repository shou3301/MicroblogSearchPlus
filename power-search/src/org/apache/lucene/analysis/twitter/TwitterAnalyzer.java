package org.apache.lucene.analysis.twitter;

import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

import twitter4j.Twitter;

public class TwitterAnalyzer extends StopwordAnalyzerBase {

	private static final CharArraySet STOP_WORDS_SET;
	private Twitter twitter;
	
	static {
	    final List<String> stopWords = Arrays.asList(
	    		"a", "an", "and", "are", "as", "at", "be", "but", "by",
	    		"for", "if", "in", "into", "is", "it",
	    		"no", "not", "of", "on", "or", "such",
	    		"that", "the", "their", "then", "there", "these",
	    	    "they", "this", "to", "was", "will", "with", "rt"
	    );
	    final CharArraySet stopSet = new CharArraySet(Version.LUCENE_CURRENT, 
	        stopWords.size(), false);
	    stopSet.addAll(stopWords);  
	    STOP_WORDS_SET = CharArraySet.unmodifiableSet(stopSet); 
	}
	
	
	public TwitterAnalyzer(Twitter twitter, Version version) {
		super(version, STOP_WORDS_SET);
		this.twitter = twitter;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {
		final TwitterTokenizer source = new TwitterTokenizer(matchVersion, reader);
		TokenStream tok = new TwitterFilter(source, twitter);
		tok = new StandardFilter(matchVersion, tok);
		tok = new LowerCaseFilter(matchVersion, tok);
        tok = new StopFilter(matchVersion, tok, stopwords);
		return new TokenStreamComponents(source, tok);
	}

}
