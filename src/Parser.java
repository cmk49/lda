import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;


public class Parser {
	
	private HashSet<String> stopwords = new HashSet<String>();
	private ArrayList<String> documentPaths = new ArrayList<String>();
	private int currentDocumentID = 0;
	private Corpus corpus;
	
	public Parser(Corpus c){
		this.corpus = c;
	}
	
	public void loadDocuments() throws IOException{
		loadStopWords();
		File folder = new File("gc/");
		loadFilesInFolder(folder);
	}
	
	private void loadFilesInFolder(File folder) throws IOException{
		for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	loadFilesInFolder(fileEntry);
	        } else {
	        	loadFile(fileEntry);
	        }
	    }
	}
	
	private void loadStopWords() throws IOException{
		File file = new File("stop-words.txt");
		BufferedReader in = new BufferedReader(new FileReader(file));
		documentPaths.add(currentDocumentID++, file.getPath());
		String line;
		while (null != (line = in.readLine())){
			stopwords.add(line.trim().toLowerCase());
		}
		in.close();
	}
	
	private void loadFile(File file) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(file));
		documentPaths.add(currentDocumentID++, file.getPath());
		String line;
		while (null != (line = in.readLine())){
			for (String word : line.split("[\\p{P} \\t\\n\\r]")){
				word = word.toLowerCase();
				if (word.length() < 2 || stopwords.contains(word))
					continue;
				//Word w = new Word(word, currentDocumentID - 1, 0);
				//corpus.addWord(w);
				corpus.addWord(word, currentDocumentID -1);
			}
		}
		in.close();
	}
}
