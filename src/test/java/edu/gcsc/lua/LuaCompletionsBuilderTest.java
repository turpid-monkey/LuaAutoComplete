package edu.gcsc.lua;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.gcsc.lua.CaretInfo;
import edu.gcsc.lua.CompletionInfo;
import edu.gcsc.lua.Completions;
import edu.gcsc.lua.LuaCompletionsBuilder;
import edu.gcsc.lua.LuaResource;
import edu.gcsc.lua.LuaSyntaxAnalyzer;
import edu.gcsc.lua.LuaSyntaxInfo;

public class LuaCompletionsBuilderTest extends LuaCompletionsBuilder {

	@Test
	public void fillCompletions() throws Exception {
		LuaSyntaxAnalyzer an = LuaSyntaxAnalyzerTest.createAndRunTestAnalyzer(
				"test = 1\n"
				+ "function Demo:test()\n"
				+ "  self.test = 1\n"
				+ "end\n"
				+ "function test()"
				+ "   return 1\n"
				+ "end\n", CaretInfo.newInstance(30));
		LuaCompletionsBuilder builder = new LuaCompletionsBuilder();
		Map<LuaResource, LuaSyntaxInfo> includes = new HashMap<LuaResource, LuaSyntaxInfo>();
		includes.put(an.getResource(), an);
		final List<CompletionInfo> comps = new ArrayList<CompletionInfo>();
		Completions completions = new Completions() {

			@Override
			public void addCompletion(CompletionInfo info,
					LuaSyntaxInfo syntaxInfo) {
				comps.add(info);
			}
		};
		builder.fillCompletions(an, includes, completions, "", CaretInfo.newInstance(30));
		assertEquals(3, comps.size());
	}

}
