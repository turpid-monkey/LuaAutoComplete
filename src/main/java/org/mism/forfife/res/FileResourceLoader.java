package org.mism.forfife.res;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.mism.forfife.LuaResource;
import org.mism.forfife.LuaResourceLoader;

public class FileResourceLoader implements LuaResourceLoader {

	@Override
	public boolean canLoad(LuaResource res) {
		return res.getResourceLink().startsWith("file:");
	}

	@Override
	public String load(LuaResource res) throws Exception {
		String fileName = res.getResourceLink().substring(5);
		File file = new File(fileName);
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

}
