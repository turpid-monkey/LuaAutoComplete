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

import static org.mism.forfife.LuaParseTreeUtil.col;
import static org.mism.forfife.LuaParseTreeUtil.getChildRuleContextRecursive;
import static org.mism.forfife.LuaParseTreeUtil.getParentStatContext;
import static org.mism.forfife.LuaParseTreeUtil.hasParentRuleContext;
import static org.mism.forfife.LuaParseTreeUtil.line;
import static org.mism.forfife.LuaParseTreeUtil.next;
import static org.mism.forfife.LuaParseTreeUtil.start;
import static org.mism.forfife.LuaParseTreeUtil.txt;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.mism.forfife.lua.LuaBaseListener;
import org.mism.forfife.lua.LuaBaseVisitor;
import org.mism.forfife.lua.LuaLexer;
import org.mism.forfife.lua.LuaParser;
import org.mism.forfife.lua.LuaParser.BlockContext;
import org.mism.forfife.lua.LuaParser.FuncbodyContext;
import org.mism.forfife.lua.LuaParser.NamelistContext;
import org.mism.forfife.lua.LuaParser.PrefixexpContext;
import org.mism.forfife.lua.LuaParser.StatContext;
import org.mism.forfife.visitors.LuaCompletionVisitor;
/**
 * Parses a script and tries to collect all relevant data for proposals.
 * 
 * @author tr1nergy
 */
class LuaSyntaxAnalyzer extends LuaSyntaxInfo {

	LuaCompletionVisitor[] visitors;

	public LuaSyntaxAnalyzer(LuaCompletionVisitor... visitors) {
		this.visitors = visitors;
		for (LuaCompletionVisitor v : visitors)
		{
			v.setInfo(this);
		}
	}

	/**
	 * @return a copy of the values in the current state of the stack.
	 */
	public Collection<CompletionInfo> getCompletions() {
		Map<String, CompletionInfo> map = new HashMap<>();
		for (Map<String, CompletionInfo> scope : relevantStack) {
			for (CompletionInfo c : scope.values()) {
				if (!map.containsKey((c.getText())))
					map.put(c.getText(), c);
			}
		}
		return map.values();
	}

	/**
	 * @param luaScript
	 * @return whether the parsing went well
	 */
	public boolean initCompletions(String luaScript, CaretInfo info) {
		try {
			endIdx = luaScript.trim().length() - 1;
			ANTLRInputStream str = new ANTLRInputStream(new StringReader(
					luaScript));
			Lexer lx = new LuaLexer(str);
			CommonTokenStream tokStr = new CommonTokenStream(lx);
			LuaParser parser = new LuaParser(tokStr);
			parser.addParseListener(new LuaListener(info));
			context = parser.chunk();
			for (LuaBaseVisitor<?> visitor : visitors) {
				context.accept(visitor);
			}
			return true;
		} catch (RecognitionException e) {
			Logging.debug("Parser unhappy with current script state. This is ok."
					+ e.getMessage());
			return false;
		} catch (Exception e) {
			Logging.error(
					"Bad code in completion-creation code, this is not ok.", e);
			return false;
		}
	}

	
	private class LuaListener extends LuaBaseListener {

		private static final String LEFT_BRACKET = "(";
		private static final String FUNCTION = "function";
		private static final String LOCAL = "local";
		private static final String FOR = "for";
		private static final String ASSIGN = "=";

		CaretInfo info;
		boolean frozen = false;
		Stack<Map<String, CompletionInfo>> scopes = new Stack<>();
		Map<String, CompletionInfo> global = new HashMap<String, CompletionInfo>();

		LuaListener(CaretInfo info) {
			this.info = info;
		}

		boolean isDeclaredLocal(String name) {
			if (global.containsKey(name))
				return false;
			for (int i = 0; i < scopes.size(); i++) {
				if (scopes.get(i).containsKey(name)) {
					return scopes.get(i).get(name).isLocal();
				}
			}
			return false;
		}

		void replaceCompletionInfoRecursive(CompletionInfo info) {
			if (global.containsKey(info.getText())) {
				global.put(info.getText(), info);
				Logging.debug("Replaced a completion '" + info.getText()
						+ "' in global scope.");
			}
			for (int i = 0; i < scopes.size(); i++) {
				if (scopes.get(i).containsKey(info.getText())) {
					scopes.get(i).put(info.getText(), info);
					Logging.debug("Replaced a completion '" + info.getText()
							+ "' in a sub-scope.");
				}
			}
		}

		void addVariable(String name, ParserRuleContext ctx, boolean local) {

			CompletionInfo varInfo = CompletionInfo.newVariableInstance(name,
					line(ctx), col(ctx), local);
			if (local || isDeclaredLocal(name)) {
				scopes.peek().put(name, varInfo);
			} else {
				global.put(name, varInfo);
			}
		}

		void addFunction(String name, ParserRuleContext ctx, boolean local) {
			CompletionInfo funcInfo = CompletionInfo.newFunctionInstance(name,
					line(ctx), col(ctx), local);
			if (local) {
				scopes.peek().put(name, funcInfo);
			} else {
				global.put(name, funcInfo);
			}

		}

