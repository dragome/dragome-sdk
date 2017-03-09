/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * This file is part of Dragome SDK.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.dragome.web.helpers.serverside;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarOutputStream;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import com.dragome.commons.DragomeConfigurator;
import com.dragome.commons.compiler.BytecodeToJavascriptCompiler;
import com.dragome.commons.compiler.BytecodeToJavascriptCompilerConfiguration;
import com.dragome.commons.compiler.BytecodeTransformer;
import com.dragome.commons.compiler.annotations.CompilerType;
import com.dragome.commons.compiler.classpath.Classpath;
import com.dragome.commons.compiler.classpath.ClasspathEntry;
import com.dragome.commons.compiler.classpath.ClasspathFile;
import com.dragome.commons.compiler.classpath.ClasspathFileFilter;
import com.dragome.commons.compiler.classpath.serverside.JarClasspathEntry;
import com.dragome.services.ServiceLocator;
import com.dragome.services.WebServiceLocator;
import com.dragome.view.VisualActivity;
import com.dragome.web.helpers.DefaultClasspathFileFilter;

import proguard.Configuration;
import proguard.ConfigurationParser;
import proguard.ParseException;
import proguard.ProGuard;

public class DragomeCompilerLauncher
{
	private static Logger LOGGER= Logger.getLogger(DragomeCompilerLauncher.class.getName());

	public static void compileWithMainClass(Classpath classPath, String target)
	{
		ServiceLocator serviceLocator= ServiceLocator.getInstance();
		DragomeConfigurator configurator= serviceLocator.getConfigurator();
		String mainClassName= VisualActivity.class.getName();
		CompilerType defaultCompilerType= configurator.getDefaultCompilerType();
		BytecodeTransformer bytecodeTransformer= configurator.getBytecodeTransformer();

		ClasspathFileFilter classpathFilter= configurator.getClasspathFilter();
		if (classpathFilter == null)
			classpathFilter= new DefaultClasspathFileFilter();

		BytecodeToJavascriptCompiler bytecodeToJavascriptCompiler= WebServiceLocator.getInstance().getBytecodeToJavascriptCompiler();

		List<ClasspathEntry> extraClasspath= configurator.getExtraClasspath(classPath);
		classPath.addEntries(extraClasspath);
		configurator.sortClassPath(classPath);
		classPath= process(classPath, configurator);

		BytecodeToJavascriptCompilerConfiguration compilerConfiguration= new BytecodeToJavascriptCompilerConfiguration(classPath, target, mainClassName, defaultCompilerType, bytecodeTransformer, new DefaultClasspathFileFilter(), configurator.isCheckingCast(), configurator.isCaching(), configurator.isFailOnError());
		bytecodeToJavascriptCompiler.configure(compilerConfiguration);
		bytecodeToJavascriptCompiler.compile();
	}

	private static Classpath process(Classpath classPath, DragomeConfigurator configurator)
	{
		try
		{
			String path= null;

			String tempDir= System.getProperty("java.io.tmpdir");
			File tmpDir= new File(tempDir + File.separatorChar + "dragomeTemp");
			Path tmpPath= tmpDir.toPath();
			FileUtils.deleteDirectory(tmpDir);
			Files.createDirectories(tmpPath);
			File file= Files.createTempFile(tmpPath, "dragome-merged-", ".jar").toFile();
			file.deleteOnExit();

			try (JarOutputStream jos= new JarOutputStream(new FileOutputStream(file)))
			{
				final ArrayList<String> keepClass= new ArrayList<>();
				final ClasspathFileFilter classpathFilter= configurator.getClasspathFilter();
				List<ClasspathEntry> entries= classPath.getEntries();
				for (ClasspathEntry classpathEntry : entries)
				{
					classpathEntry.copyFilesToJar(jos, new DefaultClasspathFileFilter()
					{
						public boolean accept(ClasspathFile classpathFile)
						{
							boolean result= super.accept(classpathFile);

							String entryName= classpathFile.getPath();
							entryName= entryName.replace("\\", "/");

							if (!keepClass.contains(entryName))
							{
								keepClass.add(entryName);

								if (entryName.endsWith(".js") || entryName.endsWith(".class") || entryName.contains("MANIFEST") || entryName.contains(".html") || entryName.contains(".css"))
									result&= true;

								if (classpathFilter != null)
									result&= classpathFilter.accept(classpathFile);
							}
							else
								result= false;
							return result;
						}
					});
				}
			}
			if (configurator.isRemoveUnusedCode())
			{
				file= executeProguard(file, "/proguard.conf", "-proguard.jar", configurator.getAdditionalCodeKeepConfigFile(), false);
				file.deleteOnExit();
			}
			if (configurator.isObfuscateCode())
			{
				file= executeProguard(file, "/proguardObf.conf", "-Obf.jar", configurator.getAdditionalObfuscateCodeKeepConfigFile(), true);
				file.deleteOnExit();
			}
			path= file.getAbsolutePath();
			return new Classpath(JarClasspathEntry.createFromPath(path));
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private static File executeProguard(File inputFile, String name, String replacement, List<URL> additionalUrls, boolean addToClasspath) throws MalformedURLException, URISyntaxException, IOException, ParseException
	{
		String outFilename= inputFile.getAbsolutePath().replace(".jar", replacement);
		File outputFile= new File(outFilename);

		Properties properties= System.getProperties();
		properties.put("in-jar-filename", inputFile.getAbsolutePath());
		properties.put("out-jar-filename", outputFile.getAbsolutePath());

		ConfigurationParser parser= new ConfigurationParser(DragomeCompilerLauncher.class.getResource(name).toURI().toURL(), properties);
		Configuration configuration= new Configuration();

		parser.parse(configuration);

		ArrayList<URL> urls= new ArrayList<URL>(additionalUrls);

		for (URL url : urls)
		{
			ConfigurationParser parserForAdditionalKeepCodeConfigFile= new ConfigurationParser(url, properties);
			parserForAdditionalKeepCodeConfigFile.parse(configuration);
		}

		if (addToClasspath)
			System.setProperty("java.class.path", outFilename + ";" + System.getProperty("java.class.path"));

		new ProGuard(configuration).execute();

		return outputFile;
	}
}
