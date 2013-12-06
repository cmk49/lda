import java.io.FileNotFoundException;
import java.io.IOException;


public class Main {
	
	static int numTopics = 100;
	static int numIters = 10;
	static double alpha = 1.0;
	static double beta = 1.0;
	static String outputPath = "output/"; // must end in a slash

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//System.out.println(java.util.Arrays.toString(args));

		parseArgs(args);
		//System.out.println(outputPath);

		Corpus corpus = new Corpus(numTopics);
		Parser parser = new Parser(corpus);
		try {
			parser.loadDocuments();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Finished loading documents");
		GibbsSampler sampler = new GibbsSampler(corpus, alpha, beta);
		corpus = sampler.go(numIters);
		//corpus = sampler.go(5);
		try {
			Results result = new Results(corpus.getAllWords(), outputPath);
			for (int topicId = 0; topicId < numIters; topicId++) {
				result.generateNWords(topicId, 10);
				result.generateCodedDocument(topicId);
				result.generateWordCloud(topicId); // I really don't want all of them...
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void parseArgs(String[] args) {
		if (args.length >= 1) numTopics = Integer.parseInt(args[0]);
		if (args.length >= 2) numIters = Integer.parseInt(args[1]);
		if (args.length >= 3) alpha = Double.parseDouble(args[2]);
		if (args.length >= 4) beta = Double.parseDouble(args[3]);
		if (args.length >= 5) outputPath = args[4];
	}

}
