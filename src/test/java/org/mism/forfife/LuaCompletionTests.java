/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mism.forfife;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;
import org.mism.forfife.LuaSyntaxAnalyzer.Completion;

/**
 *
 * @author tr1nergy
 */
public class LuaCompletionTests {

	static String toString(Collection<Completion> completions) {
		StringBuffer buf = new StringBuffer();
		List<Completion> cl;
		Collections.sort(cl = new ArrayList<Completion>(completions), new Comparator<Completion>(
				) {

					@Override
					public int compare(Completion o1, Completion o2) {
						return o1.getType().name().concat(o1.getText()).compareTo(o2.getType().name().concat(o2.getText()));
					}
		});
		for (Completion c : cl) {
			buf.append((c.isLocal()?"local ":"") + c.getType().name() + ":" + c.getText() + "; ");
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
		an.initCompletions("do i=5 end\ndo q=5 end\n", c);
		assertEquals("VARIABLE:i;", toString(an.getCompletions()));
	}

	@Test
	public void testLuaScoping_SimpleSeparateScopes2() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.newInstance(13);
		an.initCompletions("do i=5 end\ndo q=5 end\n", c);
		assertEquals("VARIABLE:q;", toString(an.getCompletions()));
	}

	@Test
	public void testLuaScoping_StackedSeparateScopes() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.newInstance(10);
		an.initCompletions("b=10\ndo i=5 end\ndo q=5 end\n", c);
		assertEquals("VARIABLE:b; VARIABLE:i;", toString(an.getCompletions()));
	}

	@Test
	public void testFunctionParameterParsing() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.HOME;
		an.initCompletions("function paramsTest(a,b,c) return 5 end", c);
		assertEquals("FUNCTION:paramsTest; local VARIABLE:a; local VARIABLE:b; local VARIABLE:c;", toString(an.getCompletions()));
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
	public void testDuplicateVarCompletions()
	{
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.newInstance(40);
		an.initCompletions("function test(arm, be, crushed)\n"
				+ "fun = 5\n"
				//+ "crushed = 2\n"
				+ "return arm * arm + crushed\n"
				+ "end", c);
		assertEquals("FUNCTION:test; local VARIABLE:arm; local VARIABLE:be; local VARIABLE:crushed; VARIABLE:fun;", toString(an.getCompletions()));
		assertEquals(3, an.getFunctionParams("test").size());
	}
	
	@Test
	public void testVarsInForLoop(){
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.HOME;
		an.initCompletions("q=5\nfor i=1,10 do\n q=q*q \n end\n", c);
		assertEquals("local VARIABLE:i; VARIABLE:q;", toString(an.getCompletions()));
	}
	
	@Test
	public void testLocalVarsAndFuncs1()
	{
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.HOME;
		an.initCompletions("someVar=5\n"
				+ "function localDanger()\n"
				+ "   local function superLoco(x)\n"
				+ "         return x+1\n"
				+ "   end\n"
				+ "   local q = superLoco(5)\n"
				+ "   return q\n"
				+ "end\n", c);
		//assertEquals("FUNCTION:localDanger; VARIABLE:someVar;", toString(an.getCompletions()));
	}
	@Test
	public void testLocalVarsAndFuncs2()
	{
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.newInstance(23);
		an.initCompletions("someVar=5\n"
				+ "function localDanger()\n"
				+ "   local function superLoco(x)\n"
				+ "         return x+1\n"
				+ "   end\n"
				+ "   local q = superLoco(5)\n"
				+ "   return q\n"
				+ "end\n", c);
		//assertEquals("FUNCTION:localDanger; FUNCTION:superLoco; VARIABLE q; VARIABLE:someVar;", toString(an.getCompletions()));
	}
	
	@Test
	public void testLocalVarsAndFuncs3()
	{
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.newInstance(42);
		an.initCompletions("someVar=5\n"
				+ "function localDanger()\n"
				+ "   local function superLoco(x)\n"
				+ "         return x+1\n"
				+ "   end\n"
				+ "   local q = superLoco(5)\n"
				+ "   return q\n"
				+ "end\n", c);
		//assertEquals("FUNCTION:localDanger; FUNCTION:superLoco; VARIABLE q; VARIABLE:someVar; VARIABLE x;", toString(an.getCompletions()));
	}
}
