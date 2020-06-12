package ca.pirurvik.iutools.corpus;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import ca.inuktitutcomputing.morph.Decomposition;
import ca.inuktitutcomputing.morph.MorphologicalAnalyzer;
import ca.nrc.config.ConfigException;
import ca.nrc.datastructure.trie.StringSegmenter;
import ca.nrc.datastructure.trie.StringSegmenterException;
import ca.nrc.datastructure.trie.StringSegmenter_Char;
import ca.nrc.datastructure.trie.StringSegmenter_IUMorpheme;
import ca.nrc.datastructure.trie.Trie;
import ca.nrc.datastructure.trie.TrieNode;
import ca.nrc.datastructure.trie.Trie_InMemory;
import ca.nrc.testing.AssertHelpers;
import ca.nrc.testing.AssertObject;
import ca.pirurvik.iutools.corpus.CompiledCorpus_InMemory.WordWithMorpheme;

public abstract class CompiledCorpus_BaseTest {
		
	protected abstract CompiledCorpus makeCorpusUnderTest(
		Class<? extends StringSegmenter> segmenterClass);
	
	protected CompiledCorpus makeCorpusUnderTest() {
		return makeCorpusUnderTest(StringSegmenter_Char.class);
	}
	
	protected File corpusDirectory = null;
	
	@After
    public void tearDown() throws Exception {
        if (corpusDirectory != null) {
        	File[] listOfFiles = corpusDirectory.listFiles();
        	for (File file : listOfFiles)
        		file.delete();
        }
        corpusDirectory = null;
    }
	
	//////////////////////////
	// DOCUMENTATION TESTS
	//////////////////////////
	
	@Test @Ignore
	public void test__DELETE_ME_LATER() {
		Assert.fail("TODO: Continue removing calls to CompiledCorpus_InMemory.compileCorpusFromScratch() from the various tests.\nThen from production code.\nWhen they are all gone, delete that method");
	}
	
