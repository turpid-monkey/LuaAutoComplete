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
package org.mism.forfife.visitors;

import static org.mism.forfife.LuaParseTreeUtil.getChildRuleContextRecursive;
import static org.mism.forfife.LuaParseTreeUtil.parse;
import static org.mism.forfife.LuaParseTreeUtil.txt;

import java.io.BufferedReader;
import java.io.StringReader;

import org.mism.forfife.lua.LuaParser;
import org.mism.forfife.lua.LuaParser.ChunkContext;
import org.mism.forfife.lua.LuaParser.FuncnameContext;

public class DoxygenVisitor extends LuaCompletionVisitor {

	@Override
	public Void visitChunk(ChunkContext ctx) {
		String script = info.getLuaScript();
		BufferedReader in = new BufferedReader(new StringReader(script));
		try {
			String line;
			StringBuffer doxyBuffer = new StringBuffer();
			while ((line = in.readLine()) != null) {
				if (line.startsWith("--!")) {
					doxyBuffer.append(fixMarkup(line.substring(4).trim()));
					doxyBuffer.append("<br>");
				} else {
					if (doxyBuffer.length() == 0)
						continue;
					ChunkContext lineCtx = parse(line);
					FuncnameContext fCtx = getChildRuleContextRecursive(
							lineCtx, FuncnameContext.class,
							LuaParser.RULE_block, LuaParser.RULE_stat,
							LuaParser.RULE_funcname);
					if (fCtx != null) {
						info.getDoxyGenMap().put(txt(fCtx),
								doxyBuffer.toString());
					}
					doxyBuffer.setLength(0);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return null;
	}

	static String fixMarkup(String in) {
		return in.replaceAll("@param", "<b>param</b>")
				.replaceAll("@return", "<b>return</b>")
				.replaceAll("\\\\param", "<b>param</b>")
				.replaceAll("\\\\return", "<b>return</b>")
				.replaceAll("\\\\code", "<CODE>")
				.replaceAll("\\\\endcode", "</CODE>")
				.replaceAll("@code", "<CODE>")
				.replaceAll("@endcode", "</CODE>")
				.replaceAll("\\\\sa", "<b>see also</b>")
				.replaceAll("@sa", "<b>see also</b>");

	}
}
