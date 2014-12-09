package org.mism.forfife;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;

import static org.easymock.EasyMock.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rsyntaxtextarea.folding.FoldType;
import org.junit.Test;

public class LuaFoldParserTest {

	@Test
	public void testInitFolds() throws Exception {
		RSyntaxTextArea textArea = createMock(RSyntaxTextArea.class);
		Document document = createMock(Document.class);
		expect(textArea.getDocument()).andReturn(document);
		expect(document.createPosition(0)).andReturn(new Position() {

			@Override
			public int getOffset() {
				return 0;
			}

		});
		replay(textArea, document);
		new Fold(FoldType.CODE, textArea, 0);
		verify(textArea, document);
	}

	public Fold createDummyFold(int offset) throws BadLocationException {
		RSyntaxTextArea textArea = createMock(RSyntaxTextArea.class);
		Document document = createMock(Document.class);
		expect(textArea.getDocument()).andReturn(document).anyTimes();
		expect(document.createPosition(offset)).andReturn(
				new Position() {

					@Override
					public int getOffset() {
						return offset;
					}

				}).anyTimes();
		replay(textArea, document);
		return new Fold(FoldType.CODE, textArea, offset);

	}

	@Test
	public void testGetFolds() throws Exception {

		TextField mock = createMock(TextField.class);
		expect(mock.getText()).andReturn(
				"function test()\n" + "  q=5\n" + "  return q\n" + "end\n"
						+ "function demo()\n" + "  test = 5\n"
						+ "  return test\n" + "end\n");

		expect(mock.getLineStartOffset(0)).andReturn(0).times(2);
		expect(mock.getLineStartOffset(3)).andReturn(12);
		expect(mock.getLineStartOffset(4)).andReturn(12);
		expect(mock.getLineStartOffset(7)).andReturn(12);

		expect(mock.createFold(0, 13)).andReturn(
				createDummyFold(12));
		expect(mock.createFold(0, 25)).andReturn(
				createDummyFold(12));

		replay(mock);
		LuaFoldParser fp = new LuaFoldParser();
		fp.getFolds(mock);
		verify(mock);
	}

}
