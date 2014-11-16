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
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
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

	public static enum CompletionType {
		FUNCTION, VARIABLE
	};

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

		static Completion newInstance(CompletionType type, String text,
				int line, int pos) {
			Completion c = new Completion();
			c.type = type;
			c.text = text;
			c.line = line;
			c.pos = pos;
			return c;
		}

		public String toString() {
			return getType().name();
		}
	}

	LuaParser.ChunkContext context;
	int endIdx;

	Stack<Map<String, Completion>> relevantStack = new Stack<>();

	Map<String, List<Parameter>> functionParams = new TreeMap<>();

	public List<Completion> getCompletions() {
		ArrayList<Completion> completions = new ArrayList<>();
		for (Map<String, Completion> scope : relevantStack) {
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
			return true;
		} catch (RecognitionException e) {
			Logging.info("Parser unhappy, this is ok: " + e.getMessage());
			return false;
		} catch (Exception e) {
			Logging.error(
					"Bad code in completion-creation code, this is not ok.", e);
			return false;
		}
	}

	private class LuaListener extends LuaBaseListener {

		CaretInfo info;
		boolean frozen = false;
		Stack<Map<String, Completion>> scopes = new Stack<>();
		String currentFunction;

		LuaListener(CaretInfo info) {
			this.info = info;
		}

		@Override
		public void exitVar(LuaParser.VarContext ctx) {
			String varName = ctx.getText();
			scopes.peek().put(
					varName,
					Completion.newInstance(CompletionType.VARIABLE, ctx
							.getText(), ctx.getStart().getLine(), ctx
							.getStart().getCharPositionInLine()));
		}

		@Override
		public void exitFuncname(LuaParser.FuncnameContext ctx) {
			currentFunction = ctx.getText();
			scopes.peek().put(
					ctx.getText(),
					Completion.newInstance(CompletionType.FUNCTION, ctx
							.getText(), ctx.getStart().getLine(), ctx
							.getStart().getCharPositionInLine()));
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
			if (currentFunction == null) {
				// anonymous function
				try {
					Token varName = ctx.getParent().getParent().getParent()
							.getParent().getStart();
					currentFunction = varName.getText();
					Logging.info("Anonymous function hit. Redefining var '" + currentFunction + "' to a function.");
					// var declaration in parent scope needs to be replaced.
					scopes.get(scopes.size()-2).put(
							currentFunction,
							Completion.newInstance(CompletionType.FUNCTION,
									currentFunction, ctx.getStart().getLine(),
									ctx.getStart().getCharPositionInLine()));
				} catch (NullPointerException e) {
					Logging.error("Unknown type of anonymous function def.");
				}
			}
			Token stop = ctx.getStop();
			if (ctx.getChildCount() == 5) {
				if (ctx.getChild(0).getText().equals("(")) {
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
								scopes.peek()
										.put(nt.getText(),
												Completion
														.newInstance(
																CompletionType.VARIABLE,
																nt.getText(),
																ctx.getStart()
																		.getLine(),
																ctx.getStart()
																		.getCharPositionInLine()));
							}
						}
					}
				}
			}
			currentFunction = null;
			popScope(stop.getStopIndex());
		}

		private void pushScope() {
			scopes.push(new TreeMap<>());
			Logging.info("Scope depth now " + scopes.size());
		}

		private void useScope() {
			relevantStack = new Stack<>();
			relevantStack.addAll(scopes);
			Logging.info("Relevant scope identified as " + relevantStack);
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

	public List<Parameter> getFunctionParams(String functionName) {
		return functionParams.get(functionName);
	}
}
