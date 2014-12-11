package org.mism.forfife.visitors;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mism.forfife.LuaParseTreeUtil;
import org.mism.forfife.LuaSyntaxInfo;
import org.mism.forfife.lua.LuaParser.ChunkContext;

public class VisitorTests {

	@Test
	public void testRequireVisitor() throws Exception {
		ChunkContext ctx = LuaParseTreeUtil.parse("require \"foo\"");
		LuaSyntaxInfo info = new LuaSyntaxInfo();
		RequireVisitor visitor = new RequireVisitor();
		visitor.setInfo(info);
		visitor.visit(ctx);
		assertEquals(1, info.getDependentResources().size());
		assertEquals("require:foo", info.getDependentResources().iterator()
				.next().getResourceLink());
	}

	@Test
	public void readDoxyGen() throws Exception {
		String script = "--! Some Comment\nfunction test()\n return 0\nend\n";
		ChunkContext ctx = LuaParseTreeUtil.parse(script);
		LuaSyntaxInfo info = new LuaSyntaxInfo();
		info.setLuaScript(script);
		DoxygenVisitor visitor = new DoxygenVisitor();
		visitor.setInfo(info);
		visitor.visit(ctx);
		assertEquals("Some Comment<br>", info.getDoxyGenMap().get("test"));
	}
	
	@Test
	public void readDoxyGenMarkup() throws Exception {
		String script = "--! Some Comment\n--! @param none\nfunction test()\n return 0\nend\n";
		ChunkContext ctx = LuaParseTreeUtil.parse(script);
		LuaSyntaxInfo info = new LuaSyntaxInfo();
		info.setLuaScript(script);
		DoxygenVisitor visitor = new DoxygenVisitor();
		visitor.setInfo(info);
		visitor.visit(ctx);
		assertEquals("Some Comment<br><b>param</b> none<br>", info.getDoxyGenMap().get("test"));
	}

	@Test
	public void readSeveralDoxyGens() throws Exception {
		String script = "--! Some Comment for NUMBER\n function testNUMBER()\n return NUMBER\n end\n\n";
		StringBuffer luaScript = new StringBuffer();
		for (int i = 0; i < 10; i++) {
			luaScript.append(script.replaceAll("NUMBER", "" + i));
		}
		ChunkContext ctx = LuaParseTreeUtil.parse(script);
		LuaSyntaxInfo info = new LuaSyntaxInfo();
		info.setLuaScript(luaScript.toString());
		DoxygenVisitor visitor = new DoxygenVisitor();
		visitor.setInfo(info);
		visitor.visit(ctx);
		assertEquals("Some Comment for 1<br>", info.getDoxyGenMap().get("test1"));
		assertEquals("Some Comment for 9<br>", info.getDoxyGenMap().get("test9"));
	}

}
