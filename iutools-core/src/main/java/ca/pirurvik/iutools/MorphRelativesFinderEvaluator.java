package ca.pirurvik.iutools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.junit.Assert;

import com.google.gson.Gson;

import ca.nrc.datastructure.trie.StringSegmenter;
import ca.nrc.datastructure.trie.TrieException;
import ca.nrc.debug.Debug;
import ca.pirurvik.iutools.MorphologicalRelative;
import ca.pirurvik.iutools.corpus.CompiledCorpus_InMemory;
import ca.pirurvik.iutools.corpus.WordInfo;
import ca.pirurvik.iutools.corpus.CompiledCorpusException;
import ca.pirurvik.iutools.corpus.CompiledCorpusRegistry;
import ca.pirurvik.iutools.corpus.CompiledCorpusRegistryException;

public class MorphRelativesFinderEvaluator {
	
	public boolean verbose = false;
	
	public CompiledCorpus_InMemory compiledCorpus = null;
	public CSVParser csvParser = null;
	public boolean computeStatsOverSurfaceForms = true;
	public float precision = -1;
	public float recall = -1;
	public float fmeasure = -1;
	
	
	/*
	 * 0. Mot original (fréquence Google du mot en syllabique),
	 * 1. Mot original en syllabique,
	 * 2. Traduction,
	 * 3. "Reformulations trouvées",
	 * 4. "Meilleures Reformulations",
	 * 5. "Syllabique des refromulations avec #hits Google",
	 * 6. "Traduction des meilleures Reformulations",
	 * 7. Notes
	 */
	
	public MorphRelativesFinderEvaluator() {
	}
	
	public MorphRelativesFinderEvaluator(String compiledCorpusTrieFilePath, String csvGoldStandardFilePath) throws IOException {
		File compiledCorpusTrieFile = new File(compiledCorpusTrieFilePath);
		setCompiledCorpus(compiledCorpusTrieFile);
		File goldStandardFile = new File(csvGoldStandardFilePath);
		setGoldStandard(goldStandardFile);
	}
		
	public void setCompiledCorpus(String corpusName) throws CompiledCorpusRegistryException {
		compiledCorpus = CompiledCorpusRegistry.getCorpus(corpusName);
	}
	
	public void setCompiledCorpus(File compiledCorpusTrieFilePath) throws IOException {
		FileReader fr = new FileReader(compiledCorpusTrieFilePath);
		compiledCorpus = new Gson().fromJson(fr, CompiledCorpus_InMemory.class);    		
		fr.close();
	}
	
	public void setCompiledCorpus(CompiledCorpus_InMemory _compiledCorpus) {
		compiledCorpus = _compiledCorpus;
	}
	
	
	public void setVerbose(boolean value) {
		verbose = value;
	}
	
	public void setGoldStandard(File csvGoldStandardFile) throws IOException {
        BufferedReader reader = Files.newBufferedReader(Paths.get(csvGoldStandardFile.getAbsolutePath()));
        csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
	}
	
