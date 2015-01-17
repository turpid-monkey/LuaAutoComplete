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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;

import edu.gcsc.lua.LuaParseTreeUtil;
import edu.gcsc.lua.grammar.LuaParser;
import edu.gcsc.lua.grammar.LuaParser.FieldContext;
import edu.gcsc.lua.grammar.LuaParser.FieldlistContext;
import edu.gcsc.lua.grammar.LuaParser.StatContext;
import edu.gcsc.lua.grammar.LuaParser.TableconstructorContext;

@Deprecated
public class TableConstructorVisitor extends LuaCompletionVisitor {

	@Override
	public Void visitTableconstructor(TableconstructorContext ctx) {
		StatContext parent = LuaParseTreeUtil.getParentStatContext(ctx);

		String tableName = LuaParseTreeUtil.start(parent);

		FieldlistContext fl = LuaParseTreeUtil.getChildRuleContext(ctx,
				LuaParser.RULE_fieldlist, FieldlistContext.class);
		List<String> entries = new ArrayList<String>();
		for (ParseTree rctx : fl.children) {
			if (rctx instanceof FieldContext) {
				entries.add(LuaParseTreeUtil.start((FieldContext) rctx));
			}
		}

		for (String entry : entries) {
			addTable(tableName, entry);
		}
		return super.visitTableconstructor(ctx);
	}

	void addTable(String tableName, String entry) {
		if (!info.getTables().containsKey(tableName)) {
			info.getTables().put(tableName, new HashSet<String>());
		}
		info.getTables().get(tableName).add(entry);
	}

}
