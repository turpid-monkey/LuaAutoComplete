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
import static org.mism.forfife.LuaParseTreeUtil.getParentStatContext;
import static org.mism.forfife.LuaParseTreeUtil.hasParentRuleContext;
import static org.mism.forfife.LuaParseTreeUtil.line;
import static org.mism.forfife.LuaParseTreeUtil.next;
import static org.mism.forfife.LuaParseTreeUtil.start;
import static org.mism.forfife.LuaParseTreeUtil.txt;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.mism.forfife.lua.LuaBaseListener;
import org.mism.forfife.lua.LuaLexer;
import org.mism.forfife.lua.LuaParser;
import org.mism.forfife.lua.LuaParser.BlockContext;
import org.mism.forfife.lua.LuaParser.FieldContext;
import org.mism.forfife.lua.LuaParser.FieldlistContext;
import org.mism.forfife.lua.LuaParser.FuncbodyContext;
import org.mism.forfife.lua.LuaParser.NamelistContext;
import org.mism.forfife.lua.LuaParser.StatContext;
import org.mism.forfife.lua.LuaParser.TableconstructorContext;
import org.mism.forfife.visitors.LuaCompletionVisitor;

/**
 * Parses a script and tries to collect all relevant data for proposals.
 */
public class LuaSyntaxAnalyzer extends LuaSyntaxInfo {

	boolean ok;
	int lastPosition;

	List<LuaCompletionVisitor> visitors = Collections.emptyList();

	public void setVisitors(List<LuaCompletionVisitor> visitors) {
		this.visitors = visitors;
	}

	public List<LuaCompletionVisitor> getVisitors() {
		return visitors;
	}

	public LuaSyntaxAnalyzer() {
		// root
	}

	public LuaSyntaxAnalyzer(LuaSyntaxAnalyzer parent, LuaResource res)
			throws Exception {
		setParent(parent);
		setVisitors(parent.visitors);
		setResourceLoaderFactory(parent.factory);
		setResource(res);
	}

	/**
	 * @param luaScript
	 * @return whether the parsing went well
	 */
	public boolean initCompletions(CaretInfo info,
			Map<LuaResource, LuaSyntaxInfo> includes) {
		try {
			if (loader.hasModifications() || !ok
					|| info.getPosition() != lastPosition) {
				clear();
				lastPosition = info.getPosition();
				String luaScript = getLuaScript();
				endIdx = luaScript.trim().length() - 1;
				ANTLRInputStream str = new ANTLRInputStream(new StringReader(
						luaScript));
				Lexer lx = new LuaLexer(str);
				CommonTokenStream tokStr = new CommonTokenStream(lx);
				LuaParser parser = new LuaParser(tokStr);
				parser.addParseListener(new LuaListener(info));
				context = parser.chunk();
				for (LuaCompletionVisitor visitor : visitors) {
					visitor.setInfo(this);
					context.accept(visitor);
				}
			}
			ok = true;
		} catch (RecognitionException e) {
			Logging.debug("Parser unhappy with current script state. This is ok."
					+ e.getMessage());
			ok = false;
		} catch (Exception e) {
			Logging.error(
					"Bad code in completion-creation code, this is not ok.", e);
			ok = false;
		}
		for (LuaResource res : includedResources) {
			if (res.equals(resource))
				continue;
			LuaSyntaxAnalyzer nested;
			if (includes.containsKey(res)) {
				nested = (LuaSyntaxAnalyzer) includes.get(res);
				nested.initCompletions(CaretInfo.HOME, includes);
			} else {
				try {
					Logging.debug("Loading included file "
							+ res.getResourceLink());
					nested = new LuaSyntaxAnalyzer(this, res);
					includes.put(res, nested);
					nested.initCompletions(CaretInfo.HOME, includes);
				} catch (Exception e) {
					Logging.error(
							"Could not load resource " + res.getResourceLink(),
							e);
				}
			}
		}
		return ok;
	}

	private void clear() {
		getClasses().clear();
		getFunctionParams().clear();
		getTypeMap().clear();
	}

	private class LuaListener extends LuaBaseListener {

		private static final String COLON = ":";
		private static final String SELF = "self";
		private static final String LEFT_BRACKET = "(";
		private static final String FUNCTION = "function";
		private static final String LOCAL = "local";
		private static final String FOR = "for";
		private static final String IF = "if";

