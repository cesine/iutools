package ca.nrc.datastructure.trie;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;

import ca.nrc.testing.AssertHelpers;
import ca.nrc.testing.AssertObject;

public abstract class TrieTest {
	
	public abstract Trie makeTrieToTest() throws Exception;
	
	/******************************************
	 * DOCUMENTATION TESTS
	 ******************************************/
	
	@Test
	public void test__Trie__Synopsis() throws Exception {
		//
		// Use a Trie to index word by a key that consists of a sequence of 
		// strings.
		// 
		// The elements of a word's key can be anything you want, for example:
		// - The word's sequence of characters
		// - The word's sequence of morpheme IDs
		// - The word's sequence of morpheme written forms
		//
		// In the rest of this test, we will assume the first use case (i.e. 
		// index words by their sequence of characters).
		//
		//
		Trie trie = makeTrieToTest();
		
		// For the rest of the test we will use a character-based trie.
		//
		// The first thing you need to do is add words to the trie:
		//
		String[] helloChars = "hello".split("");
		try {
			trie.add(helloChars, "hello");
		} catch (TrieException e) {
		}
		
		// Then, you can retrieve the node that corresponds to a particular 
		// sequence of chars.
		//
		TrieNode node = trie.getNode(helloChars);
		if (node == null) {
			// This means the string was not found in the Trie
		} else {
		}
	}
	
	@Test
	public void test_getParentNode() throws Exception {
		Trie charTrie = makeTrieToTest();
		charTrie.add("hello".split(""),"hello");
		charTrie.add("hit".split(""),"hit");
		charTrie.add("abba".split(""),"abba");
		charTrie.add("helios".split(""),"helios");
		charTrie.add("helm".split(""),"helm");
		charTrie.add("ok".split(""),"ok");
		
		TrieNode parent;
		// pass keys as argument
		parent = charTrie.getParentNode(new String[] {});
		assertTrue("The parent node of the root should be null.",parent==null);
		parent = charTrie.getParentNode("hel".split(""));
		assertEquals("The parent node of 'hel' should be 'he'.","h e",parent.keysAsString());
		// pass node as argument
		parent = charTrie.getParentNode(parent);
		assertEquals("The parent node of 'he' should be 'h'.","h",parent.keysAsString());
	}
	
	@Test
	public void test_add__check_terminal() throws Exception {
		Trie charTrie = makeTrieToTest();
		String word = "hi";
		charTrie.add(word.split(""), word);
		
		TrieNode terminalNode = charTrie.getNode((word+"\\").split(""));
		new AssertTrieNode(terminalNode, 
				"Terminal node for word "+word+" was not as expected")
			.isTerminal()
			.hasMostFrequentForm(word)
			.surfaceFormFrequenciesEqual(
				new Pair[] {
					Pair.of(word, new Long(1))
				})
			;
				
		
//		assertEquals(
//			"Surface form of terminal node is not correct for word "+word,
//			word,terminalNode.mostFrequentSurfaceForm());
//		Map<String,Long> surfaceForms = terminalNode.getSurfaceForms();
//		Map<String,Long> expSurfFormFreqs = new HashMap<String,Long>();
//		{
//			expSurfFormFreqs.put(word, new Long(1));
//		}
//		AssertObject.assertDeepEquals(
//			"Surface form frequencies not as expected for word "+word,
//			expSurfFormFreqs, terminalNode.getSurfaceForms());
	}
	
