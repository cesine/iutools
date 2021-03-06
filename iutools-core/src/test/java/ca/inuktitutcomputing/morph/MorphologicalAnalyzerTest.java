package ca.inuktitutcomputing.morph;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.inuktitutcomputing.data.LinguisticDataException;
import ca.nrc.testing.AssertNumber;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MorphologicalAnalyzerTest {
		
	@Test
	public void test__MorphologicalAnalyzer_Synopsis() throws Exception {
		// By default, the analyzer uses the linguistic database in the CSV format
		MorphologicalAnalyzer analyzer = new MorphologicalAnalyzer();
		// By default, the analysis times out after 10 seconds; time ou can be set different
		analyzer.setTimeout(15000); // in milliseconds
		// By default, the timing out is active; it can be disactivated and reactivated
		analyzer.disactivateTimeout();
		analyzer.activateTimeout();
		
		// The purpose of the analyzer is to decompose an inuktitut word into its morphemes
		String word = "iglumik";
		try {
			 Decomposition[] analyses = analyzer.decomposeWord(word);
		} catch (TimeoutException e) {
			// do something
		}
	}

	
	
	@Test(expected=TimeoutException.class)
	public void test__decomposeWord__timeout_2s() throws Exception  {
		MorphologicalAnalyzer analyzer = new MorphologicalAnalyzer();
		analyzer.setTimeout(2000);
		String word = "ilisaqsitittijunnaqsisimannginnama";
		analyzer.decomposeWord(word);
	}

	@Test(expected=TimeoutException.class)
	public void test__decomposeWord__timeout_10s() throws Exception  {
		MorphologicalAnalyzer analyzer = new MorphologicalAnalyzer();
		String word = "ilisaqsitittijunnaqsisimannginnama";
		analyzer.decomposeWord(word);
	}

	@Test
	public void test__decomposeWord__maligatigut() throws Exception  {
		MorphologicalAnalyzer analyzer = new MorphologicalAnalyzer();
		String word = "maligatigut";
		try {
			analyzer.disactivateTimeout();
			Decomposition[] decs = analyzer.decomposeWord(word);
			Assert.assertTrue(decs.length==0);
//			for (int i=0; i<decs.length; i++) {
//				System.out.println(decs[i].toStr2());
//			}
		} catch(Exception e) {
			throw e;
		}
	}

	@Test
	public void test__decomposeWord__uqaqtiup() throws Exception  {
		MorphologicalAnalyzer analyzer = new MorphologicalAnalyzer();
		String word = "uqaqtiup";
		try {
			analyzer.disactivateTimeout();;
			Decomposition[] decs = analyzer.decomposeWord(word);
			Assert.assertTrue(decs.length!=0);
			for (int i=0; i<decs.length; i++) {
				System.out.println(decs[i].toStr2());
			}
		} catch(Exception e) {
			throw e;
		}
	}

	@Test
	public void test__decomposeWord__sivuliuqtii() throws Exception  {
		MorphologicalAnalyzer analyzer = new MorphologicalAnalyzer();
		String word = "sivuliuqtii";
		try {
			analyzer.disactivateTimeout();;
			Decomposition[] decs = analyzer.decomposeWord(word);
			Assert.assertTrue(decs.length!=0);
			for (int i=0; i<decs.length; i++) {
				System.out.println(decs[i].toStr2());
			}
		} catch(Exception e) {
			throw e;
		}
	}

	@Test
	public void test__decomposeWord__ammalu() throws Exception  {
		MorphologicalAnalyzer analyzer = new MorphologicalAnalyzer();
		String word = "ammalu";
		try {
			analyzer.disactivateTimeout();;
			Decomposition[] decs = analyzer.decomposeWord(word);
			Assert.assertTrue(decs.length!=0);
			for (int i=0; i<decs.length; i++) {
				System.out.println(decs[i].toStr2());
			}
		} catch(Exception e) {
			throw e;
		}
	}

		
	/*
	 * Ce test vérifie que les analyses qui contiennent des séquences de
	 * morphèmes pour lesquelles il existe un morphème combiné sont supprimées.
	 * Ex. : la première analyse est supprimée puisque dans la seconde, il y a
	 *       le morphème juksaq qui est la combinaison de juq et ksaq.
	 * {apiq:apiq/1v}{suq:suq/1vv}{ta:jaq/1vn}{u:u/1nv}{ju:juq/1vn}{ksaq:ksaq/1nn}
	 * {apiq:apiq/1v}{suq:suq/1vv}{ta:jaq/1vn}{u:u/1nv}{juksaq:juksaq/1vn}
	 */
	@Test
	public void test__decomposeWord__apiqsuqtaujuksaq() throws Exception  {
		MorphologicalAnalyzer analyzer = new MorphologicalAnalyzer();
		String word = "apiqsuqtaujuksaq";

		try {
			analyzer.disactivateTimeout();;
			Decomposition[] decs = analyzer.decomposeWord(word);
			Assert.assertTrue(decs.length!=0);
			for (int i=0; i<decs.length; i++) {
				Assert.assertTrue(!decs[i].toStr2().contains("{ju:juq/1vn}{ksaq:ksaq/1nn}"));
			}
		} catch(Exception e) {
			throw e;
		}
	}


	@Test
	public void test__decomposeWord__immagaa() throws Exception  {
		MorphologicalAnalyzer analyzer = new MorphologicalAnalyzer();
		String word = "immagaa";

		try {
			analyzer.disactivateTimeout();;
			Decomposition[] decs = analyzer.decomposeWord(word);
			Assert.assertTrue(decs.length==0);
			for (int i=0; i<decs.length; i++) {
				System.out.println(decs[i].toStr2());
			}
		} catch(Exception e) {
			throw e;
		}
	}




	@Test
	public void test__decomposeWord__avunngaqtuq() throws Exception  {
		MorphologicalAnalyzer analyzer = new MorphologicalAnalyzer();
		String word = "avunngaqtuq";

		try {
			analyzer.disactivateTimeout();;
			Decomposition[] decs = analyzer.decomposeWord(word);
			Assert.assertTrue(decs.length!=0);
			for (int i=0; i<decs.length; i++) {
				System.out.println(decs[i].toStr2());
			}
			
			word = "avunngaujjijuq";
			decs = analyzer.decomposeWord(word);
			Assert.assertTrue(decs.length!=0);
			for (int i=0; i<decs.length; i++) {
				System.out.println(decs[i].toStr2());
			}
			
			word = "avunngautijuq";
			decs = analyzer.decomposeWord(word);
			Assert.assertTrue(decs.length!=0);
			for (int i=0; i<decs.length; i++) {
				System.out.println(decs[i].toStr2());
			}
		} catch(Exception e) {
			throw e;
		}
	}


	@Test
	public void test__decomposeWord__atuagaq() throws Exception  {
		MorphologicalAnalyzer analyzer = new MorphologicalAnalyzer();
		String word = "atuagaq";

		try {
			analyzer.disactivateTimeout();;
			Decomposition[] decs = analyzer.decomposeWord(word);
			Assert.assertTrue(decs.length!=0);
			for (int i=0; i<decs.length; i++) {
				System.out.println(decs[i].toStr2());
			}
		} catch(Exception e) {
			throw e;
		}
	}


	@Test
	public void test__decomposeWord__maligaliuqtinik() throws Exception  {
		MorphologicalAnalyzer analyzer = new MorphologicalAnalyzer();
		String word = "maligaliuqtinik";

		try {
//			analyzer.disactivateTimeout();;
			Decomposition[] decs = analyzer.decomposeWord(word);
			Assert.assertTrue(decs.length!=0);
			for (int i=0; i<decs.length; i++) {
				System.out.println(decs[i].toStr2());
			}
		} catch(Exception e) {
			throw e;
		}
	}


	@Test
	public void test__decomposeWord__sivungujuq() throws Exception  {
		MorphologicalAnalyzer analyzer = new MorphologicalAnalyzer();
		String word = "sivungujuq";

		try {
			analyzer.disactivateTimeout();;
			Decomposition[] decs = analyzer.decomposeWord(word);
			Assert.assertTrue(decs.length!=0);
			Pattern p = Pattern.compile("^"+Pattern.quote("{sivu:sivu/1n}{ngu:u/1nv}"));
			boolean found = false;
			for (int i=0; i<decs.length; i++) {
				System.out.println(decs[i].toStr2());
				Matcher mp = p.matcher(decs[i].toStr2());
				if (mp.find()) {
					found = true;
					break;
				}
			}
			Assert.assertTrue("",found);
		} catch(Exception e) {
			throw e;
		}
	}

	@Test
	public void test__decomposeWord__SameWordTwiceInARow__SecondTimeShouldBeInstantaneous() 
					throws Exception {
		Logger tLogger = Logger.getLogger("test__decomposeWord__SameWordTwiceInARow__SecondTimeShouldBeInstantaneous");
		
		MorphologicalAnalyzer analyzer = new MorphologicalAnalyzer();
		
		String word = "iglumik";
		
		// Remove the word from the decompositions cache.
		// This ensures that the first time we 
		// decompose the word, it will be done from scratch
		//
		MorphologicalAnalyzer.removeFromCache(word);
		long start = System.nanoTime();
		Decomposition[] analyses = analyzer.decomposeWord(word);
		long elapsedFirstTime = System.nanoTime() - start;
		
		// Analyses for that word should now be in the cache
		// so the second time should be much faster.
		//
		start = System.nanoTime();
		analyses = analyzer.decomposeWord(word);
		long elapsedSecondTime = System.nanoTime() - start;
		
		tLogger.trace("elapsedFirstTime="+elapsedFirstTime+", elapsedSecondTime="+elapsedSecondTime);
		
		AssertNumber.isLessOrEqualTo("Second time we decompose word "+word+" should have been 10x faster", 
				1.0 * elapsedSecondTime, 1.0 * elapsedFirstTime / 10);
	}
	
	public void test__decomposeWord__with_extendedAnalysis() throws Exception  {
		MorphologicalAnalyzer analyzer = new MorphologicalAnalyzer();
		String word = "makpiga";

		try {
			analyzer.disactivateTimeout();;
			Decomposition[] decs = analyzer.decomposeWord(word,true);
			Assert.assertTrue(decs.length!=0);
			Pattern p = Pattern.compile(Pattern.quote("{ga:gaq/1vn}"));
			boolean found = false;
			for (int i=0; i<decs.length; i++) {
				System.out.println(decs[i].toStr2());
				Matcher mp = p.matcher(decs[i].toStr2());
				if (mp.find()) {
					found = true;
					break;
				}
			}
			Assert.assertTrue("",found);
		} catch(Exception e) {
			throw e;
		}
	}
	@Test
	public void test__decomposeWord__without_extendedAnalysis() throws Exception  {
		MorphologicalAnalyzer analyzer = new MorphologicalAnalyzer();
		String word = "makpiga";

		try {
			analyzer.disactivateTimeout();;
			Decomposition[] decs = analyzer.decomposeWord(word, false);
			Assert.assertTrue("NO decompositions should have been produced", 
					decs.length==0);
		} catch(Exception e) {
			throw e;
		}
	}

//	@Test
//	public void test_validate_neutral_deletion__() {
//		MorphologicalAnalyzer analyzer = new MorphologicalAnalyzer();
//		analyzer.validate_neutral_insertion(context, action1, action2, stem, affixCandidate, form, affix, posAffix, partOfComp);
//	}

}
