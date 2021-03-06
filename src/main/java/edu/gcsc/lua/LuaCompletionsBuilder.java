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

import java.util.Map;
import java.util.Set;

public class LuaCompletionsBuilder {

	public void fillCompletions(LuaSyntaxAnalyzer analyzer,
			Map<LuaResource, LuaSyntaxInfo> includes, Completions completions,
			String alreadyEntered, CaretInfo caret) {
		analyzer.initCompletions(caret, includes);
		for (LuaSyntaxInfo info : includes.values()) {
			for (CompletionInfo comp : info.getCompletions()) {
				completions.addCompletion(comp, info);
			}
		}
		fillClassBasedCompletions(analyzer, alreadyEntered, completions);
	}

	protected void fillClassBasedCompletions(LuaSyntaxAnalyzer analyzer,
			String alreadyEntered, Completions completions) {
		for (String var : analyzer.getTypeMap().keySet()) {
			if (var.startsWith(alreadyEntered)
					|| alreadyEntered.startsWith(var)) {
				String type = analyzer.getTypeMap().get(var);
				Set<CompletionInfo> classMembers = analyzer
						.getClassMembers(type);
				if (!classMembers.isEmpty()) {
					for (CompletionInfo info : classMembers) {
						if (info.getType() == CompletionType.FUNCTION) {
							CompletionInfo fc = CompletionInfo
									.newFunctionInstance(
											analyzer.getResource(),
											info.getText().replace(type, var),
											info.getLine(), info.getPos(), info
													.isLocal());
							completions.addCompletion(fc, analyzer);
						}
					}
				}
			}
		}
		if (analyzer.hasClassContext()) {
			Set<CompletionInfo> classMembers = analyzer
					.getClassMembers(analyzer.getClassContext());
			if (!classMembers.isEmpty()) {
				for (CompletionInfo info : classMembers) {
					if (info.getType() == CompletionType.VARIABLE) {
						completions.addCompletion(info, analyzer);
					}
				}
			}
		}
	}

}