	public void setGoldStandard(String[] csvGoldStandardLines) throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(String.join("\n", csvGoldStandardLines)));
		CSVParser blah;
		CSVFormat blobl;
		csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
	}
	
	public void setOptionComputeStatsOverSurfaceForms(boolean value) {
		computeStatsOverSurfaceForms = value;
	}
	
	public void run() {
		
		// Set this to a word if you want to only run that one
		// word. Leave it at null to run all words
		String focusOnWord = null;
//		focusOnWord = "silattusarvimmi";
		
		Logger logger = Logger.getLogger("QueryExpanderEvaluator");
		
        try {
    		MorphRelativesFinder queryExpander = new MorphRelativesFinder(compiledCorpus);
            
            Pattern patMotFreq = Pattern.compile("^(.+) \\((\\d+)\\).*$");
            
            int nbTotalCases = 0;
            int nbTotalGoldStandardAlternatives = 0;
            int nbTotalExpansionsFromCorpus = 0;
            int nbTotalExpansionsNotInGSAlternatives = 0;
            int nbTotalGSAlternativesNotInExpansions = 0;
            int nbTotalCasesWithNoExpansion = 0;
            int nbTotalCasesCouldNotBeDecomposed = 0;
            
            ArrayList<String> listAllGsAlternatives = new ArrayList<String>();
            
            boolean stop = false;
           
            int i = 0;
            for (CSVRecord csvRecord : csvParser) {
            		if (i++ == 0) continue;
            		if (stop) break;
            		
                    // Accessing Values by Column Index
                    String motLatinEtFreq = csvRecord.get(0);
                    Matcher m = patMotFreq.matcher(motLatinEtFreq);
                    String mot = null;
                    long freqMotGoogle = -1;
                    if ( m.matches() ) {
                    	mot = m.group(1);
                    	freqMotGoogle = Long.parseUnsignedLong(m.group(2));
                    }
                    
                    if (focusOnWord != null && !mot.equals(focusOnWord)) {
                    	continue;
                    }
                    if (verbose) System.out.println("\n"+mot+" "+"("+freqMotGoogle+")");
                    if (mot==null) continue;
                    
                    //String decMot = String.join(" ",compiledCorpus.getSegmenter().segment(mot))+" \\";
                    nbTotalCases++;
                    if (verbose) System.out.println("    Gold Standard reformulations (frequencies in compiled corpus):");
                    String[] gsalternatives = (mot+"; "+csvRecord.get(4)).split(";\\s*");
                    String[] gsalternativesMorphemes = new String[gsalternatives.length];
                    List<String> listgsalternatives = Arrays.asList(gsalternatives);
                    nbTotalGoldStandardAlternatives += gsalternatives.length;
                    listAllGsAlternatives.addAll(listgsalternatives);
                    
                    for (int igs=0; igs<gsalternatives.length; igs++) {	
                    	String gsalternative = gsalternatives[igs];
                    	long freqGSAlternativeInCorpus = freqDansCorpus(gsalternative);
                    	if (verbose) System.out.println("        "+gsalternative+" : "+freqGSAlternativeInCorpus);
                    	String altDecomp = null;
                    	try {
                    		altDecomp = String.join(" ",compiledCorpus.decomposeWord(gsalternative));
                        	gsalternativesMorphemes[igs] = altDecomp;
                    	} catch (Exception e) {
                    		altDecomp = "";
                    	}
                    }
                    List<String> listgsalternativesmorphemes = Arrays.asList(gsalternativesMorphemes);
                    
                    if (verbose) System.out.println("    Query Expander expansions (frequencies in compiled corpus):");
                    try {
                    	MorphologicalRelative[] expansions = queryExpander.getRelatives(mot);
                    	if ( expansions != null ) {
                        	logger.debug(mot+" - expansions: "+expansions.length);
                    		ArrayList<String> listexpansionsmorphemes = new ArrayList<String>();
                    		ArrayList<String> listexpansions = new ArrayList<String>();
                    		nbTotalExpansionsFromCorpus += expansions.length;
                    		if (expansions.length==0) {
                        		nbTotalCasesWithNoExpansion++;
                        		if (verbose) System.out.println("        0 expansion");
                    		}
                    		Arrays.sort(expansions, (MorphologicalRelative a, MorphologicalRelative b) ->
                    			{
                    				if (a.getFrequency() == b.getFrequency())
                    					return 0;
                    				else
                    					return a.getFrequency() < b.getFrequency()? 1 : -1;
                    			});
                    		for (MorphologicalRelative expansion : expansions) {
                    			long freqExpansion =expansion.getFrequency();
                    			boolean expansionInGSalternatives = true;
                    			String expansionMorphemes = String.join(" ", expansion.getMorphemes());
                    			if (computeStatsOverSurfaceForms) {
                    				if ( !listgsalternatives.contains(expansion.getWord()) ) {
                    					nbTotalExpansionsNotInGSAlternatives++;
                    					expansionInGSalternatives = false;
                    				}
                    			} else {
                    				if ( !listgsalternativesmorphemes.contains(expansionMorphemes) ) {
                    					nbTotalExpansionsNotInGSAlternatives++;
                    					expansionInGSalternatives = false;
                    				}
                    			}
                    			if (verbose) System.out.println("        "+expansion.getWord()+" : "+freqExpansion+(expansionInGSalternatives? " ***":""));
                    			listexpansionsmorphemes.add(expansionMorphemes);
                    			listexpansions.add(expansion.getWord());
                    		}
                    		if (computeStatsOverSurfaceForms) {
                    			for (String gsalternative : gsalternatives)
                    				if ( !listexpansions.contains(gsalternative) )
                    					nbTotalGSAlternativesNotInExpansions++;
                    		} else {
                    			for (String gsalternativeMorphemes : gsalternativesMorphemes)
                    				if ( !listexpansionsmorphemes.contains(gsalternativeMorphemes) )
                    					nbTotalGSAlternativesNotInExpansions++;
                    		}
                    	} else {
                    		logger.debug(mot+" - expansions null");
                    		nbTotalGSAlternativesNotInExpansions += gsalternatives.length;
                    		nbTotalCasesWithNoExpansion++;
                    		nbTotalCasesCouldNotBeDecomposed++;
                    		if (verbose) System.out.println("        the word could not be decomposed.");
                    	}
                    } catch(Exception e) {
                    	System.out.println(
                    		"Error getting the expansions for word: "+mot+"\n"+
                    		Debug.printCallStack(e));
                    }
            }
            csvParser.close();
            
            for (int igsa=0; igsa<listAllGsAlternatives.size(); igsa++) {
            	if (verbose) System.out.println((igsa+1)+". "+listAllGsAlternatives.get(igsa));
            }
            
            if (verbose) {
	            System.out.println("\nTotal number of evaluated words: "+nbTotalCases);
	            System.out.println("Total number of alternatives in Gold Standard: "+nbTotalGoldStandardAlternatives);
	            System.out.println("Total number of expansions found in corpus: "+nbTotalExpansionsFromCorpus);
	            
	            System.out.println("\nTotal number of cases with no expansion found in corpus: "+nbTotalCasesWithNoExpansion+", of which");
	            System.out.println("Total number of cases that could not be decomposed: "+nbTotalCasesCouldNotBeDecomposed);
	            
	            System.out.println("\n\tNo expansion: either the word could not be decomposed"+
	            					"\n\tor the decomposition process timed out,"+
	            					"\n\tor the corpus contains nothing with the word's root.");
	            
	            System.out.println("\nTotal number of corpus expansions not in GS alternatives: "+nbTotalExpansionsNotInGSAlternatives);            
            }            
            int nbTotalGoodExpansions = nbTotalExpansionsFromCorpus - nbTotalExpansionsNotInGSAlternatives;
            if (verbose) System.out.println("\tTotal number of GOOD corpus expansion: "+nbTotalGoodExpansions);
            
            precision = (float)nbTotalGoodExpansions / (float)nbTotalExpansionsFromCorpus;
            recall = (float)nbTotalGoodExpansions / (float)nbTotalGoldStandardAlternatives;
            fmeasure = 2 * precision * recall / (precision + recall);
            
            if (verbose) System.out.println("Precision = "+precision);
            if (verbose) System.out.println("Recall = "+recall);
            if (verbose) System.out.println("F-measure = "+fmeasure);
            
        } catch(Exception e) {
        	if (verbose) System.err.println(e.getMessage());
        	if (csvParser != null)
				try {
					csvParser.close();
				} catch (IOException e1) {
					if (verbose) System.err.println(e1.getMessage());
				}
        }
        
        if (focusOnWord != null) {
        	Assert.fail("TEST WAS RUN ON A SINGLE WORD!\nRemember to set focusOnWord = null to run the test on all words");
        }
	}

	private long freqDansCorpus(String reformulation) 
			throws MorphRelativesFinderException {
		
//		String[] keys = compiledCorpus.getSegmentsCache().get(reformulation);
//		if (keys==null)
//			return 0;				
//		
//		long freqDansCorpus;
//		try {
//			freqDansCorpus = compiledCorpus.trie.getFrequency(keys);
//		} catch (TrieException e) {
//			throw new QueryExpanderException(e);
//		}
		long freqDansCorpus = 0;
		try {
			WordInfo wInfo = compiledCorpus.info4word(reformulation);
			if (wInfo != null) {
				freqDansCorpus = wInfo.frequency;
			}
		} catch (CompiledCorpusException e) {
			throw new MorphRelativesFinderException(e);
		}
	
		return freqDansCorpus;
	}
	
	public static void main(String[] args) throws IOException {
		MorphRelativesFinderEvaluator evaluator = new MorphRelativesFinderEvaluator(args[0],args[1]);
		evaluator.run();
	}
}