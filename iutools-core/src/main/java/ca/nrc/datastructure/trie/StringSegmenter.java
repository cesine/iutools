package ca.nrc.datastructure.trie;

import java.util.concurrent.TimeoutException;

import ca.inuktitutcomputing.data.LinguisticDataException;

public abstract class StringSegmenter {
	
	public abstract String[] segment(String string, boolean fullAnalysis) throws TimeoutException, StringSegmenterException, LinguisticDataException;
	
	public abstract String[][] possibleSegmentations(
			String string, boolean fullAnalysis) 
			throws TimeoutException, StringSegmenterException;
	
	public abstract void disactivateTimeout();

	public String[] segment(String string) 
			throws TimeoutException, StringSegmenterException, 
			LinguisticDataException {
		return segment(string, false);
	}
	
	public String[][] possibleSegmentations(
		String string) throws TimeoutException, StringSegmenterException {
		return possibleSegmentations(string, false);
	}

}