	@Test
	public void test_add__check_terminal_inuktitut() throws Exception {
		StringSegmenter iuSegmenter = new StringSegmenter_IUMorpheme();
		Trie iumorphemeTrie = makeTrieToTest();
		iumorphemeTrie.add(iuSegmenter.segment("takujuq"),"takujuq");
		iumorphemeTrie.add(iuSegmenter.segment("nalunaiqsivut"),"nalunaiqsivut");
		iumorphemeTrie.add(iuSegmenter.segment("nalunairsivut"),"nalunairsivut");
		iumorphemeTrie.add(iuSegmenter.segment("nalunaiqsivut"),"nalunaiqsivut");
		
		String[] segments = new String[] {
			"{nalunaq/1n}", "{iq/1nv}", "{si/2vv}", "{vut/tv-dec-3p}", "\\"
		};
		TrieNode terminalNode = iumorphemeTrie.getNode(segments);
		new AssertTrieNode(terminalNode, "Node for segments="+String.join(", ", segments))
				.isTerminal()
				.surfaceFormFrequenciesEqual(
					new Pair[] {
						Pair.of("nalunaiqsivut", new Long(2)),
						Pair.of("nalunairsivut", new Long(1))
					})
			;
		
		HashMap<String,Long> surfaceForms = terminalNode.getSurfaceForms();
		assertEquals("The number of surface forms for {nalunaq/1n} {iq/1nv} {si/2vv} {vut/tv-dec-3p} is wrong.",2,surfaceForms.size());
		ArrayList<String> keys = new ArrayList<String>(Arrays.asList(surfaceForms.keySet().toArray(new String[] {})));
		assertTrue("The surface forms should contain 'nalunaiqsivut'",keys.contains("nalunaiqsivut"));
		assertTrue("The surface forms should contain 'nalunairsivut'",keys.contains("nalunairsivut"));
		assertEquals("The frequency of 'nalunaiqsivut' is wrong",new Long(2),surfaceForms.get("nalunaiqsivut"));
		assertEquals("The frequency of 'nalunairsivut' is wrong",new Long(1),surfaceForms.get("nalunairsivut"));
	}
	
	@Test
	public void test__add_get__Char() throws Exception {
		Trie charTrie = makeTrieToTest();
		charTrie.add(new String[]{"h","e","l","l","o"},"hello");
		charTrie.add(new String[]{"h","e","l","l"," ","b","o","y"},"hello boy");
		
		TrieNode node = charTrie.getNode("hello".split(""));
		AssertTrieNode asserter = new AssertTrieNode(node, "");
		asserter.isNotNull();
		asserter
			.hasTerminalNode()
			.hasSegments("hello".split(""))
			.hasFrequency(1)
			;

		node = charTrie.getNode("hell".split(""));
		asserter = new AssertTrieNode(node, "");
		asserter.isNotNull();
		asserter
			.hasSegments("hell".split(""))
			.doesNotHaveATerminalNode()
			.hasFrequency(2)
			;		
	}

	@Test
	public void test__add_get__Word() throws Exception {
		Trie wordTrie = makeTrieToTest();
		try {
			wordTrie.add(new String[]{"hello","there"},"hello there");
		} catch (TrieException e) {
			assertFalse("An error occurred while adding an element to the trie.",true);
		}
		TrieNode node = wordTrie.getNode(new String[]{"hello"});
		assertTrue("The node for 'hello' is not null.",node!=null);
		assertEquals("The key for this node is correct.","hello",node.keysAsString());
		assertFalse("This node should not a full word.",node.isTerminal());
	}

	@Test
	public void test__add_get__IUMorpheme_same_word_twice() throws Exception {
		StringSegmenter iuSegmenter = new StringSegmenter_IUMorpheme();
		Trie iumorphemeTrie = makeTrieToTest();
		String[] takujuq_segments = null;
		takujuq_segments = iuSegmenter.segment("takujuq");
		iumorphemeTrie.add(takujuq_segments,"takujuq");
		TrieNode secondTakujuqNode = iumorphemeTrie.add(takujuq_segments,"takujuq");
		assertTrue("The node added for the second 'takujuq' should not be null.",secondTakujuqNode!=null);
	}
	
