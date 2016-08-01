package com.dragome.commons.compiler.classpath;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarOutputStream;

import com.dragome.commons.compiler.ClasspathEntryFilter;
import com.dragome.commons.compiler.CopyUtils;

public class VirtualFolderClasspathEntry implements ClasspathEntry
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

	public List<String> getAllFilesNamesFiltering(ClasspathFileFilter classpathFilter)
	{
		List<String> files= new ArrayList<String>();

		for (ClasspathFile classpathFile : classpathFiles)
		{
			File file= new File(classpathFile.getPath());
			if (classpathFilter == null || classpathFilter.accept(file, new File(".")))
				files.add(classpathFile.getPath().replace(".class", ""));
		}

		return files;
	}

	public void copyFilesToJar(JarOutputStream jos, ClasspathEntryFilter classpathEntryFilter)
	{
		for (ClasspathFile classpathFile : classpathFiles)
		{
			String entryName= classpathFile.getFilename().replace(".class", "");
			if (classpathEntryFilter.keepTheClass(entryName))
				CopyUtils.addEntryToJar(jos, classpathFile.openInputStream(), entryName);
		}
	}

	public String getName()
	{
		return "virtual-folder-classpath-entry";
	}
}
