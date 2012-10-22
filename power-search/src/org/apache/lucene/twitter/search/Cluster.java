package org.apache.lucene.twitter.search;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.TreeMap;


public class Cluster {
	
	private Hashtable<String, TreeMap<String, Double>> vectors = new Hashtable<String, TreeMap<String, Double>>();
	private TreeMap<String, Double> mean = new TreeMap<String, Double>();
	
	public Hashtable<String, TreeMap<String, Double>> getVectors() {
		return vectors;
	}
	public void setVectors(Hashtable<String, TreeMap<String, Double>> vectors) {
		this.vectors = vectors;
	}
	
	public void addVector(String doc, TreeMap<String, Double> terms) {
		vectors.put(doc, terms);
	}
	
	public void deleteVector(String doc) {
		vectors.remove(doc);
	}
	
	public TreeMap<String, Double> getMean() {
		return mean;
	}
	
	public void setMean(TreeMap<String, Double> mean) {
		this.mean = mean;
	}
	
	public boolean contains(String doc) {
		if (vectors.containsKey(doc))
			return true;
		else
			return false;
	}

}