	@Test
	public void test__add_get__IUMorpheme_one_word() throws Exception {
		StringSegmenter iuSegmenter = new StringSegmenter_IUMorpheme();
		Trie iumorphemeTrie = makeTrieToTest();
		String[] takujuq_segments = null;
		try {
			takujuq_segments = iuSegmenter.segment("takujuq");
			iumorphemeTrie.add(takujuq_segments,"takujuq");
		} catch (Exception e) {
			assertFalse("An error occurred while adding an element to the trie.",true);
		}
		TrieNode node = iumorphemeTrie.getNode(new String[]{"{taku/1v}"});
		assertTrue("The node for 'taku/1n' should not be null.",node!=null);
		assertEquals("The key for this node is not correct.","{taku/1v}",node.keysAsString());
		assertFalse("This node should not a full word.",node.isTerminal());
	}
	
	@Test
	public void test__frequenciesOfWords() throws Exception {
		Trie charTrie = makeTrieToTest();
		try {
		charTrie.add("hello".split(""),"hello");
		charTrie.add("world".split(""),"world");
		charTrie.add("hell boy".split(""),"hell boy");
		charTrie.add("heaven".split(""),"heaven");
		charTrie.add("worship".split(""),"worship");
		charTrie.add("world".split(""),"world");
		charTrie.add("heaven".split(""),"heaven");
		charTrie.add("world".split(""),"world");
		} catch (Exception e) {
			assertFalse("An error occurred while adding an element to the trie.",true);
		}
		long freq_blah = charTrie.getFrequency("blah".split(""));
		assertEquals("The frequency of the word 'blah' is wrong.",0,freq_blah);
		long freq_worship = charTrie.getFrequency("worship".split(""));
		assertEquals("The frequency of the word 'worship' is wrong.",1,freq_worship);
		long freq_heaven = charTrie.getFrequency("heaven".split(""));
		assertEquals("The frequency of the word 'heaven' is wrong.",2,freq_heaven);
		long freq_world = charTrie.getFrequency("world".split(""));
		assertEquals("The frequency of the word 'world' is wrong.",3,freq_world);
	}
	
	@Test
	public void test_getAllTerminals() throws Exception {
		Trie charTrie = makeTrieToTest();
		charTrie.add("hello".split(""),"hello");
		charTrie.add("hit".split(""),"hit");
		charTrie.add("abba".split(""),"abba");
		charTrie.add("helios".split(""),"helios");
		charTrie.add("helm".split(""),"helm");
		charTrie.add("ok".split(""),"ok");
		
		new AssertTrie(charTrie)
				.terminalsForNodeEqual(
					"h".split(""), 
					new String[] {"helios", "hello", "helm", "hit"});
		
		new AssertTrie(charTrie)
			.terminalsForNodeEqual(
				"hel".split(""), 
				new String[] {"helios", "hello", "helm"});

		new AssertTrie(charTrie)
			.terminalsForNodeEqual(
				"o".split(""), 
				new String[] {"ok"});
	}
	
	
	@Test
	public void test_getAllTerminals__Case2() throws Exception {
		Trie charTrie = makeTrieToTest();
		charTrie.add("hello".split(""),"hello");
		charTrie.add("hit".split(""),"hit");
		charTrie.add("abba".split(""),"abba");
		charTrie.add("helios".split(""),"helios");
		charTrie.add("helm".split(""),"helm");
		charTrie.add("ok".split(""),"ok");
		charTrie.add("okdoo".split(""),"okdoo");
		
		TrieNode hNode = charTrie.getNode("h".split(""));
		TrieNode[] h_terminals = charTrie.getAllTerminals(hNode);
		Assert.assertEquals("The number of words starting with 'h' should be 4.",
				4,h_terminals.length);
		
		TrieNode helNode = charTrie.getNode("hel".split(""));
		TrieNode[] hel_terminals = charTrie.getAllTerminals(helNode);
		Assert.assertEquals("The number of words starting with 'hel' should be 3.",
				3,hel_terminals.length);
		
		TrieNode oNode = charTrie.getNode("o".split(""));
		TrieNode[] o_terminals = charTrie.getAllTerminals(oNode);
		Assert.assertEquals("The number of words starting with 'o' should be 2.",
				2,o_terminals.length);
		
		TrieNode okNode = charTrie.getNode("ok".split(""));
		TrieNode[] ok_terminals = charTrie.getAllTerminals(okNode);
		Assert.assertEquals("The number of words starting with 'ok' should be 2.",
				2,o_terminals.length);
	}	
	@Test
	public void test_getNbOccurrences() throws Exception {
		Trie charTrie = makeTrieToTest();
		charTrie.add("hello".split(""),"hello");
		charTrie.add("hello".split(""),"hello");
		charTrie.add("hit".split(""),"hit");
		charTrie.add("abba".split(""),"abba");
		charTrie.add("helios".split(""),"helios");
		charTrie.add("helm".split(""),"helm");
		charTrie.add("ok".split(""),"ok");
		charTrie.add("ok".split(""),"ok");
		
		new AssertTrie(charTrie, "")
			.hasTerminals(
				new String[] {
					"abba", "helios", "hello", "helm", "hit", "ok"
				})
			.hasNbOccurences(8)
			;
	}
	
