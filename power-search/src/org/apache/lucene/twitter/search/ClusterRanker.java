package org.apache.lucene.twitter.search;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.TreeMap;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;


public class ClusterRanker {

	private Integer clu;
	private Double lammend = 0.5;
	private Integer coll;
	private ArrayList<Cluster> clusters;
	private IndexReader ir;
	
	public ClusterRanker(IndexReader ir) {
		this.ir = ir;
	}
	
	public Cluster rankCluster(String q) throws Exception {
		
		String[] strs = q.toLowerCase().split(" ");
		Double score = 0.0;
		setClu(clusters.size());
		
		
		Cluster first = new Cluster();
		
		for (Cluster c : clusters) {
			Double temp = 1d;
			
			for (String s : strs) {
				int df = ir.docFreq(new Term("text", s));
				temp *= calcCluDf(c, s) / (getClu() + lammend) + (lammend * df) / ((getClu() + lammend) * getColl());
			}
			
			if (temp > score) {
				score = temp;
				first = c;
			}
		}
		
		return first;
	}
	
	private Double calcCluDf(Cluster c, String term) {
		
		Double count = 0.0;
		
		Hashtable<String, TreeMap<String, Double>> vs = c.getVectors();
		
		ArrayList<String> keys = new ArrayList<String>(vs.keySet());
		for (String key : keys) {
			if (vs.get(key).containsKey(term)) {
				count++;
			}
		}
		
		return count;
	}
	
	public Double getLammend() {
		return lammend;
	}
	
	public void setLammend(Double lammend) {
		this.lammend = lammend;
	}
	
	public ArrayList<Cluster> getClusters() {
		return clusters;
	}
	
	public void setClusters(ArrayList<Cluster> clusters) {
		this.clusters = clusters;
	}

	public Integer getClu() {
		return clu;
	}

	public void setClu(Integer clu) {
		this.clu = clu;
	}

	public Integer getColl() {
		return coll;
	}

	public void setColl(Integer coll) {
		this.coll = coll;
	}
	
	
	
}
