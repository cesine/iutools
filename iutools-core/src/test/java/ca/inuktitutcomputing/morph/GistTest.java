package ca.inuktitutcomputing.morph;

import java.io.IOException;

import org.junit.Test;

import ca.inuktitutcomputing.data.LinguisticDataException;
import ca.nrc.datastructure.Pair;
import ca.nrc.json.PrettyPrinter;
import ca.nrc.testing.AssertHelpers;

public class GistTest {

	@Test
	public void test__Gist_Synopsis() throws Exception, LinguisticDataException {
		String word = "iglumik";
		Gist gist = new Gist(word);
		Pair<String,String>[] components = gist.wordComponents;
		Pair<String,String>[] expected = new Pair[2];
		expected[0] = new Pair<String,String>("iglu","(1) house");
		expected[1] = new Pair<String,String>("mik","accusative: a; the (one)");
		AssertHelpers.assertDeepEquals("", expected, components);
	}

	@Test
	public void test__Gist_immagaq() throws Exception {
		String word = "immagaq";
		Gist gist = new Gist(word);
		Pair<String,String>[] components = gist.wordComponents;
		System.out.println(PrettyPrinter.print(components));
	}

}
