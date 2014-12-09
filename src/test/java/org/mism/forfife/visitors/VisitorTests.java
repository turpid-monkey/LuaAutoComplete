package org.mism.forfife.visitors;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mism.forfife.LuaParseTreeUtil;
import org.mism.forfife.LuaSyntaxInfo;
import org.mism.forfife.lua.LuaParser.ChunkContext;

public class VisitorTests {
	
	@Test
	public void testRequireVisitor() throws Exception
	{
		ChunkContext ctx = LuaParseTreeUtil.parse("require \"foo\"");
		LuaSyntaxInfo info = new LuaSyntaxInfo();
		RequireVisitor visitor = new RequireVisitor();
		visitor.setInfo(info);
		visitor.visit(ctx);
		assertEquals(1, info.getDependentResources().size());
		assertEquals("require:foo", info.getDependentResources().iterator().next().getResourceLink());
	}
	
	@Test
	public void testUgLoadVisitor() throws Exception
	{
		ChunkContext ctx = LuaParseTreeUtil.parse("ug_load_script(\"ug_util.lua\")");
		LuaSyntaxInfo info = new LuaSyntaxInfo();
		UgLoadScriptVisitor visitor = new UgLoadScriptVisitor();
		visitor.setInfo(info);
		visitor.visit(ctx);
		assertEquals(1, info.getDependentResources().size());
		assertEquals("ug:ug_util.lua", info.getDependentResources().iterator().next().getResourceLink());
	}

}
