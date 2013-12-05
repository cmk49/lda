import java.io.IOException;


public class Main {
	
	static int numTopics = 100;
	static int numIters = 1;
	static double alpha = 1.0;
	static double beta = 1.0;
	static String corpusPath = "./gc";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		parseArgs(args);

		// TODO: make corpus path a parameter
		Corpus corpus = new Corpus(numTopics);
		Parser parser = new Parser(corpus);
		try {
			parser.loadDocuments();
		} catch (IOException e) {
			e.printStackTrace();
		}
		GibbsSampler sampler = new GibbsSampler(corpus, alpha, beta);
		sampler.go(numIters);
		System.out.println("Finished loading documents");
	}

	static void parseArgs(String[] args) {
		if (args.length >= 1) numTopics = Integer.parseInt(args[0]);
		if (args.length >= 2) numIters = Integer.parseInt(args[1]);
		if (args.length >= 3) alpha = Double.parseDouble(args[2]);
		if (args.length >= 4) beta = Double.parseDouble(args[3]);
		if (args.length >= 5) corpusPath = args[4];
	}

}
