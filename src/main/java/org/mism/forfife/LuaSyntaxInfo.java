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

	protected String luaScript;
	protected LuaParser.ChunkContext context;
	protected int endIdx;

	protected Stack<Map<String, CompletionInfo>> relevantStack = new Stack<>();
	protected Map<String, List<Parameter>> functionParams = new TreeMap<>();
	protected Map<String, String> typeMap = new HashMap<String, String>();

	protected Set<LuaResource> dependentResources = new HashSet<>();

	public String getLuaScript() {
		return luaScript;
	}

	public void setLuaScript(String luaScript) {
		this.luaScript = luaScript;
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

	public Set<LuaResource> getDependentResources() {
		return dependentResources;
	}

	public void setDependentResources(Set<LuaResource> dependentResources) {
		this.dependentResources = dependentResources;
	}

}
