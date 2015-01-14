package org.mism.forfife;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.junit.Test;

public class LuaAutoCompetionProviderTest {

	@Test
	public void testCreateCompletions() {
		LuaCompletionProvider prov = new LuaCompletionProvider();
		List<CompletionInfo> infos = new ArrayList<CompletionInfo>();
		infos.add(CompletionInfo.newFunctionInstance(new LuaResource("test"),
				"function", 1, 1, true));
		infos.add(CompletionInfo.newVariableInstance(new LuaResource("test"),
				"var", 2, 2, false));
		Map<String, List<FunctionParameter>> params = new HashMap<String, List<FunctionParameter>>();
		params.put("function", Arrays.asList(new FunctionParameter("param1")));
		Map<String, Set<String>> tables = new HashMap<String, Set<String>>();
		Map<String, String> functionDescr = new HashMap<String, String>();
		functionDescr.put("function", "<p>Some Info");
		List<Completion> comps = prov.initDynamicCompletions(infos, params,
				functionDescr, tables);
		assertEquals(2, comps.size());
		assertEquals("function", comps.get(0).getInputText());
		assertEquals("var", comps.get(1).getInputText());

		FunctionCompletion fc = (FunctionCompletion) comps.get(0);
		assertEquals(1, fc.getParamCount());
		assertEquals("<p>Some Info<p>included from test, line 1",
				fc.getShortDescription());

	}

	@Test
	public void testCreateCompletionsIgnoreTable() {
		LuaCompletionProvider prov = new LuaCompletionProvider();
		List<CompletionInfo> infos = new ArrayList<CompletionInfo>();
		infos.add(CompletionInfo.newVariableInstance(new LuaResource("test"),
				"var", 2, 2, false));
		Map<String, List<FunctionParameter>> params = new HashMap<String, List<FunctionParameter>>();
		Map<String, Set<String>> tables = new HashMap<String, Set<String>>();
		tables.put("var", new HashSet<String>());
		Map<String, String> functionDescr = new HashMap<String, String>();
		List<Completion> comps = prov.initDynamicCompletions(infos, params,
				functionDescr, tables);
		assertEquals(0, comps.size());
	}

}
