package ca.pirurvik.iutools.webservice;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.inuktitutcomputing.morph.Gist;
import ca.inuktitutcomputing.utilities.Alignment;
import ca.nrc.testing.AssertHelpers;
import ca.nrc.testing.AssertNumber;
import ca.nrc.testing.AssertObject;
import ca.nrc.ui.web.testing.MockHttpServletRequest;
import ca.nrc.ui.web.testing.MockHttpServletResponse;
import ca.pirurvik.iutools.search.SearchHit;
import ca.pirurvik.iutools.testing.IUTTestHelpers;
import ca.pirurvik.iutools.webservice.SearchEndpoint;
import ca.pirurvik.iutools.webservice.SearchResponse;
import ca.pirurvik.iutools.webservice.gist.GistWordEndpoint;
import ca.pirurvik.iutools.webservice.gist.GistWordResponse;
import ca.pirurvik.iutools.webservice.tokenize.TokenizeEndpoint;
import ca.pirurvik.iutools.webservice.tokenize.TokenizeResponse;

import org.junit.*;

public class IUTServiceTestHelpers {
	public static final long SHORT_WAIT = 2*1000;
	public static final long MEDIUM_WAIT = 2*SHORT_WAIT;
	public static final long LONG_WAIT = 2*MEDIUM_WAIT;
	
	public enum EndpointNames {
		GIST, GIST_WORD, MORPHEME, MORPHEMEEXAMPLE, SEARCH, TOKENIZE, SPELL};
	

	public static MockHttpServletResponse postEndpointDirectly(EndpointNames eptName, Object inputs) throws Exception {
		return postEndpointDirectly(eptName, inputs, false);
	}

	public static MockHttpServletResponse postEndpointDirectly(EndpointNames eptName, Object inputs, boolean expectServiceError) throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		String jsonBody = new ObjectMapper().writeValueAsString(inputs);
		request.setReaderContent(jsonBody);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		if (eptName == EndpointNames.GIST) {
			new GistEndpoint().doPost(request, response);
		} else if (eptName == EndpointNames.GIST_WORD) {
			new GistWordEndpoint().doPost(request, response);
		} else if (eptName == EndpointNames.MORPHEME) {
			new OccurenceSearchEndpoint().doPost(request, response);
		} else if (eptName == EndpointNames.MORPHEMEEXAMPLE) {
			new OccurenceExampleEndpoint().doPost(request, response);
		} else if (eptName == EndpointNames.SEARCH) {
			new SearchEndpoint().doPost(request, response);
		} else if (eptName == EndpointNames.SPELL) {
			new SpellEndpoint().doPost(request, response);	
		} else if (eptName == EndpointNames.TOKENIZE) {
			new TokenizeEndpoint().doPost(request, response);	
		}
		
		String srvErr = ServiceResponse.jsonErrorMessage(response.getOutput());
		if (srvErr != null && ! expectServiceError) {
			throw new Exception("Did not expect the service to return an error message but it did.\nerrorMessage: "+srvErr);
		} else if (srvErr == null && expectServiceError) {
			throw new Exception("Expected the service to return an error message but it did not.");
		}
		
