package org.mism.forfife.res;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.mism.forfife.LuaResource;
import org.mism.forfife.LuaResourceLoader;

public class FileResourceLoader implements LuaResourceLoader {

	LuaResource resource;
	File file;
	long lastModified;

	@Override
	public void setResource(LuaResource resource) {
		String fileName = resource.getResourceLink().substring(5);
		file = new File(fileName);
		lastModified = file.lastModified();
	}
	
	@Override
	public LuaResource getResource() {
		return resource;
	}

	@Override
	public boolean canLoad() {
		return resource.getResourceLink().startsWith("file:");
	}

	@Override
	public String load() throws Exception {
		return load(file);
	}

	public static String load(File file) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(file));
		StringBuffer buf = new StringBuffer();
		String line;
		while ((line = in.readLine()) != null)
			buf.append(line).append("\n");
		in.close();
		return buf.toString();
	}

	@Override
	public boolean hasModifications() {
		return lastModified < file.lastModified();
	}

}
