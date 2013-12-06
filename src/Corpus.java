
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Random;


public class Corpus {
	
	private int numTopics;
	private Random r;
	private List<Word> words;
	private Map<Integer, Integer> docs; // docid : doc size
	private Map<Integer, Integer> topics;  // topicid : topic size
	private Map<String, Map<Integer, Integer>> word_topics;  // word : (topicid : count)
	private Map<Integer, Map<Integer, Integer>> doc_topics;  // docid : (topicid : count)

	public Corpus(int numTopics) {
		this.numTopics = numTopics;
		words = new ArrayList<Word>();
		docs = new HashMap<Integer, Integer>();
		topics = new HashMap<Integer, Integer>();
		word_topics = new HashMap<String, Map<Integer, Integer>>();
		doc_topics = new HashMap<Integer, Map<Integer, Integer>>();
		r = new Random();
	}

	public Word[] getAllWords() {
		Word[] tmp = new Word[1];  // signifys the return type of toArray
		return words.toArray(tmp);
	}

	public int numWords() {
		return this.words.size();
	}

	public int numTopics() {
		return this.numTopics; }

	public int numDocuments() {
		return docs.keySet().size();
	}

	private int randTopicId() {
		return r.nextInt(numTopics);
	}

	public void addWord(String token, int docid) {
		Word w = new Word(token, docid, this.randTopicId());
		words.add(w);

		// maintain doc lengths
		this.changeMap(docs, w.docid, +1);
		this.changeMap(doc_topics, w.docid, w.topicid, +1);

		// maintain topic sizes
		this.changeMap(topics, w.topicid, +1);
		this.changeMap(word_topics, w.token, w.topicid, +1);
	}

	public Set<Integer> getDocIds() {
		return docs.keySet();
	}

	public Set<Integer> getTopicIds() {
		return topics.keySet();
	}

	// assigns word i to topic j
	public void assign(int i, int j) {
		Word word = this.getWord(i);
		String tok = word.token;
		int k = word.topicid;
		int d = word.docid;

		// decrement counter for topic current assignment
		this.changeMap(topics, k, -1);
		this.changeMap(doc_topics, d, k, -1);
		this.changeMap(word_topics, tok, k, -1);

		words.get(i).topicid = j;

		// increment counter for topic new assignment
		this.changeMap(topics, j, +1);
		this.changeMap(doc_topics, d, j, +1);
		this.changeMap(word_topics, tok, j, +1);
	}

	// careful, these things are mutable.  Don't change the returned Word
	public Word getWord(int i) {
		return words.get(i);
	}

	public List<Word> getWords(){
		return this.words;
	}
	
	public int getTopicCount(int topicid) {
		if (topics.containsKey(topicid)) {
			return topics.get(topicid);
		}
		return 0;
	}

	public int getDocCount(int docid) {
		if (docs.containsKey(docid)) {
			return docs.get(docid);
		}
		return 0;
	}

	public int getWordTopicCount(String word, int topicid) {
		return readMap(word_topics, word, topicid);
	}

	public int getDocTopicCount(int docid, int topicid) {
		return readMap(doc_topics, docid, topicid);
	}

/*
	public void assignAllRandom(int numTopics) {
		Random r = new Random();
		for(int k = 0; k < words.size(); k++) {
			this.assign(k, r.nextInt(numTopics));
		}
	}
*/

	// changes the count of the map entry by delta
	private void changeMap(Map<Integer, Integer> map, int key, int delta) {
		int curVal = 0;
		if (map.containsKey(key)) {
			curVal = map.get(key);
		}
		int newVal = curVal + delta;
		assert newVal >= 0;
		map.put(key, curVal + delta);
	}

	// changes the count of the map entry by delta
	private void changeMap(Map<String, Integer> map, String key, int delta) {
		int curVal = 0;
		if (map.containsKey(key)) {
			curVal = map.get(key);
		}

		int newVal = curVal + delta;
		assert newVal >= 0;
		map.put(key, curVal + delta);
	}

	// changes count of nested maps
	private void changeMap(Map<String, Map<Integer, Integer>> map, String key1, int key2, int delta) {
		int curVal = 0;
		if (!map.containsKey(key1)) {
			map.put(key1, new HashMap<Integer, Integer>());
		}
		Map<Integer, Integer> innerMap = map.get(key1);

		this.changeMap(innerMap, key2, delta);


/*
		if (innerMap.containsKey(key2)) {
			curVal = innerMap.get(key2);
		} 

		int newVal = curVal + delta;
		assert newVal >= 0;
		innerMap.put(key2, curVal + delta);
*/
	}
	
	// changes count of nested maps
	private void changeMap(Map<Integer, Map<Integer, Integer>> map, int key1, int key2, int delta) {
		int curVal = 0;
		if (!map.containsKey(key1)) {
			map.put(key1, new HashMap<Integer, Integer>());
		}
		Map<Integer, Integer> innerMap = map.get(key1);

		this.changeMap(innerMap, key2, delta);
/*
		if (innerMap.containsKey(key2)) {
			curVal = innerMap.get(key2);
		} 

		int newVal = curVal + delta;
		assert newVal >= 0;
		innerMap.put(key2, curVal + delta);
*/
	}

	// reads a value out of the map or returns 0 if entries are undefined
	private int readMap(Map<String, Map<Integer, Integer>> map, String key1, int key2) {
		if (map.containsKey(key1)) {
			Map<Integer, Integer> innerMap = map.get(key1);
			if (innerMap.containsKey(key2)) {
				return innerMap.get(key2);
			}
		}
		return 0;
	}

	// reads a value out of the map or returns 0 if entries are undefined
	private int readMap(Map<Integer, Map<Integer, Integer>> map, int key1, int key2) {
		if (map.containsKey(key1)) {
			Map<Integer, Integer> innerMap = map.get(key1);
			if (innerMap.containsKey(key2)) {
				return innerMap.get(key2);
			}
		}
		return 0;
	}
}

class Word {
	public final String token;
	public final int docid;
	public int topicid;

	Word(String tok, int doc, int topic) {
		token = tok;
		docid = doc;
		topicid = topic;
	}
}