	@Test
	public void test_toJSON__Char() throws Exception {
		Trie charTrie = makeTrieToTest();
		charTrie.add("he".split(""),"he");
		charTrie.add("hit".split(""),"hit");
		charTrie.add("ok".split(""),"ok");
		String json = charTrie.toJSON();
		Gson gson = new Gson();
		Trie retrievedCharTrie = (Trie) gson.fromJson(json, charTrie.getClass());
		TrieNode node = retrievedCharTrie.getNode(new String[] {"h","i","t","\\"});
		Assert.assertTrue("The node should be terminal.",node.isTerminal());

		new AssertTrieNode(node, "")
				.hasMostFrequentForm("hit");
//		Assert.assertEquals(
//			"The surface form is not correct.", 
//			"hit", node.getTerminalSurfaceForm());
	}
	
	@Test
	public void test_getMostFrequentTerminal() throws Exception {
		Trie charTrie = makeTrieToTest();
		charTrie.add("hello".split(""),"hello");
		charTrie.add("hello".split(""),"hello");
		charTrie.add("hells".split(""),"hells");
		charTrie.add("hellam".split(""),"hellam");
		charTrie.add("hellam".split(""),"hellam");
		charTrie.add("hellam".split(""),"hellam");
		charTrie.add("hit".split(""),"hit");
		charTrie.add("abba".split(""),"abba");
		charTrie.add("helios".split(""),"helios");
		charTrie.add("helm".split(""),"helm");
		charTrie.add("ok".split(""),"ok");
		charTrie.add("ok".split(""),"ok");
		charTrie.add("ok".split(""),"ok");
		charTrie.add("ok".split(""),"ok");
		TrieNode mostFrequentTerminalRoot = 
			charTrie.getMostFrequentTerminal();
		String expectedKeysMFTR = "o k \\";
		assertEquals("The terminal returned as the most frequent terminal of the whole trie is wrong.",expectedKeysMFTR,mostFrequentTerminalRoot.keysAsString());
		TrieNode mostFrequentTerminalHell = charTrie.getMostFrequentTerminal("hell".split(""));
		String expectedKeys = "h e l l a m \\";
		assertEquals("The terminal returned as the most frequent terminal related to 'hell' is wrong.",expectedKeys,mostFrequentTerminalHell.keysAsString());
		String expectedSurfaceForm = "hellam";
		assertEquals(
			"The terminal returned as the most frequent terminal related to 'hell' is wrong.",
			expectedSurfaceForm,
			mostFrequentTerminalHell.getTerminalSurfaceForm());
	}
	