		CaretInfo info;
		boolean frozen = false;
		Stack<Map<String, CompletionInfo>> scopes = new Stack<Map<String, CompletionInfo>>();
		Map<String, CompletionInfo> global = new HashMap<String, CompletionInfo>();

		LuaListener(CaretInfo info) {
			this.info = info;
		}

		boolean isMemberFunction(String funcName) {
			return funcName.contains(COLON);
		}

		String extractClassName(String funcName) {
			return funcName.substring(0, funcName.indexOf(COLON));
		}

		boolean isDeclaredLocal(String name) {
			if (name.contains(".")) {
				name = name.substring(0, name.indexOf("."));
			}
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

		Map<String, CompletionInfo> findScope(String name) {
			if (name.contains(".")) {
				name = name.substring(0, name.indexOf("."));
			}
			if (global.containsKey(name))
				return global;
			for (int i = 0; i < scopes.size(); i++) {
				if (scopes.get(i).containsKey(name)) {
					return scopes.get(i);
				}
			}
			return null;
		}

		void addVariable(String name, ParserRuleContext ctx, boolean local) {
			CompletionInfo varInfo = CompletionInfo.newVariableInstance(
					LuaSyntaxAnalyzer.this.getResource(), name, line(ctx),
					col(ctx), local || isDeclaredLocal(name));
			if (local) {
				scopes.peek().put(name, varInfo);
			} else if (isDeclaredLocal(name)) {
				findScope(name).put(name, varInfo);
			} else {
				global.put(name, varInfo);
			}
			if (isClassMember(name)) {
				StatContext statCtx = getParentStatContext(ctx);
				if (statCtx != null
						&& (statCtx = getParentStatContext(statCtx)) != null) {
					String functionName = next(statCtx);
					if (isMemberFunction(functionName)) {
						String className = extractClassName(functionName);
						Logging.debug("Found member var " + name
								+ " for class '" + className + "'");
						getClassMembers(className).add(varInfo);
					}
				}

			}
		}

		void addFunction(String name, ParserRuleContext ctx, boolean local) {
			CompletionInfo funcInfo = CompletionInfo.newFunctionInstance(
					LuaSyntaxAnalyzer.this.getResource(), name, line(ctx),
					col(ctx), local);
			if (local) {
				scopes.peek().put(name, funcInfo);
			} else {
				if (isMemberFunction(name)) {
					String className = extractClassName(name);
					getClassMembers(className).add(funcInfo);
				}
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
		public void exitTableconstructor(TableconstructorContext ctx) {
			StatContext parent = LuaParseTreeUtil.getParentStatContext(ctx);
			boolean local = false;
			String tableName;
			if (LOCAL.equals(start(parent))) {
				tableName = next(parent);
				local = true;
			} else {
				tableName = start(parent);
			}
			// inline declaration of a table in a for/if block, do nothing
			if (FOR.equals(tableName) || IF.equals(tableName))
				return;

			FieldlistContext fl = LuaParseTreeUtil.getChildRuleContext(ctx,
					LuaParser.RULE_fieldlist, FieldlistContext.class);
			List<String> entries = new ArrayList<String>();
			if (fl != null) {
				for (ParseTree rctx : fl.children) {
					if (rctx instanceof FieldContext) {
						if (((FieldContext) rctx).getChild(1) instanceof TerminalNode) {
							entries.add(LuaParseTreeUtil
									.start((FieldContext) rctx));
						}
					}
				}
			}

			// Save info about tables
			if (!local) {
				Set<String> set;
				if (!getTables().containsKey(tableName)) {
					getTables().put(tableName, new HashSet<String>());
				}
				set = getTables().get(tableName);

				for (String entry : entries)
					set.add(entry);
			}
			// Add variable completion
			for (String entry : entries) {
				addVariable(tableName + "." + entry, ctx, local);
			}
		}

		@Override
		public void exitVar(LuaParser.VarContext ctx) {
			String varName = txt(ctx);

			// if it is a subrule of prefixExp, it might as well be a function
			// call
			if (!hasParentRuleContext(ctx, LuaParser.RULE_prefixexp)
					&& !hasParentRuleContext(ctx, LuaParser.RULE_functioncall)) {
				LuaParser.StatContext statCtx = getParentStatContext(ctx);
				boolean local = statCtx != null && start(statCtx).equals(LOCAL)
						|| isClassMember(varName);
				addVariable(varName, ctx, local);
			}
		}

		@Override
		public void exitFuncname(LuaParser.FuncnameContext ctx) {
			String funcName = txt(ctx);
			addFunction(funcName, ctx, false);
			checkStartClassContext(funcName);
		}

		@Override
		public void enterStat(StatContext ctx) {
			if (LOCAL.equals(start(ctx)))
				return;
			pushScope(ctx);
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

			if (FOR.equals(startText)) {
				ParseTree t = ctx.getChild(1);
				if (t.getChildCount() == 0) // single var decl
				{
					addVariable(next(ctx), ctx, true);
				} // else should be a namelist => handled by exitNamelist()
			} else if (LOCAL.equals(startText)) {
				if (FUNCTION.equals(next(ctx))) {
					String localFunction = ctx.getChild(2).getText();
					addFunction(localFunction, ctx, true);
				}
			}
			if (LOCAL.equals(startText))
				return;
			popScope(ctx.getStart().getStartIndex(), ctx.getStop()
					.getStopIndex());
		}

		void checkEndClassContext(String funcName) {
			if (frozen)
				return;
			if (isMemberFunction(funcName)) {
				classContext = null;
			}
		}

		void checkStartClassContext(String funcName) {
			if (frozen)
				return;
			if (isMemberFunction(funcName)) {
				classContext = extractClassName(funcName);
			}
		}

		boolean isClassMember(String variable) {
			return variable.startsWith(SELF);
		}

		@Override
		public void exitFuncbody(FuncbodyContext ctx) {
			StatContext statCtx = getParentStatContext(ctx);
			String startText = statCtx.getStart().getText();
			String currentFunction;
			boolean anonFunction = false;
			if (FUNCTION.equals(startText)) {
				currentFunction = next(statCtx);
			} else if (LOCAL.equals(startText)) {
				currentFunction = statCtx.getChild(2).getText();
			} else {
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
							.newFunctionInstance(
									LuaSyntaxAnalyzer.this.getResource(),
									currentFunction, line(ctx), col(ctx), false));
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
							List<FunctionParameter> params = new ArrayList<FunctionParameter>();
							functionParams.put(currentFunction, params);
							for (int i = 0; i < childCount; i += 2) {
								ParseTree nt = t.getChild(i);
								params.add(new FunctionParameter(nt.getText()));
								addVariable(nt.getText(), ctx, true);
							}
						}
					}
				}
			}
			popScope(ctx.getStart().getStopIndex(), stop.getStopIndex());
			checkEndClassContext(currentFunction);
		}

