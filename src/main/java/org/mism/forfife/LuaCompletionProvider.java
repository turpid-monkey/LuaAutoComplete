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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.text.JTextComponent;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.fife.ui.autocomplete.VariableCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.mism.forfife.res.JTextComponentResourceLoader;
import org.mism.forfife.visitors.AssignmentVisitor;
import org.mism.forfife.visitors.DoxygenVisitor;
import org.mism.forfife.visitors.LuaCompletionVisitor;
import org.mism.forfife.visitors.RequireVisitor;

public class LuaCompletionProvider extends DefaultCompletionProvider {
	LuaCompletionHandler handler = new LuaCompletionHandler();

	StaticLuaCompletions staticCompletions = new StaticLuaCompletions(this);

	TextAreaManager textAreaManager = new TextAreaManager();

	Map<String, String> typeMap = new HashMap<String, String>();

	public Map<String, String> getTypeMap() {
		return typeMap;
	}

	public LuaCompletionHandler getHandler() {
		return handler;
	}

	@Override
	protected boolean isValidChar(char ch) {
		return super.isValidChar(ch) || ch == ':' || ch == '.';
	}

	public LuaCompletionProvider() {
		setParameterizedCompletionParams('(', ",", ')');
		setAutoActivationRules(false, ":");
		fillVisitors(textAreaManager.getVisitors());
		fillResourceLoaders(textAreaManager.getFactory().getLoaders());
		JTextComponentResourceLoader.setTextAreaManager(textAreaManager);
	}

	protected String i18n(Object o) {
		return o.toString();
	}

	protected List<Completion> initDynamicCompletions(
			Collection<CompletionInfo> infos,
			Map<String, List<Parameter>> functionParams,
			Map<String, String> functionDescr) {
		List<Completion> completions = new ArrayList<Completion>();
		for (CompletionInfo comp : infos) {
			switch (comp.getType()) {
			case FUNCTION:
				FunctionCompletion fc = new FunctionCompletion(this,
						comp.getText(), "function");
				fc.setRelevance(4000);
				List<Parameter> params = functionParams.get(comp.getText());
				fc.setParams(params);

				StringBuffer shortDescr = new StringBuffer();
				if (functionDescr.containsKey(comp.getText())) {
					shortDescr.append(functionDescr.get(comp.getText()));
				}
				if (comp.getResource() != null
						&& !comp.getResource().getResourceLink()
								.startsWith("textArea")) {
					shortDescr.append("<p>included from "
							+ comp.getResource().getResourceLink() + ", line "
							+ comp.getLine());
				} else {
					shortDescr.append("<p>from line " + comp.getLine());
				}

				fc.setShortDescription(shortDescr.toString());
				fc.setIcon(IconLib.instance().getFunctionIcon());
				completions.add(fc);
				break;
			case VARIABLE:
				VariableCompletion varCompl = new VariableCompletion(this,
						comp.getText(), "variable");
				varCompl.setRelevance(9000);
				StringBuffer summary = new StringBuffer();
				if (comp.getResource() != null
						&& !comp.getResource().getResourceLink()
								.startsWith("textArea")) {
					summary.append("<p>included from "
							+ comp.getResource().getResourceLink() + ", line "
							+ comp.getLine());
				} else {
					summary.append("<p>from line " + comp.getLine());
				}
				varCompl.setShortDescription(summary.toString());
				varCompl.setIcon(IconLib.instance().getVariableIcon());
				completions.add(varCompl);
				break;
			case LANGUAGE:
				throw new IllegalArgumentException("Not yet supported.");

			}
		}
		return completions;
	}

	protected void fillDynamicCompletions(List<Completion> completions,
			Map<LuaResource, LuaSyntaxInfo> luaFiles) {
		for (LuaSyntaxInfo info : luaFiles.values()) {
			completions.addAll(initDynamicCompletions(info.getCompletions(),
					info.getFunctionParams(), info.getDoxyGenMap()));
		}
	}

	@Override
	protected List<Completion> getCompletionsImpl(JTextComponent comp) {
		Map<LuaResource, LuaSyntaxInfo> cache = textAreaManager
				.getAnalyzerCache(comp);
		LuaSyntaxAnalyzer analyzer = (LuaSyntaxAnalyzer) cache.get(new LuaResource("textArea:" + comp.hashCode()));
		String alreadyEntered = getAlreadyEnteredText(comp);
		List<Completion> completions = new ArrayList<Completion>();
		fillCompletions(analyzer, cache, completions, alreadyEntered,
				getCaretInfoFor((RSyntaxTextArea) comp));
		super.clear();
		Logging.debug("Created " + completions.size() + " completions.");
		addCompletions(completions);
		return super.getCompletionsImpl(comp);
	}

	protected void fillCompletions(LuaSyntaxAnalyzer analyzer,
			Map<LuaResource, LuaSyntaxInfo> includes,
			List<Completion> completions, String alreadyEntered, CaretInfo info) {
		analyzer.initCompletions(info, includes);
		handler.validChange(analyzer.getContext());
		typeMap.clear();
		typeMap.putAll(analyzer.getTypeMap());
		completions.addAll(staticCompletions.getCompletions());
		fillDynamicCompletions(completions, includes);
	}

	protected void fillVisitors(List<LuaCompletionVisitor> visitors) {
		visitors.add(new RequireVisitor());
		visitors.add(new DoxygenVisitor());
		visitors.add(new AssignmentVisitor());
	}

	protected void fillResourceLoaders(
			List<Class<? extends LuaResourceLoader>> loaders) {
		loaders.add(JTextComponentResourceLoader.class);
	}

	static CaretInfo getCaretInfoFor(RSyntaxTextArea textArea) {
		return CaretInfo.newInstance(textArea.getCaretPosition(),
				textArea.getCaretLineNumber() + 1,
				textArea.getCaretOffsetFromLineStart(),
				textArea.getSelectedText() != null,
				textArea.getSelectionStart(), textArea.getSelectionEnd());
	}
}
