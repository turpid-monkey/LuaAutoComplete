package org.mism.forfife;

public class TextResourceLoader implements LuaResourceLoader {
	LuaResource resource;

	@Override
	public void setResource(LuaResource resource) {
		this.resource = resource;
	}

	public LuaResource getResource() {
		return resource;
	}
	
	@Override
	public boolean canLoad() {
		return resource.getResourceLink().startsWith("txt:");
	}

	@Override
	public String load() throws Exception {

		return resource.getResourceLink().substring(4);
	}

	@Override
	public boolean hasModifications() {
		return false;
	}

}