package ca.inuktitutcomputing.core.console;

import java.io.FileReader;

import com.google.gson.Gson;

import ca.inuktitutcomputing.applications.Decompose;
import ca.inuktitutcomputing.morph.Decomposition;
import ca.inuktitutcomputing.morph.Decomposition.DecompositionExpression;
import ca.inuktitutcomputing.script.Roman;
import ca.inuktitutcomputing.script.Syllabics;
import ca.pirurvik.iutools.MorphRelativesFinder;
import ca.pirurvik.iutools.MorphologicalRelative;
import ca.pirurvik.iutools.corpus.CompiledCorpus_InMemory;
import ca.inuktitutcomputing.morph.MorphologicalAnalyzer;

public class CmdExpandQuery extends ConsoleCommand {

	public CmdExpandQuery(String name) {
		super(name);
	}

	@Override
	public String getUsageOverview() {
		return "Return a list of most useful reformulations of an inuktitut word.";
	}

	@Override
	public void execute() throws Exception {
		String word = getWord(false);
		String latin = null;
		String syll = null;
		MorphologicalRelative[] reformulations = null;
		
		String compilationFilePath = getCorpusSavePath();
		FileReader fr = new FileReader(compilationFilePath);
		CompiledCorpus_InMemory compiledCorpus = new Gson().fromJson(fr, CompiledCorpus_InMemory.class);
		fr.close();
		MorphRelativesFinder reformulator = new MorphRelativesFinder(compiledCorpus);
		CmdConvertIUSegments convertCommand = new CmdConvertIUSegments("");
		
		MorphologicalAnalyzer morphAnalyzer = new MorphologicalAnalyzer();

		boolean interactive = false;
		if (word == null) {
			interactive = true;
		} else {
			if (Syllabics.allInuktitut(word)) {
				syll  = word;
				latin = Syllabics.transcodeToRoman(syll);
			}
			else {
				latin = word;
				syll = Roman.transcodeToUnicode(latin, null);
			}
			reformulations = reformulator.getRelatives(latin);
		}

		while (true) {
			if (interactive) {
				word = prompt("Enter Inuktut word");
				if (word == null) break;
				if (Syllabics.allInuktitut(word)) {
					syll  = word;
					latin = Syllabics.transcodeToRoman(syll);
				}
				else {
					latin = word;
					syll = Roman.transcodeToUnicode(latin, null);
				}
				reformulations = null;
				try {
					reformulations = reformulator.getRelatives(latin);
				} catch (Exception e) {
					throw e;
				}
			}

			long freqWord = 0;
			String expansions = "";
			if (reformulations != null && reformulations.length > 0) {
				String[] syllRefs = new String[reformulations.length];
				for (int i=0; i<reformulations.length; i++) {
					if (reformulations[i].getWord().equals(latin)) {
						freqWord = reformulations[i].getFrequency();
						continue;
					}
					String syllRef = Roman.transcodeToUnicode(reformulations[i].getWord(), null);
					expansions += "\n  "+reformulations[i].getWord()+" ("+syllRef+") : "+
							reformulations[i].getFrequency();
					expansions += "\n\n    "+
							String.join("\n    ", Decompose.getMeaningsInArrayOfStrings(
									String.join("", reformulations[i].getMorphemes()),"en",false,true))+
							"\n";
				}
			} else {
				expansions = "\n    No expansion could be found in the corpus.\n";
			}

			Decomposition[] decs = morphAnalyzer.decomposeWord(latin);
			Decomposition dec = null;
			if (decs.length != 0)
				dec = decs[0];
			echo("\nWord:\n\n  "+latin+" ("+syll+") : "+freqWord);
			if (dec==null)
				echo("\n    The word could not be decomposed by the inuktitut morphological analyzer.\n");
			else {
				String converted = convertCommand.convert(dec.toStr2());
				echo("\n    "+String.join("\n    ", Decompose.getMeaningsInArrayOfStrings(converted,"en",false,true)));
			}

			echo("\nExpansions:");
			echo(expansions);
			
			if (!interactive) break;				
		}

	}
	
	
	
	

}
