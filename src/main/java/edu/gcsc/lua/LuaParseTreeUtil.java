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
package edu.gcsc.lua;

import java.io.IOException;
import java.io.StringReader;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTree;

import edu.gcsc.lua.grammar.LuaLexer;
import edu.gcsc.lua.grammar.LuaParser;
import edu.gcsc.lua.grammar.LuaParser.ChunkContext;
import edu.gcsc.lua.grammar.LuaParser.StatContext;

public class LuaParseTreeUtil {
	public static <T extends ParserRuleContext> T getParentRuleContext(
			ParserRuleContext inner, final int ruleIdx, final Class<T> t) {
		while ((inner = inner.getParent()).getRuleIndex() != ruleIdx) {
			if (inner.getRuleIndex() == LuaParser.RULE_chunk)
				return null;
		}
		return t.cast(inner);
	}

	public static boolean hasParentRuleContext(ParserRuleContext inner,
			final int ruleIdx) {
		while ((inner = inner.getParent()).getRuleIndex() != ruleIdx) {
			if (inner.getRuleIndex() == LuaParser.RULE_chunk)
				return false;
		}
		return true;
	}

	public static StatContext getParentStatContext(final ParserRuleContext inner) {
		return getParentRuleContext(inner, LuaParser.RULE_stat,
				StatContext.class);
	}

	public static <T extends ParserRuleContext> T getChildRuleContext(
			final ParserRuleContext parent, final int ruleIdx,
			final Class<? extends T> t) {
		if (parent.children == null)
			return null;
		for (ParseTree child : parent.children) {
			if (child instanceof ParserRuleContext
					&& ((ParserRuleContext) child).getRuleIndex() == ruleIdx)
				return t.cast(child);
		}
		return null;
	}

	public static <T extends ParserRuleContext> T getLastChildRuleContext(
			final ParserRuleContext parent, final int ruleIdx,
			final Class<? extends T> t) {
		if (parent.children == null)
			return null;
		for (int i = parent.children.size() - 1; i >= 0; i--) {
			ParseTree child = parent.children.get(i);

			if (child instanceof ParserRuleContext
					&& ((ParserRuleContext) child).getRuleIndex() == ruleIdx)
				return t.cast(child);
		}
		return null;
	}

	public static <T extends ParserRuleContext> T getChildRuleContextByIdx(
			final ParserRuleContext parent, final int idx, final int ruleIdx,
			final Class<? extends T> t) {
		if (parent.children == null)
			return null;
		int counter = 0;
		for (ParseTree child : parent.children) {
			if (child instanceof ParserRuleContext
					&& ((ParserRuleContext) child).getRuleIndex() == ruleIdx)
				if (counter++ == idx) {
					return t.cast(child);
				}
		}
		return null;
	}

	public static int countChildRuleContextInstances(
			final ParserRuleContext parent, final int ruleIdx) {
		if (parent.children == null)
			return 0;
		int count = 0;
		for (ParseTree child : parent.children) {
			if (child instanceof ParserRuleContext
					&& ((ParserRuleContext) child).getRuleIndex() == ruleIdx)
				count++;
		}
		return count;
	}

	public static <T extends ParserRuleContext> T getChildRuleContextRecursive(
			final ParserRuleContext parent, final Class<? extends T> t,
			final int... path) {
		ParserRuleContext childCtx = parent;
		for (int ruleIdx : path) {
			childCtx = getChildRuleContext(childCtx, ruleIdx,
					ParserRuleContext.class);
			if (childCtx == null) {
				return null;
			}
		}
		return t.cast(childCtx);
	}

	public static <T extends ParserRuleContext> T getLastChildRuleContextRecursive(
			final ParserRuleContext parent, final Class<? extends T> t,
			final int... path) {
		ParserRuleContext childCtx = parent;
		for (int ruleIdx : path) {
			childCtx = getLastChildRuleContext(childCtx, ruleIdx,
					ParserRuleContext.class);
			if (childCtx == null) {
				return null;
			}
		}
		return t.cast(childCtx);
	}
	
	public static String start(ParserRuleContext ctx) {
		return ctx.getStart().getText();
	}

	public static String next(ParserRuleContext ctx) {
		return ctx.getChild(1).getText();
	}

	public static String txt(ParserRuleContext ctx, int sibling) {
		if (ctx.children != null && ctx.children.size() > sibling) {
			return ctx.getChild(sibling).getText();
		}
		return null;
	}

	public static int line(ParserRuleContext ctx) {
		return ctx.getStart().getLine();
	}

	public static int col(ParserRuleContext ctx) {
		return ctx.getStart().getCharPositionInLine();
	}

	public static String txt(ParserRuleContext ctx) {
		return ctx.getText();
	}

	public static ChunkContext parse(final String luaScript)
			throws IOException, RecognitionException {
		LuaParser parser = new LuaParser(new CommonTokenStream(new LuaLexer(
				new ANTLRInputStream(new StringReader(luaScript)))));
		parser.getErrorListeners().clear();
		return parser.chunk();
	}
}
