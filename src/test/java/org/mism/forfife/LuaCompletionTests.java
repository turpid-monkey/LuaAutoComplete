/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mism.forfife;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.junit.Test;
import org.mism.forfife.LuaSyntaxAnalyzer.Completion;

/**
 *
 * @author tr1nergy
 */
public class LuaCompletionTests {
    @Test
    public void testLuaSyntaxAnalyzer()
    {
        LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
        an.initCompletions("foo = function (n) return n*2 end\nfunction test(q) return q end\n\n", 1, 0);
        assertEquals("foo", an.getCompletions().iterator().next().getText());
        assertEquals(2, an.getCompletions().size());
    }
    
    @Test
    public void testLuaScoping_SimpleSeparateScopes1()
    {
    	 LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
         an.initCompletions("do i=5 end\ndo q=5 end\n", 1, 0);
         assertEquals("i", an.getCompletions().iterator().next().getText());
         assertEquals(an.getCompletions().size(), 1);
    }
    
    @Test
    public void testLuaScoping_SimpleSeparateScopes2()
    {
    	 LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
         an.initCompletions("do i=5 end\ndo q=5 end\n", 2, 1);
         assertEquals("q", an.getCompletions().iterator().next().getText());
         assertEquals(an.getCompletions().size(), 1);
    }
    
    @Test
    public void testLuaScoping_StackedSeparateScopes()
    {
    	LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
        an.initCompletions("b=10\ndo i=5 end\ndo q=5 end\n", 2, 1);
        Iterator<Completion> completions = an.getCompletions().iterator();
        assertEquals("b", completions.next().getText());
        assertEquals("i", completions.next().getText());
        assertEquals(an.getCompletions().size(), 2);
    }
    
    @Test
    public void testFunctionParameterParsing()
    {
    	LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
    	an.initCompletions("function paramsTest(a,b,c) return 5 end",1,0);
    	Iterator<Completion> completions = an.getCompletions().iterator();
    	assertEquals("paramsTest", completions.next().getText());
    	assertEquals("a", completions.next().getText());
    	assertEquals("b", completions.next().getText());
    	assertEquals("c", completions.next().getText());
    	assertEquals(3, an.getFunctionParams("paramsTest").size());
    }
    
    @Test
    public void testEndOfBlockParsing()
    {
    	LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
    	an.initCompletions("function paramsTest(a,b,c) return 5 end",1,40);
    	Iterator<Completion> completions = an.getCompletions().iterator();
    	assertEquals("paramsTest", completions.next().getText());
    	assertEquals("a", completions.next().getText());
    	assertEquals("b", completions.next().getText());
    	assertEquals("c", completions.next().getText());
    	assertEquals(3, an.getFunctionParams("paramsTest").size());
    }
}
