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

class CompletionInfo {
	CompletionType type;
	String text;
	String descr;
	int line;
	int pos;
	boolean local;

	public boolean isLocal() {
		return local;
	}

	public int getLine() {
		return line;
	}

	public int getPos() {
		return pos;
	}

	public String getText() {
		return text;
	}

	public CompletionType getType() {
		return type;
	}

	public String getDescr() {
		return descr;
	}

	static CompletionInfo newInstance(CompletionType type, String text,
			int line, int pos) {
		return newInstance(type, text, line, pos, false);
	}

	static CompletionInfo newInstance(CompletionType type, String text,
			int line, int pos, boolean local) {
		return newInstance(type, text, null, line, pos, local);
	}

	static CompletionInfo newInstance(CompletionType type, String text,
			String descr, int line, int pos, boolean local) {
		CompletionInfo c = new CompletionInfo();
		c.type = type;
		c.text = text;
		c.line = line;
		c.pos = pos;
		c.local = local;
		c.descr = descr;
		return c;
	}

	static CompletionInfo newVariableInstance(String text, int line, int pos,
			boolean local) {
		return newInstance(CompletionType.VARIABLE, text, line, pos, local);
	}

	static CompletionInfo newFunctionInstance(String txt, int line, int pos,
			boolean local) {
		return newInstance(CompletionType.FUNCTION, txt, line, pos, local);
	}

	static CompletionInfo newKeyWordInstance(String txt, String descr) {
		return newInstance(CompletionType.LANGUAGE, txt, 0, 0, false);
	}

	public String toString() {
		return getType().name() + ":" + getText();
	}
}