package com.dragome.commons.compiler.classpath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.logging.Logger;

import com.dragome.commons.compiler.CopyUtils;

public class JarClasspathEntry implements ClasspathEntry
{
	private static Logger LOGGER= Logger.getLogger(JarClasspathEntry.class.getName());

	private JarFile jarFile;

	public JarClasspathEntry(JarFile jarFile)
	{
		this.jarFile= jarFile;
	}

	public ClasspathFile getClasspathFileOf(String relativeName)
	{
		// There is a "bug" that using getJarEntry with "/" fails and "\\" succeed and vice versa. so solution is to loop all class
		final Enumeration<JarEntry> entries= jarFile.entries();
		while (entries.hasMoreElements())
		{
				final JarEntry entry= entries.nextElement();
				final String entryName= entry.getName().replace("\\", "/"); // force all path to "/" if its using "\\"
				if(relativeName.equals(entryName))
					return new InsideJarClasspathFile(jarFile, entry, relativeName);
		}
		return null;
	}

	private List<String> findClassesInJar(JarFile jarFile)
	{
		ArrayList<String> result= new ArrayList<String>();

		final Enumeration<JarEntry> entries= jarFile.entries();
		while (entries.hasMoreElements())
		{
			try
			{
				final JarEntry entry= entries.nextElement();
				final String entryName= entry.getName();
				if (entryName.endsWith(".class"))
					result.add(entryName.replace('/', File.separatorChar).replace(".class", ""));
			}
			catch (Exception e)
			{
				LOGGER.warning("There is an invalid jar entry: " + e.getMessage());
			}
		}

		return result;
	}

	public List<String> getAllFilesNamesFiltering(ClasspathFileFilter classpathFilter)
	{
		List<String> files= new ArrayList<String>();
		List<String> classesInJar= findClassesInJar(jarFile);

		for (String file : classesInJar)
			if (classpathFilter == null || classpathFilter.accept(new File(file), new File(jarFile.getName())))
				files.add(file);

		return files;
	}

	public static ClasspathEntry createFromPath(String path)
	{
		try
		{
			return new JarClasspathEntry(new JarFile(new File(path), false));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void copyFilesToJar(JarOutputStream jos, ArrayList<String> keepClass)
	{
		try
		{
			CopyUtils.copyJarFile(jarFile, jos, keepClass);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public String toString()
	{
		return jarFile.getName();
	}

	public String getName()
	{
		return jarFile.getName();
	}
}
