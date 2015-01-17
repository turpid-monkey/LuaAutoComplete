package edu.gcsc.lua;

import static org.junit.Assert.*;

import java.util.Map;

import javax.swing.text.JTextComponent;

import org.easymock.EasyMock;
import org.junit.Test;

import edu.gcsc.lua.LuaResource;
import edu.gcsc.lua.LuaSyntaxAnalyzer;
import edu.gcsc.lua.LuaSyntaxInfo;
import edu.gcsc.lua.TextAreaManager;
import edu.gcsc.lua.resources.JTextComponentResourceLoader;

public class TextAreaManagerTest {

	@Test
	public void getAnalyzerCache() throws Exception {
		TextAreaManager mgr = new TextAreaManager();
		JTextComponentResourceLoader.setTextAreaManager(mgr);
		mgr.getFactory().getLoaders().add(JTextComponentResourceLoader.class);
		JTextComponent dummy = EasyMock.createNiceMock(JTextComponent.class);
		Map<LuaResource, LuaSyntaxInfo> cache = mgr.getAnalyzerCache(dummy);
		assertEquals(1, cache.size());
		assertEquals("textArea:" + dummy.hashCode(), cache.keySet().iterator()
				.next().getResourceLink());
		LuaSyntaxAnalyzer info = (LuaSyntaxAnalyzer) cache.values().iterator()
				.next();
		assertEquals(info.getVisitors(), mgr.getVisitors());
		assertEquals(info.getResourceLoaderFactory(), mgr.getFactory());
	}
	
	@Test
	public void getTextArea() throws Exception {
		TextAreaManager mgr = new TextAreaManager();
		JTextComponentResourceLoader.setTextAreaManager(mgr);
		mgr.getFactory().getLoaders().add(JTextComponentResourceLoader.class);
		JTextComponent dummy = EasyMock.createNiceMock(JTextComponent.class);
		mgr.getAnalyzerCache(dummy);
		assertEquals(dummy, mgr.getTextArea(new LuaResource("textArea:" + dummy.hashCode())));
	}

}
