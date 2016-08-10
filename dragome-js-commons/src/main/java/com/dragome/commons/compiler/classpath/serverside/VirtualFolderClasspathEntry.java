package com.dragome.commons.compiler.classpath.serverside;

import java.util.ArrayList;
import java.util.List;

import com.dragome.commons.compiler.classpath.ClasspathEntry;
import com.dragome.commons.compiler.classpath.ClasspathFile;
import com.dragome.commons.compiler.classpath.ClasspathFileFilter;

public class VirtualFolderClasspathEntry extends AbstractClasspathEntry implements ClasspathEntry
{
	private List<ClasspathFile> classpathFiles;

	public VirtualFolderClasspathEntry(List<ClasspathFile> classpathFiles)
	{
		this.classpathFiles= classpathFiles;
	}

	public ClasspathFile getClasspathFileOf(String relativeName)
	{
		for (ClasspathFile compilableFile : classpathFiles)
			if (compilableFile.getPath().equals(relativeName))
				return compilableFile;

		return null;
	}

	public List<ClasspathFile> getClasspathFilesFiltering(ClasspathFileFilter classpathFilter)
	{
		List<ClasspathFile> files= new ArrayList<ClasspathFile>();

		for (ClasspathFile classpathFile : classpathFiles)
		{
			if (classpathFilter == null || classpathFilter.accept(classpathFile))
				files.add(classpathFile);
		}

		return files;
	}

	public String getName()
	{
		return "virtual-folder-classpath-entry";
	}
}