		@Override
		public void exitNamelist(NamelistContext ctx) {
			for (ParseTree pt : ctx.children) {
				if (pt.getText().matches(","))
					continue;
				addVariable(pt.getText(), ctx, true);
			}
		}

		@Override
		public void exitVar(LuaParser.VarContext ctx) {
			String varName = start(ctx);
			LuaParser.StatContext statCtx = getParentStatContext(ctx);
			boolean local = start(statCtx).equals(LOCAL);

			// if it is a subrule of prefixExp, it might as well be a function
			// call
			if (!hasParentRuleContext(ctx, LuaParser.RULE_prefixexp)
					&& !hasParentRuleContext(ctx, LuaParser.RULE_functioncall)) {
				addVariable(varName, ctx, local);
			}
		}

		@Override
		public void exitFuncname(LuaParser.FuncnameContext ctx) {
			String funcName = txt(ctx);
			addFunction(funcName, ctx, false);

		}

		@Override
		public void enterBlock(BlockContext ctx) {
			pushScope(ctx);
		}

		@Override
		public void enterFuncbody(FuncbodyContext ctx) {
			pushScope(ctx);
		}

		@Override
		public void exitStat(StatContext ctx) {
			String startText = start(ctx);

			switch (startText) {
			case FOR:
				ParseTree t = ctx.getChild(1);
				if (t.getChildCount() == 0) // single var decl
				{
					addVariable(next(ctx), ctx, true);
				} // else should be a namelist => handled by exitNamelist()
				break;
			case LOCAL:
				if (ctx.getChild(1).getText().equals(FUNCTION)) {
					String localFunction = ctx.getChild(2).getText();
					addFunction(localFunction, ctx, true);
				}

				break;
			}
			if (ctx.getChildCount() == 3) {
				if (ctx.getChild(1).getText().equals(ASSIGN)) {
					PrefixexpContext prefixExp = getChildRuleContextRecursive(
							ctx, PrefixexpContext.class,
							LuaParser.RULE_explist, LuaParser.RULE_exp,
							LuaParser.RULE_prefixexp);
					if (prefixExp != null) {
						typeMap.put(start(ctx), start(prefixExp));
						Logging.debug("Found a possible type in line "
								+ line(ctx) + ": var " + start(ctx) + " = "
								+ start(prefixExp));
					}
				}
			}
		}

		@Override
		public void exitFuncbody(FuncbodyContext ctx) {
			StatContext statCtx = getParentStatContext(ctx);
			String startText = statCtx.getStart().getText();
			String currentFunction;
			boolean anonFunction = false;
			switch (startText) {
			case FUNCTION:
				currentFunction = next(statCtx);
				break;
			case LOCAL:
				currentFunction = statCtx.getChild(2).getText();
				break;
			default:
				currentFunction = startText;
				anonFunction = true;
			}

			if (anonFunction) {
				// anonymous function
				try {
					Logging.info("Anonymous function hit. Redefining var '"
							+ currentFunction + "' to a function.");
					// var declaration in parent scope needs to be replaced.
					replaceCompletionInfoRecursive(CompletionInfo
							.newFunctionInstance(currentFunction, line(ctx),
									col(ctx), false));
				} catch (NullPointerException e) {
					Logging.error("Unknown type of anonymous function def, or some completely different construct.");
				}
			}
			// dig for Function Params
			Token stop = ctx.getStop();
			if (ctx.getChildCount() == 5) {
				if (ctx.getChild(0).getText().equals(LEFT_BRACKET)) {
					if (ctx.getChild(1).getChildCount() == 1) {
						if (ctx.getChild(1).getChild(0).getChildCount() > 0) {
							// we have a function parameter list!
							ParseTree t = ctx.getChild(1).getChild(0);
							int childCount = t.getChildCount();
							List<Parameter> params = new ArrayList<>();
							functionParams.put(currentFunction, params);
							for (int i = 0; i < childCount; i += 2) {
								ParseTree nt = t.getChild(i);
								params.add(new Parameter(null, nt.getText()));
								addVariable(nt.getText(), ctx, true);
							}
						}
					}
				}
			}
			popScope(stop.getStopIndex());
		}

		private void pushScope(ParserRuleContext ctx) {
			scopes.push(new TreeMap<>());
			// Logging.debug("Scope depth in line " + line(ctx) + ", col " +
			// col(ctx) + " now " + scopes.size());
		}

		private void useScope() {
			relevantStack = new Stack<>();
			relevantStack.add(global);
			relevantStack.addAll(scopes);
			Logging.debug("Relevant scope identified as " + relevantStack);
			frozen = true;
		}

		private void popScope(int offset) {
			// our caret offset lies in this block
			if (!frozen && offset >= info.getPosition()) {
				useScope();
			}
			// we are about to pop the last scope without having found
			// the proper one - this should only be the case when
			// the caret pos is beyond the end of the block.
			if (!frozen && scopes.size() == 1 && info.getPosition() != 0) {
				useScope();
			}
			// Logging.info("Scope popped: " + scopes.peek());
			scopes.pop();
		}

		@Override
		public void exitBlock(BlockContext ctx) {
			Token stop = ctx.getStop();
			popScope(stop.getStopIndex());
		}
	}
}
