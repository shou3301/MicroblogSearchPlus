package org.apache.lucene.twitter.search;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;


public class CoreKMeans {

	private Integer K = 1;
	private ArrayList<Cluster> clusters = new ArrayList<Cluster>();
	private Hashtable<String, TreeMap<String, Double>> vectors = new Hashtable<String, TreeMap<String, Double>>();
	
	public CoreKMeans(Integer K, Hashtable<String, TreeMap<String, Double>> vectors) {
		this.K = K;
		this.vectors = vectors;
	}
	
	public void start() {
		init();
		
		while (rearrangeClusters()) {
			
		}
	}
	
	public void showVectors() {
		
		ArrayList<String> keys = new ArrayList<String>(vectors.keySet());
		for (String key : keys) {
			System.out.println("Doc: " + key + " Vector: "
					+ vectors.get(key));
		}
	}
	
	public void showClusters() {
		int i = 0;
		
		for (Cluster c : getClusters()) {
			
			System.out.println("Cluster " + i);
			
			Hashtable<String, TreeMap<String, Double>> vs = c.getVectors();
			
			ArrayList<String> keys = new ArrayList<String>(vs.keySet());
			for (String key : keys) {
				System.out.println("Doc: " + key + " Vector: "
						+ vs.get(key));
				
			}
			System.out.println("Mean: " + calcMean(c) + "\n");
			
			i++;
		}
	}
	
	public void init() {
		int k = 0;
		
		ArrayList<String> keys = new ArrayList<String>(vectors.keySet());
		for (String key : keys) { 
			k++;
			Cluster clstr = new Cluster();
			clstr.addVector(key, vectors.get(key));		// add vector
			clstr.setMean(vectors.get(key));			// set mean (the only point)
			getClusters().add(clstr);
			if (k > K - 1)
				break;
		}
	}
	
	
	public TreeMap<String, Double> calcMean(Cluster clstr) {
		
		TreeMap<String, Double> mean = new TreeMap<String, Double>();
		
		Hashtable<String, TreeMap<String, Double>> vs = clstr.getVectors();
		
		int num = vs.size();
		
		ArrayList<String> keys = new ArrayList<String>(vs.keySet());
		for (String key : keys) {
			TreeMap<String, Double> current = vs.get(key);
			ArrayList<String> terms = new ArrayList<String>(current.keySet());
			
			for (String term : terms) {
				if (mean.containsKey(term))
					mean.put(term, mean.get(term) + (current.get(term) / num));
				else
					mean.put(term, current.get(term) / num);
			}
			
		}
		
		clstr.setMean(mean);
		
		return mean;
	}
	
	// Tested
	public Double calcDistance(TreeMap<String, Double> v1, TreeMap<String, Double> v2) {
		
		ArrayList<String> keys1 = new ArrayList<String>(v1.keySet());
		ArrayList<String> keys2 = new ArrayList<String>(v2.keySet());
		
		Double dist = 0.0;
		int i = 0, j = 0;
		while (i < keys1.size() && j < keys2.size()) {
			if (keys1.get(i).compareToIgnoreCase(keys2.get(j)) < 0) {
				dist += Math.pow(v1.get(keys1.get(i)), 2);
				i++;
			}
			else if (keys1.get(i).compareToIgnoreCase(keys2.get(j)) > 0) {
				dist += Math.pow(v2.get(keys2.get(j)), 2);
				j++;
			}
			else {
				dist += Math.pow((v1.get(keys1.get(i)) - v2.get(keys2.get(j))), 2);
				i++;
				j++;
			}
		}
		
		while (i < keys1.size()) {
			dist += Math.pow(v1.get(keys1.get(i)), 2);
			i++;
		}
		
		while (j < keys2.size()) {
			dist += Math.pow(v2.get(keys2.get(j)), 2);
			j++;
		}
		
		dist = Math.sqrt(dist);
		
		return dist;
	}
	
	
	public boolean rearrangeClusters() {
		
		boolean isChanged = false;
		
		for (String key : vectors.keySet()) {
			
			Double minDist = Double.MAX_VALUE;
			int clusterIndex = 0;
			
			// Calc distance from every vector to every cluster's mean
			for (int i = 0; i < K; i++) {
				Double dist = calcDistance(vectors.get(key), getClusters().get(i).getMean());
				if (dist < minDist) {
					minDist = dist;
					clusterIndex = i;
				}
			}
			
			// Delete from the old cluster
			for (int i = 0; i < K; i++) {
				if (getClusters().get(i).contains(key)) {
					
					if (i != clusterIndex)
						isChanged = true;
					
					getClusters().get(i).deleteVector(key);
					
					break;
				}
			}
			
			// Add to the new cluster
			getClusters().get(clusterIndex).addVector(key, vectors.get(key));
		}
		
		return isChanged;
	}

	public ArrayList<Cluster> getClusters() {
		return clusters;
	}

	public void setClusters(ArrayList<Cluster> clusters) {
		this.clusters = clusters;
	}
	
}
