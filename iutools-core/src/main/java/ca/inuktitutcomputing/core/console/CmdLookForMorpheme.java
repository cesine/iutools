package ca.inuktitutcomputing.core.console;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;

import ca.inuktitutcomputing.data.LinguisticDataSingleton;
import ca.inuktitutcomputing.data.Morpheme;
import ca.nrc.datastructure.Pair;
import ca.pirurvik.iutools.CompiledCorpus;
import ca.pirurvik.iutools.morphemesearcher.MorphemeSearcher;
import ca.pirurvik.iutools.morphemesearcher.ScoredExample;

public class CmdLookForMorpheme extends ConsoleCommand {

	public CmdLookForMorpheme(String name) {
		super(name);
	}

	@Override
	public String getUsageOverview() {
		return "Return words containing morpheme X.";
	}

	@Override
	public void execute() throws Exception {
		String morpheme = getMorpheme(false);
		//String dictionaryFilePathname = getDictFile(true);
		//File dictionaryFile = new File(dictionaryFilePathname);
		String compiledCorpusFilePath = getCompilationFile();
		FileReader fr = new FileReader(compiledCorpusFilePath);
		CompiledCorpus compiledCorpus = new Gson().fromJson(fr, CompiledCorpus.class);
		fr.close();
		
		MorphemeSearcher morphExtr = new MorphemeSearcher();
		morphExtr.useCorpus(compiledCorpus);
		
		//morphExtr.useDictionary(dictionaryFile);
		
		boolean interactive = false;
		List<MorphemeSearcher.Words> words = null;
		if (morpheme == null) {
			interactive = true;
		} else {
			words = morphExtr.wordsContainingMorpheme(morpheme);
		}

		while (true) {
			if (interactive) {
				morpheme = prompt("Enter Inuktut morpheme (nominal form)");
				if (morpheme == null) break;
				words = null;
				try {
					words = morphExtr.wordsContainingMorpheme(morpheme);
				} catch (Exception e) {
					throw e;
				}
			}
			
			if (words != null && words.size() > 0) {
				MorphemeSearcher.WordFreqComparator comparator = morphExtr.new WordFreqComparator();
				Iterator<MorphemeSearcher.Words> itWords = words.iterator();
				int nIt = 1;
				while (itWords.hasNext()) {
					MorphemeSearcher.Words wordsForMorpheme = itWords.next();
					String morphemeWithId = wordsForMorpheme.morphemeWithId;
					ScoredExample[] wordsAndFreqs = wordsForMorpheme.words.toArray(new ScoredExample[] {});
					Arrays.sort(wordsAndFreqs, comparator);

					String[] wordList = new String[wordsAndFreqs.length];
					for (int iWF=0; iWF<wordsAndFreqs.length; iWF++) {
						wordList[iWF] = wordsAndFreqs[iWF].word + "(" + wordsAndFreqs[iWF].score + ")";
					}
					
					echo("\nMORPHEME ID: "+morphemeWithId+
							"        "+
							"\""+Morpheme.getMorpheme(morphemeWithId).englishMeaning+"\""+
							"               "+
							"["+wordList.length+" words]"+"\n");
					echo(String.join("; ", wordList));
				}
			}
			
			if (!interactive) break;				
		}

	}

}
