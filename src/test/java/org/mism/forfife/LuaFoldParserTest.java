package org.mism.forfife;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;

import org.easymock.EasyMock;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rsyntaxtextarea.folding.FoldType;
import org.junit.Test;

public class LuaFoldParserTest {

	@Test
	public void testInitFolds() throws Exception {
		RSyntaxTextArea textArea = EasyMock.createMock(RSyntaxTextArea.class);
		Document document = EasyMock.createMock(Document.class);
		EasyMock.expect(textArea.getDocument()).andReturn(document);
		EasyMock.expect(document.createPosition(0)).andReturn(new Position() {

			@Override
			public int getOffset() {
				return 0;
			}

		});
		EasyMock.replay(textArea, document);
		new Fold(FoldType.CODE, textArea, 0);
		EasyMock.verify(textArea, document);
	}

	public Fold createDummyFold(int offset) throws BadLocationException {
		RSyntaxTextArea textArea = EasyMock.createMock(RSyntaxTextArea.class);
		Document document = EasyMock.createMock(Document.class);
		EasyMock.expect(textArea.getDocument()).andReturn(document).anyTimes();
		EasyMock.expect(document.createPosition(offset)).andReturn(
				new Position() {

					@Override
					public int getOffset() {
						return offset;
					}

				}).anyTimes();
		EasyMock.replay(textArea, document);
		return new Fold(FoldType.CODE, textArea, offset);

	}

	@Test
	public void testGetFolds() throws Exception {

		TextField mock = EasyMock.createMock(TextField.class);
		EasyMock.expect(mock.getText()).andReturn(
				"function test()\n" + "  q=5\n" + "  return q\n" + "end\n"
						+ "function demo()\n" + "  test = 5\n"
						+ "  return test\n" + "end\n");

		EasyMock.expect(mock.getLineStartOffset(0)).andReturn(0).times(2);
		EasyMock.expect(mock.getLineStartOffset(3)).andReturn(12);
		EasyMock.expect(mock.getLineStartOffset(4)).andReturn(12);
		EasyMock.expect(mock.getLineStartOffset(7)).andReturn(12);

		EasyMock.expect(mock.createFold(0, 13)).andReturn(
				createDummyFold(12));
		EasyMock.expect(mock.createFold(0, 25)).andReturn(
				createDummyFold(12));

		EasyMock.replay(mock);
		LuaFoldParser fp = new LuaFoldParser();
		fp.getFolds(mock);
		EasyMock.verify(mock);
	}

}
