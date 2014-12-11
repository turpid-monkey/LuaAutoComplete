package org.mism.forfife;

public interface LuaResourceLoader {
	
	boolean canLoad(LuaResource res);
	String load(LuaResource res) throws Exception;

}
