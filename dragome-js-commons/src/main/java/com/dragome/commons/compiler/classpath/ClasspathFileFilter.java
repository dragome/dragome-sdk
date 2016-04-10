package com.dragome.commons.compiler.classpath;

import java.io.File;

public interface ClasspathFileFilter
{
	boolean accept(File pathname, File folder);
}