
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Random;


public class Corpus {
	
	private List<Word> words;
	private Map<Integer, Integer> docids; // docid : doc size
	private Map<Integer, Integer> topicids;  // topicid : topic size

	public Corpus() {
		words = new ArrayList<Word>();
		docids = new HashMap<Integer, Integer>();
		topicids = new HashMap<Integer, Integer>();
	}

	public void addWord(Word w) {
		words.add(w);

		// maintain doc lengths
		if (docids.containsKey(w.docid)) {
			Integer curVal = docids.get(w.docid);
			docids.put(w.docid, curVal + 1);
		} else {
			docids.put(w.docid, 1);
		}

		// maintain topic sizes
		if (topicids.containsKey(w.topicid)) {
			Integer curVal = topicids.get(w.topicid);
			topicids.put(w.topicid, curVal + 1);
		} else {
			topicids.put(w.topicid, 1);
		}
	}

	public Set<Integer> getDocIds() {
		return docids.keySet();
	}

	public Set<Integer> getTopicIds() {
		return topicids.keySet();
	}

	// assigns word i to topic j
	public void assign(int i, int j) {
		words.get(i).topicid = j;
	}

	// careful, these things are mutable.  Don't change the returned Word
	public Word getWord(int i) {
		return words.get(i);
	}

	public void assignAllRandom(int numTopics) {
		Random r = new Random();
		for(int k = 0; k < words.size(); k++) {
			this.assign(k, r.nextInt(numTopics));
		}
	}
	
}

class Word {
	public final String token;
	public final int docid;
	public int topicid;

	Word(String tok, int doc, int topic) {
		token = tok;
		docid = doc;
		topicid = topicid;
	}
}

