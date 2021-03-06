package ca.inuktitutcomputing.core.console;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import ca.nrc.datastructure.trie.StringSegmenter_IUMorpheme;
import ca.nrc.datastructure.trie.Trie;
import ca.nrc.datastructure.trie.Trie_InMemory;
import ca.pirurvik.iutools.corpus.CompiledCorpus_InMemory;
import ca.pirurvik.iutools.corpus.CompiledCorpus;
import ca.pirurvik.iutools.corpus.CompiledCorpusRegistry;

public class CmdDescribeCorpus extends ConsoleCommand {

	@Override
	public String getUsageOverview() {
		return "Compile a corpus from a series of corpus files.";
	}

	public CmdDescribeCorpus(String name) {
		super(name);
	}

	@Override
	public void execute() throws Exception {
		
		String compilationFilePathname = getCorpusSavePath(true);
		
		echo("\nDescription of corpus:\n");
		echo("corpus json file: "+compilationFilePathname+"\n");
		
		boolean ok = checkFilePath(compilationFilePathname);
		if ( !ok ) {
			System.err.println("ERROR: The --comp-file argument points to a non-existent file. Abort.");
			System.exit(1);
		}

		File compilationFile = new File(compilationFilePathname);
		String corpusName = "this-corpus";
		CompiledCorpusRegistry.registerCorpus(corpusName, compilationFile);
		CompiledCorpus compiledCorpus = CompiledCorpusRegistry.getCorpus(corpusName);
		
		long totalOccurences = compiledCorpus.totalOccurences();
		long totalOccurencesNoDecomp = 
			compiledCorpus.totalOccurencesWithNoDecomp();
		long totalOccurenceWithDecomp = 
				totalOccurences - totalOccurencesNoDecomp;
		
		System.out.println(
				"Total number of analyzed words in trie (succeeded analysis): "+
				totalOccurenceWithDecomp);
		System.out.println("Total number of words that failed analysis: "+
				totalOccurencesNoDecomp);
		System.out.println("");
		System.out.println(
				"Number of distinct analyzed words in trie (succeeded analysis): "+
				compiledCorpus.totalWordsWithDecomps());
		System.out.println("Number of distinct words that failed analysis: "+
				compiledCorpus.totalWordsWithNoDecomp());
		System.out.println("");
		
		String action = "";
		while ( action!=null ) {
			action = prompt("q:quit    f:failed words");
			if (action==null)
				break;
			if (action.equals("f")) {
				String subaction = prompt("a:all words on screen\nf:all words in file\ns:random set of 100 words on screen\nq:quit");
				if (subaction==null)
					action = null;
				if (subaction!=null) {
// TODO-June2020: Reactivate this code
//					String allFailed[] = compiledCorpus.wor();
//					if (subaction.equals("a")) {
//						Arrays.sort(allFailed);
//						System.out.println(String.join(" ",allFailed)+"\n");
//					} else if (subaction.equals("s")) {
//						Random rand = new Random();
//						int randomIndices[] = rand.ints(0, allFailed.length).distinct().limit(50).toArray();
//						int maxWordsPerLine = 5;
//						for (int i=0; i<randomIndices.length; i++) {
//							System.out.print(allFailed[randomIndices[i]]+"   ");
//							if ((i+1) % maxWordsPerLine == 0)
//								System.out.println();
//						}
//						System.out.println("");
//					} else if (subaction.equals("f")) {
//						String fileName = prompt(">>> enter file name (or q to quit): ");
//						if (fileName==null)
//							break;
//						Arrays.sort(allFailed);
//						File outputFile = new File(fileName);
//						BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
//						int maxWordsPerLine = 7;
//						for (int i=0; i<allFailed.length; i++) {
//							bw.write(allFailed[i]+"   ");
//							if ((i+1) % maxWordsPerLine == 0)
//								bw.newLine();
//						}
//						bw.flush();
//						bw.close();
//						System.out.println("Words saved in "+outputFile.getAbsolutePath()+"\n");
//					}
				}
			}
		}

	}
	
	private void printRandom(String x) {
		System.out.println(x);
	}
	
	private boolean checkFilePath(String _trieFilePath) {
		File f = new File(_trieFilePath);
		File dirF = f.getParentFile();
		if ( dirF != null && !dirF.isDirectory() ) {
			return false;
		}
		return true;
	}
}