	@Test
	public void test_getMostFrequentTerminal__Case2() throws Exception {
		Trie charTrie = makeTrieToTest();
		charTrie.add("hello".split(""),"hello");
		charTrie.add("hint".split(""),"hint");
		charTrie.add("helicopter".split(""),"helicopter");
		charTrie.add("helios".split(""),"helios");
		charTrie.add("helicopter".split(""),"helicopter");
		charTrie.add("helios".split(""),"helios");
		charTrie.add("helios".split(""),"helios");
		TrieNode helNode = charTrie.getNode("hel".split(""));
		Assert.assertEquals("The most frequent terminal returned is faulty.",
			"helios", 
			charTrie.getMostFrequentTerminal(helNode).getTerminalSurfaceForm());
	}	
	
	@Test
	public void test_getMostFrequentTerminals() throws Exception {
		Trie charTrie = makeTrieToTest();
		charTrie.add("hello".split(""),"hello");
		charTrie.add("hello".split(""),"hello");
		charTrie.add("hells".split(""),"hells");
		charTrie.add("hellam".split(""),"hellam");
		charTrie.add("hellam".split(""),"hellam");
		charTrie.add("hellam".split(""),"hellam");
		charTrie.add("hit".split(""),"hit");
		charTrie.add("abba".split(""),"abba");
		charTrie.add("helios".split(""),"helios");
		charTrie.add("helm".split(""),"helm");
		charTrie.add("ok".split(""),"ok");
		charTrie.add("ok".split(""),"ok");
		charTrie.add("ok".split(""),"ok");
		charTrie.add("ok".split(""),"ok");
		
		TrieNode[] mostFrequentTerminals;
		String[] expected;
		String[] got;
		
		// next case: there are more than enough candidates with regard to the number requested
		mostFrequentTerminals = charTrie.getMostFrequentTerminals(2, "hell".split(""));
		assertEquals("The number of terminals returned is wrong.",2,mostFrequentTerminals.length);
		expected = new String[] {"h e l l a m \\", "h e l l o \\"};
		got = new String[] {mostFrequentTerminals[0].keysAsString(),mostFrequentTerminals[1].keysAsString()};
		assertArrayEquals("The terminals returned as the 2 most frequent terminals related to 'hell' are wrong.",expected,got);
		// next case: there are less candidates than the number requested
		mostFrequentTerminals = charTrie.getMostFrequentTerminals(4, "hell".split(""));
		assertEquals("The number of terminals returned is wrong.",3,mostFrequentTerminals.length);
		expected = new String[] {"h e l l a m \\", "h e l l o \\", "h e l l s \\"};
		got = new String[] {mostFrequentTerminals[0].keysAsString(),mostFrequentTerminals[1].keysAsString(),mostFrequentTerminals[2].keysAsString()};
		assertArrayEquals("The terminals returned as the 4 most frequent terminals related to 'hell' are wrong.",expected,got);
		expected = new String[] {"hellam", "hello", "hells"};
		got = new String[] {
			mostFrequentTerminals[0].getTerminalSurfaceForm(),
			mostFrequentTerminals[1].getTerminalSurfaceForm(),
			mostFrequentTerminals[2].getTerminalSurfaceForm()};
		assertArrayEquals(
			"The surface forms for the terminals returned as the 3 most frequent terminals related to 'hell' are wrong.",
			expected, got);
		
		mostFrequentTerminals = charTrie.getMostFrequentTerminals(1);
		expected = new String[] {"ok"};
		got = new String[] {mostFrequentTerminals[0].getTerminalSurfaceForm()};
		assertArrayEquals("The surface forms for the terminals returned as the 1 most frequent terminal of the whole trie are wrong.",expected,got);
		
		mostFrequentTerminals = charTrie.getMostFrequentTerminals(3);
		expected = new String[] {"ok","hellam","hello"};
		got = new String[] {
			mostFrequentTerminals[0].getTerminalSurfaceForm(),
			mostFrequentTerminals[1].getTerminalSurfaceForm(),
			mostFrequentTerminals[2].getTerminalSurfaceForm()
		};
		assertArrayEquals("The surface forms for the terminals returned as the 3 most frequent terminal of the whole trie are wrong.",expected,got);
	}
	
