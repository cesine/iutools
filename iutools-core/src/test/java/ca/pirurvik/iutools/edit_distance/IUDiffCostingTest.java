package ca.pirurvik.iutools.edit_distance;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import ca.nrc.datastructure.Pair;
import ca.nrc.testing.AssertObject;

public class IUDiffCostingTest {
	
	private static class CharDoublingExample {
		public String origStr = null;
		public String revStr = null;
		public String[] expResult = null;
		public String descr = null;
		public Pair<Integer,Integer> expResult_NEW = null;
		
		public CharDoublingExample(String orig, String rev, 
				String[] _expRes) {
			this.origStr = orig;
			this.revStr = rev;
			this.expResult = _expRes;
		}
		
		public CharDoublingExample setExpRes_NEW(Pair<Integer,Integer> _expRes) {
			this.expResult_NEW = _expRes;
			return this;
		}
		
		public CharDoublingExample setDescr(String _descr) {
			this.descr = _descr;
			return this;
		}
		
		public String key() {
			return origStr+"/"+revStr;
		}
		
		public String toString() {
			String str = "";
			if (descr != null) {
				str += "Case: "+descr+"\n   ";
			}
			str = "["+origStr+"->"+revStr+", exp=";
			if (expResult == null) {
				str += "null";
			} else {
				str += "'"+String.join("', '", expResult)+"'";
			}
			return str;
		}
	}
	
	//
	// Examples for testing isCharacterDoubling1way()
	//
	// Note: NOT same as data for testing isCharacterDoubling2ways
	//
	CharDoublingExample[] isCharacterDoubling1way_Data = new CharDoublingExample[] {
			new CharDoublingExample("i", "ii", new String[] {"", ""})
					.setExpRes_NEW(Pair.of(1, 0))
					.setDescr("Doubled on left, no Xtra chars"),
			new CharDoublingExample("ii", "i", null)
					.setExpRes_NEW(Pair.of(1, 0))
					.setDescr("Doubled on right, no Xtra chars"),
			new CharDoublingExample("iiq", "i", null)
					.setExpRes_NEW(Pair.of(1, 1))
					.setDescr("Doubled on left XtraChars on left"),
			new CharDoublingExample("i", "iiq", new String[] {"", "q"})					.setExpRes_NEW(Pair.of(null, null))
					.setExpRes_NEW(Pair.of(1, 1))
					.setDescr("Doubled on right, Xtra chars on right"),
			new CharDoublingExample("iq", "ii", new String[] {"q", ""})					.setExpRes_NEW(Pair.of(null, null))
					.setExpRes_NEW(Pair.of(1, 1))
					.setDescr("Douled on right, Xtra chars on left"),
			new CharDoublingExample("umm", "uum", new String[] {"", ""})
					.setExpRes_NEW(Pair.of(2, 0))
					.setDescr("Doubling a vowel immediatly followed by de-doubling a consonant"),
	};
	
	//
	// Examples for testing isCharacterDoubling2ways()
	//
	// Note: NOT same as data for testing isCharacterDoubling1way
	//
	CharDoublingExample[] isCharacterDoubling2ways_Data = new CharDoublingExample[] {
			new CharDoublingExample("i", "ii", new String[] {"", ""})					.setExpRes_NEW(Pair.of(null, null))
					.setExpRes_NEW(Pair.of(1, 0))
					.setDescr("Doubled on left, no Xtra chars"),
			new CharDoublingExample("ii", "i", new String[] {"", ""})
					.setExpRes_NEW(Pair.of(1, 0))
					.setDescr("Doubled on right, no Xtra chars"),
			new CharDoublingExample("iiq", "i", new String[] {"q", ""})
					.setExpRes_NEW(Pair.of(1, 1))
					.setDescr("Doubled on left XtraChars on left"),
			new CharDoublingExample("i", "iiq", new String[] {"", "q"})
					.setExpRes_NEW(Pair.of(1, 1))
					.setDescr("Doubled on right, Xtra chars on right"),
			new CharDoublingExample("iq", "ii", new String[] {"q", ""})
					.setExpRes_NEW(Pair.of(1, 1))
					.setDescr("Douled on right, Xtra chars on left"),
			new CharDoublingExample("umm", "uum", new String[] {"", ""})
					.setExpRes_NEW(Pair.of(2, 0))
					.setDescr("Doubling a vowel immediatly followed by de-doubling a consonant"),
	};	
	
	@Test
	public void test__isCharacterDoubling_NEW__1way() throws Exception {
		IUDiffCosting costing = new IUDiffCosting();
		
		int totalExamples = 0;
		
		String focusOnExample = null;
//		focusOnExample = "umm/uum";
		
		for (CharDoublingExample anExample: isCharacterDoubling1way_Data) {
			if (focusOnExample != null &&
					!focusOnExample.equals(anExample.key())) {
				continue;
			}
			
			totalExamples++;
			
			Pair<Integer,Integer> gotResult = 
				costing.isCharDoubling(anExample.origStr, anExample.revStr);
			AssertObject.assertDeepEquals(
					"Bad results for example: "+anExample, 
					anExample.expResult_NEW, gotResult);
		}
		
		Assert.assertTrue("No examples were tested!", totalExamples > 0);
		
		if (focusOnExample != null) {
			Assert.fail("WARNING: This test was run on just one example.");
		}		
	}
}
