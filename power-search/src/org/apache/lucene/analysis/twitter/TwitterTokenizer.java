package org.apache.lucene.analysis.twitter;

import java.io.Reader;

import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.Version;

public class TwitterTokenizer extends CharTokenizer {

	public TwitterTokenizer(Version matchVersion, Reader input) {
		super(matchVersion, input);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean isTokenChar(int c) {
		
		if (Character.getName(c).equals("NUMBER SIGN") ||
				Character.getName(c).equals("COMMERCIAL AT") ||
				Character.getName(c).equals("LOW LINE") ||
				Character.isDigit(c))
			return true;
		
		//System.out.println(Character.getName(c));
		return Character.isLetter(c);
		
	}

}
