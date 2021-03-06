package ca.inuktitutcomputing.data;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import ca.nrc.json.PrettyPrinter;

public class AffixTest {

//	@Test
//	public void test_getFormsInContext__Case_ijaq() throws Exception {
//		String affixId = "ijaq/1nv";
//		Affix affix = LinguisticData.getInstance().getAffixWithId(affixId);
//		
//		char context = 'V';
//		String form = affix.vform[0];
//		Action action1 = affix.vaction1[0];
//		Action action2 = affix.vaction2[0];
//		HashSet<SurfaceFormInContext> surfaceFormsInContext = affix.getFormsInContext(context, form, action1, action2, affixId);
//		assertEquals("",2,surfaceFormsInContext.size());
//		assertTrue("",surfaceFormsInContext.contains(new SurfaceFormInContext("ijaq","V","V",affixId)));
//		assertTrue("",surfaceFormsInContext.contains(new SurfaceFormInContext("ngijaq","VV","V",affixId)));
//		
//		context = 't';
//		form = affix.tform[0];
//		action1 = affix.taction1[0];
//		action2 = affix.taction2[0];
//		surfaceFormsInContext = affix.getFormsInContext(context, form, action1, action2, affixId);
//		assertEquals("",1,surfaceFormsInContext.size());
//		assertTrue("",surfaceFormsInContext.contains(new SurfaceFormInContext("aijaq","C","t",affixId)));
//		
//		context = 'k';
//		form = affix.kform[0];
//		action1 = affix.kaction1[0];
//		action2 = affix.kaction2[0];
//		surfaceFormsInContext = affix.getFormsInContext(context, form, action1, action2, affixId);
//		assertEquals("",2,surfaceFormsInContext.size());
//		assertTrue("",surfaceFormsInContext.contains(new SurfaceFormInContext("ijaq","V","k",affixId)));
//		assertTrue("",surfaceFormsInContext.contains(new SurfaceFormInContext("ngijaq","VV","k",affixId)));
//		
//		context = 'q';
//		form = affix.qform[0];
//		action1 = affix.qaction1[0];
//		action2 = affix.qaction2[0];
//		surfaceFormsInContext = affix.getFormsInContext(context, form, action1, action2, affixId);
//		assertEquals("",2,surfaceFormsInContext.size());
//		assertTrue("",surfaceFormsInContext.contains(new SurfaceFormInContext("ijaq","V","q",affixId)));
//		assertTrue("",surfaceFormsInContext.contains(new SurfaceFormInContext("ngijaq","VV","q",affixId)));
//	}
//	
//	@Test
//	public void test_getSurfaceFormsInContext__Case_ijaq() throws Exception {
//		String affixId = "ijaq/1nv";
//		Affix affix = LinguisticData.getInstance().getAffixWithId(affixId);
//		
//		char context = 'V';
//		HashSet<SurfaceFormInContext> surfaceFormsInContext = affix.getSurfaceFormsInContext(context, affixId);
//		assertEquals("",2,surfaceFormsInContext.size());
//		assertTrue("",surfaceFormsInContext.contains(new SurfaceFormInContext("ijaq","V","V",affixId)));
//		assertTrue("",surfaceFormsInContext.contains(new SurfaceFormInContext("ngijaq","VV","V",affixId)));
//		
//		context = 't';
//		surfaceFormsInContext = affix.getSurfaceFormsInContext(context, affixId);
//		assertEquals("",1,surfaceFormsInContext.size());
//		assertTrue("",surfaceFormsInContext.contains(new SurfaceFormInContext("aijaq","C","t",affixId)));
//		
//		context = 'k';
//		surfaceFormsInContext = affix.getSurfaceFormsInContext(context, affixId);
//		assertEquals("",2,surfaceFormsInContext.size());
//		assertTrue("",surfaceFormsInContext.contains(new SurfaceFormInContext("ijaq","V","k",affixId)));
//		assertTrue("",surfaceFormsInContext.contains(new SurfaceFormInContext("ngijaq","VV","k",affixId)));
//		
//		context = 'q';
//		surfaceFormsInContext = affix.getSurfaceFormsInContext(context, affixId);
//		assertEquals("",2,surfaceFormsInContext.size());
//		assertTrue("",surfaceFormsInContext.contains(new SurfaceFormInContext("ijaq","V","q",affixId)));
//		assertTrue("",surfaceFormsInContext.contains(new SurfaceFormInContext("ngijaq","VV","q",affixId)));
//	}

}
