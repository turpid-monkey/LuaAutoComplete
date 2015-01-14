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
import java.util.List;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.VariableCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;

public class FifeCompletions implements Completions {

	private List<Completion> completions = new ArrayList<Completion>();
	private LuaCompletionProvider provider;

	public FifeCompletions(LuaCompletionProvider provider) {
		this.provider = provider;
	}
	
	public List<Completion> getCompletions() {
		return completions;
	}

	@Override
	public void addCompletion(CompletionInfo info, LuaSyntaxInfo syntaxInfo) {
		switch (info.getType()) {
		case FUNCTION:
			addFunctionCompletion(info, syntaxInfo);
			break;
		case VARIABLE:
			addVariableCompletion(info, syntaxInfo);
			break;
		default:
			throw new IllegalArgumentException("Completion not supported.");

		}
	}

	protected void addFunctionCompletion(CompletionInfo info,
			LuaSyntaxInfo syntaxInfo) {
		FunctionCompletion fc = new FunctionCompletion(provider,
				info.getText(), "function");
		fc.setRelevance(4000);
		List<FunctionParameter> fparams = syntaxInfo.getFunctionParams(info
				.getText());
		List<Parameter> params = new ArrayList<Parameter>();
		if (fparams != null)
		{
		for (FunctionParameter parm : fparams) {
			params.add(new Parameter(null, parm.getParamName()));
		}
		fc.setParams(params);
		} else {
			Logging.debug("No parameter info found for function '" + info.getText() + "'");
		}

		StringBuffer shortDescr = new StringBuffer();
		if (syntaxInfo.getDoxyGenMap().containsKey(info.getText())) {
			shortDescr.append(syntaxInfo.getDoxyGenMap().get(info.getText()));
		}
		if (info.getResource() != null
				&& !info.getResource().getResourceLink().startsWith("textArea")) {
			shortDescr.append("<p>included from "
					+ info.getResource().getResourceLink() + ", line "
					+ info.getLine());
		} else {
			shortDescr.append("<p>from line " + info.getLine());
		}

		fc.setShortDescription(shortDescr.toString());
		fc.setIcon(IconLib.instance().getFunctionIcon());
		completions.add(fc);

	}

	protected void addVariableCompletion(CompletionInfo info,
			LuaSyntaxInfo syntaxInfo) {
		if (syntaxInfo.getTables().containsKey(info.getText())) {
			// ignore this variable
			return;
		}
		VariableCompletion varCompl = new VariableCompletion(provider,
				info.getText(), "variable");
		varCompl.setRelevance(9000);
		StringBuffer summary = new StringBuffer();
		if (info.getResource() != null
				&& !info.getResource().getResourceLink().startsWith("textArea")) {
			summary.append("<p>included from "
					+ info.getResource().getResourceLink() + ", line "
					+ info.getLine());
		} else {
			summary.append("<p>from line " + info.getLine());
		}
		varCompl.setShortDescription(summary.toString());
		varCompl.setIcon(IconLib.instance().getVariableIcon());
		completions.add(varCompl);
	}

}
