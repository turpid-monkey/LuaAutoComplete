package org.mism.forfife;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;
import org.mism.forfife.visitors.LuaCompletionVisitor;
import org.mism.forfife.visitors.RequireVisitor;

/**
 *
 * @author tr1nergy
 */
public class LuaSyntaxAnalyzerTest {

	static class TestRequireResourceLoader implements LuaResourceLoader {

		LuaResource resource;

		@Override
		public void setResource(LuaResource resource) {
			this.resource = resource;
		}
		
		@Override
		public LuaResource getResource() {
			return resource;
		}

		@Override
		public boolean canLoad() {
			return resource.getResourceLink().startsWith("require:");
		}

		@Override
		public String load() throws Exception {
			return "require \"" + resource.getResourceLink().substring(8)
					+ "\"";
		}

		@Override
		public boolean hasModifications() {
			return false;
		}
	}

	public static LuaSyntaxAnalyzer createTestAnalyzer(String script,
			CaretInfo info, LuaCompletionVisitor... visitors) throws Exception {
		LuaResourceLoaderFactory factory = new LuaResourceLoaderFactory(
				TextResourceLoader.class,
				LuaSyntaxAnalyzerTest.TestRequireResourceLoader.class);
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		an.setResourceLoaderFactory(factory);
		an.setResource(new LuaResource("txt:" + script));
		List<LuaCompletionVisitor> visitorList = new ArrayList<LuaCompletionVisitor>();
		visitorList.addAll(Arrays.asList(visitors));
		an.setVisitors(visitorList);
		return an;
	}

	public static LuaSyntaxAnalyzer createAndRunTestAnalyzer(String script,
			CaretInfo info, LuaCompletionVisitor... visitors) throws Exception {
		LuaSyntaxAnalyzer an = createTestAnalyzer(script, info, visitors);
		an.initCompletions(info);
		return an;
	}

	static String toString(Collection<CompletionInfo> completions) {
		StringBuffer buf = new StringBuffer();
		List<CompletionInfo> cl;
		Collections.sort(cl = new ArrayList<CompletionInfo>(completions),
				new Comparator<CompletionInfo>() {

					@Override
					public int compare(CompletionInfo o1, CompletionInfo o2) {
						return o1
								.getType()
								.name()
								.concat(o1.getText())
								.compareTo(
										o2.getType().name()
												.concat(o2.getText()));
					}
				});
		for (CompletionInfo c : cl) {
			buf.append((c.isLocal() ? "local " : "") + c.getType().name() + ":"
					+ c.getText() + "; ");
		}
		return buf.toString().trim();
	}

	@Test
	public void testInlineFunctionDeclAssign() throws Exception {
		LuaSyntaxAnalyzer an = createAndRunTestAnalyzer(
				"foo = function (n)\nreturn n end\nfunction test(q) return q end\n\n",
				CaretInfo.HOME);
		// currently n is recognized as function parameter, and again as inline
		// var declaration
		// within the function declaration. But in the final completion list,
		// only n shows.
		assertEquals("FUNCTION:foo; FUNCTION:test; local VARIABLE:n;",
				toString(an.getCompletions()));
		assertEquals(1, an.getFunctionParams("foo").size());
		assertEquals("n", an.getFunctionParams("foo").get(0).getName());
	}

