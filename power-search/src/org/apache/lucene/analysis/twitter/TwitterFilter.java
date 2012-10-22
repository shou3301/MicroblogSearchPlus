package org.apache.lucene.analysis.twitter;

import java.io.IOException;
import java.util.Stack;

import org.apache.lucene.analysis.AnalyzerUtils;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeSource;

import twitter4j.Twitter;

public class TwitterFilter extends TokenFilter {
	
	private Twitter twitter;
	private CharTermAttribute termAttr;
	private Stack nameStack;

	protected TwitterFilter(TokenStream input, Twitter twitter) {
		super(input);
		this.twitter = twitter;
		termAttr = (CharTermAttribute) addAttribute(CharTermAttribute.class);
		nameStack = new Stack();
	}

	@Override
	public boolean incrementToken() throws IOException {
		if (nameStack.size() > 0) {
			AttributeSource nameToken = (AttributeSource) nameStack.pop();
			this.restoreState(nameToken.captureState());
			return true;
		}
		
        if (!input.incrementToken()) {
            return false;
        }
		
		String currentTerm = new String(termAttr.buffer()).substring(0, termAttr.length());
		//System.out.println("Outer: " + currentTerm);
		String processedTerm = processCurrent(currentTerm);
		if (!processedTerm.equals(currentTerm)) {
			AttributeSource processedAttrSrc = this.cloneAttributes();
			AnalyzerUtils.setTerm(processedAttrSrc, currentTerm);
			this.restoreState(processedAttrSrc.captureState());
		}
		
		return true;
	}
	
	private String processCurrent(String current) {
		String processed = current;
		//System.out.println("Current: " + current);
		try {
			if (current.startsWith("@")) {
				processed = twitter.showUser(current.substring(1)).getName();
				//System.out.println("name: " + processed);
				String[] names = processed.split(" ");
				for (String name : names) {
					AttributeSource nameToken = this.cloneAttributes();
					AnalyzerUtils.setTerm(nameToken, name);
					AnalyzerUtils.setPositionIncrement(nameToken, 0);
					nameStack.push(nameToken);
				}
			}
			else if (current.startsWith("#")) {
				if (current.endsWith("#"))
					processed = current.substring(1, current.length()-2);
				else
					processed = current.substring(1);
			}
			else if (current.startsWith("http:")) {
				processed = "the";
			}
			else if (current.matches("[0-9]")) {
				processed = "the";
			}
		}
		catch (Exception e) {
			processed = "the";
			e.printStackTrace();
		}
		finally {
			return processed;
		}
		//return processed;
	}

}
