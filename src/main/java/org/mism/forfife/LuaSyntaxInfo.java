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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.mism.forfife.lua.LuaParser;

public class LuaSyntaxInfo {

	protected LuaResource resource;
	protected LuaResourceLoader loader;
	protected LuaResourceLoaderFactory factory;

	protected LuaSyntaxInfo parent;
	protected LuaParser.ChunkContext context;
	protected int endIdx;

	protected Stack<Map<String, CompletionInfo>> relevantStack = new Stack<Map<String, CompletionInfo>>();
	protected Map<String, List<Parameter>> functionParams = new TreeMap<String, List<Parameter>>();
	protected Map<String, String> typeMap = new HashMap<String, String>();
	protected Map<String, String> doxyGenMap = new HashMap<String, String>();

	protected Set<LuaResource> includedResources = new HashSet<LuaResource>();

	public void setResourceLoaderFactory(LuaResourceLoaderFactory factory) {
		this.factory = factory;
	}

	public LuaResourceLoaderFactory getResourceLoaderFactory() {
		return factory;
	}

	public String getLuaScript() throws Exception {
		return loader.load();
	}

	public void setResource(LuaResource res) throws Exception {
		this.resource = res;
		loader = factory.createLoader(res);
	}

	public Map<String, String> getDoxyGenMap() {
		return doxyGenMap;
	}

	public void setParent(LuaSyntaxInfo parent) {
		this.parent = parent;
	}

	public LuaSyntaxInfo getParent() {
		return parent;
	}

	public LuaParser.ChunkContext getContext() {
		return context;
	}

	public void setContext(LuaParser.ChunkContext context) {
		this.context = context;
	}

	public int getEndIdx() {
		return endIdx;
	}

	public void setEndIdx(int endIdx) {
		this.endIdx = endIdx;
	}

	public Stack<Map<String, CompletionInfo>> getRelevantStack() {
		return relevantStack;
	}

	public void setRelevantStack(
			Stack<Map<String, CompletionInfo>> relevantStack) {
		this.relevantStack = relevantStack;
	}

	public Map<String, List<Parameter>> getFunctionParams() {
		return functionParams;
	}

	public void setFunctionParams(Map<String, List<Parameter>> functionParams) {
		this.functionParams = functionParams;
	}

	public List<Parameter> getFunctionParams(String functionName) {
		return functionParams.get(functionName);
	}

	public Map<String, String> getTypeMap() {
		return typeMap;
	}

	public void setTypeMap(Map<String, String> typeMap) {
		this.typeMap = typeMap;
	}

	public Set<LuaResource> getIncludedResources() {
		return includedResources;
	}

	public void setIncludedResources(Set<LuaResource> includedResources) {
		this.includedResources = includedResources;
	}

	/**
	 * @return a copy of the values in the current state of the stack.
	 */
	public Collection<CompletionInfo> getCompletions() {
		Map<String, CompletionInfo> map = new HashMap<String, CompletionInfo>();
		for (Map<String, CompletionInfo> scope : relevantStack) {
			for (CompletionInfo c : scope.values()) {
				if (!map.containsKey((c.getText())))
					map.put(c.getText(), c);
			}
		}
		return map.values();
	}

	public LuaResource getResource() {
		return resource;
	}
}
