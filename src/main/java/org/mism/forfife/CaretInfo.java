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

/**
 * Represents the caret position in a text field and whether text is selected etc.
 */
public class CaretInfo {
	
	private int position;
	private int line;
	private int charInLine;
	private boolean selection;
	private int selectionStart;
	private int selectionEnd;
	
	/**
	 * Not necessarily set to the proper value.
	 * @return
	 */
	public int getCharInLine() {
		return charInLine;
	}
	
	/**
	 * Not necessarily set to the proper value.
	 * @return
	 */
	public int getLine() {
		return line;
	}
	
	public int getPosition() {
		return position;
	}
	
	public int getSelectionEnd() {
		return selectionEnd;
	}
	
	public int getSelectionStart() {
		return selectionStart;
	}
	
	public boolean hasSelection()
	{
		return selection;
	}
	
	static CaretInfo newInstance(int pos, int line, int charInLine, boolean selection, int selectionStart, int selectionEnd)
	{
		if (line < 1)
			throw new IllegalArgumentException(
					"Line argument should be greater or equal to 1.");
		if (pos < 0)
			throw new IllegalArgumentException(
					"Position in line argument should be greater 0.");
		if (selectionStart < 0 || selectionStart > selectionEnd)
			throw new IllegalArgumentException(
					"Selection start should be greater or equal to 0, and less than selection end."
					);
		CaretInfo info = new CaretInfo();
		info.line = line;
		info.position = pos;
		info.charInLine = charInLine;
		info.selection = selection;
		info.selectionStart = selectionStart;
		info.selectionEnd = selectionEnd;
		return info;
	}
	
	static CaretInfo newInstance(int pos)
	{
		return newInstance(pos, 1, pos, false, 0, 0);
	}
	
	/**
	 * Default starting caret <code>= newInstance(0)</code>
	 */
	static CaretInfo HOME = newInstance(0);

}
