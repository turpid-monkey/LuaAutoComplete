package org.mism.forfife.visitors;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mism.forfife.LuaParseTreeUtil;
import org.mism.forfife.LuaResource;
import org.mism.forfife.LuaResourceLoaderFactory;
import org.mism.forfife.LuaSyntaxInfo;
import org.mism.forfife.TextResourceLoader;
import org.mism.forfife.lua.LuaParser.ChunkContext;

public class VisitorTests {

	@Test
	public void testRequireVisitor() throws Exception {
		ChunkContext ctx = LuaParseTreeUtil.parse("require \"foo\"");
		LuaSyntaxInfo info = new LuaSyntaxInfo();
		RequireVisitor visitor = new RequireVisitor();
		visitor.setInfo(info);
		visitor.visit(ctx);
		assertEquals(1, info.getIncludedResources().size());
		assertEquals("require:foo", info.getIncludedResources().iterator()
				.next().getResourceLink());
	}

	@Test
	public void readDoxyGen() throws Exception {
		String script = "--! Some Comment\nfunction test()\n return 0\nend\n";
		ChunkContext ctx = LuaParseTreeUtil.parse(script);
		LuaSyntaxInfo info = new LuaSyntaxInfo();
		info.setResourceLoaderFactory(new LuaResourceLoaderFactory(
				TextResourceLoader.class));
		info.setResource(new LuaResource("txt:" + script));
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

		info.setResourceLoaderFactory(new LuaResourceLoaderFactory(
				TextResourceLoader.class));
		info.setResource(new LuaResource("txt:" + script));
		DoxygenVisitor visitor = new DoxygenVisitor();
		visitor.setInfo(info);
		visitor.visit(ctx);
		assertEquals("Some Comment<br><b>param</b> none<br>", info
				.getDoxyGenMap().get("test"));
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

		info.setResourceLoaderFactory(new LuaResourceLoaderFactory(
				TextResourceLoader.class));
		info.setResource(new LuaResource("txt:" + luaScript));
		DoxygenVisitor visitor = new DoxygenVisitor();
		visitor.setInfo(info);
		visitor.visit(ctx);
		assertEquals("Some Comment for 1<br>", info.getDoxyGenMap()
				.get("test1"));
		assertEquals("Some Comment for 9<br>", info.getDoxyGenMap()
				.get("test9"));
	}

	@Test
	public void assignment() throws Exception {
		String script = "someVar = SomeClass()";
		ChunkContext ctx = LuaParseTreeUtil.parse(script);
		LuaSyntaxInfo info = new LuaSyntaxInfo();
		AssignmentVisitor visitor = new AssignmentVisitor();
		visitor.setInfo(info);
		visitor.visit(ctx);
		assertEquals("SomeClass", info.getTypeMap().get("someVar"));
	}

	@Test
	public void nestedFunctions() throws Exception {
		String script = "function test ()\n" + "   n = 1\n"
				+ "   function member_func()\n" + "     n=n+1\n" + "   end\n"
				+ "end\n";
		ChunkContext ctx = LuaParseTreeUtil.parse(script);
		LuaSyntaxInfo info = new LuaSyntaxInfo();
		FunctionVisitor visitor = new FunctionVisitor();
		visitor.setInfo(info);
		visitor.visit(ctx);
		assertEquals("member_func", info.getTables().get("test").iterator()
				.next());
	}

	@Test
	public void nestedInlineFunction() throws Exception {
		String script = "someVar = function()\n" + "   n = 1\n"
				+ "   function member_func()\n" + "     n=n+1\n" + "   end\n"
				+ "end\n";
		ChunkContext ctx = LuaParseTreeUtil.parse(script);
		LuaSyntaxInfo info = new LuaSyntaxInfo();
		FunctionVisitor visitor = new FunctionVisitor();
		visitor.setInfo(info);
		visitor.visit(ctx);
		// TODO
		// assertEquals("member_func", info.getTables().get("test").iterator()
		//		.next());
	}

}
