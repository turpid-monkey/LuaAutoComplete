package org.mism.forfife;

import static org.junit.Assert.*;

import java.util.List;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.junit.Test;

public class LuaFoldParserTest {

	@Test
	public void testGetFolds() {
		RSyntaxTextArea textArea = new RSyntaxTextArea("function test()\n"
				+ "  q=5\n"
				+ "  return q\n"
				+ "end\n"
				+ "function demo()\n"
				+ "  test = 5\n"
				+ "  return test\n"
				+ "end\n");
		LuaFoldParser fp = new LuaFoldParser();
		List<Fold> folds = fp.getFolds(textArea);
		assertEquals(2, folds.size());
		assertEquals(4, folds.get(1).getLineCount());
		assertEquals(4, folds.get(2).getLineCount());
	}

}
