package com.dragome.commons.compiler;

import java.io.IOException;
import java.io.InputStream;

public interface ClasspathFile
{
	String getFilename();
	InputStream openInputStream() throws IOException;
	long getLastModified();
	void close();
	String getPath();
}