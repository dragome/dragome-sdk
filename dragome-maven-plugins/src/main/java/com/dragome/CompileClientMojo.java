package com.dragome;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.dragome.services.ServiceLocator;
import com.dragome.services.serverside.ServerReflectionServiceImpl;
import com.dragome.web.helpers.serverside.DragomeCompilerLauncher;

import proguard.Configuration;
import proguard.ConfigurationParser;
import proguard.ProGuard;
import ro.isdc.wro.model.resource.processor.impl.js.JSMinProcessor;

@Mojo(name= "compileclient")
public class CompileClientMojo extends AbstractMojo
{
	@Parameter(defaultValue= "${project.build.directory}")
	private String projectBuildDir;

	@Component
	private MavenProject mavenProject;

	@Component
	private MavenSession mavenSession;

	@Component
	private BuildPluginManager pluginManager;

	@Parameter
	private File destinationDirectory;

	@Parameter
	private File webappDirectory;

	@Parameter
	private boolean removeCache= true;

	@Parameter
	private boolean forceRebuild= true;

	@Parameter(defaultValue= "${project}", required= true, readonly= true)
	private MavenProject project;

	private void copyResource(String aResourceName, String aLocation)
	{
		getLog().info("Copy " + aResourceName + " to " + aLocation);
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

		getLog().info("Copy " + aResourceName + " to minified " + aLocation);
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

	private void compile() throws URISyntaxException, DependencyResolutionRequiredException, Exception
	{
		System.setProperty("dragome-compile-mode", "release");

		final StringBuilder theClassPathForCompiler= new StringBuilder();

		URLClassLoader theCurrentClassLoader= (URLClassLoader) getClass().getClassLoader();
		URL[] theConfiguredURLs= theCurrentClassLoader.getURLs();
		ServiceLocator serviceLocator= ServiceLocator.getInstance();
		serviceLocator.setReflectionService(new ServerReflectionServiceImpl());
		if (serviceLocator.getConfigurator() == null)
			serviceLocator.setConfigurator(serviceLocator.getReflectionService().getConfigurator());

		addClassloaderURLs(theClassPathForCompiler, theConfiguredURLs, serviceLocator);

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

		getLog().info("Using Dragome compiler classpath : " + theClassPathForCompiler.toString());

		DragomeCompilerLauncher.compileWithMainClass(theClassPathForCompiler.toString(), theTargetDir.toString());
		File dest= new File(theTargetDir, "webapp-original.js");
		theWebAppJS.renameTo(dest);

		// Ok, now we have a webapp.js file, do we need to minify it?
		getLog().info("Minifying webapp.js to compiled.js");
		JSMinProcessor theProcessor= new JSMinProcessor();

		//		Files.copy(theWebAppJS.toPath(), new File(theTargetDir, "webapp-1.js").toPath(), StandardCopyOption.REPLACE_EXISTING);
		CopyUtils.copyFilesOfFolder(webappDirectory, theTargetDir);

		theProcessor.process(new FileReader(dest), new FileWriter(new File(theTargetDir, "webapp.js")));
		dest.delete();

		// Finally remove the cache file
		if (removeCache)
		{
			File theCacheFile= new File(theTargetDir, "dragome.cache");
			getLog().info("Removing cache file " + theCacheFile);
			if (!theCacheFile.delete())
			{
				throw new RuntimeException("Cannot delete cache file" + theCacheFile);
			}
		}

	}

	private void addClassloaderURLs(final StringBuilder theClassPathForCompiler, URL[] theConfiguredURLs, ServiceLocator serviceLocator) throws Exception
	{
		String path= "dragome-uber.jar";

		File file= new File(projectBuildDir, path);
		file.createNewFile();

		JarOutputStream jos= new JarOutputStream(new FileOutputStream(file));

		for (URL theURL : theConfiguredURLs)
		{
			getLog().info("Found classpath element " + theURL);
			File fileClassPathEntry= new File(theURL.toURI());
			String theClassPathEntry= fileClassPathEntry.toString();
			boolean isClassesFolder= theURL.toString().endsWith("/classes/") || theURL.toString().endsWith("/classes");
			boolean addToClasspath= serviceLocator.getConfigurator().filterClassPath(theClassPathEntry);
			if (isClassesFolder || addToClasspath)
			{
				if (isClassesFolder)
					CopyUtils.copyClassToJarFile(fileClassPathEntry, jos);
			}
			else
			{
				getLog().warn("Skipping, it is not configured as an included artifact.");
			}

			if (theURL.toString().contains(".jar") && (theURL.toString().contains("dragome") || theURL.toString().contains("gdx")) && !theURL.toString().contains("dragome-bytecode-js-compiler"))
			{
				JarFile jarFile= new JarFile(fileClassPathEntry);
				CopyUtils.copyJarFile(jarFile, jos);
				jarFile.close();
			}
		}

		jos.close();

		runProguard();

		theClassPathForCompiler.append("target/dragome-uber-proguard.jar" + ";");
	}

	private void runProguard() throws Exception
	{
		URI uri= getClass().getResource("/proguard.conf").toURI();
		ConfigurationParser parser= new ConfigurationParser(uri.toURL(), System.getProperties());
		Configuration configuration= new Configuration();
		parser.parse(configuration);
		new ProGuard(configuration).execute();
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		try
		{
			getLog().info("Generating Dragome Client Application at " + destinationDirectory);

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