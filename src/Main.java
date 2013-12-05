import java.io.IOException;


public class Main {
	
	static int numTopics = 100;
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
		System.out.println("Finished loading documents");
	}

	static void parseArgs(String[] args) {
		if (args.length >= 1) numTopics = Integer.parseInt(args[0]);
		if (args.length >= 2) alpha = Double.parseDouble(args[1]);
		if (args.length >= 3) beta = Double.parseDouble(args[2]);
		if (args.length >= 4) corpusPath = args[3];
	}

}
