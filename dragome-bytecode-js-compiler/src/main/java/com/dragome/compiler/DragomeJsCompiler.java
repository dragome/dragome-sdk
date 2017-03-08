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

// Copyright 2011 The j2js Authors. All Rights Reserved.
//
// This file is part of j2js.
//
// j2js is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// j2js is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with j2js. If not, see <http://www.gnu.org/licenses/>.

package com.dragome.compiler;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.dragome.commons.compiler.BytecodeToJavascriptCompiler;
import com.dragome.commons.compiler.BytecodeToJavascriptCompilerConfiguration;
import com.dragome.commons.compiler.BytecodeTransformer;
import com.dragome.commons.compiler.annotations.CompilerType;
import com.dragome.commons.compiler.classpath.Classpath;
import com.dragome.commons.compiler.classpath.ClasspathFile;
import com.dragome.commons.compiler.classpath.ClasspathFileFilter;
import com.dragome.compiler.generators.AbstractVisitor;
import com.dragome.compiler.generators.DragomeJavaScriptGenerator;
import com.dragome.compiler.units.ClassUnit;
import com.dragome.compiler.utils.FileManager;
import com.dragome.compiler.utils.Log;
import com.dragome.compiler.writer.Assembly;

public class DragomeJsCompiler implements BytecodeToJavascriptCompiler
{
	public static DragomeJsCompiler compiler;

	public static int errorCount= 0;

	private File basedir;

	private File cacheFile;

	List<com.dragome.compiler.writer.Assembly> assemblies= new ArrayList<Assembly>();

	public FileManager fileManager;

	public boolean optimize= true;

	public boolean failOnError= false;

	private boolean compression= true;

	private String singleEntryPoint;

	private String targetPlatform;

	public int reductionLevel= 5;

	private int junkSizeInKiloBytes= Integer.MAX_VALUE;

	private boolean generateLineNumbers= false;

	public int compileCount= 0;

	public AbstractVisitor generator;

	private Log logger;

	private ClasspathFileFilter classpathFilter;

	public BytecodeTransformer bytecodeTransformer;

	public CompilerType compilerType;

	public BytecodeToJavascriptCompilerConfiguration compilerConfiguration;

	private boolean initialized= false;

	private Classpath classpath;

	public DragomeJsCompiler(BytecodeToJavascriptCompilerConfiguration compilerConfiguration)
	{
		configure(compilerConfiguration);
	}

	public void configure(BytecodeToJavascriptCompilerConfiguration compilerConfiguration)
	{
		if (!initialized)
		{
			compiler= this;
			this.compilerConfiguration= compilerConfiguration;

			initFromCompilerType(compilerConfiguration.getCompilerType());

			Project.singleton= null;
			//	    compiler.setBasedir(basedir);
			compiler.setClasspath(compilerConfiguration.getClasspath());
			compiler.addClasspathFilter(compilerConfiguration.getClasspathFilter());

			if (compilerConfiguration.getBytecodeTransformer() != null)
				compiler.setBytecodeTransformer(compilerConfiguration.getBytecodeTransformer());

			Assembly assembly= new Assembly();
			assembly.setEntryPointClassName(compilerConfiguration.getMainClassName());
			assembly.setTargetLocation(new File(compilerConfiguration.getTargetDir()));
			compiler.addAssembly(assembly);
			compiler.setGenerateLineNumbers(false);
			compiler.setCompression(false);
			logger = Log.logger;
			initialized= true;
		}
	}

	private void setClasspath(Classpath classpath)
	{
		this.classpath= classpath;
	}

	public DragomeJsCompiler(CompilerType compilerType)
	{
		initFromCompilerType(compilerType);
	}

	private void initFromCompilerType(CompilerType compilerType)
	{
		this.compilerType= compilerType;
		setBasedir(new File(System.getProperty("user.dir")));
		setTargetPlatform("web");
	}

	public DragomeJsCompiler()
	{
	}

