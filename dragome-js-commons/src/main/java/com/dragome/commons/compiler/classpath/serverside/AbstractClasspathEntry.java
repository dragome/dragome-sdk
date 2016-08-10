package com.dragome.commons.compiler.classpath.serverside;

import java.util.jar.JarOutputStream;

import com.dragome.commons.compiler.CopyUtils;
import com.dragome.commons.compiler.classpath.ClasspathEntry;
import com.dragome.commons.compiler.classpath.ClasspathFile;
import com.dragome.commons.compiler.classpath.ClasspathFileFilter;

public abstract class AbstractClasspathEntry implements ClasspathEntry
{
	public AbstractClasspathEntry()
	{
	}

	public void copyFilesToJar(JarOutputStream jos, ClasspathFileFilter classpathFileFilter)
	{
		for (ClasspathFile classpathFile : this.getClasspathFilesFiltering(classpathFileFilter))
			CopyUtils.addEntryToJar(jos, classpathFile.openInputStream(), classpathFile.getPath());
	}
}