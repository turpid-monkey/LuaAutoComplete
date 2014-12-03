package org.mism.forfife;

import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rsyntaxtextarea.folding.FoldType;

interface TextField {

	String getText();

	int getLineStartOffset(int i) throws BadLocationException;
	
	/**
	 * @param type should be one of {@link FoldType}
	 * @param offset
	 * @return
	 */
	Fold createFold(int type, int offset) throws BadLocationException ;

}
