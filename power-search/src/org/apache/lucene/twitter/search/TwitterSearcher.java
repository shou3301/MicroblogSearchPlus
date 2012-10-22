/**
 * 
 */
package org.apache.lucene.twitter.search;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author cshou
 *
 */
public class TwitterSearcher {

	/**
	 * @param args
	 */
	private static final String WELCOME = "Welcome to Twitter Searcher! If you need any help, please use HELP";
	private static final String HELP = "Use 'search' to search...";
	private static CoreSearcher searcher;
	
	public TwitterSearcher() throws Exception {
		//this.searcher = new CoreSearcher();
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		searcher = new CoreSearcher();
		command();
	}
	
	private static void command() throws Exception {
		String input = "";
		
		System.out.println(WELCOME);
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		
		while (!(input.equals("exit"))) {
			input = in.readLine();
			
			switch (input) {
				case "exit": {
					System.out.println("Shutting down program...");
					break;
				}
				case "help": {
					System.out.println(HELP);
					break;
				}
				case "search": {
					System.out.println("Please input query...");
					input = in.readLine();	// query here
					//TODO to process query
					String q = input;
					System.out.println("Searching " + input + "...");
					searcher.search(q);
					break;
				}
				case "check": {
					//TODO to show latest 20 tweets
					break;
				}
				default: {
					System.out.println("This is not a command!");
					continue;
				}
			}
		}
	}

}