	@Test
	public void test__getMostFrequentTerminals__Case2() throws Exception {
		Trie charTrie = makeTrieToTest();
		charTrie.add("hello".split(""),"hello");
		charTrie.add("hint".split(""),"hint");
		charTrie.add("helicopter".split(""),"helicopter");
		charTrie.add("helios".split(""),"helios");
		charTrie.add("helicopter".split(""),"helicopter");
		charTrie.add("helios".split(""),"helios");
		charTrie.add("helios".split(""),"helios");
		TrieNode helNode = charTrie.getNode("hel".split(""));
		// test n < number of terminals
		TrieNode[] mostFrequentTerminals = charTrie.getMostFrequentTerminals(2, helNode);
		Assert.assertEquals("The number of nodes returned is wrong.",2,mostFrequentTerminals.length);
		Assert.assertEquals(
			"The first most frequent terminal returned is faulty.",
			"helios", mostFrequentTerminals[0].getTerminalSurfaceForm());
		Assert.assertEquals(
			"The second most frequent terminal returned is faulty.",
			"helicopter", mostFrequentTerminals[1].getTerminalSurfaceForm());
		// test n > number of terminals
		TrieNode[] mostFrequentTerminals4 = charTrie.getMostFrequentTerminals(4, helNode);
		Assert.assertEquals("The number of nodes returned is wrong.",3,mostFrequentTerminals4.length);
		// test with exclusion of nodes
		TrieNode nodeToExclude = charTrie.getNode("hello\\".split(""));
		TrieNode[] mostFrequentTerminalsExcl = 
			charTrie.getMostFrequentTerminals(4, helNode, 
				new TrieNode[] {nodeToExclude});
		Assert.assertEquals("The number of nodes returned without excluded nodes is wrong.",2,mostFrequentTerminalsExcl.length);
		Assert.assertEquals("", "helios", mostFrequentTerminalsExcl[0].getTerminalSurfaceForm());
		Assert.assertEquals("", "helicopter", mostFrequentTerminalsExcl[1].getTerminalSurfaceForm());
	}
	
	
	@Test
	public void test__mostFrequentSequenceForRoot__Char() throws Exception {
		Trie charTrie = makeTrieToTest();
		charTrie.add("hello".split(""),"hello");
		charTrie.add("hint".split(""),"hint");
		charTrie.add("helicopter".split(""),"helicopter");
		charTrie.add("helios".split(""),"helios");
		charTrie.add("helicopter".split(""),"helipcopter");
		String[] mostFrequentSegments = charTrie.getMostFrequentSequenceForRoot("h");
		String[] expected = new String[] {"h","e"};
		AssertHelpers.assertDeepEquals("The most frequent sequence should be heli.",expected,mostFrequentSegments);
	}
	
	@Test
	public void test__mostFrequentSequenceForRoot__IUMorpheme() throws Exception {
		Trie morphTrie = makeTrieToTest();
		morphTrie.add(new String[] {"{taku/1v}","{juq/1vn}"},"takujuq");
		morphTrie.add(new String[] {"{taku/1v}","{laaq/2vv}","{juq/1vn}"},"takulaaqtuq");
		morphTrie.add(new String[] {"{taku/1v}","{laaq/2vv}","{sima/1vv}","{juq/1vn}"},"takulaaqsimajuq");
		morphTrie.add(new String[] {"{taku/1v}","{sima/1vv}","{juq/1vn}"},"takusimajuq");
		morphTrie.add(new String[] {"{taku/1v}","{juq/1vn}"},"takujuq");
		String[] mostFrequentSegments = morphTrie.getMostFrequentSequenceForRoot("{taku/1v}");
		String[] expected = new String[] {"{taku/1v}","{juq/1vn}"};
		AssertHelpers.assertDeepEquals("The most frequent sequence should be heli.",expected,mostFrequentSegments);
	}
	