		return response;
	}
	
	private static GistResponse toGistResponse(
			MockHttpServletResponse servletResp) throws IOException {
		String responseStr = servletResp.getOutputStream().toString();
		GistResponse response = 
				new ObjectMapper().readValue(responseStr, GistResponse.class);
		return response;
	}
	
	public static Object toGistWordResponse(
			MockHttpServletResponse gotResponse) throws IOException {
		String responseStr = gotResponse.getOutputStream().toString();
		GistWordResponse response = 
				new ObjectMapper().readValue(responseStr, GistWordResponse.class);
		return response;
	}
	
	

	public static SpellResponse toSpellResponse(
			HttpServletResponse servletResp) throws IOException {
		String responseStr = servletResp.getOutputStream().toString();
		SpellResponse response = 
				new ObjectMapper().readValue(responseStr, SpellResponse.class);
		return response;
	}

	
	public static SearchResponse toSearchResponse(
			HttpServletResponse servletResp) throws IOException {
		String responseStr = servletResp.getOutputStream().toString();
		SearchResponse response = new ObjectMapper().readValue(responseStr, SearchResponse.class);
		return response;
	}
	
	public static TokenizeResponse toTokenizeResponse(
			MockHttpServletResponse servletResp) throws IOException {
		String responseStr = servletResp.getOutputStream().toString();
		TokenizeResponse response = 
				new ObjectMapper().readValue(responseStr, TokenizeResponse.class);
		return response;
	}
	
	private static OccurenceSearchResponse toOccurenceSearchResponse(
			MockHttpServletResponse servletResp) throws IOException {
		String responseStr = servletResp.getOutputStream().toString();
		OccurenceSearchResponse response = 
				new ObjectMapper().readValue(responseStr, OccurenceSearchResponse.class);
		return response;
	}
	private static OccurenceExampleResponse toOccurenceExampleResponse(
			MockHttpServletResponse servletResp) throws IOException {
		String responseStr = servletResp.getOutputStream().toString();
		OccurenceExampleResponse response = 
				new ObjectMapper().readValue(responseStr, OccurenceExampleResponse.class);
		return response;
	}
	


	public static void assertExpandedQueryEquals(String expQuery, MockHttpServletResponse gotResponse) throws JsonParseException, JsonMappingException, IOException {
		SearchResponse gotResult = new ObjectMapper().readValue(gotResponse.getOutput(), SearchResponse.class);
		AssertHelpers.assertStringEquals("Expanded query was not as expected.", expQuery.trim(), gotResult.expandedQuery.trim());
	}


	public static void assertMostHitsMatchWords(String[] queryWords, MockHttpServletResponse gotResponse,
								double tolerance) throws JsonParseException, JsonMappingException, IOException {
		SearchResponse gotResult = new ObjectMapper().readValue(gotResponse.getOutput(), SearchResponse.class);
		List<SearchHit> gotHits = gotResult.hits;
		
		IUTTestHelpers.assertMostHitsMatchWords(queryWords, gotHits, tolerance);
		
		
		String regex = "(We would like to show you a description here but the site won’t allow us";
		for (String aWord: queryWords) {
			if (regex == null) {
				regex = "(";
			} else {
				regex += "|";
			}
			regex += aWord.toLowerCase();
		}
		regex += ")";
		
		Pattern patt = Pattern.compile(regex);
		int  hitNum = 1;
		Set<String> unmatchedURLs = new HashSet<String>();
		for (SearchHit aHit: gotHits) {
			Matcher matcher = patt.matcher(aHit.snippet);
			if (!matcher.find()) {
				unmatchedURLs.add(aHit.url);
			}
			hitNum++;
		}
		
		double unmatchedRatio = 1.0 * unmatchedURLs.size() / hitNum;
		Assert.assertTrue(
				"There were too many urls that did not match the  query words '"+regex+".\n"
			  + "Unmatched URLs were:\n  "
			  + String.join("\n  ", unmatchedURLs),
			  unmatchedRatio <= tolerance
			);
	}

	public static void assertSearchResponseIsOK(MockHttpServletResponse response, String expExpandedQuery,
			String[] queryWords, double badHitsTolerance, long minTotalHits, long minHitsRetrieved) 
			throws Exception {
		IUTServiceTestHelpers.assertExpandedQueryEquals(expExpandedQuery, response);
		
		SearchResponse srchResponse = IUTServiceTestHelpers.toSearchResponse(response);
		IUTServiceTestHelpers.assertMostHitsMatchWords(queryWords, response, badHitsTolerance);
		AssertNumber.isGreaterOrEqualTo("The total number of potential hits was too low", srchResponse.totalHits, minTotalHits);
		AssertNumber.isGreaterOrEqualTo("The number of hits actually retrieved was too low", new Long(srchResponse.hits.size()), minHitsRetrieved);
	}

	public static void assertGistResponseIsOK(
			MockHttpServletResponse response, String[] expDecompsAsStrings,
			Alignment[] expSentencePairs) throws Exception {
		
		GistResponse gistResponse = 
				IUTServiceTestHelpers.toGistResponse(response);
		
		
		String[] gotDecompsAsString = new String[gistResponse.decompositions.length];
		for (int ii=0; ii < gotDecompsAsString.length; ii++) {
			gotDecompsAsString[ii] = gistResponse.decompositions[ii].decstr;
		}
		
		AssertObject.assertDeepEquals(
				"Decompositions were not as expected", 
				expDecompsAsStrings, gotDecompsAsString);
		
		AssertObject.assertDeepEquals(
				"Sentence pairs were not as expected", 
				expSentencePairs, gistResponse.sentencePairs);
	}


	public static void assertOccurenceSearchResponseIsOK(
			MockHttpServletResponse response, Map<String,MorphemeSearchResult> expected) throws Exception {
		
		OccurenceSearchResponse occurenceSearchResponse = 
				IUTServiceTestHelpers.toOccurenceSearchResponse(response);
		
		Map<String,MorphemeSearchResult> got = occurenceSearchResponse.matchingWords;
		Assert.assertEquals("The number of morphemes with the given canonical form is not as expected.", expected.size(), got.size());
		String expMorphId = expected.keySet().toArray(new String[] {})[0];
		String gotMorphId = got.keySet().toArray(new String[] {})[0];
		Assert.assertEquals("The morpheme is not the one expected.", expMorphId, gotMorphId);
		MorphemeSearchResult expectedResult = expected.get(expMorphId);
		MorphemeSearchResult gotResult = got.get(expMorphId);
		Assert.assertEquals("The number of words returned for the morpheme is not as expected.", expectedResult.words.size(), gotResult.words.size());
		List<String> expectedWords = expectedResult.words;
		List<String> gotWords = gotResult.words;
		expectedWords.forEach ((expectedWord) -> {
			Assert.assertTrue("The word "+expectedWord+" is not in the returned list of words.",gotWords.contains(expectedWord));
		});	
	}

	public static void assertOccurenceExampleResponseIsOK(
			MockHttpServletResponse response, ExampleWordWithMorpheme expected) throws Exception {
		
		OccurenceExampleResponse occurenceExampleResponse = 
				IUTServiceTestHelpers.toOccurenceExampleResponse(response);
		
		ExampleWordWithMorpheme gotExampleWord = occurenceExampleResponse.exampleWord;
		Gist gotGist = gotExampleWord.gist;
		Gist expectedGist = expected.gist;
		AssertObject.assertDeepEquals("The gists are not equal.",expectedGist, gotGist);
		Assert.assertEquals("The word of the gist is not as expected.",  expectedGist.word, gotGist.word);
		Alignment[] gotAlignments = gotExampleWord.alignments;
		Alignment[] expectedAlignments = expected.alignments;
		Assert.assertEquals("The alignments are not equal.",
				expectedAlignments[0].get("iu").substring(0,100), 
				gotAlignments[0].get("iu").substring(0,100));
		Assert.assertEquals("The alignments are not equal.",
				expectedAlignments[0].get("en").substring(0,100), 
				gotAlignments[0].get("en").substring(0,100));
	}

}
