package com.dragome;

import java.io.IOException;
import java.io.InputStream;
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

			try
			{
				jos.putNextEntry(new JarEntry(entry.getName()));
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
			finally {
				is.close();
				jos.flush();
				jos.closeEntry();
			}
		}
	}
}