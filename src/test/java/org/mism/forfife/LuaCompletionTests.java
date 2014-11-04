/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mism.forfife;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tr1nergy
 */
public class LuaCompletionTests {
    @Test
    public void testLuaSyntaxAnalyzer()
    {
        LuaSyntaxAnalyzer an = new LuaSyntaxAnalyzer();
        an.initCompletions("foo = function (n) return n*2 end\nfunction test(q) return q end\n\n", 0, 0);
        assertEquals("foo", an.getCompletions().iterator().next().getText());
        assertEquals(an.getCompletions().size(), 4);
    }
}