	@Test
	public void testLuaScoping_SimpleSeparateScopes1() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.HOME;
		an = createAndRunTestAnalyzer("do local i=5 end\ndo local q=5 end\n", c);
		assertEquals("local VARIABLE:i;", toString(an.getCompletions()));
	}

	@Test
	public void testLuaScoping_SimpleSeparateScopes2() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.newInstance(13);
		an = createAndRunTestAnalyzer("do local i=5 end\ndo local q=5 end\n", c);
		assertEquals("local VARIABLE:q;", toString(an.getCompletions()));
	}

	@Test
	public void testLuaScoping_NameLists() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.newInstance(13);
		an = createAndRunTestAnalyzer("a,b = 5\n"
				+ "do local x, z=5 end\ndo local q=5 end\n", c);
		assertEquals(
				"VARIABLE:a; VARIABLE:b; local VARIABLE:x; local VARIABLE:z;",
				toString(an.getCompletions()));
	}

	@Test
	public void testLuaScoping_StackedSeparateScopes() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.newInstance(10);
		an = createAndRunTestAnalyzer(
				"b=10\ndo local i=5 end\ndo local q=5 end\n", c);
		assertEquals("VARIABLE:b; local VARIABLE:i;",
				toString(an.getCompletions()));
	}

	@Test
	public void testFunctionParameterParsing() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.HOME;
		an = createAndRunTestAnalyzer(
				"function paramsTest(a,b,c) return 5 end", c);
		assertEquals(
				"FUNCTION:paramsTest; local VARIABLE:a; local VARIABLE:b; local VARIABLE:c;",
				toString(an.getCompletions()));
		assertEquals(3, an.getFunctionParams("paramsTest").size());
	}

	@Test
	public void testEndOfBlockParsing() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.newInstance(40);
		an = createAndRunTestAnalyzer(
				"function paramsTest(a,b,c) return 5 end", c);
		assertEquals("FUNCTION:paramsTest;", toString(an.getCompletions()));
		assertEquals(3, an.getFunctionParams("paramsTest").size());
	}

	@Test
	public void testAnonymousFunctionBlock() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.newInstance(40);
		an = createAndRunTestAnalyzer("foo = function (n)\nreturn n*2\nend", c);
		assertEquals("FUNCTION:foo;", toString(an.getCompletions()));
		assertEquals(1, an.getFunctionParams("foo").size());
	}

	@Test
	public void testDuplicateVarCompletions() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.newInstance(40);
		an = createAndRunTestAnalyzer("function test(arm, be, crushed)\n"
				+ "fun = 5\n" + "return arm * arm + crushed\n" + "end", c);
		assertEquals(
				"FUNCTION:test; local VARIABLE:arm; local VARIABLE:be; local VARIABLE:crushed; VARIABLE:fun;",
				toString(an.getCompletions()));
		assertEquals(3, an.getFunctionParams("test").size());
	}

	@Test
	public void testVarsInForLoop() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.HOME;
		an = createAndRunTestAnalyzer("q=5\nfor i=1,10 do\n q=q*q \n end\n", c);
		assertEquals("local VARIABLE:i; VARIABLE:q;",
				toString(an.getCompletions()));
	}

	@Test
	public void testLocalVarsAndFuncsFirstAndDeepestStack1() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.HOME;
		// currently, if the caret position is within the first and deepest
		// stack, all completions in this stack are offered
		an = createAndRunTestAnalyzer("someVar=5\n"
				+ "function localDanger()\n"
				+ "   local function superLoco(x)\n" + "         return x+1\n"
				+ "   end\n" + "   local q = superLoco(5)\n" + "   return q\n"
				+ "end\n", c);
		assertEquals(
				"FUNCTION:localDanger; local FUNCTION:superLoco; local VARIABLE:q; VARIABLE:someVar; local VARIABLE:x;",
				toString(an.getCompletions()));
	}

	@Test
	public void testLocalVarsAndFuncsFirstAndDeepestStack2() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.newInstance(40);
		an = createAndRunTestAnalyzer("someVar=5\n"
				+ "function localDanger()\n"
				+ "   local function superLoco(x)\n" + "         return x+1\n"
				+ "   end\n" + "   local q = superLoco(5)\n" + "   return q\n"
				+ "end\n", c);
		assertEquals(
				"FUNCTION:localDanger; local FUNCTION:superLoco; local VARIABLE:q; VARIABLE:someVar; local VARIABLE:x;",
				toString(an.getCompletions()));
	}

	@Test
	public void testLocalFunction() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.newInstance(20);
		an = createAndRunTestAnalyzer("   local function superLoco(x)\n"
				+ "         return x+1\n" + "   end\n", c);
		assertEquals("local FUNCTION:superLoco; local VARIABLE:x;",
				toString(an.getCompletions()));
	}

	@Test
	public void testNestedLocalFunction() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.newInstance(20);
		an = createAndRunTestAnalyzer("function top()\n"
				+ "   local function superLoco(x)\n" + "         return x+1\n"
				+ "   end\n" + "end\n", c);
		assertEquals(
				"local FUNCTION:superLoco; FUNCTION:top; local VARIABLE:x;",
				toString(an.getCompletions()));
	}

	@Test
	public void testLocalVarsAndFuncsNestedStack() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.newInstance(100);
		an = createAndRunTestAnalyzer("someVar=5\n"
				+ "function localDanger()\n"
				+ "   local function superLoco(x)\n" + "         return x+1\n"
				+ "   end\n" + "   local q = superLoco(5)\n" + "   return q\n"
				+ "end\n", c);
		assertEquals(
				"FUNCTION:localDanger; local FUNCTION:superLoco; local VARIABLE:q; VARIABLE:someVar;",
				toString(an.getCompletions()));
	}

	@Test
	public void testLocalVarsAndFuncsGlobalStack() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.newInstance(130);
		an = createAndRunTestAnalyzer("someVar=5\n"
				+ "function localDanger()\n"
				+ "   local function superLoco(x)\n" + "         return x+1\n"
				+ "   end\n" + "   local q = superLoco(5)\n" + "   return q\n"
				+ "end\n", c);
		assertEquals("FUNCTION:localDanger; VARIABLE:someVar;",
				toString(an.getCompletions()));
	}

	@Test
	public void testForNamelistInExp() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.newInstance(130);
		an = createAndRunTestAnalyzer("for some, other in anyExpr do"
				+ "   nothing()" + "end\n", c);
		assertEquals("local VARIABLE:other; local VARIABLE:some;",
				toString(an.getCompletions()));
	}

	@Test
	public void testVisitorParsing() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.HOME;
		an = createAndRunTestAnalyzer("require \"foo\"", c, new RequireVisitor());
		assertEquals(1, an.getIncludedResources().size());
	}

	@Test
	public void testDummyResourceResolution() throws Exception {
		LuaSyntaxAnalyzer an = createTestAnalyzer("require \"foo\"",
				CaretInfo.HOME, new RequireVisitor());

		CaretInfo c = CaretInfo.HOME;
		an.initCompletions(c);
		assertEquals(1, an.getIncludedResources().size());
		assertEquals(1, an.getLoadedIncludes().values().size());
		assertEquals("require:foo", an.getLoadedIncludes().values().iterator()
				.next().getResource().getResourceLink());

	}
}
