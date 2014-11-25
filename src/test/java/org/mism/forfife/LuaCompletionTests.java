/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mism.forfife;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;
import org.mism.forfife.LuaSyntaxAnalyzer.Completion;

/**
 *
 * @author tr1nergy
 */
public class LuaCompletionTests {

	static String toString(Collection<Completion> completions) {
		StringBuffer buf = new StringBuffer();
		for (Completion c : completions) {
			buf.append(c.getType().name() + ":" + c.getText() + "; ");
		}
		return buf.toString().trim();
	}

	@Test
	public void testLuaSyntaxAnalyzer() {
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.HOME;
		an.initCompletions(
				"foo = function (n)\nreturn n end\nfunction test(q) return q end\n\n",
				c);
		// currently n is recognized as function parameter, and again as inline
		// var declaration
		// within the function declaration. But in the final completion list,
		// only n shows.
		assertEquals("FUNCTION:test; FUNCTION:foo; VARIABLE:n;",
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
		assertEquals("VARIABLE:a; VARIABLE:b; VARIABLE:c; FUNCTION:paramsTest;", toString(an.getCompletions()));
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
		assertEquals("VARIABLE:be; FUNCTION:test; VARIABLE:arm; VARIABLE:crushed; VARIABLE:fun;", toString(an.getCompletions()));
		assertEquals(3, an.getFunctionParams("test").size());
	}
	
	@Test
	public void testVarsInForLoop(){
		LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
		CaretInfo c = CaretInfo.HOME;
		an.initCompletions("q=5\nfor i=1,10 do\n q=q*q \n end\n", c);
		assertEquals("VARIABLE:q;VARIABLE:i;", toString(an.getCompletions()));
	}
}
