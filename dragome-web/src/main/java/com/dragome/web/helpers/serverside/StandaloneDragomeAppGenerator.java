package com.dragome.web.helpers.serverside;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.dragome.commons.compiler.ClassPath;
import com.dragome.services.ServiceLocator;
import com.dragome.services.serverside.ServerReflectionServiceImpl;

import ro.isdc.wro.model.resource.processor.impl.js.JSMinProcessor;

public class StandaloneDragomeAppGenerator
{
	private static Logger LOGGER= Logger.getLogger(StandaloneDragomeAppGenerator.class.getName());
	private File destinationDirectory;
	private File webappDirectory;
	private boolean removeCache= true;
	private boolean forceRebuild= true;

	public static void main(String[] args)
	{
		new StandaloneDragomeAppGenerator(new File(args[0]), new File(args[1]), Boolean.parseBoolean(args[2]), Boolean.parseBoolean(args[3])).execute();
	}

	public StandaloneDragomeAppGenerator(File destinationDirectory, File webappDirectory, boolean removeCache, boolean forceRebuild)
	{
		this.destinationDirectory= destinationDirectory;
		this.webappDirectory= webappDirectory;
		this.removeCache= removeCache;
		this.forceRebuild= forceRebuild;
	}

	private void copyResource(String aResourceName, String aLocation)
	{
		LOGGER.info("Copy " + aResourceName + " to " + aLocation);
		InputStream theInputStream= getClass().getResourceAsStream(aResourceName);
		if (theInputStream != null)
		{
			int theLastPathIndex= aLocation.lastIndexOf('/');
			if (theLastPathIndex > 0)
			{
				String thePath= aLocation.substring(0, theLastPathIndex);
				thePath= thePath.replace('/', IOUtils.DIR_SEPARATOR);
				File theTargetDir= new File(destinationDirectory, thePath);
				if (!theTargetDir.exists())
				{
					if (!theTargetDir.mkdirs())
					{
						throw new RuntimeException("Cannot create directory " + theTargetDir);
					}
				}
			}
			String theSystemLocation= aLocation.replace('/', IOUtils.DIR_SEPARATOR);
			File theDestinatioFile= new File(destinationDirectory, theSystemLocation);
			try (FileOutputStream theOutputStream= new FileOutputStream(theDestinatioFile))
			{
				IOUtils.copy(theInputStream, theOutputStream);
			}
			catch (Exception e)
			{
				throw new RuntimeException("Cannot write data to " + theDestinatioFile, e);
			}
		}
		else
		{
			throw new IllegalArgumentException("Cannot find ressource " + aResourceName + " in ClassPath");
		}
	}