	@Test
	public void test__getMostFrequentTerminalFromMostFrequenceSequenceFromRoot__1() throws Exception {
		Trie morphTrie = makeTrieToTest();
		morphTrie.add(new String[] {"{taku/1v}","{juq/1vn}"},"takujuq");
		morphTrie.add(new String[] {"{taku/1v}","{juq/1vn}"},"takujuq");
		morphTrie.add(new String[] {"{taku/1v}","{laaq/2vv}","{juq/1vn}"},"takulaaqtuq");
		morphTrie.add(new String[] {"{taku/1v}","{laaq/2vv}","{sima/1vv}","{juq/1vn}"},"takulaaqsimajuq");
		morphTrie.add(new String[] {"{taku/1v}","{sima/1vv}","{juq/1vn}"},"takusimajuq");
		TrieNode mostFrequentTerminal = morphTrie.getMostFrequentTerminalFromMostFrequentSequenceForRoot("{taku/1v}");
		String expected = "{taku/1v} {juq/1vn} \\";
		assertEquals("The most frequent term for 'taku' in the trie is not correct.",expected,mostFrequentTerminal.keysAsString());
	}
	
	@Test
	public void test__getMostFrequentTerminalFromMostFrequenceSequenceFromRoot__2() throws Exception {
		Trie morphTrie = makeTrieToTest();
		morphTrie.add(new String[] {"{taku/1v}","{juq/1vn}"},"takujuq");
		morphTrie.add(new String[] {"{taku/1v}","{juq/1vn}"},"takujuq");
		morphTrie.add(new String[] {"{taku/1v}","{juq/1vn}"},"takujuq");
		morphTrie.add(new String[] {"{taku/1v}","{laaq/2vv}","{juq/1vn}"},"takulaaqtuq");
		morphTrie.add(new String[] {"{taku/1v}","{laaq/2vv}","{juq/1vn}"},"takulaaqtuq");
		morphTrie.add(new String[] {"{taku/1v}","{laaq/2vv}","{sima/1vv}","{juq/1vn}"},"takulaaqsimajuq");
		morphTrie.add(new String[] {"{taku/1v}","{sima/1vv}","{juq/1vn}"},"takusimajuq");
		TrieNode mostFrequentTerminal = morphTrie.getMostFrequentTerminalFromMostFrequentSequenceForRoot("{taku/1v}");
		String expected = "{taku/1v} {juq/1vn} \\";
		assertEquals("The most frequent term for 'taku' in the trie is not correct.",expected,mostFrequentTerminal.keysAsString());
	}
	
	@Test
	public void test__getMostFrequentTerminalFromMostFrequenceSequenceFromRoot__3() throws Exception {
		Trie morphTrie = makeTrieToTest();
		morphTrie.add(new String[] {"{taku/1v}","{juq/1vn}"},"takujuq");
		morphTrie.add(new String[] {"{taku/1v}","{juq/1vn}"},"takujuq");
		morphTrie.add(new String[] {"{taku/1v}","{laaq/2vv}","{juq/1vn}"},"takulaaqtuq");
		morphTrie.add(new String[] {"{taku/1v}","{laaq/2vv}","{juq/1vn}"},"takulaaqtuq");
		morphTrie.add(new String[] {"{taku/1v}","{laaq/2vv}","{juq/1vn}"},"takulaaqtuq");
		morphTrie.add(new String[] {"{taku/1v}","{laaq/2vv}","{sima/1vv}","{juq/1vn}"},"takulaaqsimajuq");
		morphTrie.add(new String[] {"{taku/1v}","{sima/1vv}","{juq/1vn}"},"takusimajuq");
		TrieNode mostFrequentTerminal = morphTrie.getMostFrequentTerminalFromMostFrequentSequenceForRoot("{taku/1v}");
		String expected = "{taku/1v} {laaq/2vv} {juq/1vn} \\";
		assertEquals("The most frequent term for 'taku' in the trie is not correct.",expected,mostFrequentTerminal.keysAsString());
	}
}