	@Test
	public void test__CompiledCorpus__Synopsis() throws Exception {
		//
		// Use a CompiledCorpus to trie-compile a corpus and compute statistics.
		//
		//
		CompiledCorpus compiledCorpus = makeCorpusUnderTest();
		
		// 
		// By default, the compiler always computes character-ngrams.
		// 
		// But you can also provide a morpheme segmenter which will allow the 
		// corpus to keep stats on morphme-ngrams
		// 
		compiledCorpus.setSegmenterClassName(
				StringSegmenter_IUMorpheme.class.getName());

		// set verbose to false for tests only
		compiledCorpus.setVerbose(false); 
		
		// Set the maximum number of morphological decompositions that you want 
		// to keep for each word. Note that the entry for a word will always 
		// know how many decompositions existed, even if it only stores the 
		// first few of them.
		//
		compiledCorpus.setDecompsSampleSize(10);
		
		// Whenever you encounter an occurence of a word, invoke 
		// addWordOccurence(word)
		//
		// For example, say you encounter an occurence of word 'inuksuk'...
		// This will:
		// - Create a new entry for this word if this is the first time the 
		//   is encountered
		// - Increment the word's frequency by 1
		// - Increment frequency of each char-ngram contained in that word
		//
		String word = "inuksuk";
		compiledCorpus.addWordOccurence(word);
		
		// TODO-June2020: Show how you can a provide frequency increment != 1
//		compiledCorpus.addWordOccurence(word, 3); // Increase freq by +3
//		compiledCorpus.addWordOccurence(word, 0); // Freq will remain unchanged
		
		
		// Once you have added all this information to the CompiledCorpus, you 
		// can do all sort of useful stuff with that info.
		//
		// For example:
		//
		
		// Loop through all words in the corpus
		Iterator<String> iter = compiledCorpus.allWords();
		while (iter.hasNext()) {
			String aWord = iter.next();
			
			// Get that word's information
			{
				WordInfo wInfo = compiledCorpus.info4word(aWord);
				if (wInfo == null) {
					// Means the corpus does not know about this word
					//
					// Note: Should not happen in this case, because we obtained 
					// 'word' through the allWords() iterator (so it know that this
					// word was seen in the corpus).
					//
				} else {
					// Frequency of the word
					long freq = wInfo.frequency;
					
					// Total number of morphological decompositions for this word, 
					// as well as a short list of the first few decompositions 
					// found.
					// 
					// If those two values are 'null', it means that the decomps 
					// have not been provided.
					// It does NOT mean that no decomps can be computed for this 
					// word.
					//
					Integer numDecomps = wInfo.totalDecompositions;
					String[] sampleDecomps = wInfo.topDecompositions;
				}			
			}
		}
		

		// You can ask for information about the various character-ngrams 
		// that were seen in the corpus.
		//
		{
			// This returns all the words that START with "nuna"
			//
			Set<String> wordsWithNgram = 
					compiledCorpus.wordsContainingNgram("^nuna");
			
			// Words that END with "vut"
			//
			wordsWithNgram = 
					compiledCorpus.wordsContainingNgram("vut$");
	
			// Words that have "nav" ANYWHERE
			//
			wordsWithNgram = 
					compiledCorpus.wordsContainingNgram("nav");
		}
		
		// Similarly, you can also ask for information about words that contain 
		// certain sequences of ngrams (aka morphem-ngrams)
		//
		{
			// This will find all the words that START with morphemes
			// inuk/1n and titut/tn-sim-p
			//
			String[] morphemes = new String[] {
				"^", "inuk/1n", "titut/tn-sim-p"};
			Set<String> wordsWithMorphemes = 
				compiledCorpus.wordsContainingMorphNgram(morphemes);

			// This will find all the words that END with titut/tn-sim-p
			//
			morphemes = new String[] {
				"titut/tn-sim-p", "$"};
			wordsWithMorphemes = 
				compiledCorpus.wordsContainingMorphNgram(morphemes);
		
			// This will find all the words that contain morphemes 
			// nasuk/1vv and niq/2vn ANYWHERE
			//
			morphemes = new String[] {
				"nasuk/1vv", "niq/2vn"};
			wordsWithMorphemes = 
				compiledCorpus.wordsContainingMorphNgram(morphemes);
		}
	}
	
	
	///////////////////////////////
	// VERIFICATION TESTS
	///////////////////////////////
	
	@Test
	public void test__addWordOccurences__HappyPath() throws Exception {
		CompiledCorpus corpus = makeCorpusUnderTest();
		final String[] noWords = new String[] {};
		String nunavut = "nunavut";
		String nunavik = "nunavik";

		String ngram_nuna = "^nuna";
		String ngram_navik = "navik$";
		
		new AssertCompiledCorpus(corpus, "Initially...")
			.doesNotContainWords(nunavut, nunavik)
			.doesNotContainCharNgrams(ngram_nuna, ngram_navik);
		
		corpus.addWordOccurence(nunavut);
		new AssertCompiledCorpus(corpus, "After adding 1st word "+nunavut)
			.containsWords(nunavut)
			.containsCharNgrams(ngram_nuna)
			.doesNotContainCharNgrams(ngram_navik);
		
		corpus.addWordOccurence(nunavik);
		new AssertCompiledCorpus(corpus, "After adding 2nd word "+nunavik)
			.containsWords(nunavik)
			.containsCharNgrams(ngram_nuna)
			.containsCharNgrams(ngram_navik);
	}
	
