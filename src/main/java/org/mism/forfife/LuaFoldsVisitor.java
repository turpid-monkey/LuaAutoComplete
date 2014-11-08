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

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rsyntaxtextarea.folding.FoldType;
import org.mism.forfife.lua.LuaBaseVisitor;
import org.mism.forfife.lua.LuaParser.BlockContext;

class LuaFoldsVisitor extends LuaBaseVisitor<Void> {
	private final List<Fold> folds = new ArrayList<Fold>();
	private final RSyntaxTextArea textArea;

	public LuaFoldsVisitor(RSyntaxTextArea textArea) {
		this.textArea = textArea;
	}

	public List<Fold> getFolds() {
		return folds;
	}

	@Override
	public Void visitBlock(BlockContext ctx) {
		int offset;
		try {
			offset = textArea.getLineStartOffset(ctx.getParent().getStart()
					.getLine() - 1)
					+ ctx.getParent().getStart().getCharPositionInLine();
			if (offset != 0) {
				int endOffset = textArea.getLineStartOffset(ctx.getParent()
						.getStop().getLine() - 1)
						+ ctx.getParent().getStop().getCharPositionInLine();
				Fold f = new Fold(FoldType.CODE, textArea, offset);
				f.setEndOffset(endOffset);
				folds.add(f);
			}
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}
		return super.visitBlock(ctx);
	}
}