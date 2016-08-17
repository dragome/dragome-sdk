package com.dragome.commons.compiler.classpath.serverside;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import com.dragome.commons.compiler.classpath.ClasspathEntry;
import com.dragome.commons.compiler.classpath.ClasspathFile;
import com.dragome.commons.compiler.classpath.ClasspathFileFilter;

public class JarClasspathEntry extends AbstractClasspathEntry implements ClasspathEntry
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
		String tmprelativeName = relativeName.replace("\\", "/");
		final Enumeration<JarEntry> entries= jarFile.entries();
		while (entries.hasMoreElements())
		{
			final JarEntry entry= entries.nextElement();
			final String entryName= entry.getName().replace("\\", "/"); // force all path to "/" if its using "\\"
			if (tmprelativeName.equals(entryName))
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
				//				if (entryName.endsWith(".class"))
				//					result.add(entryName.replace('/', File.separatorChar).replace(".class", ""));
				result.add(entryName);
			}
			catch (Exception e)
			{
				LOGGER.warning("There is an invalid jar entry: " + e.getMessage());
			}
		}

		return result;
	}

	public List<ClasspathFile> getClasspathFilesFiltering(ClasspathFileFilter classpathFilter)
	{
		List<ClasspathFile> files= new ArrayList<ClasspathFile>();
		List<String> classesInJar= findClassesInJar(jarFile);

		for (String file : classesInJar)
		{
			ClasspathFile classpathFile= getClasspathFileOf(file);
			if (classpathFilter == null || classpathFilter.accept(classpathFile))
				files.add(classpathFile);
		}

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

	public String toString()
	{
		return jarFile.getName();
	}

	public String getName()
	{
		return jarFile.getName();
	}
}
