package ca.inuktitutcomputing.core.console;

import java.util.Scanner;

import ca.nrc.ui.commandline.SubCommand;
import ca.pirurvik.iutools.edit_distance.EditDistanceCalculatorFactory;

public abstract class ConsoleCommand extends SubCommand {
	
	public static final String OPT_CORPUS_DIR = "corpus-dir";
	public static final String OPT_CORPUS_NAME = "corpus-name";
	public static final String OPT_COMP_FILE = "comp-file";
	public static final String OPT_GS_FILE = "gs-file";
	public static final String OPT_MORPHEMES = "morphemes";
	public static final String OPT_WORD = "word";
	public static final String OPT_FROM_SCRATCH = "from-scratch";
	public static final String OPT_REDO_FAILED = "redo-failed";
	public static final String OPT_CONTENT = "content";
	public static final String OPT_INPUT_FILE = "input-file";
	public static final String OPT_FONT = "font";
	public static final String OPT_SOM = "stats-over-morphemes";
	public static final String OPT_DICT_FILE = "dict-file";
	public static final String OPT_MAX_CORR = "max-corr";
	public static final String OPT_ED_ALGO = "edit-dist";
	public static final String OPT_MORPHEME = "morpheme";
	public static final String OPT_EXTENDED_ANALYSIS = "extended-analysis";
	
	
	public ConsoleCommand(String name) {
		super(name);
	}
	
	protected String getCompilationFile() {
		return getCompilationFile(true);
	}
	protected String getCompilationFile(boolean failIfAbsent) {
		String tFile = getOptionValue(ConsoleCommand.OPT_COMP_FILE, failIfAbsent);
		if (tFile != null && !tFile.endsWith("json")) {
			tFile = tFile + ".json";
		}
		return tFile;
	}
	
	protected String getDictFile() {
		return getDictFile(true);
	}
	protected String getDictFile(boolean failIfAbsent) {
		String dFile = getOptionValue(ConsoleCommand.OPT_DICT_FILE, true);
		return dFile;
	}

	protected String getCorpusDir() {
		String dir = getOptionValue(ConsoleCommand.OPT_CORPUS_DIR, true);
		return dir;
	}

	protected String getCorpusName() {
		return getCorpusName(false);
	}
	protected String getCorpusName(boolean failIfAbsent) {
		String corpusName = getOptionValue(ConsoleCommand.OPT_CORPUS_NAME, failIfAbsent);
		return corpusName;		
	}
	
	protected String[] getMorphemes() {
		return getMorphemes(true);
	}
	
	protected String[] getMorphemes(boolean failIfAbsent) {
		String morphStr = getOptionValue(ConsoleCommand.OPT_MORPHEMES, failIfAbsent);
		String[] morphSeq = null;
		if (morphStr != null) {
			morphSeq = morphStr.split("\\s+");
		}
		
		return morphSeq;		
	}
	
	protected String getWord() {
		return getWord(true);
	}
	protected String getWord(boolean failIfAbsent) {
		String wordStr = getOptionValue(ConsoleCommand.OPT_WORD, failIfAbsent);
		return wordStr;		
	}
	
	protected String getExtendedAnalysis() {
		return getExtendedAnalysis(false);
	}
	protected String getExtendedAnalysis(boolean failIfAbsent) {
		String option = getOptionValue(ConsoleCommand.OPT_EXTENDED_ANALYSIS, failIfAbsent);
		return option;
	}
	
	protected String getMorpheme() {
		return getMorpheme(true);
	}
	protected String getMorpheme(boolean failIfAbsent) {
		String morpheme = getOptionValue(ConsoleCommand.OPT_MORPHEME, failIfAbsent);
		return morpheme;		
	}
	
	protected String getContent() {
		return getContent(true);
	}
	protected String getContent(boolean failIfAbsent) {
		return getOptionValue(ConsoleCommand.OPT_CONTENT, failIfAbsent);
	}
	
	protected String getFont() {
		return getContent(true);
	}
	protected String getFont(boolean failIfAbsent) {
		return getOptionValue(ConsoleCommand.OPT_FONT, failIfAbsent);
	}
	
	protected String getInputFile() {
		return getInputFile(true);
	}
	protected String getInputFile(boolean failIfAbsent) {
		return getOptionValue(ConsoleCommand.OPT_INPUT_FILE, failIfAbsent);
	}
	
	protected String getGoldStandardFile() {
		return getGoldStandardFile(true);
	}
	protected String getGoldStandardFile(boolean failIfAbsent) {
		return getOptionValue(ConsoleCommand.OPT_GS_FILE, failIfAbsent);
	}
	
	protected String getStatsOverMorphemes() {
		return getStatsOverMorphemes(false);
	}
	protected String getStatsOverMorphemes(boolean failIfAbsent) {
		return getOptionValue(ConsoleCommand.OPT_SOM, failIfAbsent);
	}
	
	protected String getMaxCorr() {
		return getMaxCorr(false);
	}
	protected String getMaxCorr(boolean failIfAbsent) {
		return getOptionValue(ConsoleCommand.OPT_MAX_CORR, failIfAbsent);
	}
	
	protected EditDistanceCalculatorFactory.DistanceMethod getEditDistanceAlgorithm() {
		return getEditDistanceAlgorithm(false);
	}
	
	protected EditDistanceCalculatorFactory.DistanceMethod getEditDistanceAlgorithm(boolean failIfAbsent) {
		String algName = getOptionValue(ConsoleCommand.OPT_ED_ALGO, failIfAbsent);
		EditDistanceCalculatorFactory.DistanceMethod alg = null;
		if (algName != null)
			alg = EditDistanceCalculatorFactory.DistanceMethod.valueOf(algName.toUpperCase());
		
		return alg;
	}
	
	protected static String prompt(String mess) {
		mess = "\n" + mess + " ('q' to quit).";
		System.out.print(mess+"\n> ");
		Scanner input = new Scanner(System.in);
        String resp = input.nextLine();
        if (resp.matches("^\\s*q\\s*$")) {
        	resp = null;
        }
		return resp;
	}
	

}