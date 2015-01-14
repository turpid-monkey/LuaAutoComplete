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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.JTextComponent;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.mism.forfife.res.JTextComponentResourceLoader;
import org.mism.forfife.visitors.AssignmentVisitor;
import org.mism.forfife.visitors.DoxygenVisitor;
import org.mism.forfife.visitors.FunctionVisitor;
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

	
	@Override
	protected List<Completion> getCompletionsImpl(JTextComponent comp) {
		Map<LuaResource, LuaSyntaxInfo> cache = textAreaManager
				.getAnalyzerCache(comp);
		LuaSyntaxAnalyzer analyzer = (LuaSyntaxAnalyzer) cache
				.get(new LuaResource("textArea:" + comp.hashCode()));
		String alreadyEntered = getAlreadyEnteredText(comp);
		
		List<Completion> completions = new ArrayList<Completion>();
		LuaCompletionsBuilder builder = new LuaCompletionsBuilder();
		FifeCompletions fifes = new FifeCompletions(this);
		builder.fillCompletions(analyzer, cache, fifes, alreadyEntered, getCaretInfoFor((RSyntaxTextArea) comp));
		
		super.clear();
		
		Logging.debug("Created " + completions.size() + " completions.");
		handler.validChange(analyzer.getContext());
		typeMap.clear();
		typeMap.putAll(analyzer.getTypeMap());
		
		completions.addAll(staticCompletions.getCompletions());
		completions.addAll(fifes.getCompletions());
		
		addCompletions(completions);
		
		return super.getCompletionsImpl(comp);
	}

	protected void fillCompletions(LuaSyntaxAnalyzer analyzer,
			Map<LuaResource, LuaSyntaxInfo> includes,
			List<Completion> completions, String alreadyEntered, CaretInfo info) {
		analyzer.initCompletions(info, includes);
		
	}

	protected void fillVisitors(List<LuaCompletionVisitor> visitors) {
		visitors.add(new RequireVisitor());
		visitors.add(new DoxygenVisitor());
		visitors.add(new AssignmentVisitor());
		visitors.add(new FunctionVisitor());
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
