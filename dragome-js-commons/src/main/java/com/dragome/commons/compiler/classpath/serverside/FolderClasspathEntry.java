package com.dragome.commons.compiler.classpath.serverside;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.dragome.commons.compiler.CopyUtils;
import com.dragome.commons.compiler.classpath.ClasspathEntry;
import com.dragome.commons.compiler.classpath.ClasspathFile;
import com.dragome.commons.compiler.classpath.ClasspathFileFilter;
import com.dragome.commons.compiler.classpath.JavaFileClasspathFile;

public class FolderClasspathEntry extends AbstractClasspathEntry implements ClasspathEntry
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

	public List<ClasspathFile> getClasspathFilesFiltering(ClasspathFileFilter classpathFilter)
	{
		List<ClasspathFile> files= new ArrayList<ClasspathFile>();

		//TODO: it's listing only .class files, not resources?
		Collection<File> listFiles= FileUtils.listFiles(folder, new WildcardFileFilter("*"), DirectoryFileFilter.DIRECTORY);
		for (File file : listFiles)
		{
			String substring= file.toString().substring(folder.toString().length() + 1);

			JavaFileClasspathFile classpathFile= new JavaFileClasspathFile(folder, substring);
			if (classpathFilter == null || classpathFilter.accept(classpathFile))
				files.add(classpathFile);
		}

		return files;
	}

	public static ClasspathEntry createFromPath(String classPathEntry)
	{
		return new FolderClasspathEntry(new File(classPathEntry));
	}

//	public void copyFilesToJar(JarOutputStream jos, ClasspathFileFilter classpathFileFilter)
//	{
//		try
//		{
//			CopyUtils.copyClassToJarFile(folder, jos, classpathFileFilter);
//		}
//		catch (Exception e)
//		{
//			throw new RuntimeException(e);
//		}
//	}

	public String toString()
	{
		return folder.getAbsolutePath();
	}

	public String getName()
	{
		return folder.getAbsolutePath();
	}
}
