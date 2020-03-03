package ca.pirurvik.iutools.spellchecker;

import java.util.HashSet;
import java.util.Set;

/** 
 * This class represents an example used for evaluating
 * the SpellChecker.
 * 
 * @author desilets
 *
 */
public class SpellCheckerExample extends BinaryClassifierExample {
	
	String wordToCheck = null;
	Set<String> acceptableCorrections = null;

	public SpellCheckerExample(String _wordToCheck) {
		super(_wordToCheck);
		this.init_SpellCheckerExample(_wordToCheck, new String[] {});
	}
	

	public SpellCheckerExample(String _wordToCheck, String... _acceptableCorrections) {
		super(_wordToCheck);
		this.init_SpellCheckerExample(_wordToCheck, _acceptableCorrections);
	}

	private void init_SpellCheckerExample(String _wordToCheck, String[] _acceptableCorrections) {
		this.wordToCheck = _wordToCheck;
		this.acceptableCorrections = new HashSet<String>();
		for (String anAcceptable: _acceptableCorrections) {
			this.acceptableCorrections.add(anAcceptable);
		}
		
		if (_acceptableCorrections == null && _acceptableCorrections.length == 0) {
			this.corectCategory = "ok";
		} else {
			this.corectCategory = "misspelled";
		}
	}
	
}
