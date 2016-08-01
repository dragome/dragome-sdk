package com.dragome.commons.compiler;

public interface ClasspathEntryFilter
{
	boolean keepTheClass(String name);
}