		private void pushScope(ParserRuleContext ctx) {
			scopes.push(new TreeMap<String, CompletionInfo>());
			// Logging.debug("Scope depth in line " + line(ctx) + ", col " +
			// col(ctx) + " now " + scopes.size());
		}

		private void useScope() {
			relevantStack = new Stack<Map<String, CompletionInfo>>();
			relevantStack.add(global);
			relevantStack.addAll(scopes);
			Logging.debug("Relevant scope identified as " + relevantStack);
			frozen = true;
		}

		private void popScope(int startOffset, int endOffset) {
			// caret offset lies in this block
			if (!frozen && endOffset >= info.getPosition()
					&& startOffset <= info.getPosition()) {
				useScope();
			}
			// we are about to pop the last scope without having found
			// the proper one - this should only be the case when
			// the caret pos is beyond the end of the block, or 0
			if (!frozen && scopes.size() == 1) {
				useScope();
			}
			// Logging.info("Scope popped: " + scopes.peek());
			scopes.pop();
		}

		@Override
		public void exitBlock(BlockContext ctx) {
			Token stop = ctx.getStop();
			int startOffset;
			Token start = ctx.getStart();
			if (LOCAL.equals(start.getText())) {
				startOffset = start.getStartIndex();
			} else {
				startOffset = start.getStopIndex();
			}
			popScope(startOffset, (stop != null ? stop.getStopIndex() : endIdx));
		}
	}
}
