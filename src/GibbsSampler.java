
import java.util.Random;
import org.apache.commons.math3.special.Gamma;


public class GibbsSampler {

	private Corpus corpus;
	private Random r;
	private double alpha;
	private double beta;
	private int numTopics;
	private int numWords;

	// assuming uniform dirichlet hyperparameters
	public GibbsSampler(Corpus corpus, double alpha, double beta) {
		this.corpus = corpus;
		this.numTopics = corpus.numTopics();
		this.numWords = corpus.numWords();

		this.alpha = alpha;
		this.beta = beta;

		this.r = new Random();
	}

	public Corpus go(int iterations) {

		for (int curIter = 0; curIter < iterations; curIter++) {
			for (int wordIdx = 0; wordIdx < this.numWords; wordIdx++) {
				int new_topic = sample(wordIdx);
				corpus.assign(wordIdx, new_topic);
			}

			double likelihood = logDataLikelihood();
			// this way we can just capture standard out and have a plottable file
			System.out.println(curIter + "\t" + likelihood);
		}
		return this.corpus;
	}

	// computes the categorical distribution of the word over topics
	// returns a single sample according to the categorical distribution
	int sample(int wordIdx) {
		double[] probs = new double[numTopics];
		double totalWeight = 0.0;

		// calculate weight of each topic for the given word
		for (int topicId = 0; topicId < numTopics; topicId++) {
			probs[topicId] = calcTopicWeight(wordIdx, topicId);
			totalWeight += probs[topicId];
		}

		// normalize the weights to form a well defined prob distribution
		for (int topicId = 0; topicId < numTopics; topicId++) {
			probs[topicId] /= totalWeight;
		}

		return categorical(probs);
	}

	double calcTopicWeight(int wordIdx, int topicId) {
		Word w = corpus.getWord(wordIdx);
		int leaveOutCurrent = (topicId == w.topicid ? 1 : 0);

		// get raw counts
		int wordTopicCount = corpus.getWordTopicCount(w.token, topicId);
		int docTopicCount = corpus.getDocTopicCount(w.docid, topicId);
		int topicCount = corpus.getTopicCount(topicId);
		int docCount = corpus.getDocCount(w.docid);

		// calculate terms of the equation
		double numer1 = wordTopicCount + this.beta - leaveOutCurrent;
		double denum1 = topicCount + (this.beta * this.numWords) - leaveOutCurrent;

		double numer2 = docTopicCount + this.alpha - leaveOutCurrent;
		double denum2 = docCount - 1;  // whatever w's topic, it is part of the document

		// calculate result
		double result = (numer1 / denum1) * (numer2 / denum2);
		return result;
	}

	// given a categorical distribution, return the index of the selected discrete symbol
	int categorical(double[] probs) {
		double val = this.r.nextDouble();
		double sum = 0.0;
		for (int k = 0; k < probs.length; k++) {
			sum += probs[k];
			if (val <= sum) {
				return k;
			}
		}
		// we shouldn't ever reach here if we are given a well defined distribution
		return probs.length-1;

	}

	// computes the log data likelihood given the current topic assignments
	// actually computes the log of a value proportional to the data likelihood
	double logDataLikelihood() {
		double logLikelihood = 0;
		for (int topicId = 0; topicId < this.numTopics; topicId++) {
			double numer = 0; // it's in the numerator in the unlogged equation
			for (int wordIdx = 0; wordIdx < this.numWords; wordIdx++) {
				Word w = corpus.getWord(wordIdx);
				int wordTopicCount = corpus.getWordTopicCount(w.token, topicId);
				double arg = wordTopicCount + this.beta;
				double value = Gamma.logGamma(arg);
				numer += value;
			}
			int topicCount = corpus.getTopicCount(topicId);
			double denum_arg = topicCount + this.numWords * this.beta;
			double denum_value = Gamma.logGamma(denum_arg);

			logLikelihood += (numer - denum_value);
		}
		return logLikelihood;
	}
}

