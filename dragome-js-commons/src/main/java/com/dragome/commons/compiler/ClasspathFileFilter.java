package com.dragome.commons.compiler;

import java.io.File;

public interface ClasspathFileFilter
{
	boolean accept(File pathname, File folder);
}