/*
 * Copyright (c) 2014, Goethe University, Goethe Center for Scientific Computing (GCSC), gcsc.uni-frankfurt.de
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.mism.forfife;

import java.io.StringReader;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.mism.forfife.lua.LuaBaseListener;
import org.mism.forfife.lua.LuaLexer;
import org.mism.forfife.lua.LuaParser;

/**
 *
 * @author tr1nergy
 */
public class LuaSyntaxAnalyzer {
    
    public static enum CompletionType {FUNCTION, VARIABLE};
    
    public static class Completion {
        CompletionType type;
        String text;
        int line;
        int pos;

        public int getLine() {
            return line;
        }

        public int getPos() {
            return pos;
        }

        public String getText() {
            return text;
        }

        public CompletionType getType() {
            return type;
        }
        
        static Completion newInstance(CompletionType type, String text, int line, int pos)
        {
            Completion c = new Completion();
            c.type = type;
            c.text = text;
            c.line = line;
            c.pos = pos;
            return c;
        }
    }

    LuaParser.ChunkContext context;
    Map<String, Completion> completions = new TreeMap<>();

    public Collection<Completion> getCompletions() {
        return completions.values();
    }

    public LuaParser.ChunkContext getContext() {
        return context;
    }
    

    /**
     * 
     * @param luaScript
     * @return whether the parsing went well
     */
    public boolean initCompletions(String luaScript) {
        try {

            ANTLRInputStream str = new ANTLRInputStream(new StringReader(luaScript));
            Lexer lx = new LuaLexer(str);
            CommonTokenStream tokStr = new CommonTokenStream(lx);
            LuaParser parser = new LuaParser(tokStr);
            parser.addParseListener(new LuaListener());
            context = parser.chunk();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private class LuaListener extends LuaBaseListener {

        @Override
        public void exitVar(LuaParser.VarContext ctx) {
            completions.put(ctx.getText(),
                    Completion.newInstance(CompletionType.VARIABLE, ctx.getText(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine()));
            super.exitVar(ctx);
        }

        @Override
        public void exitFuncname(LuaParser.FuncnameContext ctx) {
            
            completions.put(ctx.getText(),
                    Completion.newInstance(CompletionType.FUNCTION, ctx.getText(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine()));
            super.exitFuncname(ctx);
        }
    }
}
