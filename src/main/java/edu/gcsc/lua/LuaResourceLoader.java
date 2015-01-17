package edu.gcsc.lua;

public interface LuaResourceLoader {
	
	void setResource(LuaResource resource);
	LuaResource getResource();
	
	boolean canLoad();
	String load() throws Exception;
	boolean hasModifications();

}
