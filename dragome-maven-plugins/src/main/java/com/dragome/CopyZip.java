package com.dragome;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;;

public class CopyZip
{
	public static void copyJarFile(JarFile jarFile, JarOutputStream jos) throws IOException
	{
		Enumeration<JarEntry> entries= jarFile.entries();

		while (entries.hasMoreElements())
		{
			JarEntry entry= entries.nextElement();
			InputStream is= jarFile.getInputStream(entry);
			String entryName= entry.getName();
			addEntryToJar(jos, is, entryName);
		}
	}

	private static void addEntryToJar(JarOutputStream jos, InputStream is, String entryName) throws IOException
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
			is.close();
			jos.flush();
			jos.closeEntry();
		}
	}

	public static void copyClassToJarFile(File fileClassPathEntry, JarOutputStream jos) throws Exception
	{
		Files.walkFileTree(fileClassPathEntry.toPath(), new SimpleFileVisitor<Path>()
		{
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			{
				InputStream inputStream= Files.newInputStream(file);
				String string= fileClassPathEntry.toPath().relativize(file).toString();
				addEntryToJar(jos, inputStream, string);

				return FileVisitResult.CONTINUE;
			}
		});

	}

	public static void copyFilesOfFolder(File fileClassPathEntry, File targetFolder) throws Exception
	{
		Files.walkFileTree(fileClassPathEntry.toPath(), new SimpleFileVisitor<Path>()
		{
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			{
				File file2= new File (targetFolder.getParentFile(), fileClassPathEntry.toPath().relativize(file).toString());
				Files.copy(file, file2.toPath());

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
					File file2= new File (targetFolder.getParentFile(), fileClassPathEntry.toPath().relativize(dir).toString());
					file2.mkdirs();
					return FileVisitResult.CONTINUE;
				}
			}
		});

	}
}