	@Test
    public void test__topSegmentation__HappyPath() throws Exception {
		String[] words = new String[] {"nunavut", "takujuq", "plugak"};
		CompiledCorpus compiledCorpus = 
				makeCorpusUnderTest(StringSegmenter_IUMorpheme.class);		
		compiledCorpus.addWordOccurences(words);
		
		new AssertCompiledCorpus(compiledCorpus,"")
			.topSegmentationIs("nunavut", "{nunavut/1n}")
			.topSegmentationIs("takujuq", "{taku/1v}{juq/1vn}")
			// This is a word that does not decompose
			.topSegmentationIs("plugak", null)
			;		
    }
	
	
    @Test
    public void test__charNGramFrequency__HappyPath() throws Exception
    {
		String[] words = new String[] {
			"nunavut", "takujuq", "iijuq"};
		CompiledCorpus compiledCorpus = 
				makeCorpusUnderTest(StringSegmenter_IUMorpheme.class);		
		compiledCorpus.addWordOccurences(words);
	   
		new AssertCompiledCorpus(compiledCorpus, "")
			// Ngram with freq = 1
			.charNgramFrequencyIs("nun", 1)
			// Ngram with freq > 1
			.charNgramFrequencyIs("juq", 2)
			;
    }
        
    // TODO-June2020: This test should use makeCorpusUnderTest()
	@Test
	public void test__mostFrequentWordWithRadical() throws Exception {
		CompiledCorpus_InMemory compiledCorpus = new CompiledCorpus_InMemory();
        compiledCorpus.setVerbose(false);
        Trie_InMemory charTrie = new Trie_InMemory();
		try {
		charTrie.add("hello".split(""),"hello");
		charTrie.add("hint".split(""),"hint");
		charTrie.add("helicopter".split(""),"helicopter");
		charTrie.add("helios".split(""),"helios");
		charTrie.add("helicopter".split(""),"helicopter");
		compiledCorpus.trie = charTrie;
		} catch (Exception e) {
		Assert.assertFalse("An error occurred while adding an element to the trie.",true);
		}
		TrieNode mostFrequent = compiledCorpus.getMostFrequentTerminal("hel".split(""));
	Assert.assertEquals("The frequency of the most frequent found is wrong.",2,mostFrequent.getFrequency());
	Assert.assertEquals("The text of the the most frequent found is wrong.","h e l i c o p t e r \\",mostFrequent.keysAsString());
	}

    // TODO-June2020: This test should use makeCorpusUnderTest()
	@Test
	public void test__getTerminalsSumFreq() throws Exception {
		CompiledCorpus_InMemory compiledCorpus = new CompiledCorpus_InMemory();
        compiledCorpus.setVerbose(false);
        Trie_InMemory charTrie = new Trie_InMemory();
		charTrie.add("hello".split(""),"hello");
		charTrie.add("hint".split(""),"hint");
		charTrie.add("helicopter".split(""),"helicopter");
		charTrie.add("helios".split(""),"helios");
		charTrie.add("helicopter".split(""),"helicopter");
		compiledCorpus.trie = charTrie;
		long nbCompiledOccurrences = compiledCorpus.getNumberOfCompiledOccurrences();
	Assert.assertEquals("The sum of the frequencies of all terminals is incorrect.",5,nbCompiledOccurrences);
		
	}
	
	/*
	 * 
	 */

	
    private String createTemporaryCorpusDirectory(String[] stringOfWords) throws IOException {
       	Logger logger = Logger.getLogger("CompiledCorpusTest.createTemporaryCorpusDirectory");
        corpusDirectory = Files.createTempDirectory("").toFile();
        corpusDirectory.deleteOnExit();
        String corpusDirPath = corpusDirectory.getAbsolutePath();
        for (int i=0; i<stringOfWords.length; i++) {
        	File wordFile = new File(corpusDirPath+"/contents"+(i+1)+".txt");
        	BufferedWriter bw = new BufferedWriter(new FileWriter(wordFile));
        	bw.write(stringOfWords[i]);
        	bw.close();
        	logger.debug("wordFile= "+wordFile.getAbsolutePath());
        	logger.debug("contents= "+wordFile.length());
        }
        return corpusDirPath;
	}
    