	public void compile()
	{
		configure(compilerConfiguration);

		try
		{
			if (logger == null)
			{
				setLogger(new Log());
			}

			for (Assembly assembly : assemblies)
			{
				execute(assembly);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private boolean isMavenExecution()
	{
		return System.getProperty("localRepository") != null;
	}

	public void execute(Assembly assembly) throws Exception
	{
		long startTime= System.currentTimeMillis();

		DragomeJsCompiler.compiler= this;

		logger.debug("Entry point is " + assembly.getEntryPointClassName() + "#main(java.lang.String[])void");

		if (assembly.getEntryPointClassName() == null)
		{
			throw new RuntimeException("Field assembly.entryPointClassName must be set");
		}

		if (compilerConfiguration.isCaching() && cacheFile == null)
		{
			String property= "target";

			if (!new File(property).exists() /*|| cacheDir == null || cacheDir.trim().length() == 0*/)
			{
				property= System.getProperty("cache-dir");
			}

			File file= new File(property + "/dragome.cache");
			//	    new File(basedir, "target/dragome.cache").delete();
			//			File file= new File(basedir, "./dragome.cache");
			setCacheFile(file);
		}

		if (assembly.getTargetLocation() == null)
		{
			throw new RuntimeException("Field assembly.targetLocation must be set");

		}

		logger.debug("Creating assembly " + assembly.getTargetLocation());

		logger.infoSameLine("Compiling classes: ");

		//		System.out.print("Progress: ");
		//		for (int percentage= 0; percentage < 100; percentage++)
		//		{
		//			System.out.print(percentage + "%");
		//			Thread.sleep(10); // Stub for "long running task".
		//			int length= String.valueOf(percentage).length() + 1;
		//			while (length-- > 0)
		//			{
		//				System.out.print('\b');
		//			}
		//		}
		//		System.out.println("finished!");
		//
		//		System.out.println((char) 27 + "[01;31m;This text is red." + (char) 27 + "[00;00m");
		//		System.out.println((char) 27 + "[01;32m;This text is green." + (char) 27 + "[00;00m");

		fileManager= new FileManager(classpath, classpathFilter); // Change to get classes from jar. This jar is already filtered
		//	Project.singleton= null; //TODO revisar esto, impide cacheo!!
		Project project= Project.createSingleton(getCacheFile());
		project.setClasspathFilter(classpathFilter);
		project.setStopOnMissingClass(compilerConfiguration.isFailOnError());
		setFailOnError(compilerConfiguration.isFailOnError());
		assembly.setProject(project);
		assembly.setClasspathFilter(classpathFilter);
		generator= new DragomeJavaScriptGenerator(project);
		//	generator= new JavaScriptGenerator(project);

		errorCount= 0;

		Collection<ClasspathFile> classpathFiles= fileManager.getAllFilesInClasspath();
		for (ClasspathFile file : classpathFiles)
		{
			if (file.getPath().contains(".class"))
			{
				String className= getClassname(file);
				assembly.getProject().createClassUnit(className, file);
			}
		}

		for (ClasspathFile file : classpathFiles)
		{
			if (file.getPath().contains(".class"))
			{
				String className= getClassname(file);
				assembly.resolveNoTainting(className, file);
			}
		}

		assembly.addEntryPoint(assembly.getEntryPointClassName() + "#onCreate()void");

		for (String memberSignature : assembly.entryPoints)
		{
			assembly.taint(memberSignature);
		}

		//	String[] signatures= Utils.getProperty("dragomeJs.preTaintedSignatures").split(";");
		//	for (int i= 0; i < signatures.length; i++)
		//	{
		//	    assembly.taint(signatures[i]);
		//	}

		if (DragomeJsCompiler.compiler.getSingleEntryPoint() != null)
		{
			assembly.processSingle(project.getSignature(getSingleEntryPoint()));
		}
		else
		{
			assembly.processTainted();
		}

		int methodCount;
		try
		{
			methodCount= assembly.createAssembly();

			if (getCacheFile() != null)
			{
				Project.write();
			}
			Collection<ClassUnit> classes= assembly.getProject().getClasses();
			for (ClassUnit classUnit : classes)
			{
				try
				{
					classUnit.getClassFile().close();
				}
				catch (Exception e)
				{
				}
			}
		}
		catch (IOException e)
		{
			throw new Exception("Error while creating assembly", e);
		}

		logger.infoSameLine("\n\n");
		//		logger.infoSameLine(timesName("Compiled|Compiled", compileCount, "class|classes") /*+ ", " + timesName("packed|packed", methodCount, "method|methods")*/+ ".");

		logger.infoSameLine("Compiled classes: " + compileCount);
		logger.infoSameLine("\n");
		//	logger.info(timesName("Compiled|Compiled", project.getBadMethods(), "method|methods") + " using compiler");
		{
			final long spendTimeMs = System.currentTimeMillis() - startTime;
			final DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
			df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
			logger.infoSameLine("Total compile time: " + df.format(new Date(spendTimeMs)));
		}
		logger.infoSameLine("\n");

		if (errorCount > 0)
		{
			logger.error("There " + timesName("was|were", errorCount, "error|errors") + ".");
		}
	}

	private String getClassname(ClasspathFile file)
	{
		return file.getPath().replaceAll("\\.class$", "").replace(File.separator, ".").replace("/", ".");
	}
	private String timesName(String verb, int count, String noun)
	{
		String[] verbs= verb.split("\\|");
		String[] nouns= noun.split("\\|");
		int index= (count == 1 ? 0 : 1);
		return verbs[index] + " " + nouns[index] + ": " + count;
	}
	public void setCompression(boolean isCompression)
	{
		this.compression= isCompression;
	}

	public boolean isCompression()
	{
		return compression;
	}

	public void setSingleEntryPoint(String signature)
	{
		singleEntryPoint= signature;
	}

	public String getSingleEntryPoint()
	{
		return singleEntryPoint;
	}

	public void setTargetPlatform(String targetPlatform)
	{
		targetPlatform= targetPlatform.toLowerCase();
		if ("web".equals(targetPlatform) || "javascript".equals(targetPlatform))
		{
			this.targetPlatform= targetPlatform;
		}
		else
		{
			throw new IllegalArgumentException("Target platform must be web or javascript");
		}
	}

	public String getTargetPlatform()
	{
		return targetPlatform;
	}

	public void setFailOnError(boolean flag)
	{
		failOnError= flag;
	}

	public boolean isFailOnError()
	{
		return failOnError;
	}

	public File getCacheFile()
	{
		return cacheFile;
	}

	public void setCacheFile(File theCacheFile)
	{
		cacheFile= theCacheFile;
	}

	public List<com.dragome.compiler.writer.Assembly> getAssemblies()
	{
		return assemblies;
	}

	public void setAssemlies(List<com.dragome.compiler.writer.Assembly> assemblies)
	{
		this.assemblies= assemblies;
	}

	public void setGenerateLineNumbers(boolean theGenerateLineNumbers)
	{
		generateLineNumbers= theGenerateLineNumbers;
	}

	public boolean isGenerateLineNumbers()
	{
		return generateLineNumbers;
	}

	public void setJunkSizeInKiloBytes(int junkSizeInKiloBytes)
	{
		if (junkSizeInKiloBytes < 1)
		{
			throw new RuntimeException("Junk size must be greater than zero.");
		}
		this.junkSizeInKiloBytes= junkSizeInKiloBytes;
	}

	public int getJunkSizeInKiloBytes()
	{
		return junkSizeInKiloBytes;
	}

	public Log getLogger()
	{
		return logger;
	}

	public void setLogger(Log logger)
	{
		this.logger= logger;
		Log.logger= logger;
	}

	public void setBasedir(File basedir)
	{
		this.basedir= basedir;
	}

	public File getBasedir()
	{
		return basedir;
	}

	public void addAssembly(Assembly assembly)
	{
		assemblies.add(assembly);
	}

	public void addClasspathFilter(ClasspathFileFilter classpathFilter)
	{
		this.classpathFilter= classpathFilter;
	}

	public void setBytecodeTransformer(BytecodeTransformer bytecodeTransformer)
	{
		this.bytecodeTransformer= bytecodeTransformer;
	}

	public void setCompilerConfiguration(BytecodeToJavascriptCompilerConfiguration compilerConfiguration)
	{
		this.compilerConfiguration= compilerConfiguration;
	}
}
