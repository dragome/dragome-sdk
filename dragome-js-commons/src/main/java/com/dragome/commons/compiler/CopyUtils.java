package com.dragome.commons.compiler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;;

public class CopyUtils
{
	public static void copyJarFile(JarFile jarFile, JarOutputStream jos, ClasspathEntryFilter classpathEntryFilter) throws IOException
	{
		Enumeration<JarEntry> entries= jarFile.entries();

		while (entries.hasMoreElements())
		{
			JarEntry entry= entries.nextElement();
			String entryName= entry.getName();
			if (classpathEntryFilter.keepTheClass(entryName))
			{
				InputStream is= jarFile.getInputStream(entry);
				addEntryToJar(jos, is, entryName);
			}
		}
	}

	public static void addEntryToJar(JarOutputStream jos, InputStream is, String entryName)
	{
		try
		{
			jos.putNextEntry(new JarEntry(entryName));
			byte[] buffer= new byte[4096];
			int bytesRead= 0;
			while ((bytesRead= is.read(buffer)) != -1)
			{
				jos.write(buffer, 0, bytesRead);
			}
		}
		catch (Exception e)
		{
			//				e.printStackTrace();
		}
		finally
		{
			try
			{
				is.close();
				jos.flush();
				jos.closeEntry();
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	public static void copyClassToJarFile(final File fileClassPathEntry, final JarOutputStream jos, final ClasspathEntryFilter classpathEntryFilter) throws Exception
	{
		if (fileClassPathEntry != null && fileClassPathEntry.exists())
			Files.walkFileTree(fileClassPathEntry.toPath(), new SimpleFileVisitor<Path>()
			{
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
				{
					String string= fileClassPathEntry.toPath().relativize(file).toString();

					if (classpathEntryFilter.keepTheClass(string))
					{
						InputStream inputStream= Files.newInputStream(file);
						addEntryToJar(jos, inputStream, string);
					}

					return FileVisitResult.CONTINUE;
				}

			});
	}

	public static void copyFilesOfFolder(final File fileClassPathEntry, final File targetFolder) throws Exception
	{
		if (fileClassPathEntry != null && fileClassPathEntry.exists())
			Files.walkFileTree(fileClassPathEntry.toPath(), new SimpleFileVisitor<Path>()
			{
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
				{
					File file2= new File(targetFolder.getParentFile(), fileClassPathEntry.toPath().relativize(file).toString());
					Files.copy(file, file2.toPath(), StandardCopyOption.REPLACE_EXISTING);

					return FileVisitResult.CONTINUE;
				}

				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
				{
					if (dir.toString().contains("compiled-js") || dir.toString().contains("WEB-INF"))
					{
						return FileVisitResult.SKIP_SUBTREE;
					}
					else
					{
						File file2= new File(targetFolder.getParentFile(), fileClassPathEntry.toPath().relativize(dir).toString());
						file2.mkdirs();
						return FileVisitResult.CONTINUE;
					}
				}
			});

	}
}