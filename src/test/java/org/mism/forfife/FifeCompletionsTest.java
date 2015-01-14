package org.mism.forfife;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;

import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.VariableCompletion;
import org.junit.Test;

public class FifeCompletionsTest {

	@Test
	public void testCreateCompletions() {
		FifeCompletions comps = new FifeCompletions(new LuaCompletionProvider());
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		an.getFunctionParams().put("function",
				Arrays.asList(new FunctionParameter("param1")));
		an.getDoxyGenMap().put("function", "<p>Some Info");

		comps.addCompletion(CompletionInfo.newFunctionInstance(
				new LuaResource("test"), "function", 1, 1, true), an);

		FunctionCompletion fc = (FunctionCompletion) comps.getCompletions()
				.iterator().next();
		assertEquals(1, fc.getParamCount());
		assertEquals("<p>Some Info<p>included from test, line 1",
				fc.getShortDescription());

		comps.getCompletions().clear();

		comps.addCompletion(CompletionInfo.newVariableInstance(
				new LuaResource("test"), "var", 2, 2, false), an);

		VariableCompletion vc = (VariableCompletion) comps.getCompletions()
				.iterator().next();
		assertEquals("var", vc.getInputText());

	}

	@Test
	public void testCreateCompletionsIgnoreTable() {
		FifeCompletions comps = new FifeCompletions(new LuaCompletionProvider());
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		an.getTables().put("var", new HashSet<String>());
		comps.addCompletion(CompletionInfo.newVariableInstance(new LuaResource("test"),
				"var", 2, 2, false), an);
		assertEquals(0, comps.getCompletions().size());
	}

}