    private String createTemporaryCorpusDirectoryWithSubdirectories(String[][][] subdirs) throws IOException {
        Path corpusDirectory = Files.createTempDirectory("corpus_");
        corpusDirectory.toFile().deleteOnExit();
        for (int isubdir=0; isubdir<subdirs.length; isubdir++) {
        	Path subDirectory = Files.createTempDirectory(corpusDirectory,"sub_");
        	subDirectory.toFile().deleteOnExit();
        	String [][] subdirFiles = subdirs[isubdir];
        	for (int ifile=0; ifile<subdirFiles.length; ifile++) {
        		String[] words = subdirFiles[ifile];
        		Path filepath = Files.createTempFile(subDirectory, "file_", ".txt");
        		filepath.toFile().deleteOnExit();
        		File file = filepath.toFile();
            	BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            	String lineOfWords = String.join(" ", words);
            	bw.write(lineOfWords);
            	bw.close();
        	}
        }
        return corpusDirectory.toFile().getAbsolutePath();
    }


    // TODO-June2020: This test should use makeCorpusUnderTest()
	@Test
	public void test__getNbFailedSegmentations() throws Exception {
		String[] stringsOfWords = new String[] {
				"nunavut", "inuit", "takujuq", "amma", "kanaujaq", "iglumik", "takulaaqtuq", "nunait"
				};
        CompiledCorpus_InMemory compiledCorpus = new CompiledCorpus_InMemory(StringSegmenter_IUMorpheme.class.getName());
        compiledCorpus.addWordOccurences(stringsOfWords);
        compiledCorpus.setVerbose(false);
       Assert.assertEquals("The number of words that failed segmentation is wrong.",1,
        		compiledCorpus.getNbWordsThatFailedSegmentations());
       Assert.assertEquals("The number of occurrences that failed segmentation is wrong.",1,
        		compiledCorpus.getNbOccurrencesThatFailedSegmentations());
	}

    // TODO-June2020: This test should use makeCorpusUnderTest()
	@Test
	public void test__getWordsContainingMorpheme__HappyPath() throws Exception {
		String[] stringsOfWords = new String[] {
				"nunavut", "inuit", "takujuq", "sinilauqtuq", "uvlimik", "takulauqtunga"
				};
        CompiledCorpus_InMemory compiledCorpus = new CompiledCorpus_InMemory(StringSegmenter_IUMorpheme.class.getName());
        compiledCorpus.setVerbose(false);
        compiledCorpus.addWordOccurences(stringsOfWords);
        
        new AssertCompiledCorpus(compiledCorpus, "")
        		.wordsContainingMorphemeAre(
        			"lauq", 
        			Triple.of("sinilauqtuq", "lauq/1vv", "{sinik/1v}{lauq/1vv}{juq/1vn}"),
        			Triple.of("takulauqtunga","lauq/1vv","{taku/1v}{lauq/1vv}{junga/tv-ger-1s}")
        		);
	}	

	public static File compileToFile(String[] words) throws Exception {
		return compileToFile(words,null);
	}
	
    // TODO-June2020: Get rid of this helper method
	public static File compileToFile(String[] words, String fileId) throws Exception {
		CompiledCorpus_InMemory tempCorp = new CompiledCorpus_InMemory(StringSegmenter_IUMorpheme.class.getName());
		tempCorp.setVerbose(false);
		InputStream iStream = IOUtils.toInputStream(String.join(" ", words), "utf-8");
		InputStreamReader iSReader = new InputStreamReader(iStream);
		BufferedReader br = new BufferedReader(iSReader);
		tempCorp.processDocumentContents(br, "dummyFilePath");
		String fileName = "compiled_corpus";
		if (fileId != null)
			fileName += "-"+fileId;
		File tempFile = File.createTempFile(fileName, ".json");
		tempCorp.saveCompilerInJSONFile(tempFile.toString());
		return tempFile;
	}
}
