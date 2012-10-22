package org.apache.lucene.analysis.wikipedia;

public class TermResult implements Comparable {

	private String term;
	private Integer freq;
	
	public TermResult (String term, Integer freq) {
		this.setTerm(term);
		this.setFreq(freq);
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		TermResult other = (TermResult) o;
		
		if (this.getFreq() > other.getFreq())
			return -1;
		else if (this.getFreq() < other.getFreq())
			return 1;
		else
			return 0;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public Integer getFreq() {
		return freq;
	}

	public void setFreq(Integer freq) {
		this.freq = freq;
	}

}
