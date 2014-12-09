package org.mism.forfife;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;

import static org.junit.Assert.*;

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
		List<Completion> comps = prov.initDynamicCompletions(infos, new HashMap<>());
		assertEquals(2, comps.size());
		assertEquals("function", comps.get(0).getInputText());
		assertEquals("var", comps.get(1).getInputText());

	}

}