	private void copyResourceMinifyJS(String aResourceName)
	{
		//		JSMinProcessor theMinProcessor= new JSMinProcessor();

		String aLocation= "dragome-resources" + aResourceName;

		LOGGER.info("Copy " + aResourceName + " to minified " + aLocation);
		InputStream theInputStream= getClass().getResourceAsStream(aResourceName);
		if (theInputStream != null)
		{
			int theLastPathIndex= aLocation.lastIndexOf('/');
			if (theLastPathIndex > 0)
			{
				String thePath= aLocation.substring(0, theLastPathIndex);
				thePath= thePath.replace('/', IOUtils.DIR_SEPARATOR);
				File theTargetDir= new File(destinationDirectory, thePath);
				if (!theTargetDir.exists())
				{
					if (!theTargetDir.mkdirs())
					{
						throw new RuntimeException("Cannot create directory " + theTargetDir);
					}
				}
			}
			String theSystemLocation= aLocation.replace('/', IOUtils.DIR_SEPARATOR);
			File theDestinatioFile= new File(destinationDirectory, theSystemLocation);
			try
			{
				Files.copy(theInputStream, theDestinatioFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

				//				theMinProcessor.process(new InputStreamReader(theInputStream), new FileWriter(theDestinatioFile));
			}
			catch (Exception e)
			{
				throw new RuntimeException("Cannot write data to " + theDestinatioFile, e);
			}
		}
		else
		{
			throw new IllegalArgumentException("Cannot find resource " + aResourceName + " in ClassPath");
		}
	}

	private void compile() throws Exception
	{
		System.setProperty("dragome-compile-mode", "release");

		final ClassPath classPath= new ClassPath();

		URLClassLoader theCurrentClassLoader= (URLClassLoader) getClass().getClassLoader();
		URL[] theConfiguredURLs= theCurrentClassLoader.getURLs();
		ServiceLocator serviceLocator= ServiceLocator.getInstance();
		serviceLocator.setReflectionService(new ServerReflectionServiceImpl());
		if (serviceLocator.getConfigurator() == null)
			serviceLocator.setConfigurator(serviceLocator.getReflectionService().getConfigurator());

		for (URL theURL : theConfiguredURLs)
		{
			LOGGER.info("Found classpath element " + theURL);
			String theClassPathEntry= new File(theURL.toURI()).toString();
			boolean isClassesFolder= !theURL.toString().contains(".jar");
			boolean addToClasspath= serviceLocator.getConfigurator().filterClassPath(theClassPathEntry);

			if (isClassesFolder || addToClasspath)
				classPath.addEntry(theClassPathEntry);
			else
				LOGGER.warning("Skipping, it is not configured as an included artifact.");
		}

		File theTargetDir= new File(destinationDirectory, "compiled-js");
		if (!theTargetDir.exists() && !theTargetDir.mkdirs())
		{
			throw new RuntimeException("Cannot create directory " + theTargetDir);
		}

		File theWebAppJS= new File(theTargetDir, "webapp.js");
		if (forceRebuild && theWebAppJS.exists())
		{
			if (!theWebAppJS.delete())
			{
				throw new RuntimeException("Cannot delete file " + theWebAppJS);
			}
		}

		// Store the dragome cache file here
		System.setProperty("cache-dir", theTargetDir.toString());

		LOGGER.info("Using Dragome compiler classpath : " + classPath.toString());

		DragomeCompilerLauncher.compileWithMainClass(classPath, theTargetDir.toString());
		File dest= new File(theTargetDir, "webapp-original.js");
		theWebAppJS.renameTo(dest);

		// Ok, now we have a webapp.js file, do we need to minify it?
		LOGGER.info("Minifying webapp.js to compiled.js");
		JSMinProcessor theProcessor= new JSMinProcessor();

		//		Files.copy(theWebAppJS.toPath(), new File(theTargetDir, "webapp-1.js").toPath(), StandardCopyOption.REPLACE_EXISTING);
		CopyUtils.copyFilesOfFolder(webappDirectory, theTargetDir);

		theProcessor.process(new FileReader(dest), new FileWriter(new File(theTargetDir, "webapp.js")));
		dest.delete();

		// Finally remove the cache file
		if (removeCache)
		{
			File theCacheFile= new File(theTargetDir, "dragome.cache");
			LOGGER.info("Removing cache file " + theCacheFile);
			if (!theCacheFile.delete())
			{
				throw new RuntimeException("Cannot delete cache file" + theCacheFile);
			}
		}

	}

	public void execute()
	{
		try
		{
			LOGGER.info("Generating Dragome Client Application at " + destinationDirectory);

			copyResources();
			compile();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private void copyResources()
	{
		copyResource("/css/dragome.css", "dragome-resources/css/dragome.css");

		copyResourceMinifyJS("/dragome-debug.js");
		copyResourceMinifyJS("/dragome-production.js");
		copyResourceMinifyJS("/js/jquery-1.7.2.min.js");
		copyResourceMinifyJS("/js/hashtable.js");
		copyResourceMinifyJS("/js/deflate.js");
		copyResourceMinifyJS("/js/deflate-main.js");
		copyResourceMinifyJS("/js/console.js");
		copyResourceMinifyJS("/js/helpers.js");
		copyResourceMinifyJS("/js/String.js");
		copyResourceMinifyJS("/js/jquery.atmosphere.js");
		copyResourceMinifyJS("/js/application.js");
		copyResourceMinifyJS("/js/qx-oo-5.0.1.min.js");
	}

}