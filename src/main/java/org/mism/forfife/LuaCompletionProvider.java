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

import javax.swing.text.JTextComponent;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.fife.ui.autocomplete.VariableCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.mism.forfife.visitors.LuaCompletionVisitor;
import org.mism.forfife.visitors.RequireVisitor;
import org.mism.forfife.visitors.UgLoadScriptVisitor;

public class LuaCompletionProvider extends DefaultCompletionProvider {
	LuaCompletionHandler handler = new LuaCompletionHandler();

	StaticLuaCompletions staticCompletions = new StaticLuaCompletions(this);

	String currentScript = "";

	Map<String, String> typeMap = new HashMap<String, String>();

	List<LuaCompletionVisitor> visitors = new ArrayList<LuaCompletionVisitor>();

	public Map<String, String> getTypeMap() {
		return typeMap;
	}

	public LuaCompletionHandler getHandler() {
		return handler;
	}

	public LuaCompletionProvider() {
		setParameterizedCompletionParams('(', ",", ')');
		setAutoActivationRules(true, ":");
		fillVisitors(visitors);
	}

	protected String i18n(Object o) {
		return o.toString();
	}

	protected List<Completion> initDynamicCompletions(
			Collection<CompletionInfo> infos,
			Map<String, List<Parameter>> functionParams) {
		List<Completion> completions = new ArrayList<>();
		for (CompletionInfo comp : infos) {
			switch (comp.getType()) {
			case FUNCTION:
				FunctionCompletion fc = new FunctionCompletion(this,
						comp.getText(), "function");
				fc.setRelevance(4000);
				List<Parameter> params = functionParams.get(comp.getText());
				fc.setParams(params);
				completions.add(fc);
				break;
			case VARIABLE:
				VariableCompletion varCompl = new VariableCompletion(this,
						comp.getText(), "variable");
				varCompl.setRelevance(9000);
				completions.add(varCompl);
				break;
			case LANGUAGE:
				throw new IllegalArgumentException("Not yet supported.");

			}
		}
		return completions;
	}

	// TODO: Pass list as reference
	// TODO: Provider param information as part of the completion info object
	protected List<Completion> initDynamicCompletions(LuaSyntaxAnalyzer analyzer) {

		return initDynamicCompletions(analyzer.getCompletions(),
				analyzer.getFunctionParams());
	}

	@Override
	public List<Completion> getCompletions(JTextComponent comp) {
		List<Completion> completions = new ArrayList<Completion>();
		fillCompletions(completions, comp.getText(),
				getCaretInfoFor((RSyntaxTextArea) comp));
		super.clear();
		Logging.debug("Created " + completions.size() + " completions.");
		addCompletions(completions);
		return super.getCompletions(comp);
	}

	protected void fillCompletions(List<Completion> completions,
			String luaScript, CaretInfo info) {
		luaScript = luaScript.trim();
		if (currentScript.equals(luaScript)) {
			return;
		}
		currentScript = luaScript;
		LuaSyntaxAnalyzer analyzer = new LuaSyntaxAnalyzer(new RequireVisitor());
		if (!analyzer.initCompletions(luaScript, info))
			return;
		handler.validChange(analyzer.getContext());
		typeMap.clear();
		typeMap.putAll(analyzer.getTypeMap());
		completions.addAll(staticCompletions.getCompletions());
		completions.addAll(initDynamicCompletions(analyzer));
	}

	protected void fillVisitors(List<LuaCompletionVisitor> visitors) {
		visitors.add(new RequireVisitor());
		// TODO move visitor to UG4LuaAutoComplete project
		visitors.add(new UgLoadScriptVisitor());
	}

	static CaretInfo getCaretInfoFor(RSyntaxTextArea textArea) {
		return CaretInfo.newInstance(textArea.getCaretPosition(),
				textArea.getCaretLineNumber() + 1,
				textArea.getCaretOffsetFromLineStart(),
				textArea.getSelectedText() != null,
				textArea.getSelectionStart(), textArea.getSelectionEnd());
	}
}
