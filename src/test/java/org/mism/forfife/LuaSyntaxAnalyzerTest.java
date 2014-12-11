package org.mism.forfife;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;
import org.mism.forfife.visitors.RequireVisitor;

/**
 *
 * @author tr1nergy
 */
public class LuaSyntaxAnalyzerTest {

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
	public void testInlineFunctionDeclAssign() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.HOME;
		an.initCompletions(
				"foo = function (n)\nreturn n end\nfunction test(q) return q end\n\n",
				c);
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
	public void testLuaScoping_SimpleSeparateScopes1() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.HOME;
		an.initCompletions("do local i=5 end\ndo local q=5 end\n", c);
		assertEquals("local VARIABLE:i;", toString(an.getCompletions()));
	}

	@Test
	public void testLuaScoping_SimpleSeparateScopes2() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.newInstance(13);
		an.initCompletions("do local i=5 end\ndo local q=5 end\n", c);
		assertEquals("local VARIABLE:q;", toString(an.getCompletions()));
	}

	@Test
	public void testLuaScoping_NameLists() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.newInstance(13);
		an.initCompletions("a,b = 5\n"
				+ "do local x, z=5 end\ndo local q=5 end\n", c);
		assertEquals(
				"VARIABLE:a; VARIABLE:b; local VARIABLE:x; local VARIABLE:z;",
				toString(an.getCompletions()));
	}

	@Test
	public void testLuaScoping_StackedSeparateScopes() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.newInstance(10);
		an.initCompletions("b=10\ndo local i=5 end\ndo local q=5 end\n", c);
		assertEquals("VARIABLE:b; local VARIABLE:i;",
				toString(an.getCompletions()));
	}

	@Test
	public void testFunctionParameterParsing() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.HOME;
		an.initCompletions("function paramsTest(a,b,c) return 5 end", c);
		assertEquals(
				"FUNCTION:paramsTest; local VARIABLE:a; local VARIABLE:b; local VARIABLE:c;",
				toString(an.getCompletions()));
		assertEquals(3, an.getFunctionParams("paramsTest").size());
	}

	@Test
	public void testEndOfBlockParsing() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.newInstance(40);
		an.initCompletions("function paramsTest(a,b,c) return 5 end", c);
		assertEquals("FUNCTION:paramsTest;", toString(an.getCompletions()));
		assertEquals(3, an.getFunctionParams("paramsTest").size());
	}

	@Test
	public void testAnonymousFunctionBlock() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.newInstance(40);
		an.initCompletions("foo = function (n)\nreturn n*2\nend", c);
		assertEquals("FUNCTION:foo;", toString(an.getCompletions()));
		assertEquals(1, an.getFunctionParams("foo").size());
	}

	@Test
	public void testDuplicateVarCompletions() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.newInstance(40);
		an.initCompletions("function test(arm, be, crushed)\n" + "fun = 5\n"
				+ "return arm * arm + crushed\n" + "end", c);
		assertEquals(
				"FUNCTION:test; local VARIABLE:arm; local VARIABLE:be; local VARIABLE:crushed; VARIABLE:fun;",
				toString(an.getCompletions()));
		assertEquals(3, an.getFunctionParams("test").size());
	}

	@Test
	public void testVarsInForLoop() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.HOME;
		an.initCompletions("q=5\nfor i=1,10 do\n q=q*q \n end\n", c);
		assertEquals("local VARIABLE:i; VARIABLE:q;",
				toString(an.getCompletions()));
	}

	@Test
	public void testLocalVarsAndFuncsFirstAndDeepestStack1() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.HOME;
		// currently, if the caret position is within the first and deepest
		// stack, all completions in this stack are offered
		an.initCompletions("someVar=5\n" + "function localDanger()\n"
				+ "   local function superLoco(x)\n" + "         return x+1\n"
				+ "   end\n" + "   local q = superLoco(5)\n" + "   return q\n"
				+ "end\n", c);
		assertEquals(
				"FUNCTION:localDanger; local FUNCTION:superLoco; local VARIABLE:q; VARIABLE:someVar; local VARIABLE:x;",
				toString(an.getCompletions()));
	}

	@Test
	public void testLocalVarsAndFuncsFirstAndDeepestStack2() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.newInstance(40);
		an.initCompletions("someVar=5\n" + "function localDanger()\n"
				+ "   local function superLoco(x)\n" + "         return x+1\n"
				+ "   end\n" + "   local q = superLoco(5)\n" + "   return q\n"
				+ "end\n", c);
		assertEquals(
				"FUNCTION:localDanger; local FUNCTION:superLoco; local VARIABLE:q; VARIABLE:someVar; local VARIABLE:x;",
				toString(an.getCompletions()));
	}

	@Test
	public void testLocalFunction() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.newInstance(20);
		an.initCompletions("   local function superLoco(x)\n"
				+ "         return x+1\n" + "   end\n", c);
		assertEquals("local FUNCTION:superLoco; local VARIABLE:x;",
				toString(an.getCompletions()));
	}

	@Test
	public void testNestedLocalFunction() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.newInstance(20);
		an.initCompletions("function top()\n"
				+ "   local function superLoco(x)\n" + "         return x+1\n"
				+ "   end\n" + "end\n", c);
		assertEquals(
				"local FUNCTION:superLoco; FUNCTION:top; local VARIABLE:x;",
				toString(an.getCompletions()));
	}

	@Test
	public void testLocalVarsAndFuncsNestedStack() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.newInstance(100);
		an.initCompletions("someVar=5\n" + "function localDanger()\n"
				+ "   local function superLoco(x)\n" + "         return x+1\n"
				+ "   end\n" + "   local q = superLoco(5)\n" + "   return q\n"
				+ "end\n", c);
		assertEquals(
				"FUNCTION:localDanger; local FUNCTION:superLoco; local VARIABLE:q; VARIABLE:someVar;",
				toString(an.getCompletions()));
	}

	@Test
	public void testLocalVarsAndFuncsGlobalStack() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.newInstance(130);
		an.initCompletions("someVar=5\n" + "function localDanger()\n"
				+ "   local function superLoco(x)\n" + "         return x+1\n"
				+ "   end\n" + "   local q = superLoco(5)\n" + "   return q\n"
				+ "end\n", c);
		assertEquals("FUNCTION:localDanger; VARIABLE:someVar;",
				toString(an.getCompletions()));
	}

	@Test
	public void testForNamelistInExp() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.newInstance(130);
		an.initCompletions("for some, other in anyExpr do" + "   nothing()"
				+ "end\n", c);
		assertEquals("local VARIABLE:other; local VARIABLE:some;",
				toString(an.getCompletions()));
	}

	@Test
	public void testAssigmentTracking() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.newInstance(0);
		an.initCompletions("someVar = SuperClass()", c);
		assertEquals(an.getTypeMap().get("someVar"), "SuperClass");
	}

	@Test
	public void testVisitorParsing() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		an.setVisitors(Arrays.asList(new RequireVisitor()));
		CaretInfo c = CaretInfo.HOME;
		an.initCompletions("require \"foo\"", c);
		assertEquals(1, an.getDependentResources().size());
	}

	@Test
	public void testDummyResourceResolution() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		an.setVisitors(Arrays.asList(new RequireVisitor()));
		an.setResourceLoaders(Arrays.asList(new LuaResourceLoader() {

			@Override
			public boolean canLoad(LuaResource res) {
				return true;
			}

			@Override
			public String load(LuaResource res) throws Exception {
				return "require \"bar\"";
			}
		}));
		CaretInfo c = CaretInfo.HOME;
		an.initCompletions("require \"foo\"", c);
		assertEquals(1, an.getDependentResources().size());
		assertEquals(1, an.getDependentResourceCache().values().size());
		assertEquals("require:foo", an.getDependentResourceCache().values()
				.iterator().next().getResource().getResourceLink());
		assertEquals(1, an.getDependentResourceCache().values().iterator()
				.next().getDependentResourceCache().values().size());
		assertEquals("require:bar", an.getDependentResourceCache().values()
				.iterator().next().getDependentResourceCache().values()
				.iterator().next().getResource().getResourceLink());

	}
}
