package com.dragome.commons.compiler.classpath;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.dragome.commons.compiler.CopyUtils;

public class FolderClasspathEntry implements ClasspathEntry
{
	private File folder;

	public FolderClasspathEntry(File folder)
	{
		this.folder= folder;
	}

	public ClasspathFile getClasspathFileOf(String relativeName)
	{
		File file= new File(folder, relativeName);
		if (file.exists())
			return new JavaFileClasspathFile(folder, relativeName);
		else
			return null;
	}

	public List<String> getAllFilesNamesFiltering(ClasspathFileFilter classpathFilter)
	{
		List<String> files= new ArrayList<String>();

		Collection<File> listFiles= FileUtils.listFiles(folder, new WildcardFileFilter("*.class"), DirectoryFileFilter.DIRECTORY);
		for (File file : listFiles)
		{
			String substring= file.toString().substring(folder.toString().length() + 1);

			if (classpathFilter == null || classpathFilter.accept(file, folder))
			{
				files.add(substring.replace(".class", ""));
			}
		}

		return files;
	}

	public static ClasspathEntry createFromPath(String classPathEntry)
	{
		return new FolderClasspathEntry(new File(classPathEntry));
	}

	public void copyFilesToJar(JarOutputStream jos)
	{
		try
		{
			CopyUtils.copyClassToJarFile(folder, jos);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
