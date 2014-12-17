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
package org.mism.forfife.res;

import java.util.HashSet;
import java.util.Set;

import javax.swing.text.JTextComponent;

import org.mism.forfife.LuaResource;
import org.mism.forfife.LuaResourceLoader;

public class JTextComponentResourceLoader implements LuaResourceLoader {

	static Set<JTextComponent> TEXT_AREAS = new HashSet<JTextComponent>();

	public static Set<JTextComponent> getTextAreas() {
		return TEXT_AREAS;
	}

	JTextComponent textArea;
	String cache;
	LuaResource res;

	@Override
	public boolean hasModifications() {
		return !getTextArea().getText().equals(cache);
	}

	@Override
	public boolean canLoad() {
		return res.getResourceLink().startsWith("textArea:");
	}

	@Override
	public String load() throws Exception {
		return cache = getTextArea().getText();
	}

	@Override
	public void setResource(LuaResource resource) {
		res = resource;
	}
	
	@Override
	public LuaResource getResource() {
		return res;
	}
	
	
	private JTextComponent getTextArea()
	{
		return TEXT_AREAS.iterator().next();
	}
	
}
