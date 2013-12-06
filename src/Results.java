import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.mcavallo.opencloud.Cloud;
import org.mcavallo.opencloud.formatters.HTMLFormatter;


public class Results {
	
	// topicID : list of most common words
	private Map<Integer, Map<String, Integer>> topicToMostCommonWords = 
			new HashMap<Integer, Map<String, Integer>>();
	private Map<Integer, List<String>> sortedWords = new HashMap<Integer, List<String>>();
	// topicID : cloud of words
	private Map<Integer, Cloud> wordClouds = new HashMap<Integer, Cloud>();
	// documentID : word and topicID pairs
	private Map<Integer, ArrayList<WordTopicPair>> codedDocument = 
			new HashMap<Integer, ArrayList<WordTopicPair>>(); 

	private String dirPath;
	
	public Results(Word[] words, String dirPath){
		this.dirPath = dirPath;
		for (Word word : words){
			//word cloud
			int topicID = word.topicid;
			Cloud cloud;
			if (!wordClouds.containsKey(topicID)){
				cloud = new Cloud();
				wordClouds.put(topicID, cloud);
			}
			else {
				cloud = wordClouds.get(topicID);
			}
			cloud.addTag(word.token);
			wordClouds.put(topicID, cloud);
			
			//document coding
			int documentID = word.docid;
			ArrayList<WordTopicPair> wordsInDoc;
			if (!codedDocument.containsKey(documentID)){
				wordsInDoc = new ArrayList<WordTopicPair>();
			}
			else {
				wordsInDoc = codedDocument.get(documentID);
			}
			wordsInDoc.add(new WordTopicPair(word.token, word.topicid));
			codedDocument.put(documentID, wordsInDoc);
			
			//most common
			Map<String, Integer> wordCounts;
			if (topicToMostCommonWords.containsKey(topicID)){
				wordCounts = topicToMostCommonWords.get(topicID);
			}
			else{
				wordCounts = new HashMap<String, Integer>();
			}
			int count = 0;
			if (wordCounts.containsKey(word.token)){
				count = wordCounts.get(word.token);
			}
			count++;
			wordCounts.put(word.token, count);
			topicToMostCommonWords.put(topicID, wordCounts);
		}
		//TODO select highest values from topicToMostCommonWords

		for (Integer topicId : topicToMostCommonWords.keySet()) {
			List<String> commonWords = new ArrayList<String>();
			// hope this is how it works
			Map<String,Integer> wordCounts = topicToMostCommonWords.get(topicId);
			StrValueComparator bvc =  new StrValueComparator(wordCounts);
			TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
			for (String str : sorted_map.keySet()){
				commonWords.add(str);
			}
			sortedWords.put(topicId, commonWords);
		}
	}

	public void generateNWords(int topicId, int nWords) throws FileNotFoundException{
		String filepath = this.dirPath + "" + nWords + "-words-" + topicId + ".txt";
		PrintWriter writer = new PrintWriter(filepath);
		List<String> words = sortedWords.get(topicId);
		for (int k = 0; k < nWords && k < words.size(); k++) {
			writer.print(words.get(k));
			writer.print("\n");
		}
		writer.close();
	}
	
	public void generateWordCloud(int topicID) throws FileNotFoundException{
		HTMLFormatter formater = new HTMLFormatter();
		Cloud cloud = wordClouds.get(topicID);
		String htmlCode = formater.html(cloud);
		String filepath = this.dirPath + "word-cloud-" + topicID + ".html";
		PrintWriter writer = new PrintWriter(filepath);
		writer.print(htmlCode);
		writer.close();
	}
	
	public void generateCodedDocument(int docID) throws IOException{
		String filepath = this.dirPath + "colored-doc-" + docID + ".html";
		PrintWriter writer = new PrintWriter(filepath);
		writer.println(htmlHeader());
		Map<Integer, String> topicColors = getTopicColorAssignment(docID);
		
		// read in file
		File file = new File(Parser.getDocumentPath(docID));
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		StringBuilder document = new StringBuilder();
		while (null != (line = reader.readLine())){
			document.append(line);			
		}
		reader.close();
		String[] words = document.toString().split("[ \\t\\n\\r]"); //split on whitespace
		
		// write colored document
		ArrayList<WordTopicPair> codedWords = codedDocument.get(docID);
		int codedIndex = 0;
		for (String word : words){
			String color = "";
			while (word.toLowerCase().contains(codedWords.get(codedIndex).word)){
				color = topicColors.get(codedWords.get(codedIndex).topicID);
				codedIndex++;
			}
			if (color.isEmpty()){
				writer.print(word + " ");
			}
			else {
				writer.print("<span class='" + color +"'> " + word + "</span>");
			}
		}
		writer.println(htmlFooter());
		writer.close();
	}
	
	private Map<Integer, String> getTopicColorAssignment(int docID){
		Map<Integer, Integer> topicCounts = new HashMap<Integer, Integer>();
		for (WordTopicPair pair : codedDocument.get(docID)){
			int count = 0;
			if (topicCounts.containsKey(pair.topicID)){
				count = topicCounts.get(pair.topicID);
			}
			topicCounts.put(pair.topicID, count++);
		}
		ValueComparator bvc =  new ValueComparator(topicCounts);
        TreeMap<Integer,Integer> sorted_map = new TreeMap<Integer,Integer>(bvc);
		Map<Integer, String> topicColors = new HashMap<Integer, String>();
		int i = 0;
        for (int topicID : sorted_map.keySet()){
        	String color = "purple";
        	switch (i++){
	        	case 0: color = "red"; break;
	        	case 1: color = "orange"; break;
	        	case 2: color = "yellow"; break;
	        	case 3: color = "green"; break;
	        	case 4: color = "blue"; break;
        	}
        	topicColors.put(topicID, color);
        }
        return topicColors;
	}
	
	private String htmlHeader(){
		return "<!DOCTYPE html><html><head><style>.red { color: #FF0000; }.orange { color:" +
				"#FF6600; }.yellow { color: #FFCC00; }.green { color: #00CC00; }.blue { color:" +
				"#0000FF; }.purple { color: #660066; }</style></head><body>";
	}
	
	private String htmlFooter(){
		return "</body></html>";
	}
}

class ValueComparator implements Comparator<Integer> {

    Map<Integer, Integer> base;
    public ValueComparator(Map<Integer, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(Integer a, Integer b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}

class StrValueComparator implements Comparator<String> {

    Map<String, Integer> base;
    public StrValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}
