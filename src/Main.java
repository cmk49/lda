import java.io.IOException;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Corpus corpus = new Corpus();
		Parser parser = new Parser(corpus);
		try {
			parser.loadDocuments();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Finished loading documents");
	}

}
