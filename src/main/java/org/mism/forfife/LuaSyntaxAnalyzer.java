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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.mism.forfife.lua.LuaBaseListener;
import org.mism.forfife.lua.LuaLexer;
import org.mism.forfife.lua.LuaParser;
import org.mism.forfife.lua.LuaParser.BlockContext;
import org.mism.forfife.lua.LuaParser.FuncbodyContext;

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
    
	Stack<Map<String,Completion>> relevantStack = new Stack<>();

    public Collection<Completion> getCompletions() {
    	ArrayList<Completion> completions = new ArrayList<>();
    	for (Map<String,Completion> scope :relevantStack)
    	{
    		completions.addAll(scope.values());
    	}
        return completions;
    }

    public LuaParser.ChunkContext getContext() {
        return context;
    }
    

    /**
     * 
     * @param luaScript
     * @return whether the parsing went well
     */
    public boolean initCompletions(String luaScript, int line, int pos) {
        try {
            ANTLRInputStream str = new ANTLRInputStream(new StringReader(luaScript));
            Lexer lx = new LuaLexer(str);
            CommonTokenStream tokStr = new CommonTokenStream(lx);
            LuaParser parser = new LuaParser(tokStr);
            parser.addParseListener(new LuaListener(line, pos));
            context = parser.chunk();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private class LuaListener extends LuaBaseListener {
    	
    	int line, pos;
    	boolean frozen = false;
    	Stack<Map<String,Completion>> scopes = new Stack<>();
    	
    	LuaListener(int line, int pos) {
    		if (line < 1) throw new IllegalArgumentException("Line argument should be greater or equal to 1.");
    		if (pos < 0) throw new IllegalArgumentException("Position in line argument should be greater 0.");
 			this.line = line;
			this.pos = pos;
		}

        @Override
        public void exitVar(LuaParser.VarContext ctx) {
            scopes.peek().put(ctx.getText(),
                    Completion.newInstance(CompletionType.VARIABLE, ctx.getText(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine()));
        }

        @Override
        public void exitFuncname(LuaParser.FuncnameContext ctx) {
            
            scopes.peek().put(ctx.getText(),
                    Completion.newInstance(CompletionType.FUNCTION, ctx.getText(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine()));
        }
        
        @Override
        public void enterBlock(BlockContext ctx) {
        	pushScope();
        }
        
        @Override
        public void enterFuncbody(FuncbodyContext ctx) {
        	pushScope();
        }
        
        @Override
        public void exitFuncbody(FuncbodyContext ctx) {
        	Token start = ctx.getStart();
        	popScope(start.getLine(), start.getCharPositionInLine());
        }
        
        private void pushScope()
        {
        	scopes.push(new TreeMap<>());
        }
        
        private void popScope(int tokenLine, int tokenPos)
        {
        	if (!frozen && tokenLine>=line && tokenPos>pos)
        	{
        	   	relevantStack = (Stack<Map<String, Completion>>) scopes.clone();
        	   	frozen = true;
        	}
        	scopes.pop();
        }
        
        @Override
        public void exitBlock(BlockContext ctx) {
        	Token start = ctx.getStart();
        	popScope(start.getLine(), start.getCharPositionInLine());
        }
    }
}
