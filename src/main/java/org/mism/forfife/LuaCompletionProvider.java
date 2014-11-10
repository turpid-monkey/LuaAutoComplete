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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.autocomplete.VariableCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public class LuaCompletionProvider extends DefaultCompletionProvider {
	LuaCompletionHandler handler = new LuaCompletionHandler();

	static String[] LUA_KEY_WORDS = { "and", "break", "do", "else", "elseif",
			"end", "false", "for", "function", "if", "in", "local", "nil",
			"not", "or", "repeat", "return", "then", "true", "until", "while" };

	static String[][] LUA_SHORTCUTS = {
			{ "for i", "for i=1 to 10 do\n\nend\n" },
			{ "for j", "for j=1 to 10 do\n\nend\n" } };

	String currentScript = "";

	public LuaCompletionHandler getHandler() {
		return handler;
	}

	public LuaCompletionProvider() {
		// super.comparator = new LuaCompletionComparator();
		initStaticCompletions();
		setParameterizedCompletionParams('(', ",", ')');
	}

	protected void initStaticCompletions() {
		for (String keyWord : LUA_KEY_WORDS) {
			checkProviderAndAdd(new BasicCompletion(this, keyWord, "LUA keyword."));
		}
		for (String[] shortCut : LUA_SHORTCUTS) {
			checkProviderAndAdd(new ShorthandCompletion(this, shortCut[0],
					shortCut[1]));
		}
	}

	protected String i18n(Object o) {
		return o.toString();
	}

	protected void initDynamicCompletions(LuaSyntaxAnalyzer analyzer) {
		List<Completion> completions = new ArrayList<>();
		for (LuaSyntaxAnalyzer.Completion comp : analyzer.getCompletions()) {
			switch (comp.getType()) {
			case FUNCTION:
				FunctionCompletion fc = new FunctionCompletion(this, comp.getText(), "function");
				List<Parameter> params = analyzer.getFunctionParams(comp.getText());
				fc.setParams(params);
				completions.add(fc);
				break;
			case VARIABLE:
				completions.add(new VariableCompletion(this, comp.getText(), "variable"));

			}
		}
		addCompletions(completions);
	}

	void refreshCompletions(String luaScript, CaretInfo info) {
		luaScript = luaScript.trim();
		if (currentScript.equals(luaScript)) {
			return;
		}
		currentScript = luaScript;
		LuaSyntaxAnalyzer analyzer = new LuaSyntaxAnalyzer();
		if (!analyzer.initCompletions(luaScript, info))
			return;
		handler.validChange(analyzer.getContext());
		super.clear();
		initStaticCompletions();
		initDynamicCompletions(analyzer);
    }

	public void listenTo(RSyntaxTextArea textArea) {
		refreshCompletions(textArea.getText(), getCaretInfoFor(textArea));
		textArea.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				String curr = textArea.getText();
				CaretInfo info = getCaretInfoFor(textArea);
				refreshCompletions(curr, info);
			}
		});
	}
	
	static CaretInfo getCaretInfoFor(RSyntaxTextArea textArea)
	{
		return CaretInfo.newInstance(textArea.getCaretPosition(), textArea.getCaretLineNumber(), textArea.getCaretOffsetFromLineStart(), textArea.getSelectedText()!=null, textArea.getSelectionStart(), textArea.getSelectionEnd());
	}
}
