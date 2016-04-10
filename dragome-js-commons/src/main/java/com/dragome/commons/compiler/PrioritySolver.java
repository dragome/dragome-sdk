package com.dragome.commons.compiler;

import com.dragome.commons.compiler.classpath.ClasspathEntry;

public interface PrioritySolver
{
	int getPriorityOf(ClasspathEntry string);
}
