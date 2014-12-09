package org.mism.forfife;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.junit.Test;

public class LuaAutoCompetionProviderTest {

	@Test
	public void testCreateCompletions() {
		LuaCompletionProvider prov = new LuaCompletionProvider();
		List<CompletionInfo> infos = new ArrayList<>();
		infos.add(CompletionInfo.newFunctionInstance("function", 1, 1, true));
		infos.add(CompletionInfo.newVariableInstance("var", 2, 2, false));
		Map<String, List<Parameter>> params = new HashMap<>();
		params.put("function", Arrays.asList(new Parameter("string", "param1")));

		Map<String, String> functionDescr = new HashMap<String, String>();
		functionDescr.put("function", "<p>Some Info");
		List<Completion> comps = prov.initDynamicCompletions(infos, params,
				functionDescr);
		assertEquals(2, comps.size());
		assertEquals("function", comps.get(0).getInputText());
		assertEquals("var", comps.get(1).getInputText());

		FunctionCompletion fc = (FunctionCompletion) comps.get(0);
		assertEquals(1, fc.getParamCount());
		assertEquals("<p>Some Info", fc.getShortDescription());

	}

}
