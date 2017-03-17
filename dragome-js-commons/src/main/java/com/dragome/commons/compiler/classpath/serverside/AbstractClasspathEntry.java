package com.dragome.commons.compiler.classpath.serverside;

import java.util.jar.JarOutputStream;

import com.dragome.commons.compiler.BytecodeTransformer;
import com.dragome.commons.compiler.CopyUtils;
import com.dragome.commons.compiler.classpath.ClasspathEntry;
import com.dragome.commons.compiler.classpath.ClasspathFile;
import com.dragome.commons.compiler.classpath.ClasspathFileFilter;

public abstract class AbstractClasspathEntry implements ClasspathEntry
{
	public AbstractClasspathEntry()
	{
	}

	public void copyFilesToJar(JarOutputStream jos, BytecodeTransformer bytecodeTransformer, ClasspathFileFilter classpathFileFilter)
	{
		for (ClasspathFile classpathFile : this.getClasspathFilesFiltering(classpathFileFilter))
		{
			ClasspathFile classpathFile2=classpathFile;;
			if (classpathFile.getFilename().endsWith(".class"))
				classpathFile2= new ClasspathInstrumentedFile(classpathFile, bytecodeTransformer);

			CopyUtils.addEntryToJar(jos, classpathFile2.openInputStream(), classpathFile2.getPath());
		}
	}
}