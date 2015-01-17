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
package edu.gcsc.lua.visitors;

import static edu.gcsc.lua.LuaParseTreeUtil.start;

import java.util.HashSet;

import edu.gcsc.lua.LuaParseTreeUtil;
import edu.gcsc.lua.grammar.LuaParser;
import edu.gcsc.lua.grammar.LuaParser.PrefixexpContext;
import edu.gcsc.lua.grammar.LuaParser.StatContext;

public class AssignmentVisitor extends LuaCompletionVisitor {

	@Override
	public Void visitPrefixexp(PrefixexpContext ctx) {
		StatContext statCtx = LuaParseTreeUtil.getParentRuleContext(ctx,
				LuaParser.RULE_stat, StatContext.class);
		if (statCtx.getChildCount() == 3) {
			if (statCtx.getChild(1).getText().equals("=")) {
				info.getTypeMap().put(start(statCtx), start(ctx));
				//Logging.debug("Found a possible type in line " + line(ctx)
				//		+ ": var " + start(statCtx) + " = " + start(ctx));
			}
			if (statCtx.getChild(2).getText().endsWith("or{}"))
			{
				//hit
				String var = start(statCtx);
				if (!info.getTables().containsKey(var))
				{
					info.getTables().put(start(statCtx), new HashSet<String>());
				}
			}
		}
		return null;
	}

}
