package com.dragome.commons.compiler.classpath;

import java.io.InputStream;

public interface ClasspathFile
{
	String getFilename();
	InputStream openInputStream();
	long getLastModified();
	void close();
	String getPath();
}