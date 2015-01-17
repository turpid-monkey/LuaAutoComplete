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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.JTextComponent;

import edu.gcsc.lua.visitors.LuaCompletionVisitor;

public class TextAreaManager {
	private Map<LuaResource, JTextComponent> resourceMap = new HashMap<LuaResource, JTextComponent>();
	private Map<JTextComponent, Map<LuaResource, LuaSyntaxInfo>> analyzerCache = new HashMap<JTextComponent, Map<LuaResource, LuaSyntaxInfo>>();

	public Map<LuaResource, LuaSyntaxInfo> getAnalyzerCache(JTextComponent c) {
		if (analyzerCache.containsKey(c)) {
			return analyzerCache.get(c);
		}
		Map<LuaResource, LuaSyntaxInfo> includes = new HashMap<LuaResource, LuaSyntaxInfo>();
		analyzerCache.put(c, includes);

		LuaSyntaxAnalyzer analyzer = new LuaSyntaxAnalyzer();
		
		analyzer.setVisitors(getVisitors());
		analyzer.setResourceLoaderFactory(getFactory());
		LuaResource res = new LuaResource("textArea:" + c.hashCode());
		includes.put(res, analyzer);
		resourceMap.put(res, c);
		try {
			analyzer.setResource(res);

		} catch (Exception e) {
			Logging.error("Could not instantiate root syntax analyzer", e);
		}
		return includes;
	}

	public JTextComponent getTextArea(LuaResource res) {
		return resourceMap.get(res);
	}

	public List<LuaCompletionVisitor> getVisitors() {
		return visitors;
	}

	public void setVisitors(List<LuaCompletionVisitor> visitors) {
		this.visitors = visitors;
	}

	public LuaResourceLoaderFactory getFactory() {
		return factory;
	}

	public void setFactory(LuaResourceLoaderFactory factory) {
		this.factory = factory;
	}

	private List<LuaCompletionVisitor> visitors = new ArrayList<LuaCompletionVisitor>();
	private LuaResourceLoaderFactory factory = new LuaResourceLoaderFactory();

}