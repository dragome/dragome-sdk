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
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.logging.Logger;

import com.dragome.commons.DragomeConfigurator;
import com.dragome.commons.compiler.BytecodeToJavascriptCompiler;
import com.dragome.commons.compiler.BytecodeToJavascriptCompilerConfiguration;
import com.dragome.commons.compiler.BytecodeTransformer;
import com.dragome.commons.compiler.ClassPath;
import com.dragome.commons.compiler.ClasspathFile;
import com.dragome.commons.compiler.ClasspathFileFilter;
import com.dragome.commons.compiler.annotations.CompilerType;
import com.dragome.services.ServiceLocator;
import com.dragome.services.WebServiceLocator;
import com.dragome.view.VisualActivity;

import proguard.Configuration;
import proguard.ConfigurationParser;
import proguard.ProGuard;

public class DragomeCompilerLauncher
{
	private static Logger LOGGER= Logger.getLogger(DragomeCompilerLauncher.class.getName());

	public static void compileWithMainClass(ClassPath classPath, String target)
	{
		ServiceLocator serviceLocator= ServiceLocator.getInstance();
		DragomeConfigurator configurator= serviceLocator.getConfigurator();
		String mainClassName= VisualActivity.class.getName();
		CompilerType defaultCompilerType= configurator.getDefaultCompilerType();
		BytecodeTransformer bytecodeTransformer= configurator.getBytecodeTransformer();

		ClasspathFileFilter classpathFilter= configurator.getClasspathFilter();
		if (classpathFilter == null)
			classpathFilter= new DefaultClasspathFilter();

		BytecodeToJavascriptCompiler bytecodeToJavascriptCompiler= WebServiceLocator.getInstance().getBytecodeToJavascriptCompiler();

		configurator.sortClassPath(classPath);

		if (configurator.isRemoveUnusedCode())
			classPath= optimize(classPath, serviceLocator, configurator);

		List<ClasspathFile> extraClasspath= configurator.getExtraClasspath(classPath);

		BytecodeToJavascriptCompilerConfiguration compilerConfiguration= new BytecodeToJavascriptCompilerConfiguration(classPath, target, mainClassName, defaultCompilerType, bytecodeTransformer, classpathFilter, configurator.isCheckingCast(), extraClasspath);
		bytecodeToJavascriptCompiler.configure(compilerConfiguration);
		bytecodeToJavascriptCompiler.compile();
	}

	private static ClassPath optimize(ClassPath classPath, ServiceLocator serviceLocator, DragomeConfigurator configurator)
	{
		try
		{
			String[] entries= classPath.getEntries();
			URL[] configuredURLs= new URL[entries.length];

			for (int i= 0; i < entries.length; i++)
				configuredURLs[i]= new File(entries[i]).toURI().toURL();

			return addClassloaderURLs(configuredURLs, serviceLocator, "", configurator);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private static ClassPath addClassloaderURLs(URL[] theConfiguredURLs, ServiceLocator serviceLocator, String projectBuildDir, DragomeConfigurator configurator) throws Exception
	{
		File file= File.createTempFile("dragome-merged", ".jar");
		file.deleteOnExit();

		JarOutputStream jos= new JarOutputStream(new FileOutputStream(file));

		for (URL theURL : theConfiguredURLs)
		{
			File fileClassPathEntry= new File(theURL.toURI());
			if (!theURL.toString().contains(".jar"))
				CopyUtils.copyClassToJarFile(fileClassPathEntry, jos);
			else
			{
				JarFile jarFile= new JarFile(fileClassPathEntry);
				CopyUtils.copyJarFile(jarFile, jos);
				jarFile.close();
			}
		}

		jos.close();

		return runProguard(file, configurator);
	}

	private static ClassPath runProguard(File file, DragomeConfigurator configurator) throws Exception
	{
		URI uri= DragomeCompilerLauncher.class.getResource("/proguard.conf").toURI();
		Properties properties= System.getProperties();
		properties.put("in-jar-filename", file.getAbsolutePath());
		String outFilename= file.getAbsolutePath().replace(".jar", "-proguard.jar");
		properties.put("out-jar-filename", outFilename);
		ConfigurationParser parser= new ConfigurationParser(uri.toURL(), properties);
		URL additionalCodeKeepConfigFile= configurator.getAdditionalCodeKeepConfigFile();
		Configuration configuration= new Configuration();
		parser.parse(configuration);
		if (additionalCodeKeepConfigFile != null)
		{
			ConfigurationParser parserForAdditionalKeepCodeConfigFile= new ConfigurationParser(additionalCodeKeepConfigFile, properties);
			parserForAdditionalKeepCodeConfigFile.parse(configuration);
		}
		new ProGuard(configuration).execute();
		return new ClassPath(outFilename);
	}
}
