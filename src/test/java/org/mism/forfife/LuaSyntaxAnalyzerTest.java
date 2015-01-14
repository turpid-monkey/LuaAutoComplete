package org.mism.forfife;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		@SuppressWarnings("unchecked")
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
		Map<LuaResource, LuaSyntaxInfo> includes = new HashMap<LuaResource, LuaSyntaxInfo>();
		includes.put(new LuaResource("this"), an);
		an.initCompletions(info, includes);
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
		assertEquals("FUNCTION:foo; FUNCTION:test;",
				toString(an.getCompletions()));
		assertEquals(1, an.getFunctionParams("foo").size());
		assertEquals("n", an.getFunctionParams("foo").get(0).getParamName());
	}

	@Test
	public void testLuaScoping_SimpleSeparateScopes1() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.newInstance(5);
		an = createAndRunTestAnalyzer("do local i=5 end\ndo local q=5 end\n", c);
		assertEquals("local VARIABLE:i;", toString(an.getCompletions()));
	}

	@Test
	public void testLuaScoping_SimpleSeparateScopes2() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.newInstance(21);
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
		CaretInfo c = CaretInfo.newInstance(20);
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
		assertEquals("VARIABLE:q;", toString(an.getCompletions()));
	}

	@Test
	public void testVarsInForLoop2() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.newInstance(20);
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
		assertEquals("FUNCTION:localDanger; VARIABLE:someVar;",
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
				"FUNCTION:localDanger; local FUNCTION:superLoco; local VARIABLE:q; VARIABLE:someVar;",
				toString(an.getCompletions()));
	}

	@Test
	public void testLocalFunction() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.newInstance(26);
		an = createAndRunTestAnalyzer("local function superLoco(x)\n"
				+ "         return x+1\n" + "   end\n", c);
		assertEquals("local FUNCTION:superLoco; local VARIABLE:x;",
				toString(an.getCompletions()));
	}

	@Test
	public void testNestedLocalFunction() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.newInstance(49);
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
		CaretInfo c = CaretInfo.newInstance(31);
		an = createAndRunTestAnalyzer("for some, other in anyExpr do"
				+ "   nothing()" + "end\n", c);
		assertEquals("local VARIABLE:other; local VARIABLE:some;",
				toString(an.getCompletions()));
	}

	@Test
	public void testVisitorParsing() throws Exception {
		LuaSyntaxAnalyzer an;
		CaretInfo c = CaretInfo.HOME;
		an = createAndRunTestAnalyzer("require \"foo\"", c,
				new RequireVisitor());
		assertEquals(1, an.getIncludedResources().size());
	}

	@Test
	public void testDummyResourceResolution() throws Exception {
		LuaSyntaxAnalyzer an = createTestAnalyzer("require \"foo\"",
				CaretInfo.HOME, new RequireVisitor());

		CaretInfo c = CaretInfo.HOME;
		Map<LuaResource, LuaSyntaxInfo> includes = new HashMap<LuaResource, LuaSyntaxInfo>();
		an.initCompletions(c, includes);
		assertEquals(1, an.getIncludedResources().size());
		assertEquals("require:foo", includes.values().iterator().next()
				.getResource().getResourceLink());

	}

	@Test
	public void nestedLocalsGoGlobal() throws Exception {
		LuaSyntaxAnalyzer an = createAndRunTestAnalyzer(
				"function test()\n  local file\n  function inner()\n    file = 1\n  end\nend\n",
				CaretInfo.HOME);
		assertEquals("FUNCTION:inner; FUNCTION:test;",
				toString(an.getCompletions()));
	}

	@Test
	public void forNNinPairsGoGlobal() throws Exception {
		LuaSyntaxAnalyzer an = createAndRunTestAnalyzer(
				"someVar = 5\nfor _, plot in ipairs(plots) do\n return 5\n end\n",
				CaretInfo.HOME);
		assertEquals("VARIABLE:someVar;", toString(an.getCompletions()));
	}

	@Test
	public void assignOrCurlyBrace() throws Exception {
		LuaSyntaxAnalyzer an = createAndRunTestAnalyzer(
				"someVar = someVar or {}", CaretInfo.HOME);
		assertEquals("VARIABLE:someVar;", toString(an.getCompletions()));
	}

	@Test
	public void makeClassStyleTable() throws Exception {
		LuaSyntaxAnalyzer an = createAndRunTestAnalyzer("Account = {}\n"
				+ "Account.__index = Account\n"
				+ "function Account.create(balance)\n" + "   local acnt = {}\n"
				+ "   setmetatable(acnt,Account)\n"
				+ "   acnt.balance = balance\n" + "   return acnt\n" + "end\n"
				+ "function Account:withdraw(amount)\n"
				+ "   self.balance = self.balance - amount\n" + "end\n",
				CaretInfo.HOME);
		assertEquals(
				"FUNCTION:Account.create; FUNCTION:Account:withdraw; VARIABLE:Account; VARIABLE:Account.__index;",
				toString(an.getCompletions()));

	}

	@Test
	public void localTable() throws Exception {
		LuaSyntaxAnalyzer an = createAndRunTestAnalyzer(
				"local acnt = {}\nacnt.balance = 100\n", CaretInfo.HOME);
		assertEquals("local VARIABLE:acnt; local VARIABLE:acnt.balance;",
				toString(an.getCompletions()));
	}

	@Test
	public void testTableConstructor() throws Exception {
		LuaSyntaxAnalyzer an = createAndRunTestAnalyzer(
				"a = { b = 5 , value= 10}", CaretInfo.HOME);
		assertEquals("VARIABLE:a; VARIABLE:a.b; VARIABLE:a.value;",
				toString(an.getCompletions()));
	}

	@Test
	public void testLocalTableConstructorOutOfScope() throws Exception {
		LuaSyntaxAnalyzer an = createAndRunTestAnalyzer(
				"function x()\n local a = { b = 5 , value= 10} \n end\n",
				CaretInfo.HOME);
		assertEquals("FUNCTION:x;", toString(an.getCompletions()));
	}

	@Test
	public void testLocalTableConstructorWithinScope() throws Exception {
		LuaSyntaxAnalyzer an = createAndRunTestAnalyzer(
				"function x()\n local a = { b = 5 , value= 10} \n end\n",
				CaretInfo.newInstance(14));
		assertEquals(
				"FUNCTION:x; local VARIABLE:a; local VARIABLE:a.b; local VARIABLE:a.value;",
				toString(an.getCompletions()));
	}

	@Test
	public void classMemberFunctions() throws Exception {
		LuaSyntaxAnalyzer an = createAndRunTestAnalyzer(
				"function Account:withdraw(amount)\n self.balance = self.balance - amount\n end\n",
				CaretInfo.newInstance(35));
		assertEquals(1, an.getClasses().size());
		assertEquals(2, an.getClasses().get("Account").size());
		List<String> replTxt = new ArrayList<String>();
		for (CompletionInfo info : an.getClassMembers("Account")) {
			replTxt.add(info.getText());
		}
		assertEquals(true, replTxt.contains("Account:withdraw"));
		assertEquals(true, replTxt.contains("self.balance"));
		assertEquals(
				"FUNCTION:Account:withdraw; local VARIABLE:amount; local VARIABLE:self.balance;",
				toString(an.getCompletions()));
		assertEquals(true, an.hasClassContext());
		assertEquals("Account", an.getClassContext());
	}

	@Test
	public void classMemberFunctionsOutOfScope() throws Exception {
		LuaSyntaxAnalyzer an = createAndRunTestAnalyzer(
				"function Account:withdraw(amount)\n self.balance = self.balance - amount\n end\n",
				CaretInfo.HOME);
		assertEquals(1, an.getClasses().size());
		assertEquals(2, an.getClasses().get("Account").size());
		assertEquals(false, an.hasClassContext());
	}

	@Test
	public void numberArrayTables() throws Exception {
		LuaSyntaxAnalyzer an = createAndRunTestAnalyzer("a = { 1, 2, 3, 4 }",
				CaretInfo.HOME);
		assertEquals("VARIABLE:a;", toString(an.getCompletions()));
	}

	@Test
	public void forLoopsWithIpair() throws Exception {
		LuaSyntaxAnalyzer an = createAndRunTestAnalyzer(
				"for k, class in ipairs({\"GridFunctionGradientData\", \"GridFunctionNumberData\"}) do\n"
						+ " -- loop\n" + "end\n", CaretInfo.HOME);
		assertEquals("local VARIABLE:class; local VARIABLE:k;",
				toString(an.getCompletions()));
	}

	@Test
	public void tableArray() throws Exception {
		LuaSyntaxAnalyzer an = createAndRunTestAnalyzer("A = {}\n"
				+ "A[1] = {-1, 1}\n" + "A[2] = {-1, 1}\n" + "A[3] = {-1, 1}\n",
				CaretInfo.HOME);
		assertEquals("VARIABLE:A; VARIABLE:A[1]; VARIABLE:A[2]; VARIABLE:A[3];", toString(an.getCompletions()));
	}
	
	@Test
	public void stringArrayTables() throws Exception {
		LuaSyntaxAnalyzer an = createAndRunTestAnalyzer("a = { \"a\", \"b\" }",
				CaretInfo.HOME);
		assertEquals("VARIABLE:a;", toString(an.getCompletions()));
	}
}
