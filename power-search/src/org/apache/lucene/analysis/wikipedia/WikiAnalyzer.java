package org.apache.lucene.analysis.wikipedia;

import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.TypeTokenFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.util.Version;

public class WikiAnalyzer extends Analyzer {

	private final Version matchVersion;
	
	public WikiAnalyzer (Version matchVersion) {
		this.matchVersion = matchVersion;
	}
	
	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {
		// TODO Auto-generated method stub
		final WikipediaTokenizer source = new WikipediaTokenizer(reader);
		TokenStream tok = new StandardFilter(matchVersion, source);
		tok = tok = new LowerCaseFilter(matchVersion, tok);
        tok = new StopFilter(matchVersion, tok, StandardAnalyzer.STOP_WORDS_SET);
        Set<String> typeSet = new HashSet();
        typeSet.add("<NUM>");
        tok = new TypeTokenFilter(true, tok, typeSet);
		return new TokenStreamComponents(source, tok);
	}

}
