/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.xmlvm;

import java.io.File;
import java.util.Collection;

import org.xmlvm.main.Arguments;
import org.xmlvm.plugins.javascript.XsltRunner;
import org.xmlvm.proc.CompilationBundle;
import org.xmlvm.proc.XmlvmResource;
import org.xmlvm.proc.out.DEXmlvmOutputProcess;
import org.xmlvm.proc.out.OutputFile;
import org.xmlvm.util.universalfile.UniversalFile;
import org.xmlvm.util.universalfile.UniversalFileCreator;

public class ClassToJs
{
	public static void main(String[] args)
	{
		File file= new File(args[0]);
		transformClassFileToJs(file, true);
	}

	static String[] args= new String[] { "--in=.", "--out=.", "--enable-ref-counting" };
	static Arguments arguments= new Arguments(args);
	static DEXmlvmOutputProcess deXmlvmOutputProcess= new DEXmlvmOutputProcess(arguments);

	public static String transformClassFileToJs(File file, boolean write)
	{
		UniversalFile universalFile= UniversalFileCreator.createFile(file);
		return transformFile(file.getName(), universalFile, write);
	}

	private static String transformFile(String filename, UniversalFile universalFile, boolean write)
	{
		OutputFile classFile= new OutputFile(universalFile);
		CompilationBundle resources= new CompilationBundle();
		OutputFile outputFile= deXmlvmOutputProcess.generateDEXmlvmFile(classFile, resources);
		Collection<XmlvmResource> resources2= resources.getResources();
		boolean writing= write;
		if (writing)
			outputFile.write();

		String fileName= "";
		String fileAsString= null;
		for (XmlvmResource xmlvmResource : resources2)
		{
			OutputFile runXSLT= XsltRunner.runXSLT("xmlvm2js.xsl", xmlvmResource.getXmlvmDocument());
			if (writing)
			{
				runXSLT.setLocation(".");
				fileName= filename + ".js";
				runXSLT.setFileName(fileName);
				runXSLT.write();
			}
			fileAsString= runXSLT.getData().getFileAsString();
		}
		return fileAsString;
	}

	public static String transformClassFileToJs(String filename, byte[] bytecode)
	{
		return transformFile(filename, UniversalFileCreator.createFile(filename, bytecode), false);
	}
